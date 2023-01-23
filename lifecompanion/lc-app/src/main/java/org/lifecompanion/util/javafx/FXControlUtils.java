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

package org.lifecompanion.util.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.ui.controlsfx.glyphfont.Glyph;
import org.lifecompanion.util.converter.BoundDoubleConverter;
import org.lifecompanion.util.converter.BoundIntConverter;
import org.lifecompanion.util.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class FXControlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FXControlUtils.class);


    public static final int BUTTON_MAX_HEIGHT = -1;

    public static Triple<HBox, Label, Node> createHeader(String titleId, Consumer<Node> previousCallback) {
        Label labelTitle = new Label(Translation.getText(titleId));
        labelTitle.getStyleClass().addAll("text-h3", "text-fill-white");
        labelTitle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelTitle, Priority.ALWAYS);
        HBox.setMargin(labelTitle, new Insets(8.0));
        HBox boxTop = new HBox(labelTitle);

        Node nodePrevious = null;
        if (previousCallback != null) {
            boxTop.getStyleClass().add("opacity-80-hover");
            Glyph iconPrevious = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_LEFT).size(16).color(Color.WHITE);
            HBox.setMargin(iconPrevious, new Insets(10.0, 0.0, 10.0, 10.0));
            boxTop.getChildren().add(0, iconPrevious);
            boxTop.setOnMouseClicked(e -> {
                if (iconPrevious.isVisible()) {
                    previousCallback.accept(boxTop);
                }
            });
            Tooltip.install(boxTop, createTooltip(Translation.getText("profile.config.selection.steps.previous.button.tooltip")));
            nodePrevious = iconPrevious;
        }
        boxTop.setAlignment(Pos.CENTER_LEFT);
        boxTop.getStyleClass().addAll("background-primary-dark", "border-transparent");
        boxTop.setPrefHeight(50.0);

        return Triple.of(boxTop, labelTitle, nodePrevious);
    }

    public static Node createActionTableEntry(String actionTranslationId, Node buttonGraphic, Runnable action) {
        Label labelTitle = new Label(Translation.getText(actionTranslationId + ".title"));
        labelTitle.getStyleClass().add("text-weight-bold");
        GridPane.setHgrow(labelTitle, Priority.ALWAYS);
        GridPane.setFillWidth(labelTitle, true);
        labelTitle.setMaxWidth(Double.MAX_VALUE);

        Label labelDescription = new Label(Translation.getText(actionTranslationId + ".description"));
        labelDescription.setWrapText(true);
        GridPane.setMargin(labelDescription, new Insets(0, 0, 20.0, 0));

        Button buttonAction = createGraphicButton(buttonGraphic, actionTranslationId + ".description");
        buttonAction.setMinWidth(50.0);
        GridPane.setValignment(buttonAction, VPos.TOP);

        GridPane actionPane = new GridPane();
        actionPane.setVgap(5.0);
        actionPane.setHgap(5.0);
        actionPane.add(labelTitle, 0, 0);
        actionPane.add(labelDescription, 0, 1);
        actionPane.add(buttonAction, 1, 0, 1, 2);

        actionPane.setMaxWidth(Double.MAX_VALUE);

        if (action != null) {
            actionPane.setOnMouseClicked(me -> action.run());
            buttonAction.setOnAction(me -> action.run());
            actionPane.getStyleClass().addAll("opacity-60-pressed", "opacity-80-hover");
        }
        return actionPane;
    }

    public static Label createTitleLabel(String titleId) {
        Label label = new Label(Translation.getText(titleId));
        label.getStyleClass().addAll("text-font-size-110", "border-bottom-gray", "text-fill-dimgrey");
        label.setTextAlignment(TextAlignment.LEFT);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    public static Button createGraphicButton(final Node graphics, final String tooltipTranslationID) {
        Button button = new Button(null, graphics);
        button.getStyleClass().add("image-base-button");
        createAndAttachTooltip(button, tooltipTranslationID);
        return button;
    }

    public static Button createGraphicMaterialButton(final Node graphics, final String tooltipTranslationID) {
        Button button = createGraphicButton(graphics, tooltipTranslationID);
        button.getStyleClass().add("material-button-base");
        return button;
    }

    public static MenuButton createGraphicMenuButton(final Node graphics, final String tooltipTranslationID) {
        MenuButton button = new MenuButton(null, graphics);
        button.getStyleClass().addAll("opacity-80-hover", "opacity-60-pressed");
        button.setStyle("-fx-background-color: none, none;-fx-border-color: none;");
        createAndAttachTooltip(button, tooltipTranslationID);
        return button;
    }

    public static Button createTextButtonWithGraphics(final String label, final Node graphics, final String tooltipTranslationID) {
        Button button = new Button(label, graphics);
        button.getStyleClass().add("text-base-button");
        button.setContentDisplay(ContentDisplay.TOP);
        createAndAttachTooltip(button, tooltipTranslationID);
        return button;
    }

    public static Button createSimpleTextButton(final String label, final String tooltipTranslationID) {
        return createTextButtonWithGraphics(label, null, tooltipTranslationID);
    }

    public static Button createFixedWidthTextButton(final String label, final Node graphics, final double width, final String tooltipTranslationID) {
        Button btn = createTextButtonWithGraphics(label, graphics, tooltipTranslationID);
        btn.setPrefWidth(width);
        return btn;
    }

    public static Button createFixedWidthLeftTextButton(final String label, final Node graphics, final double width,
                                                        final String tooltipTranslationID) {
        Button btn = createLeftTextButton(label, graphics, tooltipTranslationID);
        btn.setPrefWidth(width);
        return btn;
    }

    public static Button createLeftTextButton(final String label, final Node graphics, final String tooltipTranslationID) {
        return createSideTextButton(label, graphics, TextAlignment.LEFT, Pos.CENTER_LEFT, ContentDisplay.LEFT, tooltipTranslationID);
    }

    public static Button createRightTextButton(final String label, final String imageURL, final String tooltipTranslationID) {
        return createRightTextButton(label, new ImageView(IconHelper.get(imageURL, -1, BUTTON_MAX_HEIGHT, true, true)),
                tooltipTranslationID);
    }

    public static Button createRightTextButton(final String label, final Node graphics, final String tooltipTranslationID) {
        return createSideTextButton(label, graphics, TextAlignment.RIGHT, Pos.CENTER_RIGHT, ContentDisplay.RIGHT, tooltipTranslationID);
    }

    private static Button createSideTextButton(final String label, final Node graphics, final TextAlignment ta, final Pos pos,
                                               final ContentDisplay cd, final String tooltipTranslationID) {
        Button textBtn = createTextButtonWithGraphics(label, graphics, tooltipTranslationID);
        textBtn.setTextAlignment(ta);
        textBtn.setAlignment(pos);
        textBtn.setGraphicTextGap(10.0);
        textBtn.setContentDisplay(cd);
        return textBtn;
    }

    public static Button createTextButtonWithIcon(final String label, final String imageURL, final String tooltipTranslationID) {
        return createTextButtonWithGraphics(label, new ImageView(IconHelper.get(imageURL, -1, BUTTON_MAX_HEIGHT, true, true)),
                tooltipTranslationID);
    }

    public static ToggleButton createGraphicsToggleButton(final String label, final Node graphics, final String tooltipTranslationID) {
        ToggleButton button = new ToggleButton(label, graphics);
        button.getStyleClass().add("text-base-button");
        button.setContentDisplay(ContentDisplay.TOP);
        createAndAttachTooltip(button, tooltipTranslationID);
        return button;
    }

    public static ToggleButton createTextToggleButton(final String label, final String imageURL, final String tooltipTranslationID) {
        return createGraphicsToggleButton(label, new ImageView(IconHelper.get(imageURL, -1, BUTTON_MAX_HEIGHT, true, true)),
                tooltipTranslationID);
    }

    public static Slider createBaseSlider(final double min, final double max, final double value) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(5.0);
        slider.setBlockIncrement(1.0);
        slider.setSnapToTicks(true);
        return slider;
    }

    private static void createAndAttachTooltip(final ButtonBase button, final String tooltipTranslationID) {
        if (tooltipTranslationID != null && button != null) {
            button.setTooltip(createTooltip(Translation.getText(tooltipTranslationID)));
        }
    }

    public static Tooltip createTooltip(final String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setAutoFix(true);
        tooltip.setAutoHide(true);
        tooltip.setMaxWidth(LCGraphicStyle.MAX_TOOLTIP_WIDTH);
        tooltip.setWrapText(true);
        tooltip.showDelayProperty().set(Duration.millis(LCConstant.TOOLTIP_SHOW_DELAY));
        tooltip.showDurationProperty().set(Duration.millis(LCConstant.TOOLTIP_DURATION));
        tooltip.hideDelayProperty().set(Duration.millis(LCConstant.TOOLTIP_CLOSE_DELAY));
        return tooltip;
    }

    public static void createAndAttachTooltip(Control control, String tooltipTextId) {
        control.setTooltip(createTooltip(Translation.getText(tooltipTextId)));
    }

    public static void createAndAttachTooltip(Node node, String tooltipTextId) {
        Tooltip.install(node, createTooltip(Translation.getText(tooltipTextId)));
    }

    /**
     * @return a toggle group that disabled unselected button selection
     */
    public static ToggleGroup createAlwaysSelectedToggleGroup() {
        ToggleGroup group = new ToggleGroup();
        group.selectedToggleProperty().addListener((obs, ov, nv) -> {
            if (nv == null) {
                ov.setSelected(true);
            }
        });
        return group;
    }

    public static Spinner<Integer> createIntSpinner(final int min, final int max, final int initialValue, final int step, final double width) {
        Spinner<Integer> spinner = createBaseSpinner(width);
        SpinnerValueFactory.IntegerSpinnerValueFactory integerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max,
                initialValue, step);
        integerValueFactory.setConverter(new BoundIntConverter(min, max));
        spinner.setValueFactory(integerValueFactory);
        return spinner;
    }

    public static Spinner<Double> createDoubleSpinner(final double min, final double max, final double initialValue, final double step,
                                                      final double width) {
        Spinner<Double> spinner = createBaseSpinner(width);
        final SpinnerValueFactory.DoubleSpinnerValueFactory doubleSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max,
                initialValue, step);
        doubleSpinnerValueFactory.setConverter(new BoundDoubleConverter(min, max));
        spinner.setValueFactory(doubleSpinnerValueFactory);
        return spinner;
    }

    public static <T> Spinner<T> createBaseSpinner(final double width) {
        Spinner<T> spinner = new Spinner<>();
        spinner.setPrefWidth(width);
        spinner.getEditor().setAlignment(Pos.CENTER);
        spinner.getStyleClass().add("base-spinner");
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinner.setEditable(true);
        // WORKAROUND : spinner doesn't commit its value on focus lost, so we listen for a focus lost
        // and commit its value manually
        spinner.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                if (spinner.isEditable()) {
                    String text = spinner.getEditor().getText();
                    SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
                    if (valueFactory != null) {
                        StringConverter<T> converter = valueFactory.getConverter();
                        if (converter != null) {
                            try {
                                T value = converter.fromString(text);
                                valueFactory.setValue(value);
                            } catch (Exception e) {
                                LOGGER.warn("Can't parse spinner value after focus loss {}", text, e);
                            }
                        }
                    }
                }
            }
        });
        return spinner;
    }

    public static ToggleSwitch createToggleSwitch(String toggleTextId, String tooltipTextId) {
        ToggleSwitch toggleSwitch = new ToggleSwitch(Translation.getText(toggleTextId));
        toggleSwitch.setMaxWidth(Double.MAX_VALUE);//Restore ToggleSwitch sizing before ControlsFX 8.40.13
        if (tooltipTextId != null) {
            toggleSwitch.setTooltip(createTooltip(Translation.getText(tooltipTextId)));
        }
        return toggleSwitch;
    }
}
