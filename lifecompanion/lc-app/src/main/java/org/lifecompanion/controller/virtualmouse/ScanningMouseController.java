/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.controller.virtualmouse;

import javafx.stage.Stage;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.ui.virtualmouse.ScanningMouseStage;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.RobotProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public enum ScanningMouseController implements ModeListenerI {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMouseController.class);

    /**
     * AWT robot to simulate mouse events
     */
    private Robot robot;

    /**
     * Stage to show the virtual mouse
     */
    private ScanningMouseStage scanningMouseStage;

    ScanningMouseController() {
    }

    private void frameToFrontAndFocus() {
        //mouse stage and main frame to front
        FXThreadUtils.runOnFXThread(() -> {
            Stage useStage = AppModeController.INSTANCE.getUseModeContext().getStage();
            if (useStage != null) useStage.toFront();
            this.scanningMouseStage.toFront();
            // Issue #129 : main stage should not be focused back if it's a virtual keyboard
            if (!AppModeController.INSTANCE.getUseModeContext().getConfiguration().virtualKeyboardProperty().get() && useStage != null) {
                useStage.requestFocus();
            }
        });
    }

    public void hideMouseFrame() {
        if (this.scanningMouseStage != null) {
            FXThreadUtils.runOnFXThread(() -> this.scanningMouseStage.hide());
        }
    }

    private void checkInitFrameAndRobot(final Runnable callback) {
        if (this.scanningMouseStage != null) {
            if (this.scanningMouseStage.isShowing()) {
                callback.run();
            } else {
                FXThreadUtils.runOnFXThread(() -> {
                    this.scanningMouseStage.show();
                    this.frameToFrontAndFocus();
                    callback.run();
                });
            }
        } else {
            this.checkRobotInit();
            FXThreadUtils.runOnFXThread(() -> {
                this.scanningMouseStage = ScanningMouseStage.getInstance();
                this.scanningMouseStage.show();
                this.frameToFrontAndFocus();
                callback.run();
            });
        }
    }

    public void startMouseClic() {
        this.checkInitFrameAndRobot(() -> {
            this.scanningMouseStage.getScanningMouseScene().startMouseClic((x, y) -> {
                LOGGER.info("Want to clic at : {}x{}", x, y);
            });
        });
    }


    private void checkRobotInit() {
        if (this.robot == null) {
            this.robot = RobotProvider.getInstance();
        }
    }
    //========================================================================

    // Class part : "Mode listener"
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        this.hideMouseFrame();
    }
    //========================================================================
}

