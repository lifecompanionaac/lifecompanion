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
package org.lifecompanion.base.data.action.definition;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.lifecompanion.api.action.definition.BaseConfigActionI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.control.refacto.AppModeController;
import org.lifecompanion.base.data.control.refacto.StageUtils;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.common.SystemVirtualKeyboardHelper;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.Optional;
import java.util.Random;

import static org.lifecompanion.base.data.common.UIUtils.getSourceFromEvent;

/**
 * Actions shared between configuration and use mode.<br>
 * Can be shared but not used sometimes...
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CommonActions {
    // Class part : "Key shortcut"
    //========================================================================
    public static final KeyCombination KEY_COMBINATION_GO_CONFIG_MODE = new KeyCodeCombination(KeyCode.F5, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCombination KEY_COMBINATION_SWITCH_FULLSCREEN = new KeyCodeCombination(KeyCode.F11);
    //========================================================================

    // Class part : "Handler"
    //========================================================================
    public static final EventHandler<ActionEvent> HANDLER_GO_CONFIG_MODE_CHECK = (ea) -> new GoConfigModeAction(getSourceFromEvent(ea), true).doAction();
    public static final EventHandler<ActionEvent> HANDLER_GO_CONFIG_MODE_SKIP_CHECK = (ea) -> new GoConfigModeAction(getSourceFromEvent(ea), false).doAction();
    public static final EventHandler<ActionEvent> HANDLER_SWITCH_FULLSCREEN = (ea) -> new SwitchFullScreenAction().doAction();
    //========================================================================

    // Class part : "Actions"
    //========================================================================

    /**
     * Action to switch to the config mode if available.
     */
    public static class GoConfigModeAction implements BaseConfigActionI {
        private final Node source;
        private boolean useConfirmFct;

        public GoConfigModeAction(final Node source, final boolean useConfirmFct) {
            this.source = source;
            this.useConfirmFct = useConfirmFct;
        }

        @Override
        public void doAction() {
            LCConfigurationI configuration = AppModeController.INSTANCE.getUseModeContext().configurationProperty().get();

            if (useConfirmFct && configuration.securedConfigurationModeProperty().get()) {
                // Issue #180 - Secure dialog should automatically be closed (can be the user error)
                IntegerProperty timeLeft = new SimpleIntegerProperty(LCConstant.GO_TO_CONFIG_MODE_DELAY);
                Timeline timeLineAutoHide = new Timeline(new KeyFrame(Duration.seconds(1), (e) -> timeLeft.set(timeLeft.get() - 1)));
                timeLineAutoHide.setCycleCount(LCConstant.GO_TO_CONFIG_MODE_DELAY);
                //Generate a 1000 - 9999 code
                Random random = new Random();
                String number = "" + (random.nextInt(8999) + 1000);
                TextInputDialog dialog = ConfigUIUtils.createInputDialog(StageUtils.getEditOrUseStageVisible(), null);
                dialog.headerTextProperty().bind(TranslationFX.getTextBinding("action.confirm.go.config.header", timeLeft));
                dialog.setContentText(Translation.getText("action.confirm.go.config.message", number));
                timeLineAutoHide.setOnFinished(e -> dialog.hide());
                timeLineAutoHide.play();
                SystemVirtualKeyboardHelper.INSTANCE.showIfEnabled();
                Optional<String> enteredString = dialog.showAndWait();
                timeLineAutoHide.stop();
                //Check code
                if (enteredString.isEmpty() || StringUtils.isDifferent(enteredString.get(), number)) {
                    if (enteredString.isPresent()) {
                        Alert warning = ConfigUIUtils.createAlert(source, Alert.AlertType.ERROR);
                        warning.setContentText(Translation.getText("action.confirm.go.config.error"));
                        warning.show();
                    }
                    return;
                }
            }
            AppModeController.INSTANCE.startEditMode();
        }

        @Override
        public String getNameID() {
            return "action.switch.mode";
        }
    }

    public static class SwitchFullScreenAction implements BaseConfigActionI {

        @Override
        public void doAction() {
            LCUtils.runOnFXThread(() -> {
                final Stage stage = AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
                stage.setFullScreen(!stage.isFullScreen());
            });
        }

        @Override
        public String getNameID() {
            return "action.switch.fullscreen";
        }
    }
    //========================================================================

}
