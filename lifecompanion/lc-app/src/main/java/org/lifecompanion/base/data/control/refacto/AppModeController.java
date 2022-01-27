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

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import org.lifecompanion.api.component.definition.LCConfigurationDescriptionI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.api.mode.AppMode;
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
import org.lifecompanion.config.data.component.profile.ProfileConfigSelectionController;
import org.lifecompanion.config.data.component.profile.ProfileConfigStep;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.use.view.scene.ConfigUseScene;
import org.lifecompanion.use.view.scene.UseModeStage;

import java.util.Arrays;
import java.util.List;

public enum AppModeController {
    INSTANCE;

    private final ObjectProperty<AppModeV2> mode;
    private final UseModeContext useModeContext;
    private final EditModeContext editModeContext;

    private String previousUseModeConfigurationID;

    AppModeController() {
        mode = new SimpleObjectProperty<>();
        mode.addListener((obs, ov, nv) -> {
            stopModeIfNeeded(ov);
        });
        this.useModeContext = new UseModeContext();
        this.editModeContext = new EditModeContext();
    }

    public ReadOnlyObjectProperty<AppModeV2> modeProperty() {
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
        return this.mode.get() == AppModeV2.USE;
    }

    public boolean isEditMode() {
        return this.mode.get() == AppModeV2.EDIT;
    }

    public void startEditMode() {
        LCUtils.runOnFXThread(() -> {
            mode.set(AppModeV2.EDIT);
            editModeContext.getStage().show();
            LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
            if (previousUseModeConfigurationID != null && profile != null) {
                final LCConfigurationDescriptionI usedConfigurationDesc = profile.getConfigurationById(previousUseModeConfigurationID);
                LCConfigurationActions.OpenConfigurationAction openConfigAction = new LCConfigurationActions.OpenConfigurationAction(editModeContext.getStage().getScene().getRoot(), usedConfigurationDesc, false, loaded -> {
                    if (!loaded) handleNoConfigInEditMode();
                });
                ConfigActionController.INSTANCE.executeAction(openConfigAction);
            } else {
                handleNoConfigInEditMode();
            }
        });
    }

    private void handleNoConfigInEditMode() {
        ProfileConfigSelectionController.INSTANCE.setStep(ProfileConfigStep.CONFIGURATION_LIST, null);
    }

    public void startUseModeAfterEdit() {
        final LCConfigurationI configuration = editModeContext.configurationProperty().get();
        final LCConfigurationI duplicated = (LCConfigurationI) configuration.duplicate(false);
        LCUtils.runOnFXThread(() -> startUseModeForConfiguration(duplicated, editModeContext.configurationDescriptionProperty().get()));
    }

    public void startUseModeForConfiguration(LCConfigurationI configuration, LCConfigurationDescriptionI configurationDescription) {
        LCUtils.runOnFXThread(() -> {
            this.useModeContext.switchTo(configuration, configurationDescription);
            mode.set(AppModeV2.USE);
            //editModeContext.getStage().hide();
            launchUseMode();
        });
    }

    public void switchEditModeConfiguration(LCConfigurationI configuration, LCConfigurationDescriptionI configurationDescription) {
        LCUtils.runOnFXThread(() -> editModeContext.switchTo(configuration, configurationDescription));
    }

    public void switchUseModeConfiguration(LCConfigurationI configuration, LCConfigurationDescriptionI configurationDescription) {
        LCUtils.runOnFXThread(() -> {
            clearCurrentMode();
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
        });
        startUseMode.setOnFailed(e -> startEditMode());
        AsyncExecutorController.INSTANCE.addAndExecute(true, false, startUseMode);
    }

    private void stopModeIfNeeded(AppModeV2 modeToStop) {
        if (modeToStop == AppModeV2.USE) {
            final LCConfigurationI configuration = useModeContext.configurationProperty().get();
            if (configuration != null) {
                previousUseModeConfigurationID = configuration.getID();
                USE_MODE_LISTENERS.forEach(modeListenerI -> modeListenerI.modeStop(configuration));
                IOManager.INSTANCE.saveUseInformation(configuration);
            }
            SessionStatsController.INSTANCE.modeStopped(AppMode.USE);
            useModeContext.cleanAfterStop();
        }
        if (modeToStop == AppModeV2.EDIT) {
            SessionStatsController.INSTANCE.modeStopped(AppMode.CONFIG);
            editModeContext.cleanAfterStop();
        }
    }
}
