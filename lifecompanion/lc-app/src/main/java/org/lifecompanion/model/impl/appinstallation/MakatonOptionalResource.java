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

package org.lifecompanion.model.impl.appinstallation;

import okhttp3.*;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.appinstallation.OptionalResourceController;
import org.lifecompanion.controller.appinstallation.task.GetInstallationIdTask;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.controller.metrics.SendPendingSessionStatsRunnable;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.props.ApplicationBuildProperties;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.server.update.ApplicationInstaller;
import org.lifecompanion.model.api.appinstallation.OptionalResourceI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.function.BiConsumer;

public class MakatonOptionalResource implements OptionalResourceI {
    private static final Logger LOGGER = LoggerFactory.getLogger(MakatonOptionalResource.class);

    @Override
    public boolean isInstalled() {
        // check for image database files : check also for renamed files
        return false;
    }

    @Override
    public boolean validateInstallation() throws Exception {
        String installationId = InstallationController.INSTANCE.getInstallationRegistrationInformation().getInstallationId();
        OkHttpClient okHttpClient = AppServerClient.initializeClientForExternalCalls().build();
        final ApplicationBuildProperties buildProperties = InstallationController.INSTANCE.getBuildProperties();

        Request installRequest = new Request.Builder().url(buildProperties.getAppServerUrl() + "/api/v1/services/software-service-associations/check" + StringUtils.trimToEmpty(buildProperties.getAppServerQueryParameters()))
                .post(RequestBody.create(JsonHelper.GSON.toJson(new DataDto(new CheckMakatonDto("makaton", installationId))), null))
                .addHeader("Content-Type", "application/vnd.api+json")
                .build();
        try (Response response = okHttpClient.newCall(installRequest).execute()) {

        }


        return false;
    }

    @Override
    public void uninstall() {
        // delete image database files
    }

    @Override
    public boolean isUsedOn(LCConfigurationI configuration) {
        // TODO : like PluginController.serializePluginInformation
        return false;
    }

    @Override
    public void install(BiConsumer<Long, Long> progress, Object... args) throws Exception {
        String email = (String) args[0];
        LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
        String installationId = InstallationController.INSTANCE.getInstallationRegistrationInformation().getInstallationId();
        final ApplicationBuildProperties buildProperties = InstallationController.INSTANCE.getBuildProperties();
        String name = (currentProfile != null ? currentProfile.nameProperty().get() : "") + " / " + System.getProperty("user.name") + " / " + Translation.getText(SystemType.current()
                .getLabelID());
        File zipFileTempDir = org.lifecompanion.util.IOUtils.getTempFile("makaton", ".zip");

        LOGGER.info("Will try to install makaton for email {} and installation id {}", email, installationId);

        OkHttpClient okHttpClient = AppServerClient.initializeClientForExternalCalls().build();
        Request installRequest = new Request.Builder().url(buildProperties.getAppServerUrl() + "/api/v1/services/software-service-associations/install" + StringUtils.trimToEmpty(buildProperties.getAppServerQueryParameters()))
                .post(RequestBody.create(JsonHelper.GSON.toJson(new DataDto(new InstallMakatonDto(name, "makaton", installationId, email))), null))
                .addHeader("Content-Type", "application/vnd.api+json")
                .build();
        try (Response installResponse = okHttpClient.newCall(installRequest).execute()) {
            LOGGER.info("Response to install request is {}", installResponse.code());
            if (installResponse.isSuccessful()) {
                String deliverFileUrl = JsonHelper.GSON.fromJson(installResponse.body().string(), InstallSuccessDto.class).data.deliverFile;
                LOGGER.info("Start downloading makaton to {}", deliverFileUrl);
                downloadToFile(okHttpClient, progress, deliverFileUrl, zipFileTempDir);

                File imageDictionariesRoot = new File(LCConstant.DEFAULT_IMAGE_DICTIONARIES);
                LOGGER.info("Download finished, will now install it in image dictionary folder : {}", imageDictionariesRoot.getAbsolutePath());
                IOUtils.unzipInto(zipFileTempDir, imageDictionariesRoot, null); // TODO : progress ?

            } else if (installResponse.code() >= 400 && installResponse.code() < 500) {
                if (installResponse.code() == 409) {
                    System.out.println(installResponse.body().string());
                    // {"errors":[{"status":"409","title":"Conflict","detail":"You've reached the 1 installations limit for service Makaton.","meta":{"resolve":{"title":"Reassign your devices","description":"To solve this problem, remove some of your devices for service Makaton on your LifeCompanion account.","url":"https:\/\/lifecompanionaac.org\/auth\/register?redirectUrl=%2Faccount%2Fsoftware%2Fassociations&guarded=auth&email=mathieu.thebaud%40vyv3.fr"}}}]}
                }
                // TODO : Exception to explain process
            } else {
                // TODO : Exception on unexplained error
            }
        }
    }

    private static void downloadToFile(OkHttpClient okHttpClient, BiConsumer<Long, Long> progress, String deliverFileUrl, File zipFileTempDir) throws IOException {
        try (Response downloadResponse = okHttpClient.newCall(new Request.Builder()
                .url(deliverFileUrl)
                .addHeader("Connection", "close")
                .build()).execute()) {
            if (downloadResponse.isSuccessful()) {
                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(zipFileTempDir))) {
                    try (InputStream is = new BufferedInputStream(downloadResponse.body().byteStream())) {
                        IOUtils.copyStreamCounting(is, os, null);// TODO : progress ?
                    }
                }
            }
        }
    }

    private static class InstallSuccessDto {
        private InstallSuccessDataDto data;
    }

    private static class InstallSuccessDataDto {
        private String deliverFile;
    }

    private static class DataDto {
        private final Object data;

        private DataDto(Object data) {
            this.data = data;
        }
    }

    private static class InstallMakatonDto {
        private final String name, service, installationId, email;

        private InstallMakatonDto(String name, String service, String installationId, String email) {
            this.name = name;
            this.service = service;
            this.installationId = installationId;
            this.email = email;
        }
    }

    private static class CheckMakatonDto {
        private final String service, installationId;

        private CheckMakatonDto(String service, String installationId) {
            this.service = service;
            this.installationId = installationId;
        }
    }
}
