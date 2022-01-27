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

package org.lifecompanion.base.data.control.virtual.mouse;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.lifecompanion.api.component.definition.FramePosition;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.VirtualMouseDrawing;
import org.lifecompanion.api.mode.ModeListenerI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.control.refacto.AppModeController;
import org.lifecompanion.base.data.control.refacto.StageUtils;
import org.lifecompanion.base.data.control.virtual.RobotProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * Controller to simulate mouse event on a configuration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum VirtualMouseController implements ModeListenerI {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMouseController.class);

    private static final double TIME_PER_PIXEL = 25.0;

    private static final int NUMBER_OF_FRAME_CHECK = 10;

    private static final Interpolator MOVING_INTERPOLATOR = Interpolator.EASE_IN;

    private static final int MOUSE_ACTION_DELAY = 40;

    private static final int MOUSE_SCROLL_DELAY = 20;

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
    private VirtualMouseStage virtualMouseStage;

    /**
     * Mouse position
     */
    private final DoubleProperty mouseX;
    private final DoubleProperty mouseY;

    /**
     * Width/height of the frame (max bounds)
     */
    private double frameWidth, frameHeight;

    /**
     * Frame scale (got from AWT : used to fixe mouse position)
     */
    private double frameXScale = 1.0, frameYScale = 1.0;

    /**
     * Property bounds on configuration virtual mouse parameters
     */
    private final DoubleProperty sizeScale;
    private final DoubleProperty timePerPixelSpeed;

    /**
     * View color
     */
    private final ObjectProperty<Color> color, strokeColor;

    /**
     * Mouse drawing
     */
    private final ObjectProperty<VirtualMouseDrawing> mouseDrawing;

    /**
     * To check if the mouse position is not on the main frame
     */
    private final EventHandler<ActionEvent> checkFramePosition;

    VirtualMouseController() {
        this.mouseX = new SimpleDoubleProperty();
        this.mouseY = new SimpleDoubleProperty();
        this.sizeScale = new SimpleDoubleProperty();
        this.timePerPixelSpeed = new SimpleDoubleProperty();
        this.color = new SimpleObjectProperty<>();
        this.strokeColor = new SimpleObjectProperty<>();
        this.mouseDrawing = new SimpleObjectProperty<>();
        this.timeline = new Timeline();
        this.timeline.setCycleCount(1);
        this.timeline.setAutoReverse(false);
        //Check frame position
        this.checkFramePosition = (event) -> {
            checkFramePositionWithMouse();
        };
    }

    private void checkFramePositionWithMouse() {
        Rectangle2D frameBounds = this.getMainFrameBounds();
        if (frameBounds.contains(this.mouseX.get(), this.mouseY.get())) {
            this.moveFrameToAvoidMouse();
        }
    }

    // Class part : "Properties"
    //========================================================================
    public ReadOnlyDoubleProperty mouseXProperty() {
        return this.mouseX;
    }

    public ReadOnlyDoubleProperty mouseYProperty() {
        return this.mouseY;
    }

    public ReadOnlyDoubleProperty sizeScaleProperty() {
        return this.sizeScale;
    }

    public ReadOnlyObjectProperty<Color> colorProperty() {
        return this.color;
    }

    public ReadOnlyObjectProperty<Color> strokeColorProperty() {
        return this.strokeColor;
    }

    public ReadOnlyObjectProperty<VirtualMouseDrawing> mouseDrawingProperty() {
        return this.mouseDrawing;
    }
    //========================================================================

    // Class part : "Moving API"
    //========================================================================
    public void startMovingMouseTop() {
        this.checkInitFrameAndRobot(() -> {
            this.addKeyFrame(this.mouseY.get(), 0.0, this.mouseY);
            this.startMoving();
        });
    }

    public void startMovingMouseBottom() {
        this.checkInitFrameAndRobot(() -> {
            this.addKeyFrame(this.frameHeight - this.mouseY.get(), this.frameHeight, this.mouseY);
            this.startMoving();
        });
    }

    public void startMovingMouseRight() {
        this.checkInitFrameAndRobot(() -> {
            this.addKeyFrame(this.frameWidth - this.mouseX.get(), this.frameWidth, this.mouseX);
            this.startMoving();
        });
    }

    public void startMovingMouseLeft() {
        this.checkInitFrameAndRobot(() -> {
            this.addKeyFrame(this.mouseX.get(), 0.0, this.mouseX);
            this.startMoving();
        });
    }

    public void startMovingMouseTopLeft() {
        this.checkInitFrameAndRobot(() -> {
            double diff = Math.max(this.mouseX.get(), this.mouseY.get());
            this.addKeyFrame(diff, 0.0, this.mouseX);
            this.addKeyFrame(diff, 0.0, this.mouseY);
            this.startMoving();
        });
    }

    public void startMovingMouseTopRight() {
        this.checkInitFrameAndRobot(() -> {
            double diff = Math.max(this.frameWidth - this.mouseX.get(), this.mouseY.get());
            this.addKeyFrame(diff, this.frameWidth, this.mouseX);
            this.addKeyFrame(diff, 0.0, this.mouseY);
            this.startMoving();
        });
    }

    public void startMovingMouseBottomRight() {
        this.checkInitFrameAndRobot(() -> {
            double diff = Math.max(this.frameWidth - this.mouseX.get(), this.frameHeight - this.mouseY.get());
            this.addKeyFrame(diff, this.frameWidth, this.mouseX);
            this.addKeyFrame(diff, this.frameHeight, this.mouseY);
            this.startMoving();
        });
    }

    public void startMovingMouseBottomLeft() {
        this.checkInitFrameAndRobot(() -> {
            double diff = Math.max(this.mouseX.get(), this.frameHeight - this.mouseY.get());
            this.addKeyFrame(diff, 0.0, this.mouseX);
            this.addKeyFrame(diff, this.frameHeight, this.mouseY);
            this.startMoving();
        });
    }

    public void hideMouseFrame() {
        if (this.virtualMouseStage != null) {
            LCUtils.runOnFXThread(() -> this.virtualMouseStage.hide());
        }
    }

    private void startMoving() {
        this.timeline.playFromStart();
    }

    private void addKeyFrame(final double diff, final double wantedValue, final DoubleProperty property) {
        long totalTime = (long) (diff * this.timePerPixelSpeed.get());
        KeyFrame keyFrameMoveMouse = new KeyFrame(Duration.millis(totalTime),
                new KeyValue(property, wantedValue, VirtualMouseController.MOVING_INTERPOLATOR));
        //Check X times during the animation
        long verificationTime = totalTime / VirtualMouseController.NUMBER_OF_FRAME_CHECK;
        for (int i = 0; i < VirtualMouseController.NUMBER_OF_FRAME_CHECK; i++) {
            this.timeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * verificationTime), this.checkFramePosition));
        }
        this.timeline.getKeyFrames().add(keyFrameMoveMouse);
    }

    public void stopMovingMouse() {
        this.timeline.stop();
        this.timeline.getKeyFrames().clear();
    }
    //========================================================================

    // Class part : "Clic API"
    //========================================================================
    public void executePrimaryMouseClic() {
        this.checkInitFrameAndRobot(() -> {
            this.moveMouseToWithDelay(this.mouseX.get(), this.mouseY.get());
            this.executeMouseClic(MouseEvent.BUTTON1);
            this.frameToFrontAndFocus();
        });
    }

    public void executeDoubleMouseClic() {
        this.checkInitFrameAndRobot(() -> {
            this.moveMouseToWithDelay(this.mouseX.get(), this.mouseY.get());
            this.executeMouseClic(MouseEvent.BUTTON1);
            this.executeMouseClic(MouseEvent.BUTTON1);
            this.frameToFrontAndFocus();
        });
    }

    public void executeSecondaryMouseClic() {
        this.checkInitFrameAndRobot(() -> {
            this.moveMouseToWithDelay(this.mouseX.get(), this.mouseY.get());
            this.executeMouseClic(MouseEvent.BUTTON3);
            this.frameToFrontAndFocus();
        });
    }

    public void executeMouseWheelDown(final int amount) {
        this.checkRobotInit();
        this.executeMouseWheel(amount);
    }

    public void executeMouseWheelUp(final int amount) {
        this.checkRobotInit();
        this.executeMouseWheel(-amount);
    }

    private void frameToFrontAndFocus() {
        //mouse stage and main frame to front
        LCUtils.runOnFXThread(() -> {
            AppModeController.INSTANCE.getUseModeContext().stageProperty().get().toFront();
            this.virtualMouseStage.toFront();
            AppModeController.INSTANCE.getUseModeContext().stageProperty().get().requestFocus();
            this.centerMouseOnStage();
        });
    }
    //========================================================================

    // Class part : "Internal mouse event API"
    //========================================================================
    private void moveMouseToWithDelay(final double x, final double y) {
        mouseMoveDirect(x, y);
        this.robot.delay(VirtualMouseController.MOUSE_ACTION_DELAY);
    }

    private void mouseMoveDirect(double x, double y) {
        this.robot.mouseMove((int) (x / this.frameXScale), (int) (y / this.frameYScale));
    }

    private void executeMouseClic(final int mouseButton) {
        this.robot.mousePress(InputEvent.getMaskForButton(mouseButton));
        this.robot.delay(VirtualMouseController.MOUSE_ACTION_DELAY);
        this.robot.mouseRelease(InputEvent.getMaskForButton(mouseButton));
    }

    /**
     * Simulate a mouse wheel.</br>
     * Code explanation : mouse wheel can be consumed by a background frame only if the mouse is on this frame, so to ensure that the mouse will probably be on the frame, we set the
     * mouse to the center or beside the center if the frame is on the center.</br>
     * This can sometimes not work and be dirty, but that the only pure Java solution by now...
     *
     * @param amount amount of wheel move
     */
    private void executeMouseWheel(final int amount) {
        final Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        final Stage stage = AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
        double x = screenBounds.getWidth() / 2.0, y = screenBounds.getHeight() / 2.0;
        while (x > 0 && x > y && new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).contains(x, y)) {
            x -= 10.0;
            y -= 10.0;
        }
        if (x > 0 && y > 0) {
            mouseMoveDirect(x, y);
            this.robot.delay(MOUSE_SCROLL_DELAY);
            this.robot.mouseWheel(amount);
            centerMouseOnStage();
            LOGGER.info("Found a position where the mouse can be set to scroll : {}x{}", x, y);
        }
    }


    public void centerMouseOnStage() {
        this.checkRobotInit();
        Stage stage = AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
        double x = stage.getX() + stage.getWidth() / 2.0;
        double y = stage.getY() + stage.getHeight() / 2.0;
        this.moveMouseToWithDelay(x, y);
    }

    private void moveFrameToAvoidMouse() {
        //Dirty but optimized : other rectangles are not created when not needed
        LOGGER.info("Contains : {},{} = {},{}", this.frameWidth / 2, this.frameHeight / 2, this.mouseX.get(), this.mouseY.get());
        //Top left
        if (new Rectangle2D(0, 0, this.frameWidth / 2, this.frameHeight / 2).contains(this.mouseX.get(), this.mouseY.get())) {
            StageUtils.moveStageTo(AppModeController.INSTANCE.getEditModeContext().getStage(), FramePosition.BOTTOM_RIGHT);
        }
        //Top right
        else if (new Rectangle2D(this.frameWidth / 2, 0, this.frameWidth / 2, this.frameHeight / 2).contains(this.mouseX.get(), this.mouseY.get())) {
            StageUtils.moveStageTo(AppModeController.INSTANCE.getEditModeContext().getStage(), FramePosition.BOTTOM_LEFT);
        }
        //Bottom right
        else if (new Rectangle2D(this.frameWidth / 2, this.frameHeight / 2, this.frameWidth / 2, this.frameHeight / 2).contains(this.mouseX.get(),
                this.mouseY.get())) {
            StageUtils.moveStageTo(AppModeController.INSTANCE.getEditModeContext().getStage(), FramePosition.TOP_LEFT);
        }
        //Bottom left
        else if (new Rectangle2D(0, this.frameHeight / 2, this.frameWidth / 2, this.frameHeight / 2).contains(this.mouseX.get(), this.mouseY.get())) {
            StageUtils.moveStageTo(AppModeController.INSTANCE.getEditModeContext().getStage(), FramePosition.TOP_RIGHT);
        }
    }

    /**
     * Initialize mouse stage.
     */
    private void checkInitFrameAndRobot(final Runnable callback) {
        if (this.virtualMouseStage != null) {
            if (this.virtualMouseStage.isShowing()) {
                callback.run();
            } else {
                LCUtils.runOnFXThread(() -> {
                    this.virtualMouseStage.show();
                    this.frameToFrontAndFocus();
                    callback.run();
                });
            }
        } else {
            this.checkRobotInit();
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
            this.frameWidth = primaryScreenBounds.getWidth();
            this.frameHeight = primaryScreenBounds.getHeight();

            LCUtils.runOnFXThread(() -> {
                this.virtualMouseStage = VirtualMouseStage.getInstance();
                this.virtualMouseStage.show();
                final Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                this.mouseX.set(screenBounds.getWidth() / 2.0);
                this.mouseY.set(screenBounds.getHeight() / 2.0);
                this.frameToFrontAndFocus();
                this.checkFramePositionWithMouse();
                callback.run();
            });
        }
    }


    private void checkRobotInit() {
        if (this.robot == null) {
            this.robot = RobotProvider.getInstance();
            try {
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                if (ge != null) {
                    final AffineTransform defaultTransform = ge.getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform();
                    if (defaultTransform != null) {
                        this.frameXScale = defaultTransform.getScaleX();
                        this.frameYScale = defaultTransform.getScaleY();
                        LOGGER.info("Got screen scaling factor to fix mouse positions: {}x{}", frameXScale, frameYScale);
                    }
                }
            } catch (Throwable t) {
                LOGGER.warn("Couldn't get default screen scaling factor", t);
            }
        }
    }

    private Rectangle2D getMainFrameBounds() {
        Stage stage = AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
        return new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
    }
    //========================================================================

    // Class part : "Mode listener"
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.sizeScale.bind(configuration.getVirtualMouseParameters().mouseSizeProperty().divide(10.0));
        this.timePerPixelSpeed.bind(Bindings.createDoubleBinding(
                () -> 1.0 / configuration.getVirtualMouseParameters().mouseSpeedProperty().get() * VirtualMouseController.TIME_PER_PIXEL,
                configuration.getVirtualMouseParameters().mouseSpeedProperty()));
        this.color.bind(configuration.getVirtualMouseParameters().mouseColorProperty());
        this.strokeColor.bind(configuration.getVirtualMouseParameters().mouseStrokeColorProperty());
        this.mouseDrawing.bind(configuration.getVirtualMouseParameters().mouseDrawingProperty());
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        this.sizeScale.unbind();
        this.timePerPixelSpeed.unbind();
        this.color.unbind();
        this.strokeColor.unbind();
        this.mouseDrawing.unbind();
        this.hideMouseFrame();
    }
    //========================================================================
}
