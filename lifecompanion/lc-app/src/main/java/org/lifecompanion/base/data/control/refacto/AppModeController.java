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

package org.lifecompanion.base.data.control.refacto;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import org.lifecompanion.api.component.definition.LCConfigurationDescriptionI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.api.mode.ModeListenerI;
import org.lifecompanion.base.data.common.LCTask;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.control.*;
import org.lifecompanion.base.data.control.prediction.AutoCharPredictionController;
import org.lifecompanion.base.data.control.prediction.CustomCharPredictionController;
import org.lifecompanion.base.data.control.prediction.WordPredictionController;
import org.lifecompanion.base.data.control.stats.SessionStatsController;
import org.lifecompanion.base.data.control.virtual.keyboard.VirtualKeyboardController;
import org.lifecompanion.base.data.control.virtual.keyboard.impl.WinAutoHotKeyKeyboardReceiverController;
import org.lifecompanion.base.data.control.virtual.mouse.VirtualMouseController;
import org.lifecompanion.base.data.image2.ImageDictionaries;
import org.lifecompanion.base.data.io.IOManager;
import org.lifecompanion.base.data.media.SoundPlayer;
import org.lifecompanion.base.data.plugins.PluginManager;
import org.lifecompanion.base.data.voice.VoiceSynthesizerController;
import org.lifecompanion.config.data.action.impl.LCConfigurationActions;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.use.view.scene.ConfigUseScene;
import org.lifecompanion.use.view.scene.UseModeStage;

import java.util.Arrays;
import java.util.List;

public enum AppModeController {
    INSTANCE;

    private final ObjectProperty<AppMode> mode;
    private final UseModeContext useModeContext;
    private final EditModeContext editModeContext;

    AppModeController() {
        mode = new SimpleObjectProperty<>();
        mode.addListener((obs, ov, nv) -> {
            stopModeIfNeeded(ov);
        });
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
        LCUtils.runOnFXThread(() -> {
            final LCConfigurationI usedConfiguration = useModeContext.getConfiguration();
            mode.set(AppMode.EDIT);
            editModeContext.getStage().show();
            LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
            final LCConfigurationI previousConfigurationEditMode = editModeContext.getPreviousConfiguration();
            // Load previously edited configuration : just restore as current configuration
            if (previousConfigurationEditMode != null) {
                editModeContext.switchTo(previousConfigurationEditMode, editModeContext.getPreviousConfigurationDescription());
            }
            // There is no previously edited  configuration this happens when
            // - user launch LifeCompanion directly in use mode
            // - user go to another configuration in use mode (with ChangeConfigurationAction)
            else if (usedConfiguration != null && profile != null) {
                final LCConfigurationDescriptionI usedConfigurationDesc = profile.getConfigurationById(usedConfiguration.getID());
                ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.OpenConfigurationAction(editModeContext.getStage().getScene().getRoot(), usedConfigurationDesc, false));
            }
            editModeContext.clearPreviouslyEditedConfiguration();
        });
    }

    public void startUseModeAfterEdit() {
        LCUtils.runOnFXThread(() -> startUseModeForConfiguration((LCConfigurationI) editModeContext.getConfiguration().duplicate(false), editModeContext.configurationDescriptionProperty().get()));
    }

    public void startUseModeForConfiguration(LCConfigurationI configuration, LCConfigurationDescriptionI configurationDescription) {
        LCUtils.runOnFXThread(() -> {
            this.useModeContext.switchTo(configuration, configurationDescription);
            mode.set(AppMode.USE);
            launchUseMode();
        });
    }

    public void switchEditModeConfiguration(LCConfigurationI configuration, LCConfigurationDescriptionI configurationDescription) {
        LCUtils.runOnFXThread(() -> editModeContext.switchTo(configuration, configurationDescription));
    }

    public void closeEditModeConfiguration() {
        switchEditModeConfiguration(null, null);
    }

    public void switchUseModeConfiguration(LCConfigurationI configuration, LCConfigurationDescriptionI configurationDescription) {
        LCUtils.runOnFXThread(() -> {
            clearCurrentMode();// this will allow stop then start on use mode
            startUseModeForConfiguration(configuration, configurationDescription);
        });
    }

    void clearCurrentMode() {
        mode.set(null);
    }

    private static final List<ModeListenerI> USE_MODE_LISTENERS = Arrays.asList(//
            PluginManager.INSTANCE,
            WordPredictionController.INSTANCE, //
            VirtualKeyboardController.INSTANCE, //
            VirtualMouseController.INSTANCE, //
            WritingStateController.INSTANCE, //
            CustomCharPredictionController.INSTANCE, //
            AutoCharPredictionController.INSTANCE, //
            UserActionController.INSTANCE, //
            UseVariableController.INSTANCE, //
            KeyListController.INSTANCE, //
            UserActionSequenceController.INSTANCE, //
            UseModeProgressDisplayerController.INSTANCE, //
            SoundPlayer.INSTANCE, //
            VoiceSynthesizerController.INSTANCE, //
            NoteKeyController.INSTANCE, //
            ImageDictionaries.INSTANCE,//
            GlobalKeyEventManager.INSTANCE,//
            WinAutoHotKeyKeyboardReceiverController.INSTANCE, //
            SelectionModeController.INSTANCE//Selection in last, because it will start scanning
    );

    private void launchUseMode() {
        final LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
        final LCConfigurationI configuration = useModeContext.configurationProperty().get();
        final LCConfigurationDescriptionI configurationDescription = useModeContext.configurationDescription.get();

        final LCTask<ConfigUseScene> startUseMode = new LCTask<>("change.mode.task.title") {
            @Override
            protected ConfigUseScene call() {
                final ConfigUseScene useScene = new ConfigUseScene(configuration);
                useScene.initAll();
                IOManager.INSTANCE.loadUseInformation(configuration);
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
        AsyncExecutorController.INSTANCE.addAndExecute(true, false, startUseMode);
    }

    private void stopModeIfNeeded(AppMode modeToStop) {
        if (modeToStop == AppMode.USE) {
            final LCConfigurationI configuration = useModeContext.configurationProperty().get();
            if (configuration != null) {
                USE_MODE_LISTENERS.forEach(modeListenerI -> modeListenerI.modeStop(configuration));
                IOManager.INSTANCE.saveUseInformation(configuration);
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
