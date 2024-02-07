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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.io.task.SyncConfigFromHubTask;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum HubController implements ModeListenerI, LCStateListener {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(HubController.class);

    /**
     * The current LifeCompanion hub access token
     */
    private String hubApiToken;

    /**
     * LifeCompanion hub url
     */
    private String hubUrl;

    private String currentRunningConfigurationId, currentDeviceId;


    HubController() {
    }

    public OkHttpClient getHttpClient() {
        return AppServerClient.initializeClientForExternalCalls()
                .addInterceptor((chain) -> {
                    Request request = chain.request();
                    if (StringUtils.isNotBlank(hubApiToken)) {
                        request = request.newBuilder().addHeader("Authorization", "Bearer " + this.hubApiToken).build();
                    }
                    return chain.proceed(request.newBuilder().addHeader("Content-Type", "application/vnd.api+json")
                            .addHeader("Accept", "application/vnd.api+json").build());
                }).build();
    }

    public String getHubUrl() {
        return StringUtils.endsWithIgnoreCase(hubUrl, "/") ? StringUtils.safeSubstring(hubUrl, 0, hubUrl.length()) : hubUrl;
    }

    public boolean isDifferentDeviceOrConfiguration(String deviceId, String configurationId) {
        return StringUtils.isDifferent(currentDeviceId, deviceId) || StringUtils.isDifferent(currentRunningConfigurationId, configurationId);
    }

    @Override
    public void lcStart() {
        // Get the token if available
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.HUB_URL)) {
            this.hubUrl = GlobalRuntimeConfigurationController.INSTANCE.getParameter(GlobalRuntimeConfiguration.HUB_URL);
            LOGGER.info("LifeCompanion hub url is set with the {} parameter ({})", GlobalRuntimeConfiguration.HUB_URL.getName(), this.hubUrl);
        }
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.HUB_AUTH_TOKEN)) {
            this.hubApiToken = GlobalRuntimeConfigurationController.INSTANCE.getParameter(GlobalRuntimeConfiguration.HUB_AUTH_TOKEN);
            LOGGER.info("LifeCompanion hub auth token is set with the {} parameter and will be not change", GlobalRuntimeConfiguration.HUB_AUTH_TOKEN.getName());
        }
    }

    @Override
    public void lcExit() {
    }


    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.currentRunningConfigurationId = configuration.getID();
        // FIXME : TO REMOVE
        Thread testThread = new Thread(() -> {
            while (true) {
                if (!refreshing) {
                    refreshDeviceLocalId("user_1");
                    ThreadUtils.safeSleep(5_000);
                }
            }
        });
        testThread.setDaemon(true);
        testThread.start();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
    }

    private boolean refreshing;

    public void refreshDeviceLocalId(String deviceLocalId) {
        refreshing = true;
        SyncConfigFromHubTask syncConfigFromHubTask = new SyncConfigFromHubTask(deviceLocalId);
        syncConfigFromHubTask.setOnSucceeded(e -> {
            this.currentDeviceId = deviceLocalId;
            LCConfigurationI value = syncConfigFromHubTask.getValue();
            if (value != null) {
                AppModeController.INSTANCE.switchUseModeConfiguration(value, null);
            }
            refreshing = false;
        });
        syncConfigFromHubTask.setOnFailed(e -> {
            refreshing = false;
        });
        AsyncExecutorController.INSTANCE.addAndExecute(false, true, syncConfigFromHubTask);
    }
}
