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

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import org.lifecompanion.controller.io.*;
import org.lifecompanion.controller.io.task.*;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.LCTask;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.editmode.ProfileConfigSelectionController;
import org.lifecompanion.controller.editmode.ProfileConfigStep;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.util.ConfigUIUtils;
import org.lifecompanion.controller.editmode.LCFileChooser;
import org.lifecompanion.ui.common.control.specific.selector.ProfileSelectorControl;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.lifecompanion.framework.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Class that keep actions relative to profiles.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCProfileActions {
    private final static Logger LOGGER = LoggerFactory.getLogger(LCProfileActions.class);

    // Actions
    //========================================================================
    public static abstract class AbstractSaveProfileAction implements BaseEditActionI {
        protected final LCProfileI profileToSave;
        protected final Consumer<LCProfileI> callback;

        public AbstractSaveProfileAction(final LCProfileI profileToSave, Consumer<LCProfileI> callback) {
            this.profileToSave = profileToSave;
            this.callback = callback;
        }

        @Override
        public void doAction() throws LCException {
            //Save it
            ProfileSavingTask profileSaveTask = IOManager.INSTANCE.createSaveProfileTask(this.profileToSave);
            profileSaveTask.setOnSucceeded(e -> {
                if (callback != null) {
                    callback.accept(profileToSave);
                }
            });
            AsyncExecutorController.INSTANCE.addAndExecute(true, false, profileSaveTask);
        }
    }

    public static class AddProfileAction extends AbstractSaveProfileAction {

        public AddProfileAction(Node source, LCProfileI profileToSave, List<Pair<LCConfigurationDescriptionI, File>> defaultConfigurationToAdd) {
            super(profileToSave, profile -> {
                // Select the profile on created
                Runnable onceFinishedCallback = () -> {
                    ConfigActionController.INSTANCE.executeAction(new LCProfileActions.SelectProfileAction(source, profileToSave));
                    ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_LIST, null, null);
                };

                // Add the default configuration once profile is saved and before selection
                if (LangUtils.isNotEmpty(defaultConfigurationToAdd)) {
                    List<ConfigurationDuplicateTask> configurationDuplicateTasks = new ArrayList<>();
                    for (Pair<LCConfigurationDescriptionI, File> defaultConfig : defaultConfigurationToAdd) {
                        configurationDuplicateTasks.add(IOManager.INSTANCE.createConfigurationDuplicateTaskFromDefaultConfigurationDir(defaultConfig.getLeft(), defaultConfig.getRight(), profileToSave));
                    }
                    Task<Void> createAllDefaultConfigTask = new LCTask<Void>("task.add.available.default.configuration.list") {
                        @Override
                        protected Void call() throws Exception {
                            for (ConfigurationDuplicateTask duplicateTask : configurationDuplicateTasks) {
                                LCUtils.executeInCurrentThread(duplicateTask);
                            }
                            return null;
                        }
                    };
                    createAllDefaultConfigTask.setOnSucceeded(e -> onceFinishedCallback.run());
                    AsyncExecutorController.INSTANCE.addAndExecute(true, false, createAllDefaultConfigTask);
                } else {
                    onceFinishedCallback.run();
                }
            });
        }

        @Override
        public void doAction() throws LCException {
            ProfileController.INSTANCE.getProfiles().add(this.profileToSave);
            super.doAction();
        }

        @Override
        public String getNameID() {
            return "action.add.profile.name";
        }

    }

    /**
     * Action to edit a profile (create to save changes)
     */
    public static class EditProfileAction extends AbstractSaveProfileAction {

        public EditProfileAction(LCProfileI profileToSave, Consumer<LCProfileI> callback) {
            super(profileToSave, callback);
        }

        @Override
        public String getNameID() {
            return "action.edit.profile.name";
        }
    }

    /**
     * Action to remove a profile
     */
    public static class RemoveProfileAction implements BaseEditActionI {
        private final Node source;
        private LCProfileI profileToRemove;

        public RemoveProfileAction(Node source, final LCProfileI profileToRemoveP) {
            this.source = source;
            this.profileToRemove = profileToRemoveP;
        }

        @Override
        public void doAction() throws LCException {
            if (ProfileController.INSTANCE.currentProfileProperty().get() == this.profileToRemove) {
                Alert dlg = ConfigUIUtils.createAlert(source, AlertType.CONFIRMATION);
                dlg.getDialogPane().setContentText(Translation.getText("action.remove.profile.confirm.current.profile",
                        this.profileToRemove.nameProperty().get(), this.profileToRemove.configurationCountProperty().get()));
                dlg.getDialogPane().setHeaderText(Translation.getText("action.remove.profile.confirm.header"));
                Optional<ButtonType> returned = dlg.showAndWait();
                if (returned.get() == ButtonType.OK) {
                    this.executeProfileRemove();
                    //Now, we need to select again a profil
                    ProfileController.INSTANCE.clearSelectedProfile();
                    ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_LIST, null, null);
                }
            } else {
                //Ask for confirm
                Alert dlg = ConfigUIUtils.createAlert(source, AlertType.CONFIRMATION);
                dlg.getDialogPane().setContentText(Translation.getText("action.remove.profile.confirm.message", this.profileToRemove.nameProperty().get(),
                        this.profileToRemove.configurationCountProperty().get()));
                dlg.getDialogPane().setHeaderText(Translation.getText("action.remove.profile.confirm.header"));
                Optional<ButtonType> returned = dlg.showAndWait();
                if (returned.get() == ButtonType.OK) {
                    this.executeProfileRemove();
                    ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_LIST, null, null);
                }
            }
        }

        /**
         * Execute the real profile remove
         */
        private void executeProfileRemove() {
            backupThenDeleteProfileDirectory(profileToRemove, true, () -> LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("profile.removed.notif.title")));
            ProfileController.INSTANCE.getProfiles().remove(this.profileToRemove);
        }

        @Override
        public String getNameID() {
            return "action.remove.profile.name";
        }
    }

    /**
     * Action to select a profile
     */
    public static class SelectProfileAction implements BaseEditActionI {
        private final Node source;
        private LCProfileI profileToSelect;

        public SelectProfileAction(Node source, final LCProfileI profileToSelectP) {
            this.source = source;
            this.profileToSelect = profileToSelectP;
        }

        @Override
        public void doAction() throws LCException {
            LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();

            if (profileToSelect != currentProfile) {
                //Check if we can change profile
                GlobalActions.checkModificationForCurrentConfiguration(currentProfile != null, this, source, Translation.getText("select.profile.action.confirm.message"), "select.profile.action.confirm.button", () -> {
                    // First, select the profile
                    ProfileController.INSTANCE.selectProfile(profileToSelect);

                    // Once full profile is loaded : notify, create new config, set current profile
                    Runnable afterFullLoading = () -> {
                        LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("action.select.profile.notif.title", profileToSelect.nameProperty())));
                    };

                    // Once previous profile is changed : fully load the profile
                    Runnable afterPreviousProfileHandled = () -> {
                        ProfileFullLoadingTask loadFullProfileTask = IOManager.INSTANCE.createLoadFullProfileTask(profileToSelect);
                        loadFullProfileTask.setOnSucceeded(event -> afterFullLoading.run());
                        AsyncExecutorController.INSTANCE.addAndExecute(true, false, loadFullProfileTask);
                    };

                    // Save previous profile if needed before loading the new one
                    if (currentProfile != null) {
                        // Save previous profile
                        ProfileSavingTask previousProfileSaveTask = IOManager.INSTANCE.createSaveProfileTask(currentProfile);
                        previousProfileSaveTask.setOnSucceeded(e -> afterPreviousProfileHandled.run());
                        AsyncExecutorController.INSTANCE.addAndExecute(true, false, previousProfileSaveTask);
                    } else {
                        afterPreviousProfileHandled.run();
                    }
                });
            }
        }

        @Override
        public String getNameID() {
            return "action.select.profile.name";
        }
    }

    public static class ProfileExportAction implements BaseEditActionI {
        private static final SimpleDateFormat DATE_FORMAT_FILENAME = new SimpleDateFormat("yyyyMMdd");
        private final Node source;
        private final LCProfileI selectedProfile;

        public ProfileExportAction(final Node source, final LCProfileI selectedProfile) {
            this.selectedProfile = selectedProfile;
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            FileChooser profileChooser = LCFileChooser.getChooserProfile(FileChooserType.PROFILE_EXPORT);
            profileChooser.setInitialFileName(DATE_FORMAT_FILENAME.format(new Date()) + "_" + LCUtils.getValidFileName(selectedProfile.nameProperty().get()));
            File profileExportFile = profileChooser.showSaveDialog(UIUtils.getSourceWindow(source));
            if (profileExportFile != null) {
                LCStateController.INSTANCE.updateDefaultDirectory(FileChooserType.PROFILE_EXPORT, profileExportFile.getParentFile());
                ProfileExportTask profileExportTask = IOManager.INSTANCE.createProfileExportTask(this.selectedProfile, profileExportFile);
                AsyncExecutorController.INSTANCE.addAndExecute(true, false, profileExportTask);
            }
        }

        @Override
        public String getNameID() {
            return "action.export.profile.name";
        }

    }

    public static class ProfileImportAction implements BaseEditActionI {
        private File profileImportFile;
        private final Node source;
        private final Consumer<LCProfileI> successCallback;

        public ProfileImportAction(Node source, final File profileImportFile, Consumer<LCProfileI> successCallback) {
            this.profileImportFile = profileImportFile;
            this.source = source;
            this.successCallback = successCallback;
        }

        public ProfileImportAction(final File profileImportFile) {
            this(null, profileImportFile, null);
        }

        public ProfileImportAction(Node source, Consumer<LCProfileI> successCallback) {
            this(source, null, successCallback);
        }

        @Override
        public void doAction() throws LCException {
            FileChooser profileChooser = LCFileChooser.getChooserProfile(FileChooserType.PROFILE_IMPORT);
            if (this.profileImportFile == null) {
                this.profileImportFile = profileChooser.showOpenDialog(UIUtils.getSourceWindow(source));
            }
            if (this.profileImportFile != null) {
                LCStateController.INSTANCE.updateDefaultDirectory(FileChooserType.PROFILE_IMPORT, this.profileImportFile.getParentFile());
                ProfileImportTask profileImportTask = IOManager.INSTANCE.createProfileImportTask(this.profileImportFile);
                //Check if a existing profile have the same ID
                LCProfileI previousProfile = ProfileController.INSTANCE.getByID(profileImportTask.getImportedProfileId());
                if (previousProfile != null) {
                    Alert dlg = ConfigUIUtils.createAlert(source, AlertType.CONFIRMATION);
                    dlg.getDialogPane().setContentText(Translation.getText("action.import.existing.profile.text"));
                    dlg.getDialogPane().setHeaderText(Translation.getText("action.import.existing.profile.header"));
                    Optional<ButtonType> returned = dlg.showAndWait();
                    if (returned.get() != ButtonType.OK) {
                        return;
                    }
                }
                profileImportTask.setOnSucceeded((event) -> {
                    LCProfileI importedProfile = profileImportTask.getValue();
                    //Remove when needed
                    if (previousProfile != null) {
                        ProfileController.INSTANCE.getProfiles().remove(previousProfile);
                    }
                    //Add to profile list
                    ProfileController.INSTANCE.getProfiles().add(0, importedProfile);

                    if (this.successCallback != null) {
                        this.successCallback.accept(importedProfile);
                    }
                });
                Runnable launchProfileImportTask = () -> AsyncExecutorController.INSTANCE.addAndExecute(true, false, profileImportTask);
                if (previousProfile != null) {
                    backupThenDeleteProfileDirectory(previousProfile, false, launchProfileImportTask);
                } else launchProfileImportTask.run();
            }
        }

        @Override
        public String getNameID() {
            return "action.import.profile.name";
        }

    }

    /**
     * To duplicate a selected configuration
     */
    public static class DuplicateProfileAction implements BaseEditActionI {
        private final Node source;

        public DuplicateProfileAction(Node source) {
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            // Select the profile to duplicate
            Alert dialog = ConfigUIUtils.createAlert(source, Alert.AlertType.NONE);
            dialog.setHeaderText(Translation.getText("config.duplicate.question.select.profile"));
            ProfileSelectorControl profileSelectorControl = new ProfileSelectorControl(Translation.getText("config.duplicate.field.profile"));
            dialog.getDialogPane().setContent(profileSelectorControl);
            ButtonType typeCancel = new ButtonType(Translation.getText("button.type.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType typeDuplicate = new ButtonType(Translation.getText("button.type.duplicate"), ButtonBar.ButtonData.YES);
            dialog.getButtonTypes().setAll(typeCancel, typeDuplicate);
            Optional<ButtonType> buttonType = dialog.showAndWait();

            if (buttonType.get() == typeDuplicate) {
                LCProfileI selectedProfile = profileSelectorControl.valueProperty().get();
                if (selectedProfile != null) {
                    ProfileDuplicateTask duplicateTask = IOManager.INSTANCE.createProfileDuplicateTask(selectedProfile);
                    duplicateTask.setOnSucceeded(e -> {
                        LCProfileI duplicatedProfile = duplicateTask.getValue();
                        ProfileController.INSTANCE.getProfiles().add(0, duplicatedProfile);
                        ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_EDIT, ProfileConfigStep.PROFILE_LIST, duplicatedProfile);
                    });
                    AsyncExecutorController.INSTANCE.addAndExecute(true, false, duplicateTask);
                }
            }
        }

        @Override
        public String getNameID() {
            return "action.duplicate.profile.name";
        }
    }
    //========================================================================

    // TOOL
    //========================================================================

    /**
     * Execute the move profile action in a background Thread.<br>
     * Profile are moved to avoid a complete delete : delete a profile is very dangerous and a user could want to restore its profile.
     */
    private static void backupThenDeleteProfileDirectory(LCProfileI profile, boolean deleteDirectories, Runnable postAction) {
        ProfileBackupAndThenTask backupProfileTask = IOManager.INSTANCE.createProfileBackupTask(profile, IOManager.INSTANCE.getBackupProfileDestinationPath(profile), deleteDirectories ? () -> {
            File profileDirectory = new File(IOManager.INSTANCE.getProfileDirectoryPath(profile.getID()));
            // Now try to delete configuration directory (may fail sometimes if resources are not cleared, that's why we also delete directories on startup)
            IOUtils.deleteDirectoryAndChildren(profileDirectory);
        } : null);
        backupProfileTask.setOnSucceeded(e -> {
            if (postAction != null) postAction.run();
        });
        AsyncExecutorController.INSTANCE.addAndExecute(true, false, backupProfileTask);
    }
    //========================================================================
}
