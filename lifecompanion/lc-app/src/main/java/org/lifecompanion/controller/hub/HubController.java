/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lifecompanion.controller.hub;

import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.task.AbstractLoadUtilsTask;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public enum HubController implements ModeListenerI, LCStateListener {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(HubController.class);

    private String currentRunningConfigurationId, currentDeviceId;
    private ExecutorService autoSyncService;

    private AtomicReference<Supplier<String>> requestDeviceIdChange;

    private final Object waitLock;

    HubController() {
        waitLock = new Object();
        requestDeviceIdChange = new AtomicReference<>();
    }

    @Override
    public void lcStart() {
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DEVICE_SYNC_MODE)) {
            LOGGER.info("{} detected, will launch background config sync thread", GlobalRuntimeConfiguration.DEVICE_SYNC_MODE.getName());
            this.autoSyncService = Executors.newSingleThreadExecutor(LCNamedThreadFactory.daemonThreadFactory("HubController-config-sync"));
            this.autoSyncService.submit(() -> {
                while (true) {
                    if (AppModeController.INSTANCE.isUseMode()) {
                        // Detect for changes (including null values)
                        Supplier<String> requestDeviceIdChangeVal = requestDeviceIdChange.getAndSet(null);
                        if (requestDeviceIdChangeVal != null) {
                            refreshDeviceLocalId(requestDeviceIdChangeVal.get());
                        } else {
                            refreshDeviceLocalId(currentDeviceId);
                        }
                        // Wait (allows to be notified when a change should be immediately done)
                        synchronized (waitLock) {
                            waitLock.wait(5_000);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void lcExit() {
        if (this.autoSyncService != null) {
            this.autoSyncService.shutdownNow();
        }
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        currentDeviceId = "user_1";//FIXME
        this.currentRunningConfigurationId = configuration.getID();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
    }

    public void requestRefreshDeviceLocalId(String deviceLocalId) {
        synchronized (waitLock) {
            requestDeviceIdChange.set(() -> deviceLocalId);
            waitLock.notify();
        }
    }

    private void refreshDeviceLocalId(String deviceLocalId) {
        boolean deviceIdChanged = StringUtils.isDifferent(currentDeviceId, deviceLocalId);
        this.currentDeviceId = deviceLocalId;
        if (StringUtils.isNotBlank(deviceLocalId)) {
            LOGGER.info("Starting config sync for device {}", deviceLocalId);
            try {
                HubData.HubConfigurationIds configurationsIds = HubService.INSTANCE.getConfigurationIdsForDevice(deviceLocalId);
                if (configurationsIds != null) {
                    // Get/create the configuration directory to synchronize files
                    File configurationDirectory = IOHelper.getConfigurationHubSyncDirectoryPath(deviceLocalId, configurationsIds.configurationId);
                    configurationDirectory.mkdirs();
                    LOGGER.info("Will try to synchronize configuration for device {}, config ID {}, config HUB ID {} into {}",
                            deviceLocalId,
                            configurationsIds.configurationId,
                            configurationsIds.configurationHubId,
                            configurationDirectory);

                    // Synchronize the files locally
                    boolean changeDetected = HubService.INSTANCE.synchronizeConfigurationFilesIn(configurationDirectory, configurationsIds);
                    LOGGER.info("Configuration files synced, change detected : {}", changeDetected);

                    // When changes are detected (or if the device/config changed), load and change the config
                    if (changeDetected || deviceIdChanged || StringUtils.isDifferent(currentRunningConfigurationId, configurationsIds.configurationId)) {
                        LCConfigurationI loadedConfiguration = AbstractLoadUtilsTask.loadConfiguration(configurationDirectory, null, null);
                        AppModeController.INSTANCE.switchUseModeConfiguration(loadedConfiguration, null);
                    } else {
                        LOGGER.info("Ignored configuration sync as no change were detected");
                    }
                } else {
                    LOGGER.warn("Didn't find any hub configuration for device local ID {}", deviceLocalId);
                }
            } catch (Throwable t) {
                LOGGER.error("Could not sync configuration from HUB for device local ID {}", deviceLocalId, t);
            }
        } else {
            LOGGER.warn("Incorrect given device local id {}", deviceLocalId);
        }
    }
}
