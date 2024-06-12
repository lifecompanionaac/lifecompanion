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
package org.lifecompanion.ui.configurationcomponent.usemode;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import org.lifecompanion.controller.configurationcomponent.GlobalKeyEventController;
import org.lifecompanion.controller.easteregg.JPDRetirementController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.ui.configurationcomponent.ViewProviderI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.ui.easteregg.JPDRetirementView;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.KeyCodeUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A component that will display a configuration with its scale options.<br>
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseModeConfigurationDisplayer extends Group implements LCViewInitHelper {
    /**
     * Wanted width/height for this configuration
     */
    private final ReadOnlyDoubleProperty wantedWidth;
    private final ReadOnlyDoubleProperty wantedHeight;

    /**
     * Current configuration size
     */
    private final DoubleProperty configWith, configHeight;

    /**
     * Indicates if the next typed event should be cancelled (can happen if selection mode want to filter key event)
     */
    private boolean cancelNextKeyTypedEvent;

    /**
     * Pressed key list to avoid key press event repetition
     */
    private final HashSet<KeyCode> pressedKey;

    private Scale currentScaleTransform;
    private Region configurationView;
    private Node nodeConfigurationChanging;

    private Consumer<Boolean> configurationChangingListener;

    private double ratio;
    private boolean enableKeepRatio = true;
    private final DoubleProperty scaleX;
    private final DoubleProperty scaleY;
    private final SimpleObjectProperty<Color> backgroundColor;

    private final LCConfigurationI configuration;

    public UseModeConfigurationDisplayer(LCConfigurationI configuration, final ReadOnlyDoubleProperty wantedWidthP, final ReadOnlyDoubleProperty wantedHeightP) {
        this.configuration = configuration;
        this.wantedHeight = wantedHeightP;
        this.wantedWidth = wantedWidthP;
        this.pressedKey = new HashSet<>();
        this.configWith = new SimpleDoubleProperty();
        this.configHeight = new SimpleDoubleProperty();
        this.scaleX = new SimpleDoubleProperty();
        this.scaleY = new SimpleDoubleProperty();
        this.backgroundColor = new SimpleObjectProperty<>(Color.TRANSPARENT);
        this.updateCurrentScale();
        this.initAll();
    }

    @Override
    public void initUI() {
        ProgressIndicator progressIndicator = new ProgressIndicator(-1);
        progressIndicator.setPrefSize(100.0, 100.0);
        final Label labelChangingConfig = new Label(Translation.getText("use.mode.changing.configuration"));
        labelChangingConfig.getStyleClass().addAll("text-font-size-200", "text-weight-bold");
        ImageView imageViewText = new ImageView(IconHelper.get(LCConstant.LC_BIG_ICON_PATH));
        ImageView imageViewCopyright = new ImageView(IconHelper.get(LCConstant.LC_COPYRIGHT_ICON_PATH));
        imageViewCopyright.setFitHeight(80.0);
        imageViewCopyright.setPreserveRatio(true);
        VBox.setMargin(imageViewCopyright, new Insets(0, 0, 10, 0));
        Pane paneFill1 = new Pane();
        VBox.setVgrow(paneFill1, Priority.ALWAYS);
        Pane paneFill2 = new Pane();
        VBox.setVgrow(paneFill2, Priority.ALWAYS);
        final VBox boxContent = new VBox(12.0, paneFill1, imageViewText, labelChangingConfig, progressIndicator, paneFill2, imageViewCopyright);
        boxContent.setAlignment(Pos.CENTER);
        BorderPane borderPane = new BorderPane(boxContent);
        borderPane.prefWidthProperty().bind(wantedWidth);
        borderPane.prefHeightProperty().bind(wantedHeight);
        borderPane.setStyle("-fx-background-color: white;");
        nodeConfigurationChanging = borderPane;
    }


    @Override
    public void initListener() {
        // Register filter for selection mode controller event
        this.addEventFilter(KeyEvent.ANY, (event) -> {
            if (KeyEvent.KEY_PRESSED == event.getEventType()) {
                // Check for blocked keys
                if (GlobalKeyEventController.INSTANCE.getBlockedKeyCodes().contains(event.getCode())) {
                    event.consume();
                    if (KeyCodeUtils.isTextGeneratingKeyCode(event.getCode())) {
                        this.cancelNextKeyTypedEvent = true;
                    }
                }
                // or consume special events
                else {
                    if (event.getCode() == KeyCode.DELETE) {
                        WritingStateController.INSTANCE.removeNextChar(WritingEventSource.USER_PHYSICAL_INPUT);
                    } else if (event.getCode() == KeyCode.BACK_SPACE) {
                        WritingStateController.INSTANCE.removeLastChar(WritingEventSource.USER_PHYSICAL_INPUT);
                    } else if (event.getCode() == KeyCode.LEFT) {
                        WritingStateController.INSTANCE.moveCaretBackward(WritingEventSource.USER_PHYSICAL_INPUT);
                    } else if (event.getCode() == KeyCode.RIGHT) {
                        WritingStateController.INSTANCE.moveCaretForward(WritingEventSource.USER_PHYSICAL_INPUT);
                    } else if (event.getCode() == KeyCode.DOWN) {
                        WritingStateController.INSTANCE.moveCaretDown(WritingEventSource.USER_PHYSICAL_INPUT);
                    } else if (event.getCode() == KeyCode.UP) {
                        WritingStateController.INSTANCE.moveCaretUp(WritingEventSource.USER_PHYSICAL_INPUT);
                    } else if (event.getCode() == KeyCode.HOME) {
                        WritingStateController.INSTANCE.moveCaretToStart(WritingEventSource.USER_PHYSICAL_INPUT);
                    } else if (event.getCode() == KeyCode.END) {
                        WritingStateController.INSTANCE.moveCaretToEnd(WritingEventSource.USER_PHYSICAL_INPUT);
                    } else if (event.getCode() == KeyCode.ENTER) {
                        WritingStateController.INSTANCE.newLine(WritingEventSource.USER_PHYSICAL_INPUT);
                    } else if (event.getCode() == KeyCode.TAB) {
                        WritingStateController.INSTANCE.tab(WritingEventSource.USER_PHYSICAL_INPUT);
                    }
                }
            }
            // Keyboard event to global listener (just once per event)
            if (event.getEventType() != KeyEvent.KEY_PRESSED || !this.pressedKey.contains(event.getCode())) {
                GlobalKeyEventController.INSTANCE.javaFxEventFired(event);
            }
            // At the end of processing, add the processed event to avoid process it more that once
            if (KeyEvent.KEY_PRESSED == event.getEventType()) {
                this.pressedKey.add(event.getCode());
            }
            // When key is released, remove pressed key
            if (KeyEvent.KEY_RELEASED == event.getEventType()) {
                this.pressedKey.remove(event.getCode());
            }
        });
        this.addEventHandler(KeyEvent.KEY_TYPED, (event) -> {
            if (!this.cancelNextKeyTypedEvent) {
                String text = event.getCharacter();
                if (text != null && !text.isEmpty()) {
                    int cCode = text.charAt(0);
                    if (cCode > 31 && cCode != 127) {
                        WritingStateController.INSTANCE.insertText(WritingEventSource.USER_PHYSICAL_INPUT, text);
                    }
                }
            } else {
                this.cancelNextKeyTypedEvent = false;
            }
        });
        // To test virtual cursor mode - FIXME : delete
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.PROP_DEV_MODE)) {
            this.addEventFilter(KeyEvent.ANY, ke -> {
                if (ke.getEventType() == KeyEvent.KEY_PRESSED) {
                    if (ke.getCode() == KeyCode.RIGHT) {
                        SelectionModeController.INSTANCE.moveVirtualCursorRight(null);
                    }
                    if (ke.getCode() == KeyCode.LEFT) {
                        SelectionModeController.INSTANCE.moveVirtualCursorLeft(null);
                    }
                    if (ke.getCode() == KeyCode.UP) {
                        SelectionModeController.INSTANCE.moveVirtualCursorUp(null);
                    }
                    if (ke.getCode() == KeyCode.DOWN) {
                        SelectionModeController.INSTANCE.moveVirtualCursorDown(null);
                    }
                }
                if (ke.getCode() == KeyCode.SPACE) {
                    if (ke.getEventType() == KeyEvent.KEY_PRESSED) {
                        SelectionModeController.INSTANCE.virtualCursorPressed();
                    }
                    if (ke.getEventType() == KeyEvent.KEY_RELEASED) {
                        SelectionModeController.INSTANCE.virtualCursorReleased();
                    }
                }
                ke.consume();
            });
        }

    }

    public void addMouseListener(final Scene element, final Predicate<MouseEvent> eventFilter) {
        EventHandler<MouseEvent> mouseEventFilter = (event) -> {
            if (AppModeController.INSTANCE.isUseMode() && (eventFilter == null || eventFilter.test(event))
                    && SelectionModeController.INSTANCE.globalMouseEvent(event)) {
                event.consume();
            }
        };
        // Register filter on mouse event
        element.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEventFilter);
        element.addEventFilter(MouseEvent.MOUSE_ENTERED, mouseEventFilter);
        element.addEventFilter(MouseEvent.MOUSE_EXITED, mouseEventFilter);
        element.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEventFilter);
    }

    private void showTempNode(Node node) {
        FXThreadUtils.runOnFXThread(() -> {
            this.getTransforms().clear();
            BindingUtils.unbindAndSet(layoutXProperty(), 0.0);
            BindingUtils.unbindAndSet(layoutYProperty(), 0.0);
            this.getChildren().add(node);
        });
    }

    private void hideTempNode(Node node) {
        FXThreadUtils.runOnFXThread(() -> {
            this.getChildren().remove(node);
            bindLayoutXAndY();
            if (currentScaleTransform != null) {
                this.getTransforms().clear();
                this.getTransforms().add(this.currentScaleTransform);
            }
        });
    }

    @Override
    public void initBinding() {
        SelectionModeController.INSTANCE.addConfigurationChangingListener(configurationChangingListener = changing -> {
            if (changing) {
                showTempNode(nodeConfigurationChanging);
            } else {
                hideTempNode(nodeConfigurationChanging);
            }
        });
        bindLayoutXAndY();
        this.configurationView = ViewProviderI.getOrCreateViewComponentFor(configuration, AppMode.USE).getView();
        this.backgroundColor.bind(configuration.backgroundColorProperty());
        this.configWith.bind(this.configurationView.widthProperty());
        this.configHeight.bind(this.configurationView.heightProperty());

        this.ratio = configuration.computedWidthProperty().get() / configuration.computedHeightProperty().get();
        this.enableKeepRatio = configuration.keepConfigurationRatioProperty().get();
        // Compute scale for this configuration view
        this.currentScaleTransform = new Scale();
        this.updateCurrentScale();
        // Display and apply scale
        this.getTransforms().clear();
        this.getTransforms().add(this.currentScaleTransform);
        this.getChildren().add(this.configurationView);
        // Inform config of the display scaling (providing clean scaled finite values)
        configuration.displayedConfigurationScaleXProperty().bind(BindingUtils.bindToValueOrIfInfinityOrNan(currentScaleTransform.xProperty(), 1.0));
        configuration.displayedConfigurationScaleYProperty().bind(BindingUtils.bindToValueOrIfInfinityOrNan(currentScaleTransform.yProperty(), 1.0));
    }

    // To center elements
    private void bindLayoutXAndY() {
        this.layoutXProperty().bind(this.wantedWidth.subtract(this.configWith.multiply(this.scaleX)).divide(2.0));
        this.layoutYProperty().bind(this.wantedHeight.subtract(this.configHeight.multiply(this.scaleY)).divide(2.0));
    }

    private void updateCurrentScale() {
        if (this.currentScaleTransform != null && this.configurationView != null) {
            DoubleBinding keepRatioBinding = Bindings.createDoubleBinding(() -> {
                if (configurationView != null) {
                    return this.wantedWidth.get() / this.wantedHeight.get() > this.ratio
                            ? this.wantedHeight.get() / this.configurationView.heightProperty().get()
                            : this.wantedWidth.get() / this.configurationView.widthProperty().get();
                } else {
                    return 1.0;
                }
            }, this.wantedWidth, this.configurationView.widthProperty(), this.wantedHeight, this.configurationView.heightProperty());
            DoubleBinding xScale = this.enableKeepRatio ? keepRatioBinding : this.wantedWidth.divide(this.configurationView.widthProperty());
            DoubleBinding yScale = this.enableKeepRatio ? keepRatioBinding : this.wantedHeight.divide(this.configurationView.heightProperty());
            this.currentScaleTransform.xProperty().bind(xScale);
            this.currentScaleTransform.yProperty().bind(yScale);
            this.scaleX.bind(xScale);
            this.scaleY.bind(yScale);
        }
    }

    public ReadOnlyObjectProperty<Color> backgroundColorProperty() {
        return this.backgroundColor;
    }

    public Region getConfigurationView() {
        return configurationView;
    }

    public void unbindAndClean() {
        SelectionModeController.INSTANCE.removeConfigurationChangingListener(configurationChangingListener);
        BindingUtils.unbindAndSetNull(backgroundColor);
        BindingUtils.unbindAndSet(configuration.displayedConfigurationScaleXProperty(), 1.0);
        BindingUtils.unbindAndSet(configuration.displayedConfigurationScaleYProperty(), 1.0);
        BindingUtils.unbindAndSet(configWith, 0.0);
        BindingUtils.unbindAndSet(configHeight, 0.0);
        BindingUtils.unbindAndSet(scaleX, 0.0);
        BindingUtils.unbindAndSet(scaleY, 0.0);
        this.getTransforms().clear();
        if (currentScaleTransform != null) {
            BindingUtils.unbindAndSet(currentScaleTransform.xProperty(), 1.0);
            BindingUtils.unbindAndSet(currentScaleTransform.yProperty(), 1.0);
        }
        ConfigurationComponentUtils.exploreComponentViewChildrenToUnbind(this);
    }

    // JPD RETIREMENT EASTER EGG
    //========================================================================
    public void showJPDRetirementView(JPDRetirementView jpdRetirementViewP) {
        FXThreadUtils.runOnFXThread(() -> {
            WritableImage snapshot = this.snapshot(null, null);
            jpdRetirementViewP.initBeforeShow(snapshot);
            showTempNode(jpdRetirementViewP);
        });
    }

    public JPDRetirementView createJpdRetirementView() {
        return new JPDRetirementView(this, wantedWidth, wantedHeight);
    }
    //========================================================================

}
