/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
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

package org.lifecompanion.framework.client.service;

import org.lifecompanion.framework.client.http.ApiException;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.ApplicationConstant;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.client.UpdateFileProgress;
import org.lifecompanion.framework.model.server.dto.AddApplicationUpdateStatDto;
import org.lifecompanion.framework.model.server.update.ApplicationLauncherUpdate;
import org.lifecompanion.framework.model.server.update.ApplicationPluginUpdate;
import org.lifecompanion.framework.model.server.update.ApplicationUpdate;
import org.lifecompanion.framework.model.server.update.TargetType;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppServerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppServerService.class);

    private final AppServerClient client;

    public AppServerService(AppServerClient client) {
        super();
        this.client = client;
    }

    // GENERAL
    //========================================================================
    public void downloadFileWithoutCheck(String url, File destPath) throws ApiException {
        this.client.download(url, destPath);
    }

    public void wakeup() throws ApiException {
        this.client.get("public/wakeup", String.class);
    }
    //========================================================================

    // APPLICATION UPDATE
    //========================================================================
    public UpdateFileProgress[] getUpdateDiff(String applicationId, SystemType system, String fromVersion, boolean preview) throws ApiException {
        return this.client.get("public/get-last-update-diff/" + applicationId + "/" + system + "/" + fromVersion + "/" + preview, UpdateFileProgress[].class);
    }

    public ApplicationUpdate getLastApplicationUpdate(String applicationId, boolean preview) throws ApiException {
        return this.client.get("public/get-last-application-update/" + applicationId + "/" + preview, ApplicationUpdate.class);
    }

    public String getApplicationFileDownloadUrl(String fileId) throws ApiException {
        return this.client.get("public/get-application-file-url/" + fileId, String.class);
    }
    //========================================================================

    // LAUNCHER UPDATE
    //========================================================================
    public ApplicationLauncherUpdate getLastLauncherInformation(String applicationId, SystemType system, boolean preview) throws ApiException {
        return this.client.get("public/get-last-launcher-update/" + applicationId + "/" + system + "/" + preview, ApplicationLauncherUpdate.class);
    }

    public String getLauncherDownloadUrl(String launcherId) throws ApiException {
        return this.client.get("public/get-launcher-file-url/" + launcherId, String.class);
    }
    //========================================================================

    // PLUGIN UPDATE
    //========================================================================
    public ApplicationPluginUpdate[] getPluginUpdatesOrderByVersion(String pluginId, boolean preview) throws ApiException {
        return this.client.get("public/get-plugin-updates-order-by-version/" + pluginId + "/" + preview, ApplicationPluginUpdate[].class);
    }

    public String getPluginUpdateDownloadUrl(String pluginUpdateId) throws ApiException {
        return this.client.get("public/get-plugin-update-file-url/" + pluginUpdateId, String.class);
    }
    //========================================================================

    // UPDATE STAT
    //========================================================================
    public void pushStat(String version, Date recordedAt) throws ApiException {
        AddApplicationUpdateStatDto addApplicationUpdateStatDto = new AddApplicationUpdateStatDto(version, recordedAt, SystemType.current());
        this.client.post("public/add-update-stat", addApplicationUpdateStatDto);
    }
    //========================================================================

    // HELPER
    //========================================================================
    public void downloadFileAndCheckIt(UrlSupplier urlGenerator, File filePath, String hash, int attemptCount) throws ApiException {
        String url = null;
        for (int i = 0; i < attemptCount; i++) {
            Throwable error = null;
            try {
                long start = System.currentTimeMillis();
                url = urlGenerator.getUrl();
                if (url != null) {
                    this.client.download(url, filePath);
                    long diff = System.currentTimeMillis() - start;
                    LOGGER.info("{} - finished in {} ms for {} / speed = {}/s", filePath.getName(), diff, FileNameUtils.getFileSize(filePath.length()), FileNameUtils.getFileSize((long) (filePath.length() / (diff / 1000.0))));
                    String fileHash = IOUtils.fileSha256HexToString(filePath);
                    if (StringUtils.isEquals(hash, fileHash)) {
                        return;
                    } else {
                        LOGGER.error("Incorrect hash check, found hash {}, expected {}", fileHash, hash);
                    }
                } else {
                    throw new IllegalStateException("Didn't get any file download URL from urlGenerator");
                }
                Thread.sleep(ApplicationConstant.PAUSE_BEFORE_NEXT_ATTEMPT);
            } catch (Throwable t) {
                error = t;
                if (client.isClosed()) {
                    LOGGER.warn("Download failed because client is closed (probably due to user cancel), will stop new attempt");
                    break;
                }
            }
            LOGGER.warn("Download/hash checking failed for file {} (last supplied URL {}) / {} attempt left", filePath, url, attemptCount - i - 1, error);
        }
        throw new ApiException("Download failed after " + attemptCount + " attempt");
    }

    public void extractZip(File filePath) throws ApiException, IOException {
        File destPathDirectory = getFileToUnzipDestination(filePath);
        destPathDirectory.mkdirs();
        IOUtils.unzipInto(filePath, destPathDirectory, null);
        boolean deleted = filePath.delete();
        LOGGER.info("File to unzip {} unzipped into {}, then deleted : {}", filePath.getPath(), destPathDirectory, deleted);
    }

    private File getFileToUnzipDestination(File filePath) {
        return new File(filePath.getParentFile().getPath() + File.separator + FileNameUtils.getNameWithoutExtension(filePath));
    }

    public boolean downloadAndInstallFile(AppServerService appServerService, UpdateFileProgress file, File softwareDataDir, File softwareResourcesDir, File userDataDir) throws ApiException, IOException {
        File destPath = null;
        if (file.getTargetType() == TargetType.SOFTWARE_DATA) {
            destPath = new File(softwareDataDir.getPath() + File.separator + file.getTargetPath());
        } else if (file.getTargetType() == TargetType.SOFTWARE_RESOURCES) {
            destPath = new File(softwareResourcesDir.getPath() + File.separator + file.getTargetPath());
        } else if (file.getTargetType() == TargetType.USER_DATA) {
            destPath = new File(userDataDir.getPath() + File.separator + file.getTargetPath());
        }
        LOGGER.debug("Will download update file {} to {}", file, destPath);
        // Check that the file to unzip wasn't already downloaded
        if (file.isToUnzip()) {
            File fileToUnzipDestination = getFileToUnzipDestination(destPath);
            if (fileToUnzipDestination.exists() && fileToUnzipDestination.listFiles() != null && fileToUnzipDestination.listFiles().length > 0) {
                LOGGER.info("File {} wasn't downloaded and unzipped because it was already existing and unzipped", destPath);
                return true;
            }
        }
        // Check that the file to download doesn't already exist and its hash is correct
        if (destPath.exists() && StringUtils.isEquals(IOUtils.fileSha256HexToString(destPath), file.getFileHash())) {
            LOGGER.info("File {} wasn't downloaded because it was already in destination path and its hash was correct", destPath);
            return true;
        }

        // None of the check worked, we should download the file again
        appServerService.downloadFileAndCheckIt(
                () -> appServerService.getApplicationFileDownloadUrl(file.getFileId()),
                destPath,
                file.getFileHash(),
                ApplicationConstant.DOWNLOAD_ATTEMPT_COUNT_BEFORE_FAIL);
        if (file.isToUnzip()) {
            appServerService.extractZip(destPath);
        }
        return false;
    }

    public static ExecutorService createDefaultExecutorService() {
        return Executors.newFixedThreadPool(ApplicationConstant.MAX_PARALLEL_DOWNLOAD, LCNamedThreadFactory.daemonThreadFactory("AppServerService"));
    }
    //========================================================================


}
