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

package org.lifecompanion.installer.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.props.ApplicationBuildProperties;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.installer.ui.InstallerUIConfiguration;
import org.lifecompanion.installer.ui.model.InstallerStep;
import org.lifecompanion.installer.ui.model.step.ConfigStep;
import org.lifecompanion.installer.ui.model.step.FinishedStep;
import org.lifecompanion.installer.ui.model.step.InitialStep;
import org.lifecompanion.installer.ui.model.step.InstallationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public enum InstallerManager {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(InstallerManager.class);

    private static final SystemInstallationI DEFAULT_SYSTEM_SPECIFIC = new DefaultSystemInstallation();

    private static final Map<SystemType, SystemInstallationI> SYSTEM_SPECIFICS = FluentHashMap
            .map(SystemType.WINDOWS, (SystemInstallationI) new WindowsSystemInstallation())
            .with(SystemType.UNIX, new UnixSystemInstallation())
            .with(SystemType.MAC, new MacSystemInstallation());

    private final InstallerUIConfiguration configuration;

    private final InstallerStep[] steps;

    private final ObjectProperty<InstallerStep> currentStep;

    private final ExecutorService executorService;

    private final AppServerClient client;

    private boolean installationSuccess;

    private final ApplicationBuildProperties buildProperties;

    InstallerManager() {
        configuration = new InstallerUIConfiguration();
        steps = new InstallerStep[]{new InitialStep(), new ConfigStep(), new InstallationStep(), new FinishedStep()};
        this.currentStep = new SimpleObjectProperty<>();
        this.executorService = Executors.newSingleThreadExecutor();
        buildProperties = ApplicationBuildProperties.load(this.getClass().getResourceAsStream("/installer.properties"));
        this.client = new AppServerClient(buildProperties.getUpdateServerUrl());
    }

    public ReadOnlyObjectProperty<InstallerStep> currentStepProperty() {
        return this.currentStep;
    }

    public InstallerUIConfiguration getConfiguration() {
        return this.configuration;
    }

    public void setInstallationSuccess(boolean installationSuccess) {
        this.installationSuccess = installationSuccess;
    }

    public SystemInstallationI getSpecificOrDefault() {
        return SYSTEM_SPECIFICS.getOrDefault(SystemType.current(), DEFAULT_SYSTEM_SPECIFIC);
    }

    public void submitTask(Task<?> task) {
        this.executorService.submit(task);
    }

    public ApplicationBuildProperties getBuildProperties() {
        return buildProperties;
    }

    public void cancelRequest() {
        LOGGER.info("Installer cancel request, installation success : {}", installationSuccess);
        if (!installationSuccess) {
            Alert dlg = new Alert(Alert.AlertType.CONFIRMATION);
            dlg.setTitle(Translation.getText("lc.installer.confirm.exit.dialog.title"));
            dlg.getDialogPane().setContentText(Translation.getText("lc.installer.confirm.exit.dialog.message"));
            dlg.getDialogPane().setHeaderText(Translation.getText("lc.installer.confirm.exit.dialog.header"));
            Optional<ButtonType> returned = dlg.showAndWait();
            if (returned.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                Platform.exit();
            }
        } else {
            Platform.exit();
        }
    }

    public void stop() {
        this.client.close();
        this.executorService.shutdownNow();
    }

    public void start(Application.Parameters params) {
        this.currentStep.set(steps[0]);
        // Try to find if plugins are given in filename
        if (!CollectionUtils.isEmpty(params.getRaw())) {
            // Windows : exe is given as param
            if (SystemType.current() == SystemType.WINDOWS) {
                params.getRaw().stream().filter(s -> StringUtils.endsWithIgnoreCase(s, ".exe")).findAny().ifPresent(srcExe -> {
                    final String srcExeClean = getNameWithoutExtension(srcExe);
                    LOGGER.info("Will analyse Windows installer exe name to find plugin ids : {}", srcExeClean);
                    if (srcExe.contains(";")) {
                        this.configuration.setPluginToInstallIds(Arrays.stream(srcExeClean.split(";")).skip(1).collect(Collectors.toList()));
                        LOGGER.info("Detected plugin IDs to install : {}", configuration.getPluginToInstallIds());
                    }
                });
            }
            // Mac-Unix : TODO
            else {

            }
        }
    }

    private String getNameWithoutExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        return lastIndexOf == -1 ? name : name.substring(0, lastIndexOf);
    }

    public void previousStep() {
        int currentIndex = Arrays.asList(this.steps).indexOf(currentStep.get());
        if (currentIndex > 0) {
            this.currentStep.set(steps[currentIndex - 1]);
        }
    }

    public void nextStep() {
        int currentIndex = Arrays.asList(this.steps).indexOf(currentStep.get());
        if (currentIndex < this.steps.length - 1) {
            this.currentStep.set(steps[currentIndex + 1]);
        }
    }


    public AppServerClient getClient() {
        return this.client;
    }

    // MEMORY CONFIGURATION
    //========================================================================
    private static final long ONE_GB = 1073741824L;

    private static boolean isArch32Bit() {
        return StringUtils.isEquals("x86", System.getProperty("os.arch"));
    }

    public static String getBestXmxSize(long systemMemory) {
        if (isArch32Bit()) {
            LOGGER.warn("The current JVM is a 32 bit version, system memory is considered as {}", FileNameUtils.getFileSize(ONE_GB));
            systemMemory = ONE_GB;
        }
        //If system has less than 2GB
        if (systemMemory < ONE_GB * 2L) {
            return "750m";//At leat 750MB
        }
        //If system has 2-3 GB : get 1 GB
        if (systemMemory <= ONE_GB * 3L) {
            return "1024m";
        }
        //If system has more than 3-4 GB : get 1.5 GB
        else if (systemMemory <= ONE_GB * 4L) {
            return "1536m";
        }
        // Else if more : 2 GB
        else {
            return "2048m";
        }
    }
    //========================================================================
}
