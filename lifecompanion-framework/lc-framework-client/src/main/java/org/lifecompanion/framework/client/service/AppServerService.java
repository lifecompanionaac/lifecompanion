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
import org.lifecompanion.framework.model.server.update.ApplicationPluginUpdate;
import org.lifecompanion.framework.model.server.update.ApplicationUpdate;
import org.lifecompanion.framework.model.server.update.TargetType;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

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
        return this.client.get("public/v2/get-last-update-diff/" + applicationId + "/" + system + "/" + fromVersion + "/" + preview, UpdateFileProgress[].class);
    }

    public UpdateFileProgress[] getUpdateDiffWithMax(String applicationId, SystemType system, String fromVersion, String maxVersion, boolean preview) throws ApiException {
        return this.client.get("public/get-update-diff/" + applicationId + "/" + system + "/" + fromVersion + "/" + maxVersion + "/" + preview, UpdateFileProgress[].class);
    }

    public ApplicationUpdate getLastApplicationUpdate(String applicationId, boolean preview) throws ApiException {
        return this.client.get("public/v2/get-last-application-update/" + applicationId + "/" + preview, ApplicationUpdate.class);
    }

    public String getApplicationFileDownloadUrl(String fileId) throws ApiException {
        return this.client.get("public/get-application-file-url/" + fileId, String.class);
    }
    //========================================================================

    // PLUGIN UPDATE
    //========================================================================
    public ApplicationPluginUpdate[] getPluginUpdatesOrderByVersion(String pluginId, boolean preview) throws ApiException {
        return this.client.get("public/get-plugin-updates-order-by-version/" + pluginId + "/" + preview, ApplicationPluginUpdate[].class);
    }

    public ApplicationPluginUpdate[] getPluginUpdates(String pluginId, boolean preview) throws ApiException {
        return this.client.get("public/get-plugin-updates/" + pluginId + "/" + preview, ApplicationPluginUpdate[].class);
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
        downloadFileAndCheckIt(urlGenerator, filePath, hash, attemptCount, null);
    }

    public void downloadFileAndCheckIt(UrlSupplier urlGenerator, File filePath, String hash, int attemptCount, LongConsumer consumer) throws ApiException {
        String url = null;
        for (int i = 0; i < attemptCount; i++) {
            Throwable error = null;
            try {
                long start = System.currentTimeMillis();
                url = urlGenerator.getUrl();
                if (url != null) {
                    this.client.download(url, filePath, consumer);
                    long diff = System.currentTimeMillis() - start;
                    LOGGER.info("{} - finished in {} ms for {} / speed = {}/s",
                            filePath.getName(),
                            diff,
                            FileNameUtils.getFileSize(filePath.length()),
                            FileNameUtils.getFileSize((long) (filePath.length() / (diff / 1000.0))));
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


    public static void extractZip(File filePath) throws ApiException, IOException {
        extractZip(filePath, null);
    }

    public static void extractZip(File filePath, LongConsumer counter) throws ApiException, IOException {
        File destPathDirectory = getFileToUnzipDestination(filePath);
        destPathDirectory.mkdirs();
        IOUtils.unzipIntoCounting(filePath, destPathDirectory, null, counter);
        boolean deleted = filePath.delete();
        LOGGER.info("File to unzip {} unzipped into {}, then deleted : {}", filePath.getPath(), destPathDirectory, deleted);
    }

    private static File getFileToUnzipDestination(File filePath) {
        return new File(filePath.getParentFile().getPath() + File.separator + FileNameUtils.getNameWithoutExtension(filePath));
    }

    public boolean downloadAndInstallFileV2(AppServerService appServerService,
                                            UpdateFileProgress file,
                                            File softwareDataDir,
                                            File launcherDir,
                                            File softwareResourcesDir,
                                            File userDataDir) throws IOException, ApiException {
        return downloadAndInstallFileV2(appServerService, file, softwareDataDir, launcherDir, softwareResourcesDir, userDataDir, null);
    }

    public boolean downloadAndInstallFileV2(AppServerService appServerService,
                                            UpdateFileProgress file,
                                            File softwareDataDir,
                                            File launcherDir,
                                            File softwareResourcesDir,
                                            File userDataDir,
                                            LongConsumer counter) throws ApiException, IOException {
        File destPath = getDestPathForFile(file.getTargetType(), file.getTargetPath(), softwareDataDir, launcherDir, softwareResourcesDir, userDataDir);
        LOGGER.debug("Will download update file {} to {}", file, destPath);
        // Check that the file to unzip wasn't already downloaded and extracted
        if (file.isToUnzip()) {
            File fileToUnzipDestination = getFileToUnzipDestination(destPath);
            if (fileToUnzipDestination.exists() && fileToUnzipDestination.listFiles() != null && fileToUnzipDestination.listFiles().length > 0) {
                LOGGER.info("File {} wasn't downloaded and unzipped because it was already existing and unzipped", destPath);
                counter.accept(file.getFileSize() * 2);
                return true;
            }
        }
        // Check that the file to download doesn't already exist and its hash is correct
        if (destPath.exists() && StringUtils.isEquals(IOUtils.fileSha256HexToString(destPath), file.getFileHash())) {
            LOGGER.info("File {} wasn't downloaded because it was already in destination path and its hash was correct", destPath);
            counter.accept(file.getFileSize());
            return true;
        }

        // None of the check worked, we should download the file again
        appServerService.downloadFileAndCheckIt(
                () -> appServerService.getApplicationFileDownloadUrl(file.getFileId()),
                destPath,
                file.getFileHash(),
                ApplicationConstant.DOWNLOAD_ATTEMPT_COUNT_BEFORE_FAIL,
                counter);
        if (file.isToUnzip()) {
            appServerService.extractZip(destPath, counter);
        }
        return false;
    }

    public static File getDestPathForFile(TargetType targetType, String targetPath, File softwareDataDir, File launcherDir, File softwareResourcesDir, File userDataDir) {
        File destPath = null;
        if (targetType == TargetType.SOFTWARE_DATA) {
            destPath = new File(softwareDataDir.getPath() + File.separator + targetPath);
        } else if (targetType == TargetType.SOFTWARE_RESOURCES) {
            destPath = new File(softwareResourcesDir.getPath() + File.separator + targetPath);
        } else if (targetType == TargetType.USER_DATA) {
            destPath = new File(userDataDir.getPath() + File.separator + targetPath);
        } else if (targetType == TargetType.LAUNCHER) {
            destPath = new File(launcherDir.getPath() + File.separator + targetPath);
        }
        return destPath;
    }

    public static ExecutorService createDefaultExecutorService() {
        return Executors.newFixedThreadPool(ApplicationConstant.MAX_PARALLEL_DOWNLOAD, LCNamedThreadFactory.daemonThreadFactory("AppServerService"));
    }
    //========================================================================


}
