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

package org.lifecompanion.controller.appinstallation.task;

import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.service.AppServerService;
import org.lifecompanion.framework.commons.ApplicationConstant;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.client.UpdateFileProgress;
import org.lifecompanion.framework.model.client.UpdateFileProgressType;
import org.lifecompanion.framework.model.client.UpdateProgress;
import org.lifecompanion.framework.model.client.UpdateProgressType;
import org.lifecompanion.framework.model.server.update.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DownloadUpdateAndLauncherTask extends AbstractUpdateTask<Boolean> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadUpdateAndLauncherTask.class);
    private final UpdateProgress updateProgress;

    public DownloadUpdateAndLauncherTask(AppServerClient client, String applicationId, boolean enablePreviewUpdates, boolean pauseOnStart, UpdateProgress updateProgress) {
        super(client, applicationId, enablePreviewUpdates, pauseOnStart);
        this.updateProgress = updateProgress;
        updateMessage(Translation.getText("update.task.check.application.update.prepare.download"));
    }

    @Override
    protected Boolean call() throws Exception {
        final AppServerService appServerService = new AppServerService(client);
        final File updateDirectory = new File("." + File.separator + ApplicationConstant.DIR_NAME_APPLICATION_UPDATE);
        final File applicationDirectory = new File("." + File.separator + ApplicationConstant.DIR_NAME_APPLICATION);
        final File stateFile = new File(updateDirectory.getPath() + File.separator + ApplicationConstant.UPDATE_STATE_FILENAME);

        if (updateProgress.getStatus() == UpdateProgressType.UPDATING) {
            int progress = 0;
            for (UpdateFileProgress updateFile : updateProgress.getFiles()) {
                updateMessage(Translation.getText("update.task.download.app.file", updateFile.getTargetPath(), FileNameUtils.getFileSize(updateFile.getFileSize())));
                if (isCancelled()) {
                    return false;
                }
                if (updateFile.getStatus() == UpdateFileProgressType.PROCESSED) {
                    LOGGER.info("Previous file {} was already downloaded/copied, skip it", updateFile.getTargetPath());
                } else if (updateFile.getStatus() == UpdateFileProgressType.TO_DOWNLOAD) {
                    appServerService.downloadAndInstallFile(appServerService, updateFile,
                            updateDirectory,
                            new File(updateDirectory.getPath() + File.separator + ApplicationConstant.DIR_NAME_SOFTWARE_RESOURCES_UPDATED),
                            new File(updateDirectory.getPath() + File.separator + ApplicationConstant.DIR_NAME_USER_DATA_UPDATED));
                    updateFile.setStatus(UpdateFileProgressType.PROCESSED);
                } else if (updateFile.getStatus() == UpdateFileProgressType.TO_COPY) {
                    if (updateFile.getTargetType() == TargetType.SOFTWARE_DATA) {
                        File srcFile = new File(applicationDirectory.getPath() + File.separator + updateFile.getTargetPath());
                        File targetFile = new File(updateDirectory.getPath() + File.separator + updateFile.getTargetPath());
                        LOGGER.info("Will copy update file from {} to {}", srcFile, targetFile);
                        boolean successfulFileCopy = false;
                        if (targetFile.exists() && StringUtils.isEquals(IOUtils.fileSha256HexToString(targetFile), updateFile.getFileHash())) {
                            LOGGER.info("File {} wasn't copied because it was already in destination path and its hash was correct", targetFile);
                            successfulFileCopy = true;
                        } else {
                            for (int i = 0; i < ApplicationConstant.DOWNLOAD_ATTEMPT_COUNT_BEFORE_FAIL && !successfulFileCopy; i++) {
                                IOUtils.copyFiles(srcFile, targetFile);
                                String copiedHash = IOUtils.fileSha256HexToString(targetFile);
                                if (updateFile.getFileHash() == null || StringUtils.isEquals(updateFile.getFileHash(), copiedHash)) {
                                    successfulFileCopy = true;
                                } else {
                                    LOGGER.warn("Copied file hash didn't match ({}, expected {}), will attempt another copy", copiedHash, updateFile.getFileHash());
                                }
                            }
                        }
                        if (successfulFileCopy) {
                            updateFile.setStatus(UpdateFileProgressType.PROCESSED);
                        } else {
                            LOGGER.warn("Copy failed for the file {}, will attempt on next application download launch", updateFile.getTargetPath());
                        }
                    }
                }
                saveJson(updateProgress, stateFile);
                updateProgress(++progress, updateProgress.getFiles().size());
            }

            LOGGER.info("Update processed, will now check for plugin update");
            final CheckAndDownloadPluginUpdateTask checkAndDowloadPluginTask = InstallationController.INSTANCE.createCheckAndDowloadPluginTask(false);
            InstallationController.INSTANCE.tryToAddPluginsAfterDownload(LCUtils.executeInCurrentThread(checkAndDowloadPluginTask));

            LOGGER.info("Update processed, file are downloaded/copied, will now check for launcher update");
            boolean launcherUpdateIsOk = downloadAndInstallLauncherUpdate(appServerService);
            if (launcherUpdateIsOk) {
                updateProgress.setStatus(UpdateProgressType.DONE);
                saveJson(updateProgress, stateFile);
                new File(updateDirectory.getPath() + File.separator + ApplicationConstant.UPDATE_DOWNLOAD_FINISHED_FLAG_FILE).createNewFile();
            }
            return true;
        } else {
            LOGGER.info("Current update download is already finished : from {} to {}", updateProgress.getFrom(), updateProgress.getTo());
            return true;
        }
    }


}
