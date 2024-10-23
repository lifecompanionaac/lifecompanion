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

package org.lifecompanion.controller.lifecycle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.controller.configurationcomponent.*;
import org.lifecompanion.controller.configurationcomponent.dynamickey.KeyListController;
import org.lifecompanion.controller.configurationcomponent.dynamickey.UserActionSequenceController;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.feedback.IndicationController;
import org.lifecompanion.controller.hub.HubController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.media.SoundPlayerController;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.textprediction.AutoCharPredictionController;
import org.lifecompanion.controller.textprediction.CustomCharPredictionController;
import org.lifecompanion.controller.textprediction.WordPredictionController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.controller.virtualkeyboard.VirtualKeyboardController;
import org.lifecompanion.controller.virtualkeyboard.WinAutoHotKeyKeyboardReceiverController;
import org.lifecompanion.controller.virtualmouse.DirectionalMouseController;
import org.lifecompanion.controller.virtualmouse.ScanningMouseController;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.ui.UseModeScene;
import org.lifecompanion.ui.UseModeStage;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.LCTask;

import java.util.Arrays;
import java.util.List;

public enum AppModeController {
    INSTANCE;

    private final ObjectProperty<AppMode> mode;
    private final UseModeContext useModeContext;
    private final EditModeContext editModeContext;

    AppModeController() {
        mode = new SimpleObjectProperty<>();
        mode.addListener((obs, ov, nv) -> stopModeIfNeeded(ov));
        this.useModeContext = new UseModeContext();
        this.editModeContext = new EditModeContext();
    }

    // PROPS
    //========================================================================
    public ReadOnlyObjectProperty<AppMode> modeProperty() {
        return mode;
    }

    public UseModeContext getUseModeContext() {
        return useModeContext;
    }

    public EditModeContext getEditModeContext() {
        return editModeContext;
    }

    public void initEditModeStage(Stage stage) {
        this.editModeContext.initStage(stage);
    }

    public boolean isUseMode() {
        return this.mode.get() == AppMode.USE;
    }

    public boolean isEditMode() {
        return this.mode.get() == AppMode.EDIT;
    }
    //========================================================================


    public void startEditMode() {
        // Note : DON'T check for CommandLineArgumentEnum.DISABLE_SWITCH_TO_EDIT_MODE here : if done, LifeCompanion could be blocked on startup. Instead, check for callers.
        FXThreadUtils.runOnFXThread(() -> {
            final LCConfigurationI usedConfiguration = useModeContext.getConfiguration();
            mode.set(AppMode.EDIT);
            editModeContext.getStage().show();
            LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
            final LCConfigurationI previousConfigurationEditMode = editModeContext.getPreviousConfiguration();
            // Load previously edited configuration : just restore as current configuration
            if (previousConfigurationEditMode != null) {
                editModeContext.switchTo(previousConfigurationEditMode, editModeContext.getPreviousConfigurationDescription());
                editModeContext.tryToRestoreUseModeStateInEditMode(useModeContext.getUseStateAndClear());
            }
            // There is no previously edited  configuration this happens when
            // - user launch LifeCompanion directly in use mode (default configuration to launch, or command to import/use)
            // - user go to another configuration in use mode (with ChangeConfigurationAction)
            // will try to open the used configuration in edit mode (may be not possible, for example when the configuration is just used and not imported, cf -directImportAndLaunch arg)
            else {
                if (usedConfiguration != null && profile != null) {
                    final LCConfigurationDescriptionI configurationById = profile.getConfigurationById(usedConfiguration.getID());
                    if (configurationById != null) {
                        ConfigActionController.INSTANCE.executeAction(
                                new LCConfigurationActions.OpenConfigurationAction(editModeContext.getStage().getScene().getRoot(), configurationById, false, loaded -> {
                                    if (loaded)
                                        editModeContext.tryToRestoreUseModeStateInEditMode(useModeContext.getUseStateAndClear());
                                })
                        );
                    }
                }
            }
            editModeContext.clearPreviouslyEditedConfiguration();
        });
    }

    public void startUseModeAfterEdit() {
        FXThreadUtils.runOnFXThread(() -> startUseModeForConfiguration((LCConfigurationI) editModeContext.getConfiguration().duplicate(false),
                editModeContext.configurationDescriptionProperty().get()));
    }

    public void startUseModeForConfiguration(LCConfigurationI configuration, LCConfigurationDescriptionI configurationDescription) {
        FXThreadUtils.runOnFXThread(() -> {
            this.useModeContext.switchTo(configuration, configurationDescription);
            boolean notify = !isEditMode();
            mode.set(AppMode.USE);
            launchUseMode(notify);
        });
    }

    public void switchEditModeConfiguration(LCConfigurationI configuration, LCConfigurationDescriptionI configurationDescription) {
        FXThreadUtils.runOnFXThread(() -> editModeContext.switchTo(configuration, configurationDescription));
    }

    public void closeEditModeConfiguration() {
        switchEditModeConfiguration(null, null);
    }

    public void switchUseModeConfiguration(LCConfigurationI configuration, LCConfigurationDescriptionI configurationDescription) {
        FXThreadUtils.runOnFXThread(() -> {
            clearCurrentMode();// this will allow stop then start on use mode
            startUseModeForConfiguration(configuration, configurationDescription);
        });
    }

    void clearCurrentMode() {
        mode.set(null);
    }

    private static final List<ModeListenerI> USE_MODE_LISTENERS = Arrays.asList(//
            PluginController.INSTANCE,
            WordPredictionController.INSTANCE, //
            VirtualKeyboardController.INSTANCE, //
            ScanningMouseController.INSTANCE, //
            DirectionalMouseController.INSTANCE, //
            WritingStateController.INSTANCE, //
            CustomCharPredictionController.INSTANCE, //
            AutoCharPredictionController.INSTANCE, //
            UseActionController.INSTANCE, //
            UseVariableController.INSTANCE, //
            KeyListController.INSTANCE, //
            ConfigListController.INSTANCE,//
            DynamicKeyFillController.INSTANCE,//
            UserActionSequenceController.INSTANCE, //
            UseModeProgressDisplayerController.INSTANCE, //
            UseModeWhiteboardController.INSTANCE,//
            SoundPlayerController.INSTANCE, //
            VoiceSynthesizerController.INSTANCE, //
            NoteKeyController.INSTANCE, //
            ImageDictionaries.INSTANCE,//
            GlobalKeyEventController.INSTANCE,//
            UseTimerController.INSTANCE,//
            HubController.INSTANCE,//
            IndicationController.INSTANCE, //
            SelectionModeController.INSTANCE,// Selection in last, because it will start scanning
            WinAutoHotKeyKeyboardReceiverController.INSTANCE // Need the "blocked keys" from other modes
    );

    private void launchUseMode(boolean notifyChange) {
        final LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
        final LCConfigurationI configuration = useModeContext.configurationProperty().get();
        final LCConfigurationDescriptionI configurationDescription = useModeContext.configurationDescription.get();

        final LCTask<UseModeScene> startUseMode = new LCTask<>("change.mode.task.title") {
            @Override
            protected UseModeScene call() {
                final UseModeScene useScene = new UseModeScene(configuration);
                useScene.initAll();
                IOHelper.loadUseInformation(configuration);
                USE_MODE_LISTENERS.forEach(modeListenerI -> modeListenerI.modeStart(configuration));
                SessionStatsController.INSTANCE.modeStarted(AppMode.USE, configuration);
                return useScene;
            }
        };
        startUseMode.setOnSucceeded(e -> {
            UseModeStage useModeStage = new UseModeStage(currentProfile, configuration, configurationDescription, startUseMode.getValue());
            useModeContext.initStage(useModeStage);
            useModeStage.show();
            editModeContext.getStage().hide();
        });
        startUseMode.setOnFailed(e -> startEditMode());
        AsyncExecutorController.INSTANCE.addAndExecute(true, notifyChange, startUseMode);
    }

    private void stopModeIfNeeded(AppMode modeToStop) {
        if (modeToStop == AppMode.USE) {
            final LCConfigurationI configuration = useModeContext.configurationProperty().get();
            useModeContext.saveStateBeforeStop();
            if (configuration != null) {
                USE_MODE_LISTENERS.forEach(modeListenerI -> modeListenerI.modeStop(configuration));
                IOHelper.saveUseInformation(configuration);
            }
            SessionStatsController.INSTANCE.modeStopped(AppMode.USE);
            useModeContext.cleanAfterStop();
        }
        if (modeToStop == AppMode.EDIT) {
            SessionStatsController.INSTANCE.modeStopped(AppMode.EDIT);
            editModeContext.cleanAfterStop();
        }
    }
}
