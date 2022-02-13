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

package org.lifecompanion.installer.task;

import javafx.concurrent.Task;
import org.lifecompanion.framework.client.http.ApiException;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.props.ApplicationBuildProperties;
import org.lifecompanion.framework.client.service.AppServerService;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.configuration.InstallationConfiguration;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.model.client.UpdateFileProgress;
import org.lifecompanion.framework.model.client.UpdateFileProgressType;
import org.lifecompanion.framework.model.server.update.ApplicationPluginUpdate;
import org.lifecompanion.installer.controller.InstallerManager;
import org.lifecompanion.installer.controller.SystemInstallationI;
import org.lifecompanion.installer.ui.InstallerUIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static org.lifecompanion.framework.commons.ApplicationConstant.*;

public class FullInstallTask extends Task<InstallResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FullInstallTask.class);
    private final InstallerUIConfiguration installerConfiguration;
    private final Consumer<String> logAppender;
    private final AppServerClient client;
    private final SystemInstallationI systemInstallation;
    private final ApplicationBuildProperties buildProperties;

    public FullInstallTask(Consumer<String> logAppender, InstallerUIConfiguration installerConfiguration, AppServerClient client, SystemInstallationI systemInstallation, ApplicationBuildProperties buildProperties) {
        this.installerConfiguration = installerConfiguration;
        this.logAppender = logAppender;
        this.client = client;
        this.systemInstallation = systemInstallation;
        this.buildProperties = buildProperties;
        updateProgress(-1, -1);
    }

    @Override
    protected InstallResult call() {
        // Create installation directories
        updateMessage(Translation.getText("lc.installer.task.installing.progress.general.directories"));
        logAppender.accept(Translation.getText("lc.installer.task.installing.progress.detail.directory", installerConfiguration.getInstallationSoftwareDirectory()));
        installerConfiguration.getInstallationSoftwareDirectory().mkdirs();
        logAppender.accept(Translation.getText("lc.installer.task.installing.progress.detail.directory", installerConfiguration.getInstallationUserDataDirectory()));
        installerConfiguration.getInstallationUserDataDirectory().mkdirs();
        try {
            logAppender.accept(Translation.getText("lc.installer.task.installing.progress.detail.directory.rights"));
            systemInstallation.directoriesInitialized(installerConfiguration);
        } catch (Exception e) {
            LOGGER.error("Couldn't handle post dir creation hook", e);
            return InstallResult.INSTALLATION_FAILED_ADMIN_RIGHTS;
        }

        AtomicLong workProgress = new AtomicLong(0);
        long totalWork = 4;

        // Download files and launcher
        updateMessage(Translation.getText("lc.installer.task.installing.progress.general.downloading"));
        ExecutorService downloadExecutorService = AppServerService.createDefaultExecutorService();
        try {
            AppServerService appServerService = new AppServerService(client);
            UpdateFileProgress[] filesToInstall = appServerService.getUpdateDiff(buildProperties.getAppId(), SystemType.current(), "0", false);
            totalWork += filesToInstall.length;
            final long totalWorkF = totalWork;

            AtomicBoolean downloading = new AtomicBoolean(true);

            // Create all download tasks (that can be stopped if one download fail, or if this task is cancelled)
            List<Callable<Void>> downloadFileCallables = new ArrayList<>();
            for (UpdateFileProgress fileToInstall : filesToInstall) {
                // Keep only file to download (as it is a first install)
                if (fileToInstall.getStatus() == UpdateFileProgressType.TO_DOWNLOAD) {
                    downloadFileCallables.add(() -> {
                        if (FullInstallTask.this.isCancelled()) {
                            downloading.set(false);
                        }
                        if (downloading.get()) {
                            try {
                                downloadFileToInstall(appServerService, totalWorkF, workProgress, fileToInstall);
                            } catch (Exception e) {
                                LOGGER.error("Download error", e);
                                downloading.set(false);
                            }
                        }
                        return null;
                    });
                }
            }
            downloadExecutorService.invokeAll(downloadFileCallables);
            if (!downloading.get()) {
                LOGGER.error("Download was cancelled (task or error), task will return failed");
                return InstallResult.INSTALLATION_FAILED_DOWNLOAD;
            }
            downloadPluginsToInstall(appServerService, totalWork, workProgress);
        } catch (Exception e) {
            LOGGER.error("Error while downloading installation files", e);
            return InstallResult.INSTALLATION_FAILED_DOWNLOAD;
        } finally {
            downloadExecutorService.shutdownNow();
        }

        try {
            writeInstallationConfiguration(totalWork, workProgress);
        } catch (Exception e) {
            LOGGER.error("Couldn't write installation configuration", e);
            return InstallResult.INSTALLATION_FAILED_SYSTEM_SPECIFIC;
        }
        try {
            executeSystemSpecificTasks(totalWork, workProgress);
        } catch (Exception e) {
            LOGGER.error("Couldn't execute system specific task", e);
            return InstallResult.INSTALLATION_FAILED_SYSTEM_SPECIFIC;
        }
        updateMessage(Translation.getText("lc.installer.installation.result.success"));
        updateProgress(1, 1);
        return InstallResult.INSTALLATION_SUCCESS;
    }

    @Override
    protected void updateMessage(String message) {
        logAppender.accept(message);
        super.updateMessage(message);
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        if (this.getProgress() < 0) {
            this.updateProgress(0, 0);
        }
    }

    private void executeSystemSpecificTasks(long totalWork, AtomicLong workProgress) throws Exception {
        updateMessage(Translation.getText("lc.installer.task.installing.progress.general.system.specific"));
        systemInstallation.runSystemSpecificInstallationTask(installerConfiguration, logAppender);
        updateProgress(workProgress.incrementAndGet(), totalWork);
    }

    private void writeInstallationConfiguration(long totalWork, AtomicLong workProgress) throws IOException {
        updateMessage(Translation.getText("lc.installer.task.installing.progress.general.installation.configuration"));
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        String xmxConfiguration = InstallerManager.getBestXmxSize(hardware.getMemory().getTotal());
        InstallationConfiguration installConfig = new InstallationConfiguration(xmxConfiguration, installerConfiguration.getInstallationUserDataDirectory());
        installConfig.save(new File(this.installerConfiguration.getInstallationSoftwareDirectory() + File.separator + DIR_NAME_APPLICATION_DATA + File.separator + "installation.properties"));
        updateProgress(workProgress.incrementAndGet(), totalWork);
    }

    private void downloadFileToInstall(AppServerService appServerService, long totalWork, AtomicLong workProgress, UpdateFileProgress fileToInstall) throws ApiException, IOException {
        logAppender.accept(Translation.getText("lc.installer.task.installing.progress.detail.downloading", fileToInstall.getTargetPath(), FileNameUtils.getFileSize(fileToInstall.getFileSize())));
        appServerService.downloadAndInstallFileV2(
                appServerService,
                fileToInstall,
                new File(this.installerConfiguration.getInstallationSoftwareDirectory().getPath() + File.separator + DIR_NAME_APPLICATION),
                this.installerConfiguration.getInstallationSoftwareDirectory(),
                new File(this.installerConfiguration.getInstallationSoftwareDirectory().getPath() + File.separator + DIR_NAME_APPLICATION_DATA),
                this.installerConfiguration.getInstallationUserDataDirectory()
        );
        updateProgress(workProgress.incrementAndGet(), totalWork);
    }

    private void downloadPluginsToInstall(AppServerService appServerService, long totalWork, AtomicLong workProgress) throws ApiException {
        List<String> pluginIds = installerConfiguration.getPluginToInstallIds();
        File pluginRootDirectory = new File(this.installerConfiguration.getInstallationSoftwareDirectory().getPath() + File.separator + DIR_NAME_APPLICATION_DATA + File.separator + "plugins");
        File pluginJarDirectory = new File(pluginRootDirectory.getPath() + File.separator + "jars");
        List<String> validPluginsPath = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pluginIds)) {
            try {
                for (String pluginId : pluginIds) {
                    LOGGER.info("Will try to install plugin : {}", pluginId);
                    // LC version is always the latest installed, so when don't check for compatibility here
                    ApplicationPluginUpdate[] lastPluginUpdates = appServerService.getPluginUpdatesOrderByVersion(pluginId, false);
                    if (lastPluginUpdates != null && lastPluginUpdates.length > 0) {
                        ApplicationPluginUpdate lastPluginUpdate = lastPluginUpdates[0];
                        File pluginUpdateFile = new File(pluginJarDirectory.getPath() + File.separator + lastPluginUpdate.getFileName());
                        IOUtils.createParentDirectoryIfNeeded(pluginUpdateFile);
                        LOGGER.info("Found the plugin {}, will try to download version {} (file saved to {})", lastPluginUpdate.getId(), lastPluginUpdate.getVersion(), pluginUpdateFile);
                        appServerService.downloadFileAndCheckIt(() -> appServerService.getPluginUpdateDownloadUrl(lastPluginUpdate.getId()), pluginUpdateFile, lastPluginUpdate.getFileHash(), DOWNLOAD_ATTEMPT_COUNT_BEFORE_FAIL);
                        LOGGER.info("Plugin update downloaded");
                        validPluginsPath.add(DIR_NAME_APPLICATION_DATA + File.separator + "plugins" + File.separator + "jars" + File.separator + pluginUpdateFile.getName());
                        logAppender.accept(Translation.getText("lc.installer.task.installing.progress.plugin.installation.detail", pluginId, lastPluginUpdate.getVersion(), FileNameUtils.getFileSize(lastPluginUpdate.getFileSize())));
                    } else {
                        LOGGER.info("No plugin version found for {}", pluginId);
                        logAppender.accept(Translation.getText("lc.installer.task.installing.progress.plugin.installation.failed", pluginId));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Download error", e);
            }
            if (!CollectionUtils.isEmpty(validPluginsPath)) {
                File cpConfigFile = new File(pluginRootDirectory + File.separator + "plugin-classpath");
                IOUtils.createParentDirectoryIfNeeded(cpConfigFile);
                try (PrintWriter pw = new PrintWriter(cpConfigFile, StandardCharsets.UTF_8)) {
                    pw.println(String.join(File.pathSeparator, validPluginsPath));
                } catch (Exception e) {
                    LOGGER.error("Couldn't write the classpath configuration for plugins", e);
                }
            }
        }
    }
}
