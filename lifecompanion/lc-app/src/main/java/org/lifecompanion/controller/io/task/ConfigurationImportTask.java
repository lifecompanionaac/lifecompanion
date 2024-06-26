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
package org.lifecompanion.controller.io.task;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.util.Pair;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.framework.commons.utils.app.VersionUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.ui.app.userconfiguration.AboutSubmenu;
import org.lifecompanion.ui.app.userconfiguration.PluginConfigSubmenu;
import org.lifecompanion.ui.app.userconfiguration.UserConfigurationView;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.AbstractAlertBuilder;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.StageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Task to import a configuration from a file.<br>
 * This task can import the configuration, and can load it, but if needed, can also just import the description without loading the configuration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigurationImportTask extends AbstractLoadUtilsTask<Pair<LCConfigurationDescriptionI, LCConfigurationI>> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationImportTask.class);

    /**
     * Paths
     */
    private final File importDirectory;
    private final File configFile;

    /**
     * The configuration ID of the imported configuration
     */
    private final String importedConfigurationID;

    /**
     * If the configuration should be really loaded, or if we just need the description
     */
    private final boolean loadConfiguration;

    /**
     * The default configuration id on the profile where this configuration will be imported.
     */
    private final String profileDefaultConfigurationId;

    public ConfigurationImportTask(final File importDirectoryP, final File configFileP, final String importedConfigurationIDP,
                                   final boolean loadConfigurationP, String profileDefaultConfigurationId) {
        super("task.title.config.import");
        this.importDirectory = importDirectoryP;
        this.configFile = configFileP;
        this.importedConfigurationID = importedConfigurationIDP;
        this.loadConfiguration = loadConfigurationP;
        this.profileDefaultConfigurationId = profileDefaultConfigurationId;
    }

    public File getImportDirectory() {
        return importDirectory;
    }

    @Override
    protected Pair<LCConfigurationDescriptionI, LCConfigurationI> call() throws Exception {
        // Extract the configuration into a temp directory and load the description
        File tempDir = org.lifecompanion.util.IOUtils.getTempDir("import-configuration");
        tempDir.mkdirs();
        IOUtils.unzipInto(this.configFile, tempDir, null);
        LCConfigurationDescriptionI loadedTempDescription = this.loadDescription(tempDir);
        LOGGER.info("Configuration description loaded from temp directory : {}", tempDir);

        // Issue #182 : if a configuration is imported from a newer version
        if (VersionUtils.compare(loadedTempDescription.getTechInfo().getVersion(), InstallationController.INSTANCE.getBuildProperties().getVersionLabel()) > 0) {
            LCException.newException()
                    .withOnCatchCallback(() -> {
                        ButtonType typeTryUpdate = new ButtonType(Translation.getText("button.type.try.update"), ButtonBar.ButtonData.YES);
                        if (typeTryUpdate == DialogUtils.alertWithSourceAndType(StageUtils.getOnTopWindowExcludingNotification(), Alert.AlertType.ERROR)
                                .withHeaderText(Translation.getText("configuration.import.version.error.header"))
                                .withContentText(Translation.getText("configuration.import.version.error.message", loadedTempDescription.getTechInfo().getVersion()))
                                .withButtonTypes(ButtonType.CLOSE, typeTryUpdate)
                                .showAndWait()) {
                            UserConfigurationView userConfigurationView = UserConfigurationController.INSTANCE.getUserConfigurationView();
                            userConfigurationView.showView(() -> userConfigurationView.showTab(AboutSubmenu.class,
                                    AboutSubmenu::startUpdateCheckIfPossible));
                        }
                    })
                    .buildAndThrow();
        }

        // If configuration was successfully checked, copy the extracted file into the right directory
        IOUtils.copyDirectory(tempDir, this.importDirectory);
        LCConfigurationDescriptionI loadedDescription = this.loadDescription(this.importDirectory);
        ConfigurationImportTask.LOGGER.info("Configuration imported to the directory {}", this.importDirectory);

        /*
         * Issue #156
         * If we import a configuration that is not the default, but the current profile version was the default configuration : set default to true.
         * If we import a configuration that is the default, but the current profile default is not this one : set default to false
         */
        final boolean defaultShouldBeSetToTrue = !loadedDescription.launchInUseModeProperty().get()
                && StringUtils.isEquals(importedConfigurationID, profileDefaultConfigurationId);
        final boolean defaultShouldBeSetToFalse = loadedDescription.launchInUseModeProperty().get()
                && !StringUtils.isEquals(importedConfigurationID, profileDefaultConfigurationId);
        if (defaultShouldBeSetToTrue || defaultShouldBeSetToFalse) {
            try {
                ConfigurationImportTask.LOGGER.info(
                        "Configuration description will be saved again on import, because launch in use mode changed to {} (previous = {}, profile default = {}, config id = {})",
                        defaultShouldBeSetToTrue, loadedDescription.launchInUseModeProperty().get(), profileDefaultConfigurationId, importedConfigurationID);
                loadedDescription.launchInUseModeProperty().set(defaultShouldBeSetToTrue);
                ThreadUtils.executeInCurrentThread(new ConfigurationDescriptionSavingTask(this.importDirectory, loadedDescription));
            } catch (Exception e) {
                ConfigurationImportTask.LOGGER.warn(
                        "Configuration was a default configuration and the default property was removed, but couldn't save the description back", e);
            }
        }
        //Then, load the configuration
        LCConfigurationI loadedConfiguration = this.loadConfiguration ? this.loadConfiguration(this.importDirectory, loadedDescription) : null;
        return new Pair<>(loadedDescription, loadedConfiguration);
    }

    public String getImportedConfigurationID() {
        return this.importedConfigurationID;
    }
}
