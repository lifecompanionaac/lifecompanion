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
package org.lifecompanion.use.view.pane;

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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.control.events.WritingEventSource;
import org.lifecompanion.api.mode.AppMode;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.data.control.GlobalKeyEventManager;
import org.lifecompanion.base.data.control.SelectionModeController;
import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.function.Predicate;

/**
 * A component that will display a configuration with its scale options.<br>
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SimpleUseConfigurationDisplayer extends Group implements LCViewInitHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUseConfigurationDisplayer.class);

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

    private double ratio;
    private boolean enableKeepRatio = true;
    private final DoubleProperty scaleX;
    private final DoubleProperty scaleY;
    private final SimpleObjectProperty<Color> backgroundColor;

    public SimpleUseConfigurationDisplayer(final ReadOnlyDoubleProperty wantedWidthP, final ReadOnlyDoubleProperty wantedHeightP) {
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
        ImageView imageViewText = new ImageView(IconManager.get(LCConstant.LC_BIG_ICON_PATH));
        ImageView imageViewCopyright = new ImageView(IconManager.get(LCConstant.LC_COPYRIGHT_ICON_PATH));
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
        // Configuration change indicator
        SelectionModeController.INSTANCE.setListenerStartChangeConfigurationInUseMode(changing -> {
            if (changing) {
                showConfigurationChanging();
            } else {
                restoreAfterConfigurationChangingDisplayed();
            }
        });
        SelectionModeController.INSTANCE.setClearPressedKeyListener(pressedKey::clear);
        // Register filter for selection mode controller event
        this.addEventFilter(KeyEvent.ANY, (event) -> {
            // Keyboard event without filter except no repeat
            if (event.getEventType() != KeyEvent.KEY_PRESSED || !this.pressedKey.contains(event.getCode())) {
                GlobalKeyEventManager.INSTANCE.javaFxEventFired(event);
            }
            // Filter the next key event on key press (happens before key typed)
            if (KeyEvent.KEY_PRESSED == event.getEventType() && !this.pressedKey.contains(event.getCode())
                    && (SelectionModeController.INSTANCE.isValidSelectionModeKeyboardEvent(event) || SelectionModeController.INSTANCE.isValidNextScanSelectionModeKeyboardEvent(event))) {
                this.cancelNextKeyTypedEvent = true;
            } else if (KeyEvent.KEY_PRESSED == event.getEventType() && !this.pressedKey.contains(event.getCode())) {
                this.cancelNextKeyTypedEvent = false;
            }
            // Try to execute the keyevent
            if ((event.getEventType() != KeyEvent.KEY_PRESSED || !this.pressedKey.contains(event.getCode()))
                    && SelectionModeController.INSTANCE.globalKeyboardEvent(event)) {
                event.consume();
            } else {
                if (KeyEvent.KEY_PRESSED == event.getEventType()) {
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
            }
        });

    }

    private void showConfigurationChanging() {
        LCUtils.runOnFXThread(() -> {
            this.getTransforms().clear();
            this.layoutXProperty().unbind();
            this.layoutYProperty().unbind();
            this.setLayoutX(0.0);
            this.setLayoutY(0.0);
            this.getChildren().add(nodeConfigurationChanging);
        });
    }

    private void restoreAfterConfigurationChangingDisplayed() {
        LCUtils.runOnFXThread(() -> {
            this.getChildren().remove(nodeConfigurationChanging);
            bindLayoutXAndY();
        });
    }

    public void addMouseListener(final Scene element) {
        this.addMouseListener(element, null);
    }

    public void addMouseListener(final Scene element, final Predicate<MouseEvent> eventFilter) {
        EventHandler<MouseEvent> mouseEventFilter = (event) -> {
            if (AppController.INSTANCE.currentModeProperty().get() == AppMode.USE && (eventFilter == null || eventFilter.test(event))
                    && SelectionModeController.INSTANCE.globalMouseEvent(event)) {
                event.consume();
            }
        };
        // Registrer filter on mouse event
        element.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEventFilter);
        element.addEventFilter(MouseEvent.MOUSE_ENTERED, mouseEventFilter);
        element.addEventFilter(MouseEvent.MOUSE_EXITED, mouseEventFilter);
        element.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEventFilter);
    }

    @Override
    public void initBinding() {
        AppController.INSTANCE.currentUseConfigurationProperty()
                .addListener((observableP, oldValueP, newValueP) -> {
                    if (oldValueP != null) {
                        this.getChildren().remove(this.configurationView);
                        // Free configuration binding and set
                        this.freePreviousConfiguration(oldValueP);
                    }
                    // Display the view
                    this.updateConfiguration(newValueP);
                });
        bindLayoutXAndY();
    }

    // To center elements
    private void bindLayoutXAndY() {
        this.layoutXProperty().bind(this.wantedWidth.subtract(this.configWith.multiply(this.scaleX)).divide(2.0));
        this.layoutYProperty().bind(this.wantedHeight.subtract(this.configHeight.multiply(this.scaleY)).divide(2.0));
    }

    private void freePreviousConfiguration(LCConfigurationI previous) {
        previous.displayedConfigurationScaleXProperty().unbind();
        previous.displayedConfigurationScaleXProperty().set(1.0);
        previous.displayedConfigurationScaleYProperty().unbind();
        previous.displayedConfigurationScaleYProperty().set(1.0);
        this.configurationView = null;
        this.configWith.unbind();
        this.configHeight.unbind();
        this.backgroundColor.unbind();
        this.scaleX.unbind();
        this.scaleY.unbind();
        this.currentScaleTransform.xProperty().unbind();
        this.currentScaleTransform.yProperty().unbind();
    }

    public void updateConfiguration(final LCConfigurationI newValueP) {
        restoreAfterConfigurationChangingDisplayed();
        if (newValueP != null) {
            Region view = newValueP.getDisplay(AppController.INSTANCE.getViewProvider(AppMode.USE), true).getView();
            this.configurationView = view;
            this.backgroundColor.bind(newValueP.backgroundColorProperty());
            this.configWith.bind(this.configurationView.widthProperty());
            this.configHeight.bind(this.configurationView.heightProperty());
            this.showConfiguration(newValueP);
        }
    }

    private void showConfiguration(final LCConfigurationI config) {
        this.ratio = config.computedWidthProperty().get() / config.computedHeightProperty().get();
        this.enableKeepRatio = config.keepConfigurationRatioProperty().get();
        // Compute scale for this configuration view
        this.currentScaleTransform = new Scale();
        // this.currentScaleTransform.
        this.updateCurrentScale();
        // Display and apply scale
        this.getTransforms().clear();
        this.getTransforms().add(this.currentScaleTransform);
        this.getChildren().add(this.configurationView);
        // Inform config of the display scaling (providing clean scaled finite values)
        config.displayedConfigurationScaleXProperty().bind(LCUtils.bindToValueOrIfInfinityOrNan(currentScaleTransform.xProperty(), 1.0));
        config.displayedConfigurationScaleYProperty().bind(LCUtils.bindToValueOrIfInfinityOrNan(currentScaleTransform.yProperty(), 1.0));
        // To receive key event
        this.requestFocus();
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

}
