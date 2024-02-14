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

import okhttp3.*;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.service.UrlSupplier;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public enum HubService implements LCStateListener {
    INSTANCE;

    private final static int MAX_ATTEMPT_COUNT = 3;
    private final static long PAUSE_BETWEEN_DOWNLOAD = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(HubService.class);

    /**
     * The current LifeCompanion hub access token
     */
    private String hubApiToken;

    /**
     * LifeCompanion hub url
     */
    private String hubUrl;

    /**
     * Cached hub client
     */
    private OkHttpClient hubClient2;

    HubService() {
    }

    // HTTP CLIENT
    //========================================================================
    private OkHttpClient getHubClient() {
        if (hubClient2 == null) {
            hubClient2 = AppServerClient.initializeClientForExternalCalls().addInterceptor((chain) -> {
                Request request = chain.request();
                if (StringUtils.isNotBlank(hubApiToken)) {
                    request = request.newBuilder().addHeader("Authorization", "Bearer " + this.hubApiToken).build();
                }
                return chain.proceed(request.newBuilder().addHeader("Content-Type", "application/vnd.api+json").addHeader("Accept", "application/vnd.api+json").build());
            }).build();
        }
        return hubClient2;
    }


    private String getHubUrl() {
        return StringUtils.endsWithIgnoreCase(hubUrl, "/") ? StringUtils.safeSubstring(hubUrl, 0, hubUrl.length()) : hubUrl;
    }
    //========================================================================

    // START/STOP
    //========================================================================
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
    //========================================================================

    // SERVICES
    //========================================================================
    public HubData.HubConfigInfo getHubConfigInfoForDeviceLocalId(String deviceLocalId) throws Exception {
        try (Response responseDeviceConfig = getHubClient().newCall(new Request.Builder().url(getHubUrl() + "/api/v1/lc-devices?include=config&filter[localId]=" + deviceLocalId).build()).execute()) {
            if (checkResponse(responseDeviceConfig)) {
                HubData.GetDeviceConfigResult getDeviceConfigResult = JsonHelper.GSON.fromJson(responseDeviceConfig.body().string(), HubData.GetDeviceConfigResult.class);
                if (!CollectionUtils.isEmpty(getDeviceConfigResult.included)) {
                    HubData.LcConfig config = getDeviceConfigResult.included.get(0);
                    return new HubData.HubConfigInfo(config.attributes.localId, config.id, config.attributes.updatedAt);
                }
            }
        }
        return null;
    }


    public boolean synchronizeConfigurationFilesIn(File configurationDirectory, HubData.HubConfigInfo configurationIds) throws Exception {
        // Create local file hashes
        Map<String, String> hashes = new HashMap<>();
        exploreAndHashFiles(configurationDirectory, configurationDirectory, hashes);
        HubData.LocalHashes localHashes = new HubData.LocalHashes(hashes);

        // Get the diff from server
        try (Response response = getHubClient().newCall(
                new Request.Builder()
                        .url(getHubUrl() + "/api/v1/lc-configs/" + configurationIds.configurationHubId + "/-actions/sync/client/prepare")
                        .post(RequestBody.create(JsonHelper.GSON.toJson(localHashes), null))
                        .build()).execute()) {
            if (checkResponse(response)) {
                HubData.UpdateFiles updateFiles = JsonHelper.GSON.fromJson(response.body().string(), HubData.UpdateFilesResult.class).data;
                LOGGER.info("Changes detected from hub, added = {}, modified = {}, removed = {}", updateFiles.getAddedCount(), updateFiles.getModifiedCount(), updateFiles.getRemovedCount());

                // Only when changes are detected
                if (updateFiles.getRemovedCount() + updateFiles.getAddedCount() + updateFiles.getModifiedCount() > 0) {
                    // Download added/modified files in a temp dir
                    if (updateFiles.getAddedCount() + updateFiles.getModifiedCount() > 0) {
                        File tempDir = org.lifecompanion.util.IOUtils.getTempDir("config-sync-" + configurationIds.configurationHubId);
                        LOGGER.info("Temp download directory to sync configuration {} : {}", configurationIds.configurationHubId, tempDir);
                        long start = System.currentTimeMillis();
                        downloadFilesFrom(configurationIds.configurationHubId, tempDir, updateFiles.added);
                        downloadFilesFrom(configurationIds.configurationHubId, tempDir, updateFiles.modified);
                        LOGGER.info("Successfully downloaded every sync {} files in {}s, will now update local files",
                                updateFiles.getAddedCount() + updateFiles.getModifiedCount(),
                                (System.currentTimeMillis() - start) / 1000.0);
                        copyToFinalDest(tempDir, configurationDirectory, updateFiles.added);
                        copyToFinalDest(tempDir, configurationDirectory, updateFiles.modified);
                    }
                    // Remove files
                    if (updateFiles.getRemovedCount() > 0) {
                        for (Map.Entry<String, HubData.UpdatedFileData> downloadedFile : updateFiles.removed.entrySet()) {
                            File destFile = new File(configurationDirectory + File.separator + downloadedFile.getKey());
                            destFile.delete();
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public HubDeviceLocalData getDeviceLocalData(String deviceLocalId) {
        File deviceLocalDataPath = IOHelper.getDeviceLocalDataPath(deviceLocalId);
        if (deviceLocalDataPath.exists()) {
            try {
                return JsonHelper.GSON.fromJson(IOUtils.readFileLines(deviceLocalDataPath, StandardCharsets.UTF_8.name()), HubDeviceLocalData.class);
            } catch (Exception e) {
                LOGGER.info("Could not read hub device local data", e);
            }
        }
        return null;
    }

    public void saveDeviceLocalData(String deviceLocalId, HubDeviceLocalData hubDeviceLocalData) {
        File deviceLocalDataPath = IOHelper.getDeviceLocalDataPath(deviceLocalId);
        try (PrintWriter pw = new PrintWriter(deviceLocalDataPath, StandardCharsets.UTF_8)) {
            JsonHelper.GSON.toJson(hubDeviceLocalData, pw);
        } catch (Exception e) {
            LOGGER.info("Could not write hub device local data", e);
        }
    }

    public HubConfigLocalData getHubConfigLocalData(File configInfoFile) {
        if (configInfoFile.exists()) {
            try {
                return JsonHelper.GSON.fromJson(IOUtils.readFileLines(configInfoFile, StandardCharsets.UTF_8.name()), HubConfigLocalData.class);
            } catch (Exception e) {
                LOGGER.info("Could not read hub config local data", e);
            }
        }
        return null;
    }

    public void saveHubConfigLocalData(File configInfoFile, HubConfigLocalData hubConfigLocalData) {
        try (PrintWriter pw = new PrintWriter(configInfoFile, StandardCharsets.UTF_8)) {
            JsonHelper.GSON.toJson(hubConfigLocalData, pw);
        } catch (Exception e) {
            LOGGER.info("Could not write hub config local data", e);
        }
    }

    public boolean isFileSyncShouldBeDone(HubConfigLocalData hubConfigLocalData, HubData.HubConfigInfo configurationsIds) {
        if (hubConfigLocalData == null) return true;
        else {
            return configurationsIds.updatedAt.isAfter(hubConfigLocalData.getUpdatedAt());
        }
    }
    //========================================================================

    // INTERNAL
    //========================================================================
    private boolean checkResponse(Response response) throws Exception {
        if (!response.isSuccessful()) {
            ResponseBody body = response.body();
            throw new Exception("Incorrect server response,\n\thttp code=" + response.code() + "\n\tbody=" + (body != null ? body.string() : "(EMPTY BODY)"));
        }
        return true;
    }

    private static void exploreAndHashFiles(File root, File file, Map<String, String> hashes) throws IOException {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File f : listFiles) {
                    exploreAndHashFiles(root, f, hashes);
                }
            }
        } else {
            String targetPath = IOUtils.getRelativePath(file.getAbsolutePath(), root.getAbsolutePath());
            String hash = IOUtils.fileMd5HexToString(file);
            hashes.put(targetPath, hash);
        }
    }

    private void copyToFinalDest(File tempDir, File destDir, Map<String, HubData.UpdatedFileData> updateFilesData) throws Exception {
        for (Map.Entry<String, HubData.UpdatedFileData> downloadedFile : updateFilesData.entrySet()) {
            File sourceFile = new File(tempDir + File.separator + downloadedFile.getKey());
            File destFile = new File(destDir + File.separator + downloadedFile.getKey());
            IOUtils.copyFiles(sourceFile, destFile);
            sourceFile.delete();
        }
    }

    private void downloadFilesFrom(String configurationHubId, File tempDir, Map<String, HubData.UpdatedFileData> updateFilesData) throws Exception {
        OkHttpClient downloadClient = AppServerClient.initializeClientForExternalCalls().build();
        for (Map.Entry<String, HubData.UpdatedFileData> fileToDownload : updateFilesData.entrySet()) {
            HubData.UpdatedFileData updatedFileData = fileToDownload.getValue();
            String relativePath = fileToDownload.getKey();
            downloadFile(downloadClient, () -> {
                try (Response response = getHubClient().newCall(new Request.Builder().url(getHubUrl() + "/api/v1/lc-configs/" + configurationHubId + "/-actions/sync/files/" + updatedFileData.id)
                        .build()).execute()) {
                    if (checkResponse(response)) {
                        return JsonHelper.GSON.fromJson(response.body().string(), HubData.FileGetEndpointResult.class).data.endpoint;
                    }
                    return null;
                }
            }, updatedFileData.hash, new File(tempDir + File.separator + relativePath));
            if (PAUSE_BETWEEN_DOWNLOAD > 0) {
                Thread.sleep(PAUSE_BETWEEN_DOWNLOAD);
            }
        }
    }

    private void downloadFile(OkHttpClient client, UrlSupplier urlSupplier, String hash, File destPath) throws Exception {
        String url = null;
        for (int i = 0; i < MAX_ATTEMPT_COUNT; ++i) {
            Throwable error = null;
            try {
                long start = System.currentTimeMillis();
                url = urlSupplier.getUrl();
                if (url == null) {
                    throw new IllegalStateException("Didn't get any file download URL from urlGenerator");
                }
                download(client, url, destPath);
                long diff = System.currentTimeMillis() - start;
                LOGGER.info("{} - finished in {} ms for {} / speed = {}/s",
                        destPath.getName(),
                        diff,
                        FileNameUtils.getFileSize(destPath.length()),
                        FileNameUtils.getFileSize((long) ((double) destPath.length() / ((double) diff / 1000.0))));
                String fileHash = IOUtils.fileMd5HexToString(destPath);
                if (StringUtils.isEquals(hash, fileHash)) {
                    return;
                }
                LOGGER.error("Incorrect hash check, found hash {}, expected {}", fileHash, hash);
            } catch (Throwable t) {
                error = t;
            }
            LOGGER.warn("Download/hash checking failed for file {} (last supplied URL {}) / {} attempt left", destPath, url, MAX_ATTEMPT_COUNT - i - 1, error);
            Thread.sleep(10_000L);
        }
        throw new Exception("Download failed after " + MAX_ATTEMPT_COUNT + " attempt");
    }

    private void download(OkHttpClient client, String url, File destPath) throws Exception {
        File parentDir = destPath.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }
        Call call = client.newCall(new Request.Builder().url(url).addHeader("Connection", "close").build());
        try (Response response = call.execute()) {
            if (checkResponse(response)) {
                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(destPath))) {
                    try (InputStream is = new BufferedInputStream(response.body().byteStream())) {
                        IOUtils.copyStream(is, os);
                    }
                }
            }
        }
    }


    //========================================================================

}
