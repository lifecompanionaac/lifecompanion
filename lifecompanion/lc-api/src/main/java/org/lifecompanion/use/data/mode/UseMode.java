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
package org.lifecompanion.use.data.mode;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.lifecompanion.api.component.definition.LCConfigurationDescriptionI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.api.mode.AppMode;
import org.lifecompanion.api.mode.LCModeI;
import org.lifecompanion.api.mode.ModeListenerI;
import org.lifecompanion.api.ui.ViewProviderI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.control.*;
import org.lifecompanion.base.data.control.prediction.AutoCharPredictionController;
import org.lifecompanion.base.data.control.prediction.CustomCharPredictionController;
import org.lifecompanion.base.data.control.prediction.WordPredictionController;
import org.lifecompanion.base.data.control.stats.SessionStatsController;
import org.lifecompanion.base.data.control.virtual.keyboard.impl.WinAutoHotKeyKeyboardReceiverController;
import org.lifecompanion.base.data.control.virtual.keyboard.VirtualKeyboardController;
import org.lifecompanion.base.data.control.virtual.mouse.VirtualMouseController;
import org.lifecompanion.base.data.image2.ImageDictionaries;
import org.lifecompanion.base.data.io.IOManager;
import org.lifecompanion.base.data.media.SoundPlayer;
import org.lifecompanion.base.data.plugins.PluginManager;
import org.lifecompanion.base.data.prediction.LCCharPredictor;
import org.lifecompanion.base.data.prediction.predict4all.predictor.Predict4AllWordPredictor;
import org.lifecompanion.base.data.voice.SAPIVoiceSynthesizer;
import org.lifecompanion.base.data.voice.SayCommandVoiceSynthesizer;
import org.lifecompanion.base.data.voice.VoiceSynthesizerController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.use.data.ui.UseViewProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * The use mode description of LifeCompanion.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseMode implements LCModeI {
    private final static Logger LOGGER = LoggerFactory.getLogger(UseMode.class);

    private final ViewProviderI useViewProvider;
    private final Scene useScene;
    private final ObjectProperty<LCConfigurationI> currentConfiguration;
    private final ObjectProperty<LCConfigurationI> configurationBeforeChange;
    private boolean skipNextModeStart;

    public UseMode(final Scene useSceneP) {
        this.useScene = useSceneP;
        this.useViewProvider = new UseViewProvider();
        this.currentConfiguration = new SimpleObjectProperty<>();
        this.configurationBeforeChange = new SimpleObjectProperty<>();
    }

    // Class part : "Base"
    //========================================================================
    @Override
    public ViewProviderI getViewProvider() {
        return this.useViewProvider;
    }

    @Override
    public Scene initializeAndGetScene() {
        return this.useScene;
    }

    @Override
    public AppMode getMode() {
        return AppMode.USE;
    }

    @Override
    public ObjectProperty<LCConfigurationI> currentConfigurationProperty() {
        return this.currentConfiguration;
    }

    @Override
    public ObjectProperty<LCConfigurationI> configurationBeforeChangeProperty() {
        return this.configurationBeforeChange;
    }
    //========================================================================

    // Class part : "LifeCompanion Start/stop"
    //========================================================================
    @Override
    public void lcStart() {
        //Voice
        UseMode.LOGGER.info("Initialize voices synthesizer in use mode");
        SAPIVoiceSynthesizer synthesizer = new SAPIVoiceSynthesizer();
        VoiceSynthesizerController.INSTANCE.registrerVoiceSynthesizer(synthesizer);
        VoiceSynthesizerController.INSTANCE.setDefaultVoiceSynthesizer(SystemType.WINDOWS, synthesizer);
        SayCommandVoiceSynthesizer sayCommandVoiceSynthesizer = new SayCommandVoiceSynthesizer();
        VoiceSynthesizerController.INSTANCE.registrerVoiceSynthesizer(sayCommandVoiceSynthesizer);
        VoiceSynthesizerController.INSTANCE.setDefaultVoiceSynthesizer(SystemType.MAC, sayCommandVoiceSynthesizer);
        //Prediction
        AutoCharPredictionController.INSTANCE.getAvailablePredictor().add(LCCharPredictor.INSTANCE);
        WordPredictionController.INSTANCE.getAvailablePredictor().add(new Predict4AllWordPredictor());

    }

    @Override
    public void lcExit() {
    }
    //========================================================================

    // Class part : "Mode start/stop"
    //========================================================================
    private static final List<ModeListenerI> useModeListeners = Arrays.asList(//
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

    @SuppressWarnings("deprecation")
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        IOManager.INSTANCE.loadUseInformation(configuration);
        //Fire start
        UseMode.LOGGER.info("Use mode start, will fire to {} component that listen for use mode", UseMode.useModeListeners.size());
        for (ModeListenerI useModeListenerI : UseMode.useModeListeners) {
            useModeListenerI.modeStart(configuration);
        }
        if (!AppController.INSTANCE.isOnEmbeddedDevice()) {
            //Prepare frame if not on embedded device
            LCUtils.runOnFXThread(() -> {
                final Stage mainStage = AppController.INSTANCE.getMainStage();
                LCProfileI currentProfile = AppController.INSTANCE.currentProfileProperty().get();
                LCConfigurationDescriptionI currentConfigDescription = AppController.INSTANCE.currentConfigDescriptionProperty().get();
                mainStage.setTitle(
                        AppController.INSTANCE.getMainStageDefaultTitle() +
                                (currentProfile != null ? " - " + currentProfile.nameProperty().get() : "") +
                                (currentConfigDescription != null ? " - " + currentConfigDescription.configurationNameProperty().get() : "")
                );
                mainStage.setFullScreenExitHint(Translation.getText("fullscreen.exit.hint"));
                mainStage.setIconified(false);
                mainStage.setFullScreen(false);
                mainStage.setMaximized(false);
                mainStage.setAlwaysOnTop(true);
                mainStage.opacityProperty().bind(configuration.frameOpacityProperty());
                if (configuration.fullScreenOnLaunchProperty().get()) {
                    mainStage.setMaximized(true);
                } else {
                    mainStage.setMaximized(false);
                    mainStage.setWidth(configuration.computedFrameWidthProperty().get());
                    mainStage.setHeight(configuration.computedFrameHeightProperty().get());
                    AppController.INSTANCE.moveFrameTo(configuration.framePositionOnLaunchProperty().get());
                }
                //Focusable/always on top state
                if (configuration.virtualKeyboardProperty().get()) {
                    LOGGER.info("Virtual keyboard detected for the stage, will change the focusable state for main stage");
                    LCUtils.setFocusableSafe(mainStage, false);
                }
            });
        }
        SessionStatsController.INSTANCE.modeStarted(AppMode.USE, configuration);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void modeStop(final LCConfigurationI configuration) {
        UseMode.LOGGER.info("Use mode stop, will fire to {} component that listen for use mode", UseMode.useModeListeners.size());
        for (ModeListenerI useModeListenerI : UseMode.useModeListeners) {
            useModeListenerI.modeStop(configuration);
        }

        //Save the use informations to the configuration
        IOManager.INSTANCE.saveUseInformation(configuration);

        //Focusable/always on top state
        if (!AppController.INSTANCE.isOnEmbeddedDevice()) {
            LCUtils.runOnFXThread(() -> {
                AppController.INSTANCE.getMainStage().setAlwaysOnTop(false);
                if (configuration.virtualKeyboardProperty().get()) {
                    LCUtils.setFocusableSafe(AppController.INSTANCE.getMainStage(), true);
                }
            });
        }
        SessionStatsController.INSTANCE.modeStopped(AppMode.USE);
    }

    @Override
    public boolean isSkipNextModeStartAndReset() {
        boolean skip = this.skipNextModeStart;
        this.skipNextModeStart = false;
        return skip;
    }

    public void enableSkipNextModeStart() {
        this.skipNextModeStart = true;
    }
    //========================================================================

}
