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

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.ui.virtualmouse.ScanningMouseStage;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.RobotProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.lifecompanion.controller.configurationcomponent.ConfigListController;

import java.awt.*;

public enum ScanningMouseController implements ModeListenerI {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMouseController.class);
    private static final double TIME_PER_PIXEL = 25.0;

    private static final Interpolator MOVING_INTERPOLATOR = Interpolator.EASE_IN;

    /**
     * Timeline to animate mouse movement
     */
    private final Timeline timeline;


    /**
     * AWT robot to simulate mouse events
     */
    private Robot robot;

    /**
     * Stage to show the virtual mouse
     */
    private ScanningMouseStage scanningMouseStage;

    /**
     * Mouse X position
     */
    private final DoubleProperty mouseX;
    /**
     * Mouse Y position
     */
    private final DoubleProperty mouseY;
    /**
     * Width/height of the frame (max bounds)
     */
    private double frameWidth, frameHeight;

    /**
     * Frame scale (got from AWT : used to correct mouse position)
     */
    private double frameXScale = 1.0, frameYScale = 1.0;

    /**
     * Output scale (got from JFX : used to correct mouse position)
     */
    private double xOutputScale = 1.0, yOutputScale = 1.0;

    private final DoubleProperty timePerPixelSpeed;

    private boolean nextPage = true;


    ScanningMouseController() {
        this.mouseX = new SimpleDoubleProperty();
        this.mouseY = new SimpleDoubleProperty();
        this.timePerPixelSpeed = new SimpleDoubleProperty();
        this.timeline = new Timeline();
        this.timeline.setCycleCount(1);
        this.timeline.setAutoReverse(false);
    }

    public ReadOnlyDoubleProperty mouseXProperty() {
return this.mouseX;
    }

    public ReadOnlyDoubleProperty mouseYProperty() {
        return this.mouseY;
    }

    public void startMovingMouseForX() {
        this.checkInitFrameAndRobot(() -> {
            this.addKeyFrame(this.frameWidth - this.mouseX.get(), this.frameWidth, this.mouseX);
            this.startMoving(this::startMovingMouseForXBack);
        });
    }

    public void startMovingMouseForXBack() {
        this.checkInitFrameAndRobot(() -> {
            this.addKeyFrame(this.mouseX.get(), 0.0, this.mouseX);
            this.startMoving(this::startMovingMouseForX);
        });
    }

    public void startMovingMouseForY() {
        this.checkInitFrameAndRobot(() -> {
            this.addKeyFrame( this.frameHeight - this.mouseY.get(), this.frameHeight, this.mouseY);
            this.startMoving(this::startMovingMouseForYBack);
        });
    }

    public void startMovingMouseForYBack() {
        this.checkInitFrameAndRobot(() -> {
            this.addKeyFrame(this.mouseY.get(), 0.0, this.mouseY);
            this.startMoving(this::startMovingMouseForY);
        });
    }

    private void addKeyFrame(double diff, final double wantedValue, final DoubleProperty property) {
        long totalTime = (long) (diff * this.timePerPixelSpeed.get());
        KeyFrame keyFrameMoveMouse = new KeyFrame(Duration.millis(totalTime),
                new KeyValue(property, wantedValue, MOVING_INTERPOLATOR));

        this.timeline.getKeyFrames().add(keyFrameMoveMouse);
    }

    private void startMoving(Runnable onFinished) {
        this.timeline.setOnFinished(event -> {
            this.timeline.getKeyFrames().clear();
            onFinished.run();
        });
        this.timeline.playFromStart();
    }

    public void stopMovingMouse() {
        this.timeline.stop();
        this.timeline.getKeyFrames().clear();
    }

    private void mouseMoveDirect(double x, double y) {
        this.robot.mouseMove((int) (x * this.xOutputScale / this.frameXScale), (int) (y * this.yOutputScale / this.frameYScale));
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
                FXThreadUtils.runOnFXThread(() -> {
                    callback.run();
                });
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
                this.mouseX.set(0);
                this.mouseY.set(0);
                this.frameToFrontAndFocus();
                callback.run();
            });
        }
    }

    public void startMouseClic() {
        this.checkInitFrameAndRobot(() -> {
            this.scanningMouseStage.getScanningMouseScene().startMouseClic((x, y) -> {
                LOGGER.info("Want to clic at : {}x{}", x, y);
                this.mouseX.set(0);
                this.mouseY.set(0);
                this.mouseMoveDirect(x, y);
                if ( nextPage ) {
                    ConfigListController.INSTANCE.nextPage();
                }
            });
        });
    }


    private void checkRobotInit() {
        if (this.robot == null) {
            this.robot = RobotProvider.getInstance();
            Screen primaryScreen = Screen.getPrimary();
            Rectangle2D primaryScreenBounds = primaryScreen.getBounds();
            this.frameWidth = primaryScreenBounds.getWidth();
            this.frameHeight = primaryScreenBounds.getHeight();
            this.xOutputScale = primaryScreen.getOutputScaleX();
            this.yOutputScale = primaryScreen.getOutputScaleY();
            LOGGER.info("Screen scaling : \n\tAWT : {}x{}\n\tOutput scale : {}x{}", frameXScale, frameYScale, xOutputScale, yOutputScale);
        }
    }
    //========================================================================

    // Class part : "Mode listener"
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.timePerPixelSpeed.bind(Bindings.createDoubleBinding(
                () -> 1.0 / configuration.getVirtualMouseParameters().mouseSpeedProperty().get() * ScanningMouseController.TIME_PER_PIXEL,
                configuration.getVirtualMouseParameters().mouseSpeedProperty()));
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        this.timePerPixelSpeed.unbind();
        this.hideMouseFrame();
    }
    //========================================================================
}

