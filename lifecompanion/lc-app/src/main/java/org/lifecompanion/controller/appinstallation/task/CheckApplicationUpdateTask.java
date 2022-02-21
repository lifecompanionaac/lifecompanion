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
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.service.AppServerService;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.app.VersionUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.client.UpdateFileProgress;
import org.lifecompanion.framework.model.client.UpdateFileProgressType;
import org.lifecompanion.framework.model.client.UpdateProgress;
import org.lifecompanion.framework.model.client.UpdateProgressType;
import org.lifecompanion.framework.model.server.update.ApplicationUpdate;
import org.lifecompanion.framework.model.server.update.TargetType;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.lifecompanion.framework.commons.ApplicationConstant.*;

public class CheckApplicationUpdateTask extends AbstractUpdateTask<UpdateProgress> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckApplicationUpdateTask.class);

    public CheckApplicationUpdateTask(AppServerClient client, String applicationId, boolean enablePreviewUpdates, boolean pauseOnStart) {
        super(client, applicationId, enablePreviewUpdates, pauseOnStart);
        updateMessage(Translation.getText("update.task.check.application.update.start"));
    }

    @Override
    protected UpdateProgress call() throws Exception {
        final File updateDirectory = new File(DIR_NAME_APPLICATION_UPDATE);
        final File stateFile = new File(updateDirectory.getPath() + File.separator + UPDATE_STATE_FILENAME);

        // Check last update check date : check updates every X days (skip this check if an update is downloading)
        Date lastUpdateCheckDate = InstallationController.INSTANCE.readLastUpdateCheckDate();
        if (lastUpdateCheckDate != null && !stateFile.exists() && System.currentTimeMillis() - lastUpdateCheckDate.getTime() < LCConstant.UPDATE_CHECK_DELAY) {
            LOGGER.info("Last update check was on {}, will not check again for update", lastUpdateCheckDate);
            return null;
        }

        // Doesn't directly check for update
        ThreadUtils.safeSleep(TASK_START_LONG_DELAY);

        updateMessage(Translation.getText("update.task.check.application.update.request.server"));
        try {
            final File applicationDirectory = new File(DIR_NAME_APPLICATION);

            // Check on the server if an update exist
            AppServerService appServerService = new AppServerService(client);

            LOGGER.info("Will check update for {} version {} on system {}", applicationId, InstallationController.INSTANCE.getBuildProperties().getVersionLabel(), SystemType.current());
            ApplicationUpdate lastUpdate = appServerService.getLastApplicationUpdate(applicationId, enablePreviewUpdates);

            if (lastUpdate != null) {
                InstallationController.INSTANCE.tryToSendUpdateStats();

                LOGGER.info("Got last application update information from server : {}", lastUpdate);

                // Save last successful check
                InstallationController.INSTANCE.writeLastUpdateCheckDate(new Date());

                if (VersionUtils.compare(InstallationController.INSTANCE.getBuildProperties().getVersionLabel(), lastUpdate.getVersion()) < 0) {
                    updateMessage(Translation.getText("update.task.check.application.update.prepare.download"));

                    // Check if there is an existing update downloading > version should be the same that on server
                    if (stateFile.exists()) {
                        UpdateProgress updateProgressFromFile = readJson(stateFile, UpdateProgress.class);
                        if (updateProgressFromFile != null && StringUtils.isEquals(InstallationController.INSTANCE.getBuildProperties().getVersionLabel(), updateProgressFromFile.getFrom()) && StringUtils.isEquals(lastUpdate.getVersion(), updateProgressFromFile.getTo())) {
                            LOGGER.info("Found an existing update in progress, from {} to {} / status : {}", updateProgressFromFile.getFrom(), updateProgressFromFile.getTo(), updateProgressFromFile.getStatus());
                            return updateProgressFromFile;
                        } else {
                            LOGGER.info("Existing update versions are different from last update from server (or update file is corrupted), will ask for a fresh update");
                        }
                    }

                    // Download fresh update
                    UpdateFileProgress[] updateFiles = appServerService.getUpdateDiff(applicationId, SystemType.current(), InstallationController.INSTANCE.getBuildProperties().getVersionLabel(), enablePreviewUpdates);
                    if (updateFiles.length > 0) {
                        List<UpdateFileProgress> updateFilesList = new ArrayList<>(Arrays.asList(updateFiles));
                        LOGGER.info("Got fresh update progress from server, contains {} files", updateFilesList.size());

                        // Create progress (add existing software_data file to copy)
                        Set<String> pathFileToRemove = Arrays.stream(updateFiles).filter(f -> f.getStatus() != UpdateFileProgressType.TO_COPY).map(UpdateFileProgress::getTargetPath).collect(Collectors.toSet());
                        addFileFrom(applicationDirectory, applicationDirectory, pathFileToRemove, updateFilesList);

                        // Save update progress state and return
                        UpdateProgress updateProgress = new UpdateProgress(InstallationController.INSTANCE.getBuildProperties().getVersionLabel(), lastUpdate.getVersion(), UpdateProgressType.UPDATING, updateFilesList);
                        IOUtils.createParentDirectoryIfNeeded(stateFile);
                        saveJson(updateProgress, stateFile);
                        return updateProgress;
                    } else {
                        LOGGER.info("Got an update from server, but the file list was empty, update is ignored");
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("Can't check application update for version {}", InstallationController.INSTANCE.getBuildProperties().getVersionLabel(), t);
        }
        return null;
    }

    private static void addFileFrom(File root, File current, Set<String> pathFileToRemove, List<UpdateFileProgress> updateFilesList) {
        if (current.exists()) {
            if (current.isDirectory()) {
                File[] files = current.listFiles();
                if (files != null) {
                    for (File file : files) {
                        addFileFrom(root, file, pathFileToRemove, updateFilesList);
                    }
                }
            } else {
                String relativePath = IOUtils.getRelativePath(current.getPath(), root.getPath());
                if (!pathFileToRemove.contains(relativePath)) {
                    String fileHash = null;
                    try {
                        fileHash = IOUtils.fileSha256HexToString(current);
                    } catch (IOException e) {
                        LOGGER.error("Couldn't hash file to copy {}, ignore hash for this file", relativePath, e);
                    }
                    updateFilesList.add(new UpdateFileProgress(null, relativePath, UpdateFileProgressType.TO_COPY, TargetType.SOFTWARE_DATA, fileHash, current.length(), false));
                }
            }
        }
    }
}
