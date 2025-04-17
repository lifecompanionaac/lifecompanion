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
package org.lifecompanion.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.shape.Rectangle;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.impl.configurationcomponent.WriterEntry;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.editaction.CommonActions;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.ui.configurationcomponent.usemode.UseModeConfigurationDisplayer;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.ui.selectionmode.AbstractSelectionModeView;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.util.javafx.FXControlUtils;

/**
 * The scene that display the component when the application is in use mode in "normal" LifeCompanion (use and config mode)<br>
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseModeScene extends Scene implements LCViewInitHelper {
    private static final KeyCombination KEY_COMBINATION_COPY = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination KEY_COMBINATION_PASTE = new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN);

    /**
     * The root of this scene
     */
    private final Group root;

    /**
     * The configuration displayer
     */
    private UseModeConfigurationDisplayer configurationDisplayer;

    /**
     * Button to go back to configuration mode
     */
    private Button buttonGoToConfigMode;

    /**
     * Button to enable/disable fullscreen
     */
    private Button buttonFullscreen;

    private Button buttonPreviousConfiguration;

    private Rectangle backgroundReductionRectangle;
    private ChangeListener<SelectionModeI> selectionModeChangeListener;

    private final LCConfigurationI configuration;


    public UseModeScene(LCConfigurationI configuration) {
        super(new Group());
        this.configuration = configuration;
        this.root = (Group) getRoot();
        this.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);//FIXME : replace with use mode css
    }

    @Override
    public void initUI() {
        this.backgroundReductionRectangle = new Rectangle();
        this.backgroundReductionRectangle.setStrokeWidth(0.0);
        this.backgroundReductionRectangle.widthProperty().bind(widthProperty());
        this.backgroundReductionRectangle.heightProperty().bind(heightProperty());
        this.root.getChildren().add(this.backgroundReductionRectangle);

        this.configurationDisplayer = new UseModeConfigurationDisplayer(this.configuration, this.widthProperty(), this.heightProperty());
        this.root.getChildren().add(this.configurationDisplayer);


        // Button go config mode
        this.buttonGoToConfigMode = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).size(18).color(LCGraphicStyle.SECOND_DARK),
                null);
        this.buttonGoToConfigMode.getStyleClass().add("background-almost-transparent");
        this.buttonGoToConfigMode.setLayoutX(-5.0);
        this.buttonGoToConfigMode.setLayoutY(-5.0);
        this.buttonGoToConfigMode.setFocusTraversable(false);

        // Button previous configuration
        this.buttonPreviousConfiguration = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.BARS).size(17).color(LCGraphicStyle.SECOND_DARK),
                null);
        this.buttonPreviousConfiguration.getStyleClass().add("background-almost-transparent");
        this.buttonPreviousConfiguration.setLayoutX(-5.0);
        this.buttonPreviousConfiguration.setLayoutY(18.0);
        this.buttonPreviousConfiguration.setFocusTraversable(false);


        // Button to switch fullscreen state
        buttonFullscreen = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.EXPAND).size(18).color(LCGraphicStyle.SECOND_DARK),
                null);
        this.buttonFullscreen.getStyleClass().add("background-almost-transparent");
        this.buttonFullscreen.layoutXProperty().bind(widthProperty().subtract(28.0));
        this.buttonFullscreen.setLayoutY(-5.0);
        this.buttonFullscreen.setFocusTraversable(false);


        if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_SWITCH_TO_EDIT_MODE)) {
            this.root.getChildren().addAll(this.buttonGoToConfigMode);
        }
        if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_WINDOW_FULLSCREEN)) {
            this.root.getChildren().addAll(buttonFullscreen);
        }
        this.root.getChildren().addAll(buttonPreviousConfiguration);

        if (this.configuration.getSelectionModeParameter().hideMouseCursorProperty().get()) {
            this.setCursor(Cursor.NONE);
        }
    }

    @Override
    public void initBinding() {
        this.fillProperty().bind(this.configurationDisplayer.backgroundColorProperty());
        this.selectionModeChangeListener = (obs, ov, nv) -> {
            if (nv != null && nv.getSelectionView() instanceof AbstractSelectionModeView<?> selectionModeView) {
                this.backgroundReductionRectangle.visibleProperty().bind(selectionModeView.getBackgroundReductionRectangle().visibleProperty());
                this.backgroundReductionRectangle.opacityProperty().bind(selectionModeView.getBackgroundReductionRectangle().opacityProperty().multiply(0.8));
                this.backgroundReductionRectangle.fillProperty().bind(selectionModeView.getBackgroundReductionRectangle().fillProperty());
            }
        };
        this.configuration.selectionModeProperty().addListener(new WeakChangeListener<>(selectionModeChangeListener));
        this.buttonFullscreen.visibleProperty().bind(UserConfigurationController.INSTANCE.disableFullscreenShortcutProperty().not());
        this.buttonPreviousConfiguration.visibleProperty().bind(SelectionModeController.INSTANCE.hasPreviousConfigInUseModeProperty().and(UserConfigurationController.INSTANCE.enablePreviousConfigurationShortcutProperty()));
    }

    @Override
    public void initListener() {
        //Keyboard shortcut
        this.addEventHandler(KeyEvent.KEY_RELEASED, eventP -> {
            if (CommonActions.KEY_COMBINATION_GO_CONFIG_MODE.match(eventP)) {
                if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_SWITCH_TO_EDIT_MODE)) {
                    CommonActions.HANDLER_GO_CONFIG_MODE_SKIP_CHECK.handle(null);
                    eventP.consume();
                }
            }
            if (CommonActions.KEY_COMBINATION_SWITCH_FULLSCREEN.match(eventP)) {
                if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_WINDOW_FULLSCREEN)) {
                    CommonActions.HANDLER_SWITCH_FULLSCREEN.handle(null);
                    eventP.consume();
                }
            }
            if (KEY_COMBINATION_COPY.match(eventP) && !configuration.virtualKeyboardProperty().get()) {
                copyFromCurrentTextEditor();
                eventP.consume();
            }
            if (KEY_COMBINATION_PASTE.match(eventP) && !configuration.virtualKeyboardProperty().get()) {
                pasteToCurrentTextEditor();
                eventP.consume();
            }
        });
        //Filter mouse event to keep the goToConfig event
        this.configurationDisplayer.addMouseListener(this, mouseEvent -> {
            if (mouseEvent.getTarget() instanceof Node target) {
                return !isChildOf(target, this.buttonGoToConfigMode) && !isChildOf(target, this.buttonFullscreen) && !isChildOf(target, buttonPreviousConfiguration);
            }
            return true;
        });
        this.buttonGoToConfigMode.setOnAction(CommonActions.HANDLER_GO_CONFIG_MODE_CHECK);
        // Button to switch fullscreen mode
        this.buttonFullscreen.setOnAction(CommonActions.HANDLER_SWITCH_FULLSCREEN);

        this.buttonPreviousConfiguration.setOnAction(e -> SelectionModeController.INSTANCE.changeConfigurationForPrevious());

        SessionStatsController.INSTANCE.registerScene(this);
    }

    private boolean isChildOf(Node node, Node parent) {
        if (node == parent) return true;
        if (node.getParent() != null) return isChildOf(node.getParent(), parent);
        return false;
    }

    private void pasteToCurrentTextEditor() {
        Clipboard systemClipboard = Clipboard.getSystemClipboard();
        if (systemClipboard.hasString()) {
            String clipboardString = systemClipboard.getString();
            if (StringUtils.isNotBlank(clipboardString)) {
                WritingStateController.INSTANCE.insert(WritingEventSource.USER_ACTIONS, new WriterEntry(clipboardString, true));
            }
        }
    }

    private void copyFromCurrentTextEditor() {
        String currentText = WritingStateController.INSTANCE.currentTextProperty().get();
        final ClipboardContent content = new ClipboardContent();
        content.putString(currentText);
        Clipboard.getSystemClipboard().setContent(content);
    }

    public void unbindAndClean() {
        this.selectionModeChangeListener = null;
        BindingUtils.unbindAndSet(this.backgroundReductionRectangle.visibleProperty(), false);
        BindingUtils.unbindAndSet(this.backgroundReductionRectangle.opacityProperty(), 0.0);
        BindingUtils.unbindAndSetNull(this.backgroundReductionRectangle.fillProperty());
        SessionStatsController.INSTANCE.unregisterScene(this);
        this.configurationDisplayer.unbindAndClean();
        this.buttonFullscreen.visibleProperty().unbind();
        this.buttonPreviousConfiguration.visibleProperty().unbind();
    }

    public void requestFocus() {
        if (configurationDisplayer != null) {
            configurationDisplayer.requestFocus();
        }
    }

    public UseModeConfigurationDisplayer getConfigurationDisplayer() {
        return configurationDisplayer;
    }
}
