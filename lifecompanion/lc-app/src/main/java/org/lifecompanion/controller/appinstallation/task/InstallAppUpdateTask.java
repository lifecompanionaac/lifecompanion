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

import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.configuration.InstallationConfiguration;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.client.UpdateFileProgress;
import org.lifecompanion.framework.model.client.UpdateFileProgressType;
import org.lifecompanion.framework.model.client.UpdateProgress;
import org.lifecompanion.framework.model.server.update.TargetType;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.lifecompanion.framework.commons.ApplicationConstant.*;

/**
 * Note : this task should be launch while blocking the application > files shouldn't be modified while executing this task as it could cause data loss.
 */
public class InstallAppUpdateTask extends AbstractUpdateTask<Boolean> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstallAppUpdateTask.class);

    private final InstallationConfiguration installationConfiguration;

    public InstallAppUpdateTask(AppServerClient client, String applicationId, boolean enablePreviewUpdates, InstallationConfiguration installationConfiguration) {
        super(client, applicationId, enablePreviewUpdates, false);
        this.installationConfiguration = installationConfiguration;
    }

    @Override
    protected Boolean call() throws Exception {
        final File updateDirectory = new File(DIR_NAME_APPLICATION_UPDATE);
        final File applicationDirectory = new File(DIR_NAME_APPLICATION);
        final File softwareDataDir = new File(DIR_NAME_APPLICATION_DATA);

        LOGGER.info("Update finished, will copy update file to application directory");
        File stateFileInUpdate = new File(updateDirectory.getPath() + File.separator + UPDATE_STATE_FILENAME);
        File updateFlagInUpdatePath = new File(updateDirectory.getPath() + File.separator + UPDATE_DOWNLOAD_FINISHED_FLAG_FILE);
        UpdateProgress updateProgress = readJson(stateFileInUpdate, UpdateProgress.class);
        // Update state could be corrupted, if it is the case, the application should run without update flag
        // This will allow on next update check to have a clean state.json (and every already downloaded files will not be downloaded again thanks to hashing !)
        if (updateProgress == null) {
            stateFileInUpdate.delete();
            updateFlagInUpdatePath.delete();
            LOGGER.warn("Couldn't read update file {}, cancel update installation, will rerun without updating", stateFileInUpdate);
            return false;
        }
        // Remove every files that should be removed (only in software data or not changed user data)
        for (UpdateFileProgress updateFile : updateProgress.getFiles()) {
            if (updateFile.getStatus() == UpdateFileProgressType.TO_REMOVE) {
                if (updateFile.getTargetType() == TargetType.SOFTWARE_DATA) {
                    File fileToRemove = new File(applicationDirectory.getPath() + File.separator + updateFile.getTargetPath());
                    boolean deleted = fileToRemove.delete();
                    LOGGER.info("Deleted software data file {} as a file to remove in current update : {}", fileToRemove.getPath(), deleted);
                } else if (updateFile.getTargetType() == TargetType.SOFTWARE_RESOURCES) {
                    File fileToRemove = new File(softwareDataDir.getPath() + File.separator + updateFile.getTargetPath());
                    if (fileToRemove.exists() && StringUtils.isEquals(IOUtils.fileSha256HexToString(fileToRemove), updateFile.getFileHash())) {
                        boolean deleted = fileToRemove.delete();
                        LOGGER.info("Deleted software resource file {} as a file to remove in current update and because it wasn't changed by user: {}", fileToRemove.getPath(), deleted);
                    } else {
                        LOGGER.info("Skipped delete of {} because file is deleted or doesn't match previous update file", fileToRemove.getPath());
                    }
                }
            }
        }
        // Note : we don't use update file list as the user could add its own file to software directories and we don't want these files to be deleted
        Set<String> dirNamesToIgnore = new HashSet<>(Arrays.asList(DIR_NAME_SOFTWARE_RESOURCES_UPDATED, DIR_NAME_USER_DATA_UPDATED, DIR_NAME_LAUNCHER_UPDATED));
        copyFileFrom(updateDirectory, updateDirectory, applicationDirectory, dirNamesToIgnore);

        File softwareResourcesDir = new File(updateDirectory.getPath() + File.separator + DIR_NAME_SOFTWARE_RESOURCES_UPDATED);
        copyFileFrom(softwareResourcesDir, softwareResourcesDir, softwareDataDir, Collections.emptySet());

        File userDataDir = new File(updateDirectory.getPath() + File.separator + DIR_NAME_USER_DATA_UPDATED);
        copyFileFrom(userDataDir, userDataDir, installationConfiguration.getUserDataDirectory(), Collections.emptySet());

        // TODO : check hash after copy
        boolean stateDeletedInUpdate = stateFileInUpdate.delete();
        boolean updateFlagDeleteInUpdate = updateFlagInUpdatePath.delete();
        boolean stateDeletedInApplication = new File(applicationDirectory.getPath() + File.separator + UPDATE_STATE_FILENAME).delete();
        boolean updateFlagDeleteInApplication = new File(applicationDirectory.getPath() + File.separator + UPDATE_DOWNLOAD_FINISHED_FLAG_FILE).delete();
        LOGGER.info("State file {} deleted in update : {} in application {}", stateFileInUpdate.getName(), stateDeletedInUpdate, stateDeletedInApplication);
        LOGGER.info("Flag file {} deleted in update : {} in application {}", UPDATE_DOWNLOAD_FINISHED_FLAG_FILE, updateFlagDeleteInUpdate, updateFlagDeleteInApplication);

        // Create update info cache with the installation date in it (use the version as file name, so we don't have it twice)
        File updateDoneFile = new File(LCConstant.PATH_UPDATE_STAT_CACHE + File.separator + updateProgress.getTo());
        IOUtils.createParentDirectoryIfNeeded(updateDoneFile);
        IOUtils.writeToFile(updateDoneFile, String.valueOf(new Date().getTime()));
        return true;
    }

    private static void copyFileFrom(File root, File src, File destDir, Set<String> dirNamesToIgnore) throws IOException {
        if (src.exists()) {
            if (src.isDirectory()) {
                if (!dirNamesToIgnore.contains(src.getName())) {
                    File[] files = src.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            copyFileFrom(root, file, destDir, dirNamesToIgnore);
                        }
                    }
                }
            } else {
                String relativePath = IOUtils.getRelativePath(src.getPath(), root.getPath());
                File destFile = new File(destDir.getPath() + File.separator + relativePath);
                IOUtils.createParentDirectoryIfNeeded(destFile);
                IOUtils.copyFiles(src, destFile);
            }
        }
    }
}
