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

package org.lifecompanion.base.data.control.update;

import org.lifecompanion.base.data.common.LCTask;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.props.LauncherProperties;
import org.lifecompanion.framework.client.service.AppServerService;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.app.VersionUtils;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.model.server.update.ApplicationLauncherUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.lifecompanion.framework.commons.ApplicationConstant.DIR_NAME_APPLICATION_UPDATE;
import static org.lifecompanion.framework.commons.ApplicationConstant.DOWNLOAD_ATTEMPT_COUNT_BEFORE_FAIL;

public abstract class AbstractUpdateTask<V> extends LCTask<V> {
    protected final long TASK_START_LONG_DELAY;
    protected final long TASK_START_SHORT_DELAY;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUpdateTask.class);

    protected final AppServerClient client;
    protected final String applicationId;
    protected final boolean enablePreviewUpdates;

    protected AbstractUpdateTask(AppServerClient client, String applicationId, boolean enablePreviewUpdates, boolean pauseOnStart) {
        super("update.generic.task.title");
        this.client = client;
        this.applicationId = applicationId;
        this.enablePreviewUpdates = enablePreviewUpdates;
        this.TASK_START_LONG_DELAY = pauseOnStart ? 30_000 : 0;
        this.TASK_START_SHORT_DELAY = pauseOnStart ? 5_000 : 0;
        updateProgress(-1, -1);
    }

    protected void saveJson(Object obj, File destFile) {
        try (PrintStream fos = new PrintStream(destFile, StandardCharsets.UTF_8)) {
            client.gson().toJson(obj, fos);
        } catch (Exception e) {
            LOGGER.error("Couldn't save JSON to {}", destFile, e);
        }
    }

    protected <T> T readJson(File srcFile, Class<T> typeClass) {
        try (InputStreamReader is = new InputStreamReader(new FileInputStream(srcFile), StandardCharsets.UTF_8)) {
            return client.gson().fromJson(is, typeClass);
        } catch (Exception e) {
            LOGGER.error("Couldn't read JSON from {}", srcFile, e);
            return null;
        }
    }

    protected boolean downloadAndInstallLauncherUpdate(final AppServerService appServerService) {
        updateMessage(Translation.getText("update.task.check.launcher.update.request.server"));

        LauncherProperties launcherProperties = InstallationController.INSTANCE.getLauncherProperties();
        String versionLabel = launcherProperties.getVersionLabel();
        LOGGER.info("Launcher version read : {}, will check update", versionLabel);

        // Check for update
        try {
            ApplicationLauncherUpdate lastLauncher = appServerService.getLastLauncherInformation(applicationId, SystemType.current(), enablePreviewUpdates);
            if (lastLauncher != null) {
                LOGGER.info("Got last launcher information from server : {}", lastLauncher);
                if (VersionUtils.compare(versionLabel, lastLauncher.getVersion()) < 0) {
                    LOGGER.info("Launcher update found, will now download it");

                    updateMessage(Translation.getText("update.task.check.launcher.prepare.download"));
                    // Create temp files to avoid replacing existing launcher if update fail
                    File launcherFile = new File(lastLauncher.getFilePath());
                    File destDownloadLauncherFile = new File(DIR_NAME_APPLICATION_UPDATE + File.separator + launcherFile.getName() + "-update");
                    File backupLauncherFile = new File(DIR_NAME_APPLICATION_UPDATE + launcherFile.getName() + "-backup");

                    IOUtils.copyFiles(launcherFile, backupLauncherFile);
                    LOGGER.info("Update detect for launcher, will download file {} ({} byte)", lastLauncher.getFilePath(), lastLauncher.getFileSize());
                    try {
                        updateMessage(Translation.getText("update.task.check.launcher.download", lastLauncher.getVersion(), FileNameUtils.getFileSize(lastLauncher.getFileSize())));
                        appServerService.downloadFileAndCheckIt(() -> appServerService.getLauncherDownloadUrl(lastLauncher.getId()), destDownloadLauncherFile, lastLauncher.getFileHash(), DOWNLOAD_ATTEMPT_COUNT_BEFORE_FAIL);
                        LOGGER.info("Launcher update downloaded, will now copy it to main directory");
                        updateMessage(Translation.getText("update.task.check.launcher.install", lastLauncher.getVersion()));
                        IOUtils.copyFiles(destDownloadLauncherFile, launcherFile);
                        // Remove backup and update temp launcher file
                        backupLauncherFile.delete();
                        destDownloadLauncherFile.delete();
                        LOGGER.info("Launcher updated to {}", lastLauncher.getVersion());
                        // On Mac and Unix, launcher should be executable
                        setLaunchExecutable(launcherFile);
                    } catch (Exception e) {
                        LOGGER.error("Problem with launcher update, backing up the previous launcher from {}", backupLauncherFile, e);
                        IOUtils.copyFiles(backupLauncherFile, launcherFile);
                        setLaunchExecutable(launcherFile);
                        return false;
                    }
                } else {
                    LOGGER.info("Launcher is already up to date");
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("Can't check launcher update for launcher {}", versionLabel, t);
            return false;
        }
        return true;
    }

    protected void setLaunchExecutable(File launcherFile) {
        if (SystemType.current() == SystemType.MAC || SystemType.current() == SystemType.UNIX) {
            launcherFile.setExecutable(true);
        }
    }

}
