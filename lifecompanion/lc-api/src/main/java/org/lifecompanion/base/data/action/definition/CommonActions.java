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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.lifecompanion.api.action.definition.BaseConfigActionI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.mode.AppMode;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.control.AppController;

import java.util.function.BiFunction;

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
            LCConfigurationI configuration = AppController.INSTANCE.currentUseConfigurationProperty().get();
            BiFunction<Node, LCConfigurationI, Boolean> confirmFunction = AppController.INSTANCE.getConfirmConfigurationModeFunction();
            if (this.useConfirmFct && confirmFunction != null && !confirmFunction.apply(source, configuration)) {
                return;
            }
            if (!AppController.INSTANCE.isUseModeOnly()) {
                AppController.INSTANCE.currentModeProperty().set(AppMode.CONFIG);
            }
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
                Stage mainFrame = AppController.INSTANCE.getMainStage();
                mainFrame.setFullScreen(!mainFrame.isFullScreen());
            });
        }

        @Override
        public String getNameID() {
            return "action.switch.fullscreen";
        }
    }
    //========================================================================

}
