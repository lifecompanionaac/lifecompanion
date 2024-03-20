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
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public enum HubController implements ModeListenerI, LCStateListener {
    INSTANCE;

    private static final long CONFIG_CHECK_INTERVAL_USE_MODE = 10_000, CONFIG_CHECK_INTERVAL_STARTING = 1000;

    private static final Logger LOGGER = LoggerFactory.getLogger(HubController.class);

    private String currentRunningConfigurationId, currentDeviceId;
    private ExecutorService autoSyncService;

    private final AtomicReference<Supplier<String>> requestDeviceIdChange;
    private final Object waitLock;

    HubController() {
        waitLock = new Object();
        requestDeviceIdChange = new AtomicReference<>();
    }

    @Override
    public void lcStart() {
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DEVICE_SYNC_MODE)) {
            if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DEVICE_LOCAL_ID)) {
                String injectedDeviceId = GlobalRuntimeConfigurationController.INSTANCE.getParameter(GlobalRuntimeConfiguration.DEVICE_LOCAL_ID);
                requestDeviceIdChange.set(() -> injectedDeviceId);
            }
            LOGGER.info("{} detected, will launch background config sync thread (auto sync = {})",
                    GlobalRuntimeConfiguration.DEVICE_SYNC_MODE.getName(),
                    GlobalRuntimeConfiguration.DEVICE_SYNC_AUTO_REFRESH.getName());
            this.autoSyncService = Executors.newSingleThreadExecutor(LCNamedThreadFactory.daemonThreadFactory("HubController-config-sync"));
            this.autoSyncService.submit(() -> {
                while (true) {
                    if (AppModeController.INSTANCE.isUseMode()) {
                        // Detect for changes (including null values)
                        Supplier<String> requestDeviceIdChangeVal = requestDeviceIdChange.getAndSet(null);
                        if (requestDeviceIdChangeVal != null) {
                            refreshDeviceLocalId(requestDeviceIdChangeVal.get());
                        } else if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DEVICE_SYNC_AUTO_REFRESH)) {
                            refreshDeviceLocalId(currentDeviceId);
                        }
                    }
                    // Wait (allows to be notified when a change should be immediately done)
                    synchronized (waitLock) {
                        waitLock.wait(AppModeController.INSTANCE.isUseMode() ? CONFIG_CHECK_INTERVAL_USE_MODE : CONFIG_CHECK_INTERVAL_STARTING);
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
        this.currentRunningConfigurationId = configuration.getID();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
    }

    public void requestRefreshDeviceLocalId(String deviceLocalId) {
        synchronized (waitLock) {
            LOGGER.info("Request a local device ID change to {}", deviceLocalId);
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
                HubData.HubConfigInfo configInfo = HubService.INSTANCE.getHubConfigInfoForDeviceLocalId(deviceLocalId);
                if (configInfo != null) {
                    // Get/create the configuration directory to synchronize files
                    File configurationDirectory = IOHelper.getConfigurationHubSyncDirectoryPath(deviceLocalId, configInfo.configurationId);
                    configurationDirectory.mkdirs();
                    LOGGER.info("Will try to synchronize configuration for device {}, config ID {}, config HUB ID {} into {}",
                            deviceLocalId,
                            configInfo.configurationId,
                            configInfo.configurationHubId,
                            configurationDirectory);


                    // Check if there is an info file
                    File configInfoFile = IOHelper.getConfigurationHubSyncInfoPath(deviceLocalId, configInfo.configurationId);
                    HubConfigLocalData hubConfigLocalData = HubService.INSTANCE.getHubConfigLocalData(configInfoFile);

                    // Synchronize the files locally
                    boolean changeDetected = false;
                    if (HubService.INSTANCE.isFileSyncShouldBeDone(hubConfigLocalData, configInfo)) {
                        changeDetected = HubService.INSTANCE.synchronizeConfigurationFilesIn(configurationDirectory, configInfo);
                        LOGGER.info("Configuration files synced, file change detected : {}", changeDetected);
                        HubService.INSTANCE.saveHubConfigLocalData(configInfoFile, new HubConfigLocalData(configInfo.updatedAt));
                    } else {
                        LOGGER.info("Ignored file sync as the local update date and distant update date are the same");
                    }

                    // When changes are detected (or if the device/config changed), load and change the config
                    if (changeDetected || deviceIdChanged || StringUtils.isDifferent(currentRunningConfigurationId, configInfo.configurationId)) {
                        loadAndChangeConfigTo(configInfo.configurationId, configurationDirectory);
                        HubService.INSTANCE.saveDeviceLocalData(deviceLocalId, new HubDeviceLocalData(configInfo.configurationId, ZonedDateTime.now()));
                    } else {
                        LOGGER.info("Ignored configuration sync as no change were detected");
                    }
                } else {
                    LOGGER.warn("Didn't find any hub configuration for device local ID {}", deviceLocalId);
                    tryToLoadLastConfigurationFor(deviceLocalId);
                }
            } catch (Throwable t) {
                LOGGER.error("Could not sync configuration from HUB for device local ID {}, will try to load the previously loaded configuration for device ID", deviceLocalId, t);
                tryToLoadLastConfigurationFor(deviceLocalId);
            }
        } else {
            // TODO : should load an empty config ? is blank device means "clean"
            LOGGER.warn("Incorrect given device local id {}", deviceLocalId);
        }
    }

    private void loadAndChangeConfigTo(String configId, File configurationDirectory) throws Exception {
        this.currentRunningConfigurationId = configId;
        LCConfigurationI loadedConfiguration = AbstractLoadUtilsTask.loadConfiguration(configurationDirectory, null, null);
        AppModeController.INSTANCE.switchUseModeConfiguration(loadedConfiguration, null);
    }

    private void tryToLoadLastConfigurationFor(String deviceLocalId) {
        LOGGER.info("Will try to load the last local configuration for {}", deviceLocalId);
        try {
            HubDeviceLocalData deviceLocalData = HubService.INSTANCE.getDeviceLocalData(deviceLocalId);
            if (deviceLocalData != null && StringUtils.isNotBlank(deviceLocalData.getConfigurationId())) {
                File configurationDirectory = IOHelper.getConfigurationHubSyncDirectoryPath(deviceLocalId, deviceLocalData.getConfigurationId());
                loadAndChangeConfigTo(deviceLocalData.getConfigurationId(), configurationDirectory);
            }
        } catch (Throwable t) {
            LOGGER.warn("Could not load last local configuration for {}", deviceLocalId, t);
        }
    }
}
