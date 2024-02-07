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

package org.lifecompanion.controller.io.task;

import okhttp3.*;
import org.lifecompanion.controller.hub.HubController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.service.UrlSupplier;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.exception.LCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: error+exception, handling cancel, translation, cleaning...
 */
public class SyncConfigFromHubTask extends AbstractLoadUtilsTask<LCConfigurationI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncConfigFromHubTask.class);
    private final String deviceLocalId;
    private final static int MAX_ATTEMPT_COUNT = 3;

    public SyncConfigFromHubTask(String deviceLocalId) {
        super("task.title.sync.config.from.hub");
        this.deviceLocalId = deviceLocalId;
    }

    @Override
    protected LCConfigurationI call() throws Exception {
        String configurationHubId = null;
        String configurationId = null;

        LOGGER.info("Start sync checking for device id {}", deviceLocalId);

        OkHttpClient hubClient = HubController.INSTANCE.getHttpClient();

        // Get the configuration for device
        try (Response responseDeviceConfig = hubClient.newCall(new Request.Builder().url(HubController.INSTANCE.getHubUrl() + "/api/v1/lc-devices?include=config&filter[localId]=" + deviceLocalId)
                .build()).execute()) {
            GetDeviceConfigResult getDeviceConfigResult = JsonHelper.GSON.fromJson(responseDeviceConfig.body().string(), GetDeviceConfigResult.class);
            if (!CollectionUtils.isEmpty(getDeviceConfigResult.included)) {
                configurationHubId = getDeviceConfigResult.included.get(0).id;
                configurationId = getDeviceConfigResult.included.get(0).attributes.localId;
            }
        }
        if (StringUtils.isBlank(configurationId) || StringUtils.isBlank(configurationHubId)) {
            // TODO : message
            LCException.newException().buildAndThrow();
        }

        // Get/create local directory (create when needed)
        File configDirectory = IOHelper.getConfigurationHubSyncDirectoryPath(deviceLocalId, configurationId);
        configDirectory.mkdirs();
        LOGGER.info("Sync configuration directory : {}", configDirectory);

        // Create local hash
        Map<String, String> hashes = new HashMap<>();
        exploreAndHashFiles(configDirectory, configDirectory, hashes);
        LocalHashes localHashes = new LocalHashes(hashes);

        // Get the diff from server
        boolean changeOnConfig = false;
        try (Response response = hubClient.newCall(
                new Request.Builder()
                        .url(HubController.INSTANCE.getHubUrl() + "/api/v1/lc-configs/" + configurationHubId + "/-actions/sync/client/prepare")
                        .post(RequestBody.create(JsonHelper.GSON.toJson(localHashes), null))
                        .build()).execute()) {
            if (response.isSuccessful()) {
                UpdateFiles updateFiles = JsonHelper.GSON.fromJson(response.body().string(), UpdateFilesResult.class).data;
                LOGGER.info("Changes detected from hub, added = {}, modified = {}, removed = {}", updateFiles.getAddedCount(), updateFiles.getModifiedCount(), updateFiles.getRemovedCount());
                if (updateFiles.getRemovedCount() + updateFiles.getAddedCount() + updateFiles.getModifiedCount() > 0) {
                    changeOnConfig = true;
                    // Download added/modified files in a temp dir
                    if (updateFiles.getAddedCount() + updateFiles.getModifiedCount() > 0) {
                        File tempDir = org.lifecompanion.util.IOUtils.getTempDir("config-sync-" + configurationHubId);
                        LOGGER.info("Temp download directory to sync configuration {} : {}", configurationHubId, tempDir);
                        downloadFilesFrom(hubClient, configurationHubId, tempDir, updateFiles.added);
                        downloadFilesFrom(hubClient, configurationHubId, tempDir, updateFiles.modified);
                        LOGGER.info("Successfully downloaded every sync file, will now update local file ");
                        copyToFinalDest(tempDir, configDirectory, updateFiles.added);
                        copyToFinalDest(tempDir, configDirectory, updateFiles.modified);
                    }
                    // Remove files
                    if (updateFiles.getRemovedCount() > 0) {
                        for (Map.Entry<String, UpdatedFileData> downloadedFile : updateFiles.removed.entrySet()) {
                            File destFile = new File(configDirectory + File.separator + downloadedFile.getKey());
                            destFile.delete();
                        }
                    }
                }
            } else {
                if (response.body() != null) {
                    LOGGER.error("Problem on configuration sync from hub, error body\n{}", response.body().string());
                }
                // TODO "Can't sync configuration " + configurationHubId + " from hub, compare request failed"
                LCException.newException().buildAndThrow();
            }
        }

        // TODO : or the config/device changed from previously loaded
        // Load the synced configuration when changes are detected
        if (changeOnConfig || HubController.INSTANCE.isDifferentDeviceOrConfiguration(deviceLocalId, configurationId)) {
            return loadConfiguration(configDirectory, null);
        } else {
            LOGGER.info("Didn't detect any change in current device configuration, will not change it");
            return null;
        }
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

    private void copyToFinalDest(File tempDir, File destDir, Map<String, UpdatedFileData> updateFilesData) throws Exception {
        for (Map.Entry<String, UpdatedFileData> downloadedFile : updateFilesData.entrySet()) {
            File sourceFile = new File(tempDir + File.separator + downloadedFile.getKey());
            File destFile = new File(destDir + File.separator + downloadedFile.getKey());
            IOUtils.copyFiles(sourceFile, destFile);
            sourceFile.delete();
        }
    }

    private void downloadFilesFrom(OkHttpClient hubClient, String configurationHubId, File tempDir, Map<String, UpdatedFileData> updateFilesData) throws Exception {
        OkHttpClient downloadClient = AppServerClient.initializeClientForExternalCalls().build();
        for (Map.Entry<String, UpdatedFileData> fileToDownload : updateFilesData.entrySet()) {
            UpdatedFileData updatedFileData = fileToDownload.getValue();
            String relativePath = fileToDownload.getKey();
            downloadFile(downloadClient, () -> {
                try (Response response = hubClient.newCall(new Request.Builder().url(HubController.INSTANCE.getHubUrl() + "/api/v1/lc-configs/" + configurationHubId + "/-actions/sync/files/" + updatedFileData.id)
                        .build()).execute()) {
                    if (checkResponse(response)) {
                        return JsonHelper.GSON.fromJson(response.body().string(), FileGetEndpointResult.class).data.endpoint;
                    }
                    return null;
                }
            }, updatedFileData.hash, new File(tempDir + File.separator + relativePath));
            // FIXME delete
            Thread.sleep(1000);
        }
    }

    private boolean checkResponse(Response response) throws IOException, LCException {
        if (!response.isSuccessful()) {
            ResponseBody body = response.body();
            LOGGER.warn("Incorrect server response, code {}, body\n{}", response.code(), body != null ? body.string() : "no body");
            LCException.newException().buildAndThrow();
            return false;
        }
        return true;
    }


    private void downloadFile(OkHttpClient client, UrlSupplier urlSupplier, String hash, File destPath) throws Exception {
        String url = null;
        for (int i = 0; i < MAX_ATTEMPT_COUNT; ++i) {
            Throwable error = null;
            try {
                long start = System.currentTimeMillis();
                // TODO : URL generation with a call
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
                Thread.sleep(10_000L);
            } catch (Throwable var14) {
                error = var14;
            }
            LOGGER.warn("Download/hash checking failed for file {} (last supplied URL {}) / {} attempt left", destPath, url, MAX_ATTEMPT_COUNT - i - 1, error);
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
            if (response.isSuccessful()) {
                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(destPath))) {
                    try (InputStream is = new BufferedInputStream(response.body().byteStream())) {
                        IOUtils.copyStream(is, os);
                    }
                }
            } else {
                throw new Exception("Download server returned error : " + response.code());
            }
        }
    }

    private static class LocalHashes {
        private final Map<String, String> data;

        private LocalHashes(Map<String, String> data) {
            this.data = data;
        }
    }

    private static class UpdateFilesResult {
        private UpdateFiles data;
    }

    private static class UpdateFiles {
        private Map<String, UpdatedFileData> added;
        private Map<String, UpdatedFileData> modified;
        private Map<String, UpdatedFileData> removed;

        public int getAddedCount() {
            return added != null ? added.size() : 0;
        }

        public int getModifiedCount() {
            return modified != null ? modified.size() : 0;
        }

        public int getRemovedCount() {
            return removed != null ? removed.size() : 0;
        }
    }

    private static class UpdatedFileData {
        private String id;
        private String hash;
    }

    private static class FileEndpoint {
        private String endpoint;
    }

    private static class FileGetEndpointResult {
        private FileEndpoint data;
    }

    private static class GetDeviceConfigResult {
        private List<LcConfig> included;
    }

    private static class LcConfig {
        private String id;
        private LcConfigAttributes attributes;
    }

    private static class LcConfigAttributes {
        private String localId;
    }
}
