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
package org.lifecompanion.controller.editaction;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.lifecompanion.controller.io.*;
import org.lifecompanion.controller.io.task.*;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.profile.LCConfigurationDescription;
import org.lifecompanion.model.impl.configurationcomponent.LCConfigurationComponent;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.editmode.ProfileConfigSelectionController;
import org.lifecompanion.controller.editmode.ProfileConfigStep;
import org.lifecompanion.controller.io.task.ExportGridsToPdfTask;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.util.ConfigUIUtils;
import org.lifecompanion.controller.editmode.LCFileChooser;
import org.lifecompanion.ui.common.control.specific.selector.ConfigurationSelectorControl;
import org.lifecompanion.controller.editmode.NodeSnapshotCache;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

import static org.lifecompanion.util.UIUtils.getSourceFromEvent;
import static org.lifecompanion.controller.editmode.FileChooserType.EXPORT_PDF;

/**
 * Class that hold all software classic action.<br>
 * New,open,save,etc...
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCConfigurationActions {
    private final static Logger LOGGER = LoggerFactory.getLogger(LCConfigurationActions.class);

    public static final EventHandler<ActionEvent> HANDLER_NEW = (ea) -> ConfigActionController.INSTANCE.executeAction(new ShowNewEditAction(getSourceFromEvent(ea)));
    public static final EventHandler<ActionEvent> HANDLER_MANAGE = (ea) -> ConfigActionController.INSTANCE.executeAction(new ManageConfigurationDialogAction());
    public static final EventHandler<ActionEvent> HANDLER_SAVE = (ea) -> ConfigActionController.INSTANCE.executeAction(new SaveAction(getSourceFromEvent(ea)));

    public static final EventHandler<ActionEvent> HANDLER_EXPORT = (ea) -> ConfigActionController.INSTANCE.executeAction(new ExportEditAction(getSourceFromEvent(ea)));
    public static final EventHandler<ActionEvent> HANDLER_IMPORT_OPEN = (ea) -> ConfigActionController.INSTANCE.executeAction(new ImportOpenEditAction(getSourceFromEvent(ea)));

    public static final KeyCombination KEY_COMBINATION_NEW = new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCombination KEY_COMBINATION_OPEN = new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCombination KEY_COMBINATION_SAVE = new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);


    public static class ShowNewEditAction implements BaseEditActionI {
        private final Node source;

        public ShowNewEditAction(Node source) {
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            GlobalActions.checkModificationForCurrentConfiguration(this, source, Translation.getText("new.config.action.confirm.message"), "new.config.action.confirm.button", () -> {
                ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_ADD, null, null);
            });
        }

        @Override
        public String getNameID() {
            return "action.new.name";
        }
    }

    public static class CloseEditAction implements BaseEditActionI {
        private final Node source;

        public CloseEditAction(Node source) {
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            GlobalActions.checkModificationForCurrentConfiguration(this, source,
                    Translation.getText("close.config.action.confirm.message"),
                    "close.config.action.confirm.button",
                    AppModeController.INSTANCE::closeEditModeConfiguration
            );
        }

        @Override
        public String getNameID() {
            return "action.new.name";
        }
    }

    public static class NewEditInListAction implements BaseEditActionI {

        @Override
        public void doAction() throws LCException {
            //Create the configuration
            LCConfigurationI configuration = new LCConfigurationComponent();
            //Create the base description
            LCConfigurationDescriptionI configDescription = LCConfigurationActions.createConfigurationForCurrentProfile();
            configDescription.loadedConfigurationProperty().set(configuration);
            configDescription.configurationNameProperty().set(Translation.getText("configuration.menu.no.name"));
            //Display the create
            ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_CREATE, ProfileConfigStep.CONFIGURATION_LIST, configDescription);
        }

        @Override
        public String getNameID() {
            return "action.new.name";
        }
    }

    /**
     * Set default configuration for current profile
     */
    public static class SetDefaultEditAction implements BaseEditActionI {
        private final LCConfigurationDescriptionI configuration;
        private final boolean value;

        public SetDefaultEditAction(final LCConfigurationDescriptionI configuration, boolean value) {
            this.configuration = configuration;
            this.value = value;
        }

        @Override
        public void doAction() throws LCException {
            LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
            LCConfigurationDescriptionI currentDefaultConfiguration = currentProfile.getCurrentDefaultConfiguration();
            //Reset only if enabled and it's not the current
            if (value && currentDefaultConfiguration != null && currentDefaultConfiguration != this.configuration) {
                currentDefaultConfiguration.launchInUseModeProperty().set(false);
                LCConfigurationActions.saveConfigurationDescription(currentDefaultConfiguration);
            }
            //Change on current
            this.configuration.launchInUseModeProperty().set(value);
            LCConfigurationActions.saveConfigurationDescription(this.configuration);
        }

        @Override
        public String getNameID() {
            return "action.set.default.configuration.profile";
        }
    }

    /**
     * To create and add the configuration from dialog
     */
    public static class AddNewEditAction implements BaseEditActionI {
        private final Node source;
        private final LCConfigurationDescriptionI configDescription;

        public AddNewEditAction(final Node source, final LCConfigurationDescriptionI configDescriptionP) {
            this.source = source;
            this.configDescription = configDescriptionP;
        }

        @Override
        public void doAction() throws LCException {
            //Add to configuration list
            LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
            currentProfile.getConfiguration().add(0, this.configDescription);
            //Save the configuration
            ConfigurationSavingTask saveTask = IOManager.INSTANCE
                    .createSaveConfigurationTask(this.configDescription.loadedConfigurationProperty().get(), currentProfile);
            try {
                LCUtils.executeInCurrentThread(saveTask);
                LCConfigurationActions.LOGGER.info("New configuration saved after the edit screen displayed");
                //Now select the created configuration
                ProfileConfigSelectionController.INSTANCE.hideStage();
                OpenConfigurationAction openConfiguration = new OpenConfigurationAction(source, this.configDescription);
                ConfigActionController.INSTANCE.executeAction(openConfiguration);
            } catch (Exception e) {
                LCConfigurationActions.LOGGER.info("Can't save the configuration on new configuration action", e);
            }
        }

        @Override
        public String getNameID() {
            return "action.new.name";
        }
    }

    public static class AddNewEditFromDefaultAction implements BaseEditActionI {
        private final Node source;
        private final org.lifecompanion.framework.utils.Pair<LCConfigurationDescriptionI, File> defaultConfiguration;

        public AddNewEditFromDefaultAction(final Node source, org.lifecompanion.framework.utils.Pair<LCConfigurationDescriptionI, File> defaultConfiguration) {
            this.source = source;
            this.defaultConfiguration = defaultConfiguration;
        }

        @Override
        public void doAction() throws LCException {
            final LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
            final ConfigurationDuplicateTask configurationDuplicateTask = IOManager.INSTANCE.createConfigurationDuplicateTaskFromDefaultConfigurationDir(defaultConfiguration.getLeft(), defaultConfiguration.getRight(), profile);
            configurationDuplicateTask.setOnSucceeded(e -> {
                // Add to profile and then open
                LCConfigurationDescriptionI duplicated = configurationDuplicateTask.getValue();
                profile.getConfiguration().add(0, duplicated);
                ConfigActionController.INSTANCE.executeAction(new OpenConfigurationAction(source, duplicated));
            });
            AsyncExecutorController.INSTANCE.addAndExecute(true, false, configurationDuplicateTask);
        }

        @Override
        public String getNameID() {
            return "action.add.new.from.default.name";
        }
    }

    /**
     * Save configuration, or execute save as if configuration was never saved.
     */
    public static class SaveAction implements BaseEditActionI {
        private final Consumer<Boolean> callback;
        private final Node source;

        //        public SaveAction() {
        //            this(AppController.INSTANCE.getMainStageRoot(), null);
        //        }

        public SaveAction(final Node source) {
            this(source, null);
        }

        public SaveAction(final Node source, final Consumer<Boolean> callbackP) {
            this.source = source;
            this.callback = callbackP;
        }

        @Override
        public void doAction() throws LCException {
            LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext().configurationProperty().get();

            //Check if the configuration description exist, and create when needed
            // FIXME : this will not happen now that the create view had been uniformized
            LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
            if (currentProfile.getConfigurationById(configuration.getID()) == null) {
                //Ask for name
                TextInputDialog dialog = ConfigUIUtils.createInputDialog(source, Translation.getText("action.save.config.default.name"));
                dialog.setHeaderText(Translation.getText("action.save.config.dialog.header"));
                dialog.setContentText(Translation.getText("action.save.config.dialog.message"));

                // Get the name
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    LCConfigurationDescriptionI configDescription = LCConfigurationActions.createConfigurationForCurrentProfile();
                    configDescription.loadedConfigurationProperty().set(configuration);
                    configDescription.configurationNameProperty().set(result.get());
                    currentProfile.getConfiguration().add(configDescription);
                    AppModeController.INSTANCE.switchEditModeConfiguration(configuration, configDescription);
                } else {
                    return;
                }
            }

            //Create the task
            ConfigurationSavingTask saveConfigTask = IOManager.INSTANCE.createSaveConfigurationTask(configuration, currentProfile);
            LCConfigurationDescriptionI configDescription = AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get();
            // Update the config description image because we are on JavaFX Thread
            // Unknown bug : snapshot can sometimes fail
            try {
                // FIXME : bad background style on configuration
                configDescription.configurationImageProperty().set(NodeSnapshotCache.getComponentSnapshot(configuration, false, -1, -1));
            } catch (Throwable t) {
                LCConfigurationActions.LOGGER.warn("Couldn't take a snapshot of configuration", t);
            }
            //Define action after
            saveConfigTask.setOnFailed((wse) -> {
                if (this.callback != null) {
                    this.callback.accept(false);
                }
            });
            saveConfigTask.setOnSucceeded((wse) -> {
                AppModeController.INSTANCE.getEditModeContext().resetUnsavedActionOnCurrentConfiguration();
                if (this.callback != null) {
                    this.callback.accept(true);
                }
            });
            //Execute
            AsyncExecutorController.INSTANCE.addAndExecute(true, false, saveConfigTask);
        }

        @Override
        public String getNameID() {
            return "action.save.name";
        }
    }

    /**
     * To import and directly open a configuration
     */
    public static class ImportOpenEditAction implements BaseEditActionI {
        private final Node source;
        private File configurationPath;

        public ImportOpenEditAction(final Node source) {
            this.source = source;
        }

        public ImportOpenEditAction(final Node source, final File configurationPathP) {
            this(source);
            this.configurationPath = configurationPathP;
        }

        public ImportOpenEditAction(final File configurationPathP) {
            this(null, configurationPathP);
        }

        @Override
        public void doAction() throws LCException {
            GlobalActions.checkModificationForCurrentConfiguration(this, source, Translation.getText("import.config.action.confirm.message"), "import.config.action.confirm.button", () -> {
                new ImportEditAction(source, this.configurationPath, desc -> {
                    if (desc != null) {
                        OpenConfigurationAction openConfigAction = new OpenConfigurationAction(source, desc, false);
                        try {
                            openConfigAction.doAction();
                        } catch (Exception e) {
                            LCConfigurationActions.LOGGER.info("Can't execute the configuration open action after the configuration import");
                        }
                    }
                }).doAction();
            });
        }

        @Override
        public String getNameID() {
            return "action.import.open.name";
        }

    }

    /**
     * To duplicate a selected configuration
     */
    public static class DuplicateEditAction implements BaseEditActionI {
        private final Node source;

        public DuplicateEditAction(Node source) {
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            // Select a configuration to duplicate
            Alert dialog = ConfigUIUtils.createAlert(source, Alert.AlertType.NONE);
            dialog.setHeaderText(Translation.getText("config.duplicate.question.select.config"));
            ConfigurationSelectorControl configurationSelectorControl = new ConfigurationSelectorControl(Translation.getText("config.duplicate.field.config"));
            configurationSelectorControl.setPrefWidth(400.0);
            dialog.getDialogPane().setContent(configurationSelectorControl);
            ButtonType typeCancel = new ButtonType(Translation.getText("button.type.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType typeDuplicate = new ButtonType(Translation.getText("button.type.duplicate"), ButtonBar.ButtonData.YES);
            dialog.getButtonTypes().setAll(typeCancel, typeDuplicate);
            Optional<ButtonType> buttonType = dialog.showAndWait();
            if (buttonType.get() == typeDuplicate) {
                LCConfigurationDescriptionI selectedConfigurationDescription = configurationSelectorControl.valueProperty().get();
                if (selectedConfigurationDescription != null) {
                    // Duplicate selected configuration on profile
                    LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
                    ConfigurationDuplicateTask duplicateTask = IOManager.INSTANCE.createConfigurationDuplicateTaskFromCurrentProfile(selectedConfigurationDescription, currentProfile);
                    duplicateTask.setOnSucceeded(e -> {
                        // Add duplicated
                        LCConfigurationDescriptionI duplicate = duplicateTask.getValue();
                        currentProfile.getConfiguration().add(0, duplicate);
                        // Show edit step
                        ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_EDIT, ProfileConfigStep.CONFIGURATION_LIST, duplicate);
                    });
                    AsyncExecutorController.INSTANCE.addAndExecute(true, false, duplicateTask);
                }
            }
        }

        @Override
        public String getNameID() {
            return "action.duplicated.config.name";
        }
    }

    /**
     * Action to import a configuration from file
     */
    public static class ImportEditAction implements BaseEditActionI {
        private static final int ANSWER_DELAY = 4_000;

        private final Node source;
        private Consumer<LCConfigurationDescriptionI> callback;
        private File configurationPath;


        public ImportEditAction(final Node source) {
            this.source = source;
        }

        public ImportEditAction(final Node source, final File configurationPathP, final Consumer<LCConfigurationDescriptionI> callbackP) {
            this(source);
            this.callback = callbackP;
            this.configurationPath = configurationPathP;
        }

        @SuppressWarnings({"unchecked"})
        @Override
        public void doAction() throws LCException {
            //Choose the file
            LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
            if (this.configurationPath == null) {
                FileChooser configChooser = LCFileChooser.getChooserConfiguration(FileChooserType.CONFIG_IMPORT);
                this.configurationPath = configChooser.showOpenDialog(UIUtils.getSourceWindow(source));
            }
            if (this.configurationPath != null) {
                LCStateController.INSTANCE.updateDefaultDirectory(FileChooserType.CONFIG_IMPORT, this.configurationPath.getParentFile());

                //Check if configuration file is valid and already exist ?
                ConfigurationImportTask configurationImportTask = IOManager.INSTANCE.createConfigurationImport(currentProfile, this.configurationPath);
                String importedConfigurationID = configurationImportTask.getImportedConfigurationID();
                LCConfigurationDescriptionI previousConfigDescription = currentProfile.getConfigurationById(importedConfigurationID);

                // Read imported configuration description
                final LCConfigurationDescriptionI importedConfigurationDescription;
                try {
                    importedConfigurationDescription = LCUtils.executeInCurrentThread(new LoadConfigurationDescriptionTask(configurationPath));
                } catch (Exception e) {
                    LOGGER.error("Couldn't read imported configuration description");
                    throw LCException.newException().withMessageId("exception.invalid.config.profil.file").withCause(e).build();
                }

                ButtonType typeReplacePrevious = new ButtonType(Translation.getText("button.type.replace.previous"), ButtonBar.ButtonData.YES);
                ButtonType typeKeepBoth = new ButtonType(Translation.getText("button.type.keep.both"), ButtonBar.ButtonData.NO);
                ButtonType typeCancel = new ButtonType(Translation.getText("button.type.cancel.import.config"), ButtonBar.ButtonData.CANCEL_CLOSE);

                ButtonType result;

                if (previousConfigDescription != null) {
                    Alert dlg = ConfigUIUtils.createAlert(source, AlertType.WARNING);
                    dlg.getDialogPane().setHeaderText(Translation.getText("action.import.existing.configuration.header", currentProfile.nameProperty().get()));
                    dlg.getDialogPane().setContentText(Translation.getText("action.import.existing.configuration.message",
                            previousConfigDescription.configurationNameProperty().get(),
                            getLastModificationDateIn(previousConfigDescription),
                            getLastModificationAuthorIn(previousConfigDescription),
                            importedConfigurationDescription.configurationNameProperty().get(),
                            getLastModificationDateIn(importedConfigurationDescription),
                            getLastModificationAuthorIn(importedConfigurationDescription)
                    ));
                    dlg.getButtonTypes().setAll(typeReplacePrevious, typeKeepBoth, typeCancel);
                    dlg.getDialogPane().lookupButton(typeKeepBoth).setDisable(true);
                    dlg.getDialogPane().lookupButton(typeReplacePrevious).setDisable(true);

                    // Launch a thread to enable button after a delay : we don't want the user to make the choice too quickly !
                    LCNamedThreadFactory.daemonThreadFactory("DelayBeforeAnswerWaiting").newThread(() -> {
                        LCUtils.safeSleep(ANSWER_DELAY);
                        LCUtils.runOnFXThread(() -> {
                            dlg.getDialogPane().setCursor(null);
                            dlg.getDialogPane().lookupButton(typeKeepBoth).setDisable(false);
                            dlg.getDialogPane().lookupButton(typeReplacePrevious).setDisable(false);
                        });
                    }).start();

                    dlg.getDialogPane().setCursor(Cursor.WAIT);
                    result = dlg.showAndWait().orElse(null);
                } else {
                    result = null;
                }
                Runnable oncePreviousConfigurationHadBeenHandled = () -> {
                    configurationImportTask.setOnSucceeded((wse) -> {
                        if (result == typeKeepBoth || result == typeReplacePrevious) {
                            currentProfile.getConfiguration().remove(previousConfigDescription);
                        }
                        //Add the configuration description to current profile
                        Pair<LCConfigurationDescriptionI, LCConfigurationI> resultPair = (Pair<LCConfigurationDescriptionI, LCConfigurationI>) wse
                                .getSource().getValue();
                        LCConfigurationDescriptionI importedDescription = resultPair.getKey();
                        currentProfile.getConfiguration().add(0, importedDescription);
                        //Callback
                        if (this.callback != null) {
                            this.callback.accept(importedDescription);
                        }
                    });
                    AsyncExecutorController.INSTANCE.addAndExecute(true, false, configurationImportTask);
                };

                if (result == null) {
                    oncePreviousConfigurationHadBeenHandled.run();
                } else if (result == typeKeepBoth) {
                    ConfigurationDuplicateTask duplicateTask = IOManager.INSTANCE.createConfigurationDuplicateTaskFromCurrentProfileChangeIdOnly(previousConfigDescription, currentProfile);
                    duplicateTask.setOnSucceeded(e -> {
                        LCConfigurationDescriptionI duplicated = duplicateTask.getValue();
                        currentProfile.getConfiguration().add(0, duplicated);
                        oncePreviousConfigurationHadBeenHandled.run();
                    });
                    AsyncExecutorController.INSTANCE.addAndExecute(true, false, duplicateTask);
                } else if (result == typeReplacePrevious) {
                    backupThenDeleteConfigurationDirectory(previousConfigDescription, currentProfile, false, oncePreviousConfigurationHadBeenHandled);
                }
            } else {
                LCConfigurationActions.LOGGER.info("Configuration will not be imported because user cancelled open dialog");
            }
        }

        private String getLastModificationAuthorIn(LCConfigurationDescriptionI configurationDescription) {
            return configurationDescription.getChangelogEntries()
                    .stream()
                    .min((e1, e2) -> e2.getWhen().compareTo(e1.getWhen()))
                    .map(entry -> entry.getProfileName() + " (" + entry.getSystemUserName() + ")")
                    .orElse("?");
        }

        private String getLastModificationDateIn(LCConfigurationDescriptionI configurationDescription) {
            return configurationDescription.getChangelogEntries()
                    .stream()
                    .min((e1, e2) -> e2.getWhen().compareTo(e1.getWhen()))
                    .map(entry -> StringUtils.dateToStringDateWithHour(entry.getWhen()))
                    .orElse(StringUtils.dateToStringDateWithHour(configurationDescription.configurationLastDateProperty().get()));
        }

        @Override
        public String getNameID() {
            return "action.import.name";
        }

    }

    /**
     * Export the configuration, can be done only when configuration is saved
     */
    public static class ExportEditAction implements BaseEditActionI {
        private LCConfigurationDescriptionI configurationDescription;
        private final Node source;

        public ExportEditAction(Node source, LCConfigurationDescriptionI configurationDescription) {
            this.source = source;
            this.configurationDescription = configurationDescription;
        }

        public ExportEditAction(Node source) {
            this(source, null);
        }

        @Override
        public void doAction() throws LCException {
            //Check if the configuration is already saved
            LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
            LCConfigurationI currentConfiguration = AppModeController.INSTANCE.getEditModeContext().configurationProperty().get();

            // If config description is null, take it from current config
            if (configurationDescription == null) {
                configurationDescription = currentProfile.getConfigurationById(currentConfiguration.getID());
            }

            GlobalActions.checkModificationForCurrentConfiguration(StringUtils.isEquals(configurationDescription.getConfigurationId(), currentConfiguration.getID()), this, source, Translation.getText("export.config.action.confirm.message"), "export.config.action.confirm.button", () -> {
                FileChooser configChooser = LCFileChooser.getChooserConfiguration(FileChooserType.CONFIG_EXPORT);
                // Issue #139 : default name for configuration
                configChooser.setInitialFileName(IOManager.DATE_FORMAT_FILENAME_WITHOUT_TIME.format(new Date()) + "_"
                        + LCUtils.getValidFileName(configurationDescription.configurationNameProperty().get()));
                File configExportFile = configChooser.showSaveDialog(UIUtils.getSourceWindow(source));
                if (configExportFile != null) {
                    LCStateController.INSTANCE.updateDefaultDirectory(FileChooserType.CONFIG_EXPORT, configExportFile.getParentFile());
                    ConfigurationExportTask exportConfigTask = IOManager.INSTANCE.createConfigurationExportTask(configurationDescription, currentProfile,
                            configExportFile);
                    //Execute
                    AsyncExecutorController.INSTANCE.addAndExecute(true, false, exportConfigTask);
                } else {
                    LCConfigurationActions.LOGGER.info("Configuration will no be exported because user cancelled the save dialog");
                }
            });
        }

        @Override
        public String getNameID() {
            return "action.export.name";
        }
    }

    public static class ExportEditGridsToPdfAction implements BaseEditActionI {
        private final Node source;

        public ExportEditGridsToPdfAction(Node source) {
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
            LCConfigurationI currentConfiguration = AppModeController.INSTANCE.getEditModeContext().configurationProperty().get();
            LCConfigurationDescriptionI currentConfigurationDescription = AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get();

            FileChooser configChooser = LCFileChooser.getOtherFileChooser(Translation.getText("pdf.export.chooser.dialog.title"), new FileChooser.ExtensionFilter("PDF", "*.pdf"), EXPORT_PDF);
            configChooser.setInitialFileName(Translation.getText("pdf.export.default.file.name", IOManager.DATE_FORMAT_FILENAME_WITHOUT_TIME.format(new Date()), LCUtils.getValidFileName(currentConfigurationDescription.configurationNameProperty().get())));

            File pdfFile = configChooser.showSaveDialog(UIUtils.getSourceWindow(source));
            if (pdfFile != null) {
                LCStateController.INSTANCE.updateDefaultDirectory(EXPORT_PDF, pdfFile.getParentFile());
                ExportGridsToPdfTask exportGridsToPdfTask = new ExportGridsToPdfTask(currentConfiguration, pdfFile, currentProfile, currentConfigurationDescription);
                exportGridsToPdfTask.setOnSucceeded(ev -> {
                    try {
                        Desktop.getDesktop().open(pdfFile);
                    } catch (Exception e) {
                        LOGGER.warn("Couldn't open PDF file {} after export", pdfFile, e);
                    }
                });
                AsyncExecutorController.INSTANCE.addAndExecute(true, false, exportGridsToPdfTask);
            }
        }

        @Override
        public String getNameID() {
            return "config.action.export.grids.pdf";
        }
    }

    public static class ManageConfigurationDialogAction implements BaseEditActionI {

        @Override
        public void doAction() throws LCException {
            ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_LIST, null, null);
        }

        @Override
        public String getNameID() {
            return "action.manage.config.dialog.name";
        }

    }

    public static class OpenConfigurationAction implements BaseEditActionI {
        private final Node source;
        private final LCConfigurationDescriptionI configDescription;
        private final boolean askUnsaved;
        private final Consumer<Boolean> callback;

        public OpenConfigurationAction(final Node source, final LCConfigurationDescriptionI configP) {
            this(source, configP, true, null);
        }

        public OpenConfigurationAction(final Node source, final LCConfigurationDescriptionI configP, final boolean askUnsavedP, Consumer<Boolean> callback) {
            this.source = source;
            this.configDescription = configP;
            this.askUnsaved = askUnsavedP;
            this.callback = callback;
        }

        public OpenConfigurationAction(final Node source, final LCConfigurationDescriptionI configP, final boolean askUnsavedP) {
            this(source, configP, askUnsavedP, null);
        }

        @Override
        public void doAction() throws LCException {
            GlobalActions.checkModificationForCurrentConfiguration(askUnsaved, this, source, Translation.getText("open.config.action.confirm.message"), "open.config.action.confirm.button", () -> {
                LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
                // Check plugin dependencies
                PluginActions.warnOnPluginDependencies(source,
                        new File(IOManager.INSTANCE.getConfigurationPath(currentProfile.getID(), configDescription.getConfigurationId()) + File.separator + LCConstant.CONFIGURATION_XML_NAME),
                        () -> {
                            ConfigurationLoadingTask loadTask = IOManager.INSTANCE.createLoadConfigurationTask(this.configDescription, currentProfile);
                            loadTask.setOnSucceeded((ea) -> {
                                //Set current configuration
                                AppModeController.INSTANCE.switchEditModeConfiguration(loadTask.getValue(), this.configDescription);
                                if (this.callback != null) {
                                    this.callback.accept(true);
                                }
                            });
                            loadTask.setOnFailed((ea) -> {
                                if (this.callback != null) {
                                    this.callback.accept(false);
                                }
                            });
                            AsyncExecutorController.INSTANCE.addAndExecute(true, false, loadTask);
                        });
            });
        }

        @Override
        public String getNameID() {
            return "action.load.name";
        }

    }

    /**
     * Configuration edition (just for history)
     */
    public static class EditConfigurationAction implements BaseEditActionI {
        private final LCConfigurationDescriptionI configDescription;
        private final Consumer<LCConfigurationDescriptionI> callback;

        public EditConfigurationAction(final LCConfigurationDescriptionI configDescription, Consumer<LCConfigurationDescriptionI> callback) {
            this.configDescription = configDescription;
            this.callback = callback;
        }

        @Override
        public void doAction() throws LCException {
            LCConfigurationActions.saveConfigurationDescription(this.configDescription);
            if (this.callback != null) {
                this.callback.accept(configDescription);
            }
        }

        @Override
        public String getNameID() {
            return "action.edit.config.info.name";
        }

    }

    /**
     * Try to save the configuration description for the current profile
     *
     * @param configDescription the configuration description to save
     * @throws LCException if saving failed
     */
    private static void saveConfigurationDescription(final LCConfigurationDescriptionI configDescription) throws LCException {
        LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
        try {
            ConfigurationDescriptionSavingTask configDescriptionSaveTask = IOManager.INSTANCE.createSaveConfigDescriptionTask(configDescription,
                    profile);
            LCUtils.executeInCurrentThread(configDescriptionSaveTask);
            LCConfigurationActions.LOGGER.info("Configuration description saved for {}", configDescription.getConfigurationId());
        } catch (Exception e) {
            LCConfigurationActions.LOGGER.warn("Couldn't save the configuration description for {}", configDescription.getConfigurationId());
            LCException.newException().withMessageId("error.save.config.description").withCause(e).buildAndThrow();
        }
    }

    /**
     * To remove a configuration from a profile
     */
    public static class RemoveConfigurationAction implements BaseEditActionI {
        private final Node source;
        private final Consumer<LCConfigurationDescriptionI> removedCallback;
        private final LCProfileI profile;
        private final LCConfigurationDescriptionI configDescription;
        private final boolean askAndNotify = true;

        public RemoveConfigurationAction(final Node source, final LCProfileI profile, final LCConfigurationDescriptionI configDescription, Consumer<LCConfigurationDescriptionI> removedCallback) {
            this.source = source;
            this.profile = profile;
            this.configDescription = configDescription;
            this.removedCallback = removedCallback;
        }

        public RemoveConfigurationAction(final Node source, final LCProfileI profile, final LCConfigurationDescriptionI configDescription) {
            this(source, profile, configDescription, null);
        }

        @Override
        public void doAction() throws LCException {
            if (this.askAndNotify) {
                //Ask confirm
                Alert dlg = ConfigUIUtils.createAlert(source, AlertType.CONFIRMATION);
                dlg.getDialogPane().setContentText(
                        Translation.getText("action.remove.config.confirm.message", this.configDescription.configurationNameProperty().get()));
                dlg.getDialogPane().setHeaderText(Translation.getText("action.remove.config.confirm.header"));
                Optional<ButtonType> returned = dlg.showAndWait();
                if (returned.get() != ButtonType.OK) {
                    return;
                }
            }
            //Remove from description
            backupThenDeleteConfigurationDirectory(configDescription, profile, true, null);
            this.profile.getConfiguration().remove(this.configDescription);

            // Notification
            LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("configuration.removed.notification.title"));

            //If the configuration is the currently loaded configuration
            if (AppModeController.INSTANCE.getEditModeContext().getConfigurationDescription() == this.configDescription) {
                AppModeController.INSTANCE.closeEditModeConfiguration();
            }

            if (this.removedCallback != null) {
                removedCallback.accept(configDescription);
            }
        }

        @Override
        public String getNameID() {
            return "action.remove.config.name";
        }
    }

    public static class CreateDesktopShortcut implements BaseEditActionI {
        private final LCProfileI profile;
        private final LCConfigurationDescriptionI configuration;

        public CreateDesktopShortcut(LCProfileI profile, LCConfigurationDescriptionI configuration) {
            this.profile = profile;
            this.configuration = configuration;
        }

        @Override
        public void doAction() throws LCException {
            AsyncExecutorController.INSTANCE.addAndExecute(true, false, new CreateConfigurationDesktopShortcutTask(profile, configuration));
        }

        @Override
        public String getNameID() {
            return "action.remove.config.name";
        }
    }

    private static LCConfigurationDescriptionI createConfigurationForCurrentProfile() {
        LCConfigurationDescriptionI description = new LCConfigurationDescription();
        description.configurationAuthorProperty().set(ProfileController.INSTANCE.currentProfileProperty().get().nameProperty().get());
        return description;
    }


    // BACKUP CONFIGURATION
    //========================================================================

    /**
     * Backup the removed configuration to a hidden directory, then remove the configuration and directory and configuration from the list.
     */
    private static void backupThenDeleteConfigurationDirectory(LCConfigurationDescriptionI configurationDescription, LCProfileI profile, boolean deleteConfigurationDirectory, Runnable postAction) {
        ConfigurationBackupAndThenTask backupConfigurationTask = IOManager.INSTANCE.createConfigurationBackupTask(configurationDescription, profile, IOManager.INSTANCE.getBackupConfigurationDestinationPath(configurationDescription), deleteConfigurationDirectory ? () -> {
            final File configDir = new File(IOManager.INSTANCE.getConfigurationDirectoryPath(profile.getID(), configurationDescription.getConfigurationId()));
            // Now try to delete configuration directory (may fail sometimes if resources are not cleared, that's why we also delete directories on startup)
            IOUtils.deleteDirectoryAndChildren(configDir);
        } : null);
        backupConfigurationTask.setOnSucceeded(e -> {
            if (postAction != null) postAction.run();
        });
        AsyncExecutorController.INSTANCE.addAndExecute(true, false, backupConfigurationTask);
    }
    //========================================================================
}
