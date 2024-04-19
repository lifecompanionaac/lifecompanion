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

package org.lifecompanion.controller.virtualmouse;

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
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.DirectionalMouseDrawing;
import org.lifecompanion.model.api.configurationcomponent.VirtualMouseType;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.ui.virtualmouse.DirectionalMouseStage;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.StageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.MouseEvent;

/**
 * Controller to simulate mouse event on a configuration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum DirectionalMouseController implements ModeListenerI {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectionalMouseController.class);

    private static final double TIME_PER_PIXEL = 25.0;

    private static final int NUMBER_OF_FRAME_CHECK = 10;

    private static final Interpolator MOVING_INTERPOLATOR = Interpolator.EASE_IN;

    /**
     * Stage to show the virtual mouse
     */
    private DirectionalMouseStage directionalMouseStage;
    private LCConfigurationI configuration;

    /**
     * Timeline to animate mouse movement
     */
    private final Timeline timeline;

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
     * Property bounds on configuration virtual mouse parameters
     */
    private final DoubleProperty sizeScale;
    private final DoubleProperty timePerPixelSpeed;

    /**
     * View color
     */
    private final ObjectProperty<Color> color, strokeColor;

    /**
     * Type of mouse drawing
     */
    private final ObjectProperty<VirtualMouseType> typeMouseDrawing;


    /**
     * Mouse drawing
     */
    private final ObjectProperty<DirectionalMouseDrawing> mouseDrawing;

    /**
     * To check if the mouse position is not on the main frame
     */
    private final EventHandler<ActionEvent> checkFramePosition;

    DirectionalMouseController() {
        this.mouseX = new SimpleDoubleProperty();
        this.mouseY = new SimpleDoubleProperty();
        this.sizeScale = new SimpleDoubleProperty();
        this.timePerPixelSpeed = new SimpleDoubleProperty();
        this.color = new SimpleObjectProperty<>();
        this.strokeColor = new SimpleObjectProperty<>();
        this.timeline = new Timeline();
        this.timeline.setCycleCount(1);
        this.timeline.setAutoReverse(false);
        this.typeMouseDrawing = new SimpleObjectProperty<>();
        this.mouseDrawing = new SimpleObjectProperty<>();
        //Check frame position
        this.checkFramePosition = event -> checkFramePositionWithMouse();
    }

    private void checkFramePositionWithMouse() {
        Rectangle2D frameBounds = VirtualMouseController.INSTANCE.getStageBoundsRelativeCurrentScreen();
        if (frameBounds.contains(this.mouseX.get(), this.mouseY.get())) {
            VirtualMouseController.INSTANCE.moveFrameToAvoidMouse(this.frameWidth, this.frameHeight, this.mouseX.get(), this.mouseY.get());
        }
    }

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

    public ReadOnlyObjectProperty<DirectionalMouseDrawing> mouseDrawingProperty() {
        return this.mouseDrawing;
    }

    public void startMovingMouseTop() {
        if (isValidAction()) {
            this.checkInitFrame(() -> {
                this.addKeyFrame(this.mouseY.get(), 0.0, this.mouseY);
                this.startMoving();
            });
        }
    }

    public void startMovingMouseBottom() {
        if (isValidAction()) {
            this.checkInitFrame(() -> {
                this.addKeyFrame(this.frameHeight - this.mouseY.get(), this.frameHeight, this.mouseY);
                this.startMoving();
            });
        }
    }

    public void startMovingMouseRight() {
        if (isValidAction()) {
            this.checkInitFrame(() -> {
                this.addKeyFrame(this.frameWidth - this.mouseX.get(), this.frameWidth, this.mouseX);
                this.startMoving();
            });
        }
    }

    public void startMovingMouseLeft() {
        if (isValidAction()) {
            this.checkInitFrame(() -> {
                this.addKeyFrame(this.mouseX.get(), 0.0, this.mouseX);
                this.startMoving();
            });
        }
    }

    public void startMovingMouseTopLeft() {
        if (isValidAction()) {
            this.checkInitFrame(() -> {
                double diff = Math.max(this.mouseX.get(), this.mouseY.get());
                this.addKeyFrame(diff, 0.0, this.mouseX);
                this.addKeyFrame(diff, 0.0, this.mouseY);
                this.startMoving();
            });
        }
    }

    public void startMovingMouseTopRight() {
        if (isValidAction()) {
            this.checkInitFrame(() -> {
                double diff = Math.max(this.frameWidth - this.mouseX.get(), this.mouseY.get());
                this.addKeyFrame(diff, this.frameWidth, this.mouseX);
                this.addKeyFrame(diff, 0.0, this.mouseY);
                this.startMoving();
            });
        }
    }

    public void startMovingMouseBottomRight() {
        if (isValidAction()) {
            this.checkInitFrame(() -> {
                double diff = Math.max(this.frameWidth - this.mouseX.get(), this.frameHeight - this.mouseY.get());
                this.addKeyFrame(diff, this.frameWidth, this.mouseX);
                this.addKeyFrame(diff, this.frameHeight, this.mouseY);
                this.startMoving();
            });
        }
    }

    public void startMovingMouseBottomLeft() {
        if (isValidAction()) {
            this.checkInitFrame(() -> {
                double diff = Math.max(this.mouseX.get(), this.frameHeight - this.mouseY.get());
                this.addKeyFrame(diff, 0.0, this.mouseX);
                this.addKeyFrame(diff, this.frameHeight, this.mouseY);
                this.startMoving();
            });
        }
    }

    public void hideMouseFrame() {
        if (this.directionalMouseStage != null) {
            FXThreadUtils.runOnFXThread(() -> this.directionalMouseStage.hide());
        }
    }

    private void startMoving() {
        this.timeline.playFromStart();
        SelectionModeController.INSTANCE.pauseCurrentScanningUntilNextSelection(() -> {
            DirectionalMouseController.INSTANCE.stopMovingMouse();
            return false;
        });
    }

    private void addKeyFrame(final double diff, final double wantedValue, final DoubleProperty property) {
        long totalTime = (long) (diff * this.timePerPixelSpeed.get());
        KeyFrame keyFrameMoveMouse = new KeyFrame(Duration.millis(totalTime),
                new KeyValue(property, wantedValue, DirectionalMouseController.MOVING_INTERPOLATOR));
        //Check X times during the animation
        long verificationTime = totalTime / DirectionalMouseController.NUMBER_OF_FRAME_CHECK;
        for (int i = 0; i < DirectionalMouseController.NUMBER_OF_FRAME_CHECK; i++) {
            this.timeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * verificationTime), this.checkFramePosition));
        }
        this.timeline.getKeyFrames().add(keyFrameMoveMouse);
    }

    public void stopMovingMouse() {
        this.timeline.stop();
        this.timeline.getKeyFrames().clear();
    }

    public void executePrimaryMouseClic() {
        if (isCurrentType()) {
            this.checkInitFrame(() -> {
                VirtualMouseController.INSTANCE.moveMouseRelativeScreen(this.mouseX.get(), this.mouseY.get());
                VirtualMouseController.INSTANCE.pauseBeforeNext();
                VirtualMouseController.INSTANCE.executeMouseClic(MouseEvent.BUTTON1);
                this.frameToFrontAndFocus();
            });
        }

    }

    public void executeDoubleMouseClic() {
        if (isValidAction()) {
            this.checkInitFrame(() -> {
                VirtualMouseController.INSTANCE.moveMouseRelativeScreen(this.mouseX.get(), this.mouseY.get());
                VirtualMouseController.INSTANCE.pauseBeforeNext();
                VirtualMouseController.INSTANCE.executeMouseClic(MouseEvent.BUTTON1);
                VirtualMouseController.INSTANCE.pauseBeforeNext();
                VirtualMouseController.INSTANCE.executeMouseClic(MouseEvent.BUTTON1);
                this.frameToFrontAndFocus();
            });
        }
    }

    public void executeSecondaryMouseClic() {
        if (isValidAction()) {
            this.checkInitFrame(() -> {
                VirtualMouseController.INSTANCE.moveMouseRelativeScreen(this.mouseX.get(), this.mouseY.get());
                VirtualMouseController.INSTANCE.pauseBeforeNext();
                VirtualMouseController.INSTANCE.executeMouseClic(MouseEvent.BUTTON3);
                this.frameToFrontAndFocus();
            });
        }
    }

    public void pressMouseMiddleClicWithoutNoVirtualPosition() {
        if (isValidAction()) {
            VirtualMouseController.INSTANCE.executeMouseClic(MouseEvent.BUTTON2);
        }
    }

    private void frameToFrontAndFocus() {
        //mouse stage and main frame to front
        FXThreadUtils.runOnFXThread(() -> {
            Stage useStage = AppModeController.INSTANCE.getUseModeContext().getStage();
            if (useStage != null) useStage.toFront();
            this.directionalMouseStage.toFront();
            // Issue #129 : main stage should not be focused back if it's a virtual keyboard
            if (!AppModeController.INSTANCE.getUseModeContext().getConfiguration().virtualKeyboardProperty().get() && useStage != null) {
                useStage.requestFocus();
            }
            VirtualMouseController.INSTANCE.centerMouseOnStage();
        });
    }

    /**
     * Initialize mouse stage.
     */
    private void checkInitFrame(final Runnable callback) {
        if (this.directionalMouseStage != null) {
            if (this.directionalMouseStage.isShowing()) {
                callback.run();
            } else {
                FXThreadUtils.runOnFXThread(() -> {
                    this.directionalMouseStage.show();
                    this.frameToFrontAndFocus();
                    callback.run();
                });
            }
        } else {
            FXThreadUtils.runOnFXThread(() -> {
                this.directionalMouseStage = DirectionalMouseStage.getInstance();
                this.directionalMouseStage.show();
                final Rectangle2D screenBounds = VirtualMouseController.INSTANCE.getGraphicContext().getJfxBounds();
                this.mouseX.set(screenBounds.getWidth() / 2.0);
                this.mouseY.set(screenBounds.getHeight() / 2.0);
                this.frameToFrontAndFocus();
                this.checkFramePositionWithMouse();
                callback.run();
            });
        }
    }

    private boolean isValidAction() {
        return isCurrentType() && checkIfVirtualMouseEnabled();
    }

    private boolean isCurrentType() {
        return this.configuration.getVirtualMouseParameters().virtualMouseTypeProperty().get() == VirtualMouseType.DIRECTIONAL;
    }

    private boolean checkIfVirtualMouseEnabled() {
        boolean enabled = !GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_VIRTUAL_MOUSE);
        if (!enabled) {
            LOGGER.info("Ignored virtual mouse action because {} is enabled", GlobalRuntimeConfiguration.DISABLE_VIRTUAL_MOUSE);
        }
        return enabled;
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.configuration = configuration;
        GraphicContext graphicContext = VirtualMouseController.INSTANCE.getGraphicContext();
        this.frameWidth = graphicContext.getJfxBounds().getWidth();
        this.frameHeight = graphicContext.getJfxBounds().getHeight();
        this.sizeScale.bind(configuration.getVirtualMouseParameters().mouseSizeProperty().divide(10.0));
        this.timePerPixelSpeed.bind(Bindings.createDoubleBinding(
                () -> 1.0 / configuration.getVirtualMouseParameters().mouseSpeedProperty().get() * DirectionalMouseController.TIME_PER_PIXEL,
                configuration.getVirtualMouseParameters().mouseSpeedProperty()));
        this.color.bind(configuration.getVirtualMouseParameters().mouseColorProperty());
        this.strokeColor.bind(configuration.getVirtualMouseParameters().mouseStrokeColorProperty());
        this.typeMouseDrawing.bind(configuration.getVirtualMouseParameters().virtualMouseTypeProperty());
        this.mouseDrawing.bind(configuration.getVirtualMouseParameters().directionalMouseDrawingProperty());
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        stopMovingMouse();
        this.sizeScale.unbind();
        this.timePerPixelSpeed.unbind();
        this.color.unbind();
        this.strokeColor.unbind();
        this.typeMouseDrawing.unbind();
        this.mouseDrawing.unbind();
        this.hideMouseFrame();
        this.configuration = null;
    }
}
