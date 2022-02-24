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

import gnu.trove.impl.sync.TSynchronizedShortObjectMap;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.service.AppServerService;
import org.lifecompanion.framework.commons.ApplicationConstant;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.app.VersionUtils;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.client.UpdateFileProgress;
import org.lifecompanion.framework.model.client.UpdateFileProgressType;
import org.lifecompanion.framework.model.client.UpdateProgress;
import org.lifecompanion.framework.model.client.UpdateProgressType;
import org.lifecompanion.framework.model.server.update.TargetType;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.plugin.PluginInfo;
import org.lifecompanion.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DownloadUpdateTask extends AbstractUpdateTask<Boolean> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadUpdateTask.class);
    private final UpdateProgress updateProgress;

    public DownloadUpdateTask(AppServerClient client, String applicationId, boolean enablePreviewUpdates, boolean pauseOnStart, UpdateProgress updateProgress) {
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
        final File updateDownloadFinishedFlagFile = new File(updateDirectory.getPath() + File.separator + ApplicationConstant.UPDATE_DOWNLOAD_FINISHED_FLAG_FILE);

        if (updateProgress.getStatus() == UpdateProgressType.UPDATING || !updateDownloadFinishedFlagFile.exists()) {
            int progress = 0;
            for (UpdateFileProgress updateFile : updateProgress.getFiles()) {
                updateMessage(Translation.getText("update.task.download.app.file", updateFile.getTargetPath(), FileNameUtils.getFileSize(updateFile.getFileSize())));
                if (isCancelled()) {
                    return false;
                }
                if (updateFile.getStatus() == UpdateFileProgressType.PROCESSED) {
                    LOGGER.info("Previous file {} was already downloaded/copied, skip it", updateFile.getTargetPath());
                } else if (updateFile.getStatus() == UpdateFileProgressType.TO_DOWNLOAD) {
                    appServerService.downloadAndInstallFileV2(
                            appServerService,
                            updateFile,
                            updateDirectory,
                            new File(updateDirectory.getPath() + File.separator + ApplicationConstant.DIR_NAME_LAUNCHER_UPDATED),
                            new File(updateDirectory.getPath() + File.separator + ApplicationConstant.DIR_NAME_SOFTWARE_RESOURCES_UPDATED),
                            new File(updateDirectory.getPath() + File.separator + ApplicationConstant.DIR_NAME_USER_DATA_UPDATED)
                    );
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

            preparePluginUpdate();

            LOGGER.info("Update processed, file are downloaded/copied, will now check to install launcher update");
            // If the launcher was updated, copy it before set update finished
            final Optional<UpdateFileProgress> launcherFileProgressFound = updateProgress.getFiles() != null ? updateProgress.getFiles().stream().filter(updateFileProgress -> updateFileProgress.getTargetType() == TargetType.LAUNCHER).findAny() : Optional.empty();
            if (launcherFileProgressFound.isPresent()) {
                updateMessage(Translation.getText("update.task.check.launcher.install2"));
                final UpdateFileProgress launcherFileProgress = launcherFileProgressFound.get();
                // Replace launcher file, if doesn't fail, update is ready
                final String updatedLauncherPath = updateDirectory.getPath() + File.separator + ApplicationConstant.DIR_NAME_LAUNCHER_UPDATED + File.separator + launcherFileProgress.getTargetPath();
                File launcherUpdatedFile = new File(updatedLauncherPath);
                File launcherFile = new File(launcherFileProgress.getTargetPath());
                File launcherBackupFile = new File(updatedLauncherPath + "-backup");
                IOUtils.copyFiles(launcherFile, launcherBackupFile);
                try {
                    IOUtils.copyFiles(launcherUpdatedFile, launcherFile);
                    if (StringUtils.isDifferent(IOUtils.fileSha256HexToString(launcherFile), launcherFileProgress.getFileHash())) {
                        throw new IOException("Copied launcher file hash is incorrect");
                    }
                    setLauncherExecutable(launcherFile);
                    setUpdateFinished(updateDownloadFinishedFlagFile, stateFile);
                } catch (Exception e) {
                    LOGGER.error("Launcher update failed (wanted to update {} with {}) will copy previous launcher back", launcherFile, launcherUpdatedFile, e);
                    IOUtils.copyFiles(launcherBackupFile, launcherFile);
                    setLauncherExecutable(launcherFile);
                    return false;
                }
            } else {
                setUpdateFinished(updateDownloadFinishedFlagFile, stateFile);
            }
            return true;
        } else {
            LOGGER.info("Current update download is already finished : from {} to {}", updateProgress.getFrom(), updateProgress.getTo());
            return true;
        }
    }

    public void preparePluginUpdate() throws Exception {
        LOGGER.info("Update processed, will now check for plugin updates for next app version ({})", updateProgress.getTo());
        // This will just download last plugin updates (installation of newer plugin update is then handled in InstallAppUpdateTask)
        DownloadAllPluginUpdateTask downloadAllPlugin = InstallationController.INSTANCE.createDownloadAllPlugin(false, updateProgress.getTo());

        // Create the most "up to date" plugin list merging updated plugins and existing ones
        Map<String, List<Pair<PluginInfo, File>>> plugins = new HashMap<>();

        List<File> pluginFilesToInstallOnNextUpdate = ThreadUtils.executeInCurrentThread(downloadAllPlugin);
        for (File pluginFile : pluginFilesToInstallOnNextUpdate) {
            try {
                PluginInfo pluginInfo = PluginInfo.createFromJarManifest(pluginFile);
                plugins.computeIfAbsent(pluginInfo.getPluginId(), id -> new ArrayList<>()).add(Pair.of(pluginInfo, pluginFile));
            } catch (Exception e) {
                LOGGER.error("Couldn't read plugin file info", e);
            }
        }

        List<PluginInfo> pluginInfoList = new ArrayList<>(PluginController.INSTANCE.getPluginInfoList());
        pluginInfoList.forEach(p -> plugins.computeIfAbsent(p.getPluginId(), id -> new ArrayList<>()).add(Pair.of(p, new File(LCConstant.PATH_PLUGIN_JAR_DIR + p.getFileName()))));

        // Sort it to get the most up-to-date plugin for next version
        plugins.forEach((pluginId, infos) -> {
            Pair<PluginInfo, File> info = infos.stream().min((p1, p2) -> VersionUtils.compare(p2.getLeft().getPluginVersion(), p1.getLeft().getPluginVersion())).orElse(null);
            System.err.println(info.getLeft() + " - " + info.getRight());
        });
    }

    private void setUpdateFinished(File updateDownloadFinishedFlagFile, File stateFile) throws IOException {
        updateProgress.setStatus(UpdateProgressType.DONE);
        saveJson(updateProgress, stateFile);
        final boolean created = updateDownloadFinishedFlagFile.createNewFile();
        LOGGER.info("Update done flag file created : {}", created);
    }
}
