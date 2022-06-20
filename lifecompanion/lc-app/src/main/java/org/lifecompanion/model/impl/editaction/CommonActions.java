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
package org.lifecompanion.model.impl.editaction;

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
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.StageUtils;

import java.util.Optional;
import java.util.Random;

import static org.lifecompanion.util.javafx.FXUtils.getSourceFromEvent;

/**
 * Actions shared between configuration and use mode.<br>
 * Can be shared but not used sometimes...
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CommonActions {
    public static final KeyCombination KEY_COMBINATION_GO_CONFIG_MODE = new KeyCodeCombination(KeyCode.F5, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCombination KEY_COMBINATION_SWITCH_FULLSCREEN = new KeyCodeCombination(KeyCode.F11);

    public static final EventHandler<ActionEvent> HANDLER_GO_CONFIG_MODE_CHECK = (ea) -> new GoEditModeAction(getSourceFromEvent(ea), true).doAction();
    public static final EventHandler<ActionEvent> HANDLER_GO_CONFIG_MODE_SKIP_CHECK = (ea) -> new GoEditModeAction(getSourceFromEvent(ea), false).doAction();
    public static final EventHandler<ActionEvent> HANDLER_SWITCH_FULLSCREEN = (ea) -> new SwitchFullScreenAction().doAction();

    public static class GoEditModeAction implements BaseEditActionI {
        private final Node source;
        private boolean useConfirmFct;

        public GoEditModeAction(final Node source, final boolean useConfirmFct) {
            this.source = source;
            this.useConfirmFct = useConfirmFct;
        }

        @Override
        public void doAction() {
            LCConfigurationI configuration = AppModeController.INSTANCE.getUseModeContext().configurationProperty().get();

            if (useConfirmFct && UserConfigurationController.INSTANCE.secureGoToEditModeProperty().get()) {
                // Issue #180 - Secure dialog should automatically be closed (can be the user error)
                IntegerProperty timeLeft = new SimpleIntegerProperty(LCConstant.GO_TO_CONFIG_MODE_DELAY);
                Timeline timeLineAutoHide = new Timeline(new KeyFrame(Duration.seconds(1), (e) -> timeLeft.set(timeLeft.get() - 1)));
                timeLineAutoHide.setCycleCount(LCConstant.GO_TO_CONFIG_MODE_DELAY);
                //Generate a 1000 - 9999 code
                Random random = new Random();
                String number = "" + (random.nextInt(8999) + 1000);
                final TextInputDialog dialog = DialogUtils
                        .textInputDialogWithSource(StageUtils.getEditOrUseStageVisible())
                        .withContentText(Translation.getText("action.confirm.go.config.message", number))
                        .build();
                dialog.headerTextProperty().bind(TranslationFX.getTextBinding("action.confirm.go.config.header", timeLeft));
                timeLineAutoHide.setOnFinished(e -> dialog.hide());
                timeLineAutoHide.play();
                SystemVirtualKeyboardController.INSTANCE.showIfEnabled();
                Optional<String> enteredString = dialog.showAndWait();
                timeLineAutoHide.stop();
                //Check code
                if (enteredString.isEmpty() || StringUtils.isDifferent(enteredString.get(), number)) {
                    if (enteredString.isPresent()) {
                        DialogUtils.alertWithSourceAndType(source, Alert.AlertType.ERROR).withContentText(Translation.getText("action.confirm.go.config.error")).show();
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

    public static class SwitchFullScreenAction implements BaseEditActionI {

        @Override
        public void doAction() {
            FXThreadUtils.runOnFXThread(() -> {
                final Stage stage = AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
                boolean fullScreen = !stage.isFullScreen();
                stage.setFullScreen(fullScreen);
                if (!fullScreen && StageUtils.isStageOutOfScreenBounds(stage)) {
                    stage.setMaximized(true);
                }
            });
        }

        @Override
        public String getNameID() {
            return "action.switch.fullscreen";
        }
    }
}
