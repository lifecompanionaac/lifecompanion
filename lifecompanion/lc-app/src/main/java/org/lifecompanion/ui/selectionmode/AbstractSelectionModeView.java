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

package org.lifecompanion.ui.selectionmode;

import javafx.animation.*;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import javafx.util.Pair;
import org.lifecompanion.controller.feedback.IndicationController;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.selectionmode.ProgressDrawMode;
import org.lifecompanion.model.api.selectionmode.ScanningDirection;
import org.lifecompanion.model.api.style.ShapeStyle;
import org.lifecompanion.model.api.style.StylePropertyI;
import org.lifecompanion.model.api.ui.configurationcomponent.ViewProviderI;
import org.lifecompanion.util.LangUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.model.impl.selectionmode.DrawSelectionModeI;
import org.lifecompanion.ui.configurationcomponent.base.LCConfigurationChildContainerPane;
import org.lifecompanion.util.model.ConfigurationComponentLayoutUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Abstract view for all selection mode that use a "scanning" method that scan line,column and parts of grid.
 *
 * @param <T> the selection type
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class AbstractSelectionModeView<T extends DrawSelectionModeI> extends Group {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSelectionModeView.class);


    /**
     * Time between each part change
     */
    protected static final long TIME = 170;

    /**
     * Time that progress will be displayed 100% finished
     */
    protected static final long EXTRA_TIME_END_PROGRESS = 120;

    /**
     * Time waiting before playing progress moving
     */
    protected static final long EXTRA_TIME_BEFORE_PROGRESS = 120;

    /**
     * How key become bigger on scanning
     */
    private static final double SCALE_MANIFY = 1.3;

    /**
     * Gaussian blur radius for background reduction
     */
    private static final double GAUSSIAN_BLUR_RADIUS = 10.0;

    //Animations
    private final TranslateTransition partTranslateTransition;
    private final Timeline timeLineSize;
    private final Timeline timeLineProgress;
    private KeyFrame keyFrameProgress;

    //Model
    protected T selectionMode;
    protected ScanningDirection direction;

    //Specific to part moving
    private final BooleanProperty keyProgressRectangleVisible;
    private final Rectangle keyStrokeRectangle;
    private final Rectangle keyProgressRectangle;
    private final Rectangle clipKeyProgressRectangle;

    private final Rectangle backgroundReductionRectangle;

    private final FillTransition fillTransition;

    public AbstractSelectionModeView(final T selectionModeP, final ScanningDirection direction) {
        this.setPickOnBounds(false);
        this.setMouseTransparent(true);

        this.selectionMode = selectionModeP;
        this.keyProgressRectangleVisible = new SimpleBooleanProperty();
        this.direction = direction;

        //Create rectangles
        this.keyStrokeRectangle = new Rectangle();
        this.keyProgressRectangle = new Rectangle();
        this.clipKeyProgressRectangle = new Rectangle();
        this.configureAndAddScanningRectangles(this.keyStrokeRectangle, this.selectionMode.currentPartNotNullProperty(),
                this.keyProgressRectangleVisible, this.keyProgressRectangle, this.clipKeyProgressRectangle);
        keyStrokeRectangle.boundsInParentProperty().addListener((obs, ov, nv) -> requestBackgroundReductionUpdate());

        // Rectangle for background reduction (if enabled)
        backgroundReductionRectangle = new Rectangle();
        backgroundReductionRectangle.setStrokeWidth(0.0);
        backgroundReductionRectangle.setFill(Color.BLACK);
        this.getChildren().add(backgroundReductionRectangle);
        backgroundReductionRectangle.toBack();
        backgroundReductionRectangle.opacityProperty().bind(selectionMode.backgroundReductionLevelProperty());
        backgroundReductionRectangle.visibleProperty().bind(selectionMode.backgroundReductionEnabledProperty()
                .and(keyStrokeRectangle.visibleProperty()
                        .or(getOtherOptionalStrokeRectangle() != null ? getOtherOptionalStrokeRectangle().visibleProperty() : new SimpleBooleanProperty(false))));

        //Transitions
        this.partTranslateTransition = new TranslateTransition(Duration.millis(AbstractSelectionModeView.TIME), this.keyStrokeRectangle);
        this.timeLineSize = new Timeline();
        this.timeLineProgress = new Timeline();
        this.selectionMode.playingProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                this.timeLineProgress.play();
            } else {
                this.timeLineProgress.pause();
            }
        });

        this.fillTransition = new FillTransition(Duration.millis(IndicationController.TRANSITION_TIME_MS), this.keyStrokeRectangle);
    }

    /**
     * To be implemented by subview if another stroke rectangle should be taken as a rectangle to update the background reduction<br>
     * Note that this should return a non null value in parent constructor so the implementation may be a "lazy initialization" one.
     *
     * @return the rectangle or null if not possible.
     */
    protected Rectangle getOtherOptionalStrokeRectangle() {
        return null;
    }

    public Rectangle getBackgroundReductionRectangle() {
        return backgroundReductionRectangle;
    }

    protected void requestBackgroundReductionUpdate() {
        if (backgroundReductionRectangle.isVisible() && this.getParent() != null) {
            try {
                // Find the configuration container node
                ObservableList<Node> parentChildren = this.getParent().getChildrenUnmodifiable();
                for (Node screenShotSourceConfig : parentChildren) {
                    if (screenShotSourceConfig instanceof LCConfigurationChildContainerPane) {

                        // Take the selection rectangle bounds
                        Rectangle strokeRectangle = getOtherOptionalStrokeRectangle() != null && getOtherOptionalStrokeRectangle().isVisible() ? getOtherOptionalStrokeRectangle() : this.keyStrokeRectangle;
                        final Bounds strokeRectangleBoundsInParent = strokeRectangle.getBoundsInParent();

                        // Determine global size and key stroke exact bounds in parent
                        Bounds groupBounds = this.getParent().getBoundsInLocal();
                        Bounds sourceConfigBoundsInParent = screenShotSourceConfig.getBoundsInParent();
                        backgroundReductionRectangle.setLayoutX(sourceConfigBoundsInParent.getMinX());
                        backgroundReductionRectangle.setLayoutY(sourceConfigBoundsInParent.getMinY());
                        backgroundReductionRectangle.setWidth(sourceConfigBoundsInParent.getWidth());
                        backgroundReductionRectangle.setHeight(sourceConfigBoundsInParent.getHeight());
                        Rectangle keyStrokeRectangleFromBounds = rectangleFromBounds(strokeRectangleBoundsInParent);
                        keyStrokeRectangleFromBounds.setX(strokeRectangle.getTranslateX() - strokeRectangle.getStrokeWidth() * strokeRectangle.getScaleX() / 2.0 - sourceConfigBoundsInParent.getMinX() - ((strokeRectangle.getScaleX() * strokeRectangle.getWidth()) - strokeRectangle.getWidth()) / 2.0);
                        keyStrokeRectangleFromBounds.setY(strokeRectangle.getTranslateY() - strokeRectangle.getStrokeWidth() * strokeRectangle.getScaleY() / 2.0 - sourceConfigBoundsInParent.getMinY() - ((strokeRectangle.getScaleY() * strokeRectangle.getHeight()) - strokeRectangle.getHeight()) / 2.0);

                        // Clip on selection stroke + all the root text displayer (because they should stay visible)
                        Shape clipShape = Shape.subtract(rectangleFromBounds(groupBounds), keyStrokeRectangleFromBounds);
                        LCConfigurationI configuration = ((LCConfigurationChildContainerPane) screenShotSourceConfig).getConfiguration();
                        Collection<RootGraphicComponentI> allRootComponents = configuration.getChildren();
                        for (RootGraphicComponentI comp : allRootComponents) {
                            if (comp instanceof WriterDisplayerI) {
                                WriterDisplayerI textDisplayerComponent = (WriterDisplayerI) comp;
                                final Region view = ViewProviderI.getOrCreateViewComponentFor(textDisplayerComponent, AppMode.USE).getView();
                                clipShape = Shape.subtract(clipShape, rectangleFromBounds(view.getBoundsInParent()));
                            }
                        }
                        // Set clip to background reduction
                        backgroundReductionRectangle.setClip(clipShape);
                        return;
                    }
                }
            } catch (Throwable t) {
                LOGGER.warn("Background reduction update failed, will skip it...", t);
            }
        }
    }

    private Rectangle rectangleFromBounds(Bounds groupBounds) {
        return new Rectangle(groupBounds.getMinX(), groupBounds.getMinY(), groupBounds.getWidth(), groupBounds.getHeight());
    }

    // Class part : "Public API"
    //========================================================================
    public void moveToPart(final GridPartComponentI gridPart, final long progressTime, final boolean enableAnimation) {
        FXThreadUtils.runOnFXThread(() -> this.updatePartMoveAnimation(gridPart, progressTime, enableAnimation));
    }

    public void moveNullPart() {
        FXThreadUtils.runOnFXThread(this::scaleDownPreviousViews);
    }

    public void dispose() {
        this.scaleDownPreviousViews();
        this.fillTransition.stop();
    }
    //========================================================================

    private void updatePartMoveAnimation(final GridPartComponentI keyP, final long progressTime, final boolean enableAnimation) {
        //Get values
        Pair<Double, Double> pos = ConfigurationComponentLayoutUtils.getConfigurationPosition(keyP);
        StylePropertyI<Number> radiusProp = keyP instanceof GridPartKeyComponentI ? keyP.getKeyStyle().shapeRadiusProperty()
                : keyP.getGridShapeStyle().shapeRadiusProperty();
        ShapeStyle shapeStyle = keyP instanceof GridPartKeyComponentI ? keyP.getKeyStyle().shapeStyleProperty().value().getValue() : ShapeStyle.CLASSIC;
        //Launch
        this.updateMoveAnimation(enableAnimation, ViewProviderI.getOrCreateViewComponentFor(keyP, AppMode.USE).getView(), pos,
                new Pair<>(keyP.layoutWidthProperty().get(), keyP.layoutHeightProperty().get()),
                LangUtils.nullToZeroDouble(radiusProp.value().getValue()), shapeStyle, this.partTranslateTransition, this.keyStrokeRectangle,
                this.keyProgressRectangle, this.keyProgressRectangleVisible, () -> {
                    this.startProgressTransition(pos, progressTime, keyP);
                });
    }

    private void startProgressTransition(final Pair<Double, Double> position, final long progressTime, final GridPartComponentI key) {
        if (this.direction != null) {
            this.startProgressTransition(this.selectionMode.getParameters().manifyKeyOverProperty().get(), position,
                    new Pair<>(key.layoutWidthProperty().get(), key.layoutHeightProperty().get()), progressTime, this.direction,
                    this.keyProgressRectangleVisible, this.keyProgressRectangle);
        }
    }

    // Class part : "Common protected method"
    //========================================================================

    protected void configureAndAddScanningRectangles(final Rectangle strokeRectangle, final BooleanBinding visibleBindingStroke,
                                                     final BooleanProperty progressVisibleProperty, final Rectangle progressRectangle, final Rectangle clipRectangle) {
        //Stroke rectangle
        strokeRectangle.setFill(Color.TRANSPARENT);
        strokeRectangle.setPickOnBounds(false);
        strokeRectangle.strokeProperty().bind(this.selectionMode.strokeFillProperty());
        strokeRectangle.visibleProperty().bind(visibleBindingStroke);

        //Progress rectangle
        progressRectangle.setPickOnBounds(false);
        progressRectangle.fillProperty().bind(this.selectionMode.progressFillProperty());
        progressRectangle.visibleProperty().bind(this.selectionMode.drawProgressProperty().and(visibleBindingStroke).and(progressVisibleProperty));

        //Clip rectangle
        clipRectangle.widthProperty().bind(progressRectangle.widthProperty());
        clipRectangle.heightProperty().bind(progressRectangle.heightProperty());
        clipRectangle.layoutXProperty().bind(strokeRectangle.layoutXProperty());
        clipRectangle.layoutYProperty().bind(strokeRectangle.layoutYProperty());
        clipRectangle.arcWidthProperty().bind(strokeRectangle.arcWidthProperty());
        clipRectangle.arcHeightProperty().bind(strokeRectangle.arcHeightProperty());
        progressRectangle.setClip(clipRectangle);

        this.getChildren().addAll(progressRectangle, strokeRectangle);
    }

    private ParallelTransition previousAllAnimation;
    private List<Node> previousViews;

    protected void updateMoveAnimation(final boolean enableAnimation, final Node view, final Pair<Double, Double> position,
                                       final Pair<Double, Double> size, final double shapeRadius, ShapeStyle shapeStyle, final TranslateTransition translateAnimation, final Rectangle strokeRectangle,
                                       final Rectangle progressRectangle, final BooleanProperty progressVisibleProperty, final Runnable translateAnimationCallback) {
        progressVisibleProperty.set(false);

        double strokeSize = this.selectionMode.getParameters().selectionViewSizeProperty().get();

        this.scaleDownPreviousViews();
        //Stop previous
        if (this.previousAllAnimation != null) {
            this.previousAllAnimation.stop();
        }

        //Create new
        this.previousAllAnimation = new ParallelTransition();
        if (enableAnimation) {
            //Create position transition animation
            translateAnimation.setFromX(strokeRectangle.translateXProperty().get());
            translateAnimation.setFromY(strokeRectangle.translateYProperty().get());
            translateAnimation.setToX(position.getKey());
            translateAnimation.setToY(position.getValue());

            //Create size and stroke animation
            KeyFrame keyFrame = this.getSizeTransition(strokeRectangle, size.getKey(), size.getValue(), shapeRadius, strokeSize, shapeStyle);
            this.timeLineSize.getKeyFrames().clear();
            this.timeLineSize.getKeyFrames().add(keyFrame);

            this.previousAllAnimation.getChildren().addAll(this.timeLineSize, translateAnimation);
        } else {
            strokeRectangle.setTranslateX(position.getKey());
            strokeRectangle.setTranslateY(position.getValue());
            strokeRectangle.setWidth(size.getKey());
            strokeRectangle.setHeight(size.getValue());
            double arcValue = ConfigurationComponentLayoutUtils.computeArcAndStroke(shapeRadius, size.getKey(), size.getValue(), strokeSize, shapeStyle);
            strokeRectangle.setArcWidth(arcValue);
            strokeRectangle.setArcHeight(arcValue);
            strokeRectangle.setStrokeWidth(strokeSize);
        }
        //TODO : no view and no animation, call on finished ?
        if (view != null && this.selectionMode.getParameters().manifyKeyOverProperty().get()) {
            view.toFront();
            strokeRectangle.toFront();
            this.previousViews = Arrays.asList(view, strokeRectangle);
            this.previousAllAnimation.getChildren().addAll(this.createScaleTransition(view, AbstractSelectionModeView.SCALE_MANIFY),
                    this.createScaleTransition(strokeRectangle, AbstractSelectionModeView.SCALE_MANIFY));
        }
        this.previousAllAnimation.setOnFinished((ea) -> {
            translateAnimationCallback.run();
        });
        this.previousAllAnimation.play();
    }

    //TODO : optimize
    private void scaleDownPreviousViews() {
        if (this.previousViews != null && this.selectionMode.getParameters().manifyKeyOverProperty().get()) {
            ParallelTransition para = new ParallelTransition();
            for (Node node : this.previousViews) {
                para.getChildren().add(this.createScaleTransition(node, 1.0));
            }
            para.play();
            this.previousViews = null;
        }
    }

    private ScaleTransition createScaleTransition(final Node view, final double to) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(AbstractSelectionModeView.TIME), view);
        transition.setFromX(view.getScaleX());
        transition.setFromY(view.getScaleY());
        transition.setToX(to);
        transition.setToY(to);
        return transition;
    }

    protected void startProgressTransition(final boolean scaleProgress, final Pair<Double, Double> position, final Pair<Double, Double> size,
                                           final long progressTime, final ScanningDirection direction, final BooleanProperty progressVisibleProperty,
                                           final Rectangle progressRectangle) {
        this.timeLineProgress.stop();

        //Remove previous progress
        if (this.keyFrameProgress != null) {
            this.timeLineProgress.getKeyFrames().remove(this.keyFrameProgress);
        }

        //Draw the progress only when selection mode is playing
        if (this.selectionMode.playingProperty().get()) {
            progressVisibleProperty.set(true);

            //Compute the position/size with scale (scaling on progress rectangle doesn't work)
            double x = scaleProgress ? position.getKey() - (AbstractSelectionModeView.SCALE_MANIFY - 1) / 2.0 * size.getKey()
                    : position.getKey();
            double y = scaleProgress ? position.getValue() - (AbstractSelectionModeView.SCALE_MANIFY - 1) / 2.0 * size.getValue()
                    : position.getValue();
            double width = scaleProgress ? size.getKey() * AbstractSelectionModeView.SCALE_MANIFY : size.getKey();
            double height = scaleProgress ? size.getValue() * AbstractSelectionModeView.SCALE_MANIFY : size.getValue();

            //Start
            if (this.selectionMode.drawProgressProperty().get()) {
                progressRectangle.translateXProperty().set(x);
                progressRectangle.translateYProperty().set(y);
                if (direction == ScanningDirection.HORIZONTAL) {
                    if (this.selectionMode.getParameters().progressDrawModeProperty().get() == ProgressDrawMode.FILL_PART) {
                        progressRectangle.widthProperty().set(0.0);
                    } else {
                        progressRectangle.widthProperty()
                                .set(Math.min(width, this.selectionMode.getParameters().progressViewBarSizeProperty().get()));
                    }
                    progressRectangle.heightProperty().set(height);
                } else {
                    if (this.selectionMode.getParameters().progressDrawModeProperty().get() == ProgressDrawMode.FILL_PART) {
                        progressRectangle.heightProperty().set(0.0);
                    } else {
                        progressRectangle.heightProperty()
                                .set(Math.min(height, this.selectionMode.getParameters().progressViewBarSizeProperty().get()));
                    }
                    progressRectangle.widthProperty().set(width);
                }
            }
            //Create new ones
            WritableValue<Number> changingProperty;
            Double endValue;
            if (direction == ScanningDirection.HORIZONTAL) {
                if (this.selectionMode.getParameters().progressDrawModeProperty().get() == ProgressDrawMode.FILL_PART) {
                    changingProperty = progressRectangle.widthProperty();
                    endValue = width;
                } else {
                    changingProperty = progressRectangle.translateXProperty();
                    endValue = x + width - Math.min(width, this.selectionMode.getParameters().progressViewBarSizeProperty().get());
                }
            } else {
                if (this.selectionMode.getParameters().progressDrawModeProperty().get() == ProgressDrawMode.FILL_PART) {
                    changingProperty = progressRectangle.heightProperty();
                    endValue = height;
                } else {
                    changingProperty = progressRectangle.translateYProperty();
                    endValue = y + height - Math.min(height, this.selectionMode.getParameters().progressViewBarSizeProperty().get());
                }
            }
            KeyValue keyValue = new KeyValue(changingProperty, endValue, Interpolator.EASE_BOTH);
            long progressValue = progressTime - AbstractSelectionModeView.TIME
                    - AbstractSelectionModeView.EXTRA_TIME_END_PROGRESS - AbstractSelectionModeView.EXTRA_TIME_BEFORE_PROGRESS;
            if (progressValue > 0) {
                this.keyFrameProgress = new KeyFrame(Duration.millis(progressValue), keyValue);
                this.timeLineProgress.getKeyFrames().add(this.keyFrameProgress);
            }
            this.timeLineProgress.setDelay(Duration.millis(AbstractSelectionModeView.EXTRA_TIME_BEFORE_PROGRESS));
            this.timeLineProgress.play();
        }
    }

    protected KeyFrame getSizeTransition(final Rectangle rectangle, final double width, final double height, final double round,
                                         final double stroke, ShapeStyle shapeStyle) {
        final KeyValue kvW = new KeyValue(rectangle.widthProperty(), width, Interpolator.EASE_BOTH);
        final KeyValue kvH = new KeyValue(rectangle.heightProperty(), height, Interpolator.EASE_BOTH);
        double arcValue = ConfigurationComponentLayoutUtils.computeArcAndStroke(round, width, height, stroke, shapeStyle);
        final KeyValue kvRW = new KeyValue(rectangle.arcWidthProperty(), arcValue, Interpolator.EASE_BOTH);
        final KeyValue kvRH = new KeyValue(rectangle.arcHeightProperty(), arcValue, Interpolator.EASE_BOTH);
        final KeyValue kvStr = new KeyValue(rectangle.strokeWidthProperty(), stroke, Interpolator.EASE_BOTH);
        final KeyFrame kf = new KeyFrame(Duration.millis(AbstractSelectionModeView.TIME), kvW, kvH, kvRW, kvRH, kvStr);
        return kf;
    }
    //========================================================================

    public void showActivationRequest(Color color) {
        this.fillTransition.stop();
        if (keyStrokeRectangle.getFill() != color) {
            fillTransition.setToValue(color);
            fillTransition.play();
        }


    }

    public void hideActivationRequest() {
        this.fillTransition.stop();
        if (keyStrokeRectangle.getFill() != Color.TRANSPARENT) {
            fillTransition.setToValue(Color.TRANSPARENT);
            fillTransition.play();
        }
    }

}
