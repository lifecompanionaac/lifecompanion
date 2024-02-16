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
import org.lifecompanion.model.api.configurationcomponent.VirtualMouseType;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.ui.virtualmouse.CrossScanningMouseStage;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.MouseEvent;

/**
 * @author Oscar PAVOINE
 */
public enum ScanningMouseController implements ModeListenerI {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanningMouseController.class);

    private static final double TIME_PER_PIXEL = 25.0;

    private static final int NUMBER_OF_FRAME_CHECK = 10;

    private static final Interpolator MOVING_INTERPOLATOR = Interpolator.EASE_IN;

    /**
     * Timeline to animate mouse movement
     */
    private final Timeline timeline;

    /**
     * Stage to show the virtual mouse
     */
    private CrossScanningMouseStage crossScanningMouseStage;

    /**
     * Mouse position
     */
    private final DoubleProperty mouseX;
    private final DoubleProperty mouseY;
    private final DoubleProperty mouseXAccuracy;
    private final DoubleProperty mouseYAccuracy;

    /**
     * Width/height of the frame (max bounds)
     */
    private double frameWidth, frameHeight;

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
     * Visibility of the mouse accuracy
     */
    private final ObjectProperty<Boolean> visibilityMouseX;
    private final ObjectProperty<Boolean> visibilityMouseY;
    private final ObjectProperty<Boolean> visibilityMouseXAccuracy;
    private final ObjectProperty<Boolean> visibilityMouseYAccuracy;
    private final ObjectProperty<Boolean> mouseAccuracy;
    private final IntegerProperty maxLoop;

    /**
     * To check if the mouse position is not on the main frame
     */
    private final EventHandler<ActionEvent> checkFramePosition;

    private LCConfigurationI configuration;

    private boolean lineAccuracy = false;

    private int nbLoop = 0;

    ScanningMouseController() {
        this.timeline = new Timeline();
        this.timeline.setCycleCount(1);
        this.timeline.setAutoReverse(false);
        this.mouseX = new SimpleDoubleProperty(0.0);
        this.mouseY = new SimpleDoubleProperty(0.0);
        this.mouseXAccuracy = new SimpleDoubleProperty(0.0);
        this.mouseYAccuracy = new SimpleDoubleProperty(0.0);
        this.sizeScale = new SimpleDoubleProperty();
        this.timePerPixelSpeed = new SimpleDoubleProperty();
        this.color = new SimpleObjectProperty<>();
        this.strokeColor = new SimpleObjectProperty<>();
        this.typeMouseDrawing = new SimpleObjectProperty<>();
        this.visibilityMouseX = new SimpleObjectProperty<>(false);
        this.visibilityMouseY = new SimpleObjectProperty<>(false);
        this.visibilityMouseXAccuracy = new SimpleObjectProperty<>(false);
        this.visibilityMouseYAccuracy = new SimpleObjectProperty<>(false);
        this.mouseAccuracy = new SimpleObjectProperty<>();
        this.maxLoop = new SimpleIntegerProperty();
        //Check frame position
        this.checkFramePosition = event -> checkFramePositionWithMouse();
    }

    private void checkFramePositionWithMouse() {
        Rectangle2D frameBounds = VirtualMouseController.INSTANCE.getMainFrameBounds();
        if (frameBounds.contains(this.mouseX.get(), this.mouseY.get())) {
            VirtualMouseController.INSTANCE.moveFrameToAvoidMouse(this.frameWidth, this.frameHeight, this.mouseX.get(), this.mouseY.get());
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

    public ReadOnlyDoubleProperty mouseXAccuracyProperty() {
        return this.mouseXAccuracy;
    }

    public ReadOnlyDoubleProperty mouseYAccuracyProperty() {
        return this.mouseYAccuracy;
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

    public ReadOnlyObjectProperty<VirtualMouseType> mouseDrawingProperty() {
        return this.typeMouseDrawing;
    }

    public ReadOnlyObjectProperty<Boolean> visibilityMouseXProperty() {
        return this.visibilityMouseX;
    }

    public ReadOnlyObjectProperty<Boolean> visibilityMouseYProperty() {
        return this.visibilityMouseY;
    }

    public ReadOnlyObjectProperty<Boolean> visibilityMouseXAccuracyProperty() {
        return this.visibilityMouseXAccuracy;
    }

    public ReadOnlyObjectProperty<Boolean> visibilityMouseYAccuracyProperty() {
        return this.visibilityMouseYAccuracy;
    }

    //========================================================================

    // Class part : "Moving API"
    //========================================================================
    private void movingCursorStrip(Runnable action) {
        if (checkIfVirtualMouseEnabled()) {
            this.visibilityMouseX.set(true);
            this.visibilityMouseY.set(true);
            if (this.mouseAccuracy.get()) {
                this.visibilityMouseXAccuracy.set(true);
                this.visibilityMouseYAccuracy.set(true);
                this.lineAccuracy = false;
                this.nbLoop = 0;
                startMovingLineRight();
                SelectionModeController.INSTANCE.pauseCurrentScanningUntilNextSelection(() -> {
                    stopMovingMouse();
                    this.mouseXAccuracy.set(this.mouseXAccuracy.get() - sizeScaleProperty().get() * 5 * 0.9);
                    this.lineAccuracy = true;
                    this.nbLoop = 0;
                    startMovingLineRight();
                    SelectionModeController.INSTANCE.pauseCurrentScanningUntilNextSelection(() -> {
                        stopMovingMouse();
                        this.visibilityMouseXAccuracy.set(false);
                        this.lineAccuracy = false;
                        this.nbLoop = 0;
                        startMovingLineBottom();
                        SelectionModeController.INSTANCE.pauseCurrentScanningUntilNextSelection(() -> {
                            stopMovingMouse();
                            this.mouseYAccuracy.set(this.mouseY.get() - sizeScaleProperty().get() * 5 * 0.9);
                            this.lineAccuracy = true;
                            this.nbLoop = 0;
                            startMovingLineBottom();
                            SelectionModeController.INSTANCE.pauseCurrentScanningUntilNextSelection(() -> {
                                stopMovingMouse();
                                this.nbLoop = 0;
                                action.run();
                                setUpCursorStripView();
                                this.lineAccuracy = false;
                                return false;
                            });
                            return false;
                        });
                        return false;
                    });
                    return false;
                });
            } else {
                startMovingLineRight();
                this.nbLoop = 0;
                SelectionModeController.INSTANCE.pauseCurrentScanningUntilNextSelection(() -> {
                    stopMovingMouse();
                    this.nbLoop = 0;
                    startMovingLineBottom();
                    SelectionModeController.INSTANCE.pauseCurrentScanningUntilNextSelection(() -> {
                        stopMovingMouse();
                        this.nbLoop = 0;
                        action.run();
                        setUpCursorStripView();
                        return false;
                    });
                    return false;
                });
            }
            this.checkInitFrame(() -> {
            });
        }
    }

    private void startMovingLineRight() {
        this.checkInitFrame(() -> {
            if (this.nbLoop < this.maxLoop.get()) {
                this.nbLoop++;
                double targetX;
                double margin = 75 - sizeScaleProperty().get() * 5;

                if (this.mouseAccuracy.get()) {
                    if (this.lineAccuracy) {
                        targetX = this.mouseX.get() + 75;
                    } else {
                        this.addKeyFrame(this.frameWidth, this.frameWidth - margin, this.mouseXAccuracy);
                        targetX = this.frameWidth - margin;
                    }
                } else {
                    targetX = this.frameWidth;
                }
                this.addKeyFrame(this.frameWidth, targetX, this.mouseX);

                this.startMoving(this::startMovingLineLeft);
            } else {
                setUpCursorStripView();
            }
        });
    }

    private void startMovingLineLeft() {
        this.checkInitFrame(() -> {
            double targetX = this.lineAccuracy ? this.mouseX.get() - 75 : 0.0;
            this.addKeyFrame(this.frameWidth, targetX, this.mouseX);

            if (this.mouseAccuracy.get() && !this.lineAccuracy) {
                this.addKeyFrame(this.frameWidth, 0.0, this.mouseXAccuracy);
            }

            this.startMoving(this::startMovingLineRight);
        });
    }

    private void startMovingLineBottom() {
        this.checkInitFrame(() -> {
            if (this.nbLoop < this.maxLoop.get()) {
                this.nbLoop++;
                double targetY;
                double margin = 75 - sizeScaleProperty().get() * 5;

                if (this.mouseAccuracy.get()) {
                    if (this.lineAccuracy) {
                        targetY = this.mouseY.get() + 75;
                    } else {
                        this.addKeyFrame(this.frameHeight, this.frameHeight - margin, this.mouseYAccuracy);
                        targetY = this.frameHeight - margin;
                    }
                } else {
                    targetY = this.frameHeight;
                }
                this.addKeyFrame(this.frameHeight, targetY, this.mouseY);

                this.startMoving(this::startMovingLineTop);
            } else {
                setUpCursorStripView();
            }
        });
    }

    private void startMovingLineTop() {
        this.checkInitFrame(() -> {
            double targetY = this.lineAccuracy ? this.mouseY.get() - 75 : 0.0;
            this.addKeyFrame(this.frameHeight, targetY, this.mouseY);

            if (this.mouseAccuracy.get() && !this.lineAccuracy) {
                this.addKeyFrame(this.frameHeight, 0.0, this.mouseYAccuracy);
            }
            this.startMoving(this::startMovingLineBottom);
        });
    }

    private void setUpCursorStripView() {
        SelectionModeController.INSTANCE.restartCurrentScanning();
        ScanningMouseController.INSTANCE.stopMovingMouse();
        this.visibilityMouseX.set(false);
        this.visibilityMouseY.set(false);
        this.visibilityMouseXAccuracy.set(false);
        this.visibilityMouseYAccuracy.set(false);
        this.mouseX.set(0);
        this.mouseY.set(0);
        this.mouseXAccuracy.set(0);
        this.mouseYAccuracy.set(0);
        this.nbLoop = 0;
        VirtualMouseController.INSTANCE.centerMouseOnStage();
    }

    private void addKeyFrame(final double diff, final double wantedValue, final DoubleProperty property) {
        long totalTime = (long) (diff * this.timePerPixelSpeed.get());
        KeyFrame keyFrameMoveMouse = new KeyFrame(Duration.millis(totalTime),
                new KeyValue(property, wantedValue, ScanningMouseController.MOVING_INTERPOLATOR));
        //Check X times during the animation
        long verificationTime = totalTime / ScanningMouseController.NUMBER_OF_FRAME_CHECK;
        for (int i = 0; i < ScanningMouseController.NUMBER_OF_FRAME_CHECK; i++) {
            this.timeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * verificationTime), this.checkFramePosition));
        }
        this.timeline.getKeyFrames().add(keyFrameMoveMouse);
    }

    private void startMoving(Runnable comeBack) {
        this.timeline.setOnFinished(event -> {
            this.timeline.getKeyFrames().clear();
            comeBack.run();
        });
        this.timeline.playFromStart();
    }

    private void stopMovingMouse() {
        this.timeline.stop();
        this.timeline.getKeyFrames().clear();
    }
    //========================================================================

    // Class part : "Clic API"
    //========================================================================
    public void executePrimaryMouseClic() {
        if (isCurrentCursor() && checkIfVirtualMouseEnabled()) {
            movingCursorStrip(() -> {
                VirtualMouseController.INSTANCE.moveMouseToWithDelay(this.mouseX.get(), this.mouseY.get());
                VirtualMouseController.INSTANCE.executeMouseClic(MouseEvent.BUTTON1);
                VirtualMouseController.INSTANCE.executeMouseClic(MouseEvent.BUTTON1);
                this.frameToFrontAndFocus();
            });
        }
    }

    private boolean isCurrentCursor() {
        return this.configuration.getVirtualMouseParameters().virtualMouseTypeProperty().get() == VirtualMouseType.CROSS_SCANNING;
    }

    private static boolean checkIfVirtualMouseEnabled() {
        boolean enabled = !GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_VIRTUAL_MOUSE);
        if (!enabled) {
            LOGGER.info("Ignored virtual mouse action because {} is enabled", GlobalRuntimeConfiguration.DISABLE_VIRTUAL_MOUSE);
        }
        return enabled;
    }

    private void frameToFrontAndFocus() {
        //mouse stage and main frame to front
        FXThreadUtils.runOnFXThread(() -> {
            Stage useStage = AppModeController.INSTANCE.getUseModeContext().getStage();
            if (useStage != null) useStage.toFront();
            this.crossScanningMouseStage.toFront();
            // Issue #129 : main stage should not be focused back if it's a  virtual keyboard
            if (!AppModeController.INSTANCE.getUseModeContext().getConfiguration().virtualKeyboardProperty().get() && useStage != null) {
                useStage.requestFocus();
            }
        });
    }

    //========================================================================

    // Class part : "Internal mouse event API"
    //========================================================================

    /**
     * Initialize mouse stage.
     */
    private void checkInitFrame(final Runnable callback) {
        if (this.crossScanningMouseStage != null) {
            if (this.crossScanningMouseStage.isShowing()) {
                callback.run();
            } else {
                FXThreadUtils.runOnFXThread(() -> {
                    this.crossScanningMouseStage.show();
                    this.frameToFrontAndFocus();
                    callback.run();
                });
            }
        } else {
            FXThreadUtils.runOnFXThread(() -> {
                Screen primaryScreen = Screen.getPrimary();
                this.crossScanningMouseStage = CrossScanningMouseStage.getInstance();
                this.crossScanningMouseStage.show();
                final Rectangle2D screenBounds = primaryScreen.getBounds();
                this.mouseX.set(screenBounds.getWidth() / 2.0);
                this.mouseY.set(screenBounds.getHeight() / 2.0);
                this.frameToFrontAndFocus();
                this.checkFramePositionWithMouse();
                callback.run();
            });
        }
    }

    public void hideMouseFrame() {
        if (this.crossScanningMouseStage != null) {
            FXThreadUtils.runOnFXThread(() -> this.crossScanningMouseStage.hide());
        }
    }
    //========================================================================

    // Class part : "Mode listener"
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.configuration = configuration;
        this.sizeScale.bind(configuration.getVirtualMouseParameters().mouseSizeProperty().divide(10.0));
        this.timePerPixelSpeed.bind(Bindings.createDoubleBinding(
                () -> 1.0 / configuration.getVirtualMouseParameters().mouseSpeedProperty().get() * ScanningMouseController.TIME_PER_PIXEL,
                configuration.getVirtualMouseParameters().mouseSpeedProperty()));
        this.color.bind(configuration.getVirtualMouseParameters().mouseColorProperty());
        this.strokeColor.bind(configuration.getVirtualMouseParameters().mouseStrokeColorProperty());
        this.typeMouseDrawing.bind(configuration.getVirtualMouseParameters().virtualMouseTypeProperty());
        this.mouseAccuracy.bind(configuration.getVirtualMouseParameters().mouseAccuracyProperty());
        this.maxLoop.bind(configuration.getVirtualMouseParameters().mouseMaxLoopProperty());
        Screen primaryScreen = Screen.getPrimary();
        Rectangle2D primaryScreenBounds = primaryScreen.getBounds();
        this.frameWidth = primaryScreenBounds.getWidth();
        this.frameHeight = primaryScreenBounds.getHeight();
        setUpCursorStripView();
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        setUpCursorStripView();
        this.sizeScale.unbind();
        this.timePerPixelSpeed.unbind();
        this.color.unbind();
        this.strokeColor.unbind();
        this.typeMouseDrawing.unbind();
        this.mouseAccuracy.unbind();
        this.maxLoop.unbind();
        this.hideMouseFrame();
        this.configuration = null;
    }
    //========================================================================
}