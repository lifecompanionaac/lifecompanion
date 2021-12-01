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
package org.lifecompanion.base.data.common;

import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;

/**
 * Useful class to create/manage ui components.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UIUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(UIUtils.class);
    private static final int BUTTON_MAX_HEIGHT = -1;

    private UIUtils() {
    }

    // Class part : "Buttons"
    //========================================================================
    public static Button createGraphicButton(final Node graphics, final String tooltipTranslationID) {
        Button button = new Button(null, graphics);
        button.getStyleClass().add("image-base-button");
        UIUtils.createAndAttachTooltip(button, tooltipTranslationID);
        return button;
    }

    public static Button createGraphicMaterialButton(final Node graphics, final String tooltipTranslationID) {
        Button button = UIUtils.createGraphicButton(graphics, tooltipTranslationID);
        button.getStyleClass().add("material-button-base");
        return button;
    }

    public static MenuButton createGraphicMenuButton(final Node graphics, final String tooltipTranslationID) {
        MenuButton button = new MenuButton(null, graphics);
        button.getStyleClass().add("image-base-button");
        UIUtils.createAndAttachTooltip(button, tooltipTranslationID);
        return button;
    }

    public static Button createTextButtonWithGraphics(final String label, final Node graphics, final String tooltipTranslationID) {
        Button button = new Button(label, graphics);
        button.getStyleClass().add("text-base-button");
        button.setContentDisplay(ContentDisplay.TOP);
        UIUtils.createAndAttachTooltip(button, tooltipTranslationID);
        return button;
    }

    public static Button createSimpleTextButton(final String label, final String tooltipTranslationID) {
        return UIUtils.createTextButtonWithGraphics(label, null, tooltipTranslationID);
    }

    public static Button createFixedWidthTextButton(final String label, final Node graphics, final double width, final String tooltipTranslationID) {
        Button btn = UIUtils.createTextButtonWithGraphics(label, graphics, tooltipTranslationID);
        btn.setPrefWidth(width);
        return btn;
    }

    public static Button createFixedWidthLeftTextButton(final String label, final Node graphics, final double width,
                                                        final String tooltipTranslationID) {
        Button btn = UIUtils.createLeftTextButton(label, graphics, tooltipTranslationID);
        btn.setPrefWidth(width);
        return btn;
    }

    public static Button createLeftTextButton(final String label, final Node graphics, final String tooltipTranslationID) {
        return UIUtils.createSideTextButton(label, graphics, TextAlignment.LEFT, Pos.CENTER_LEFT, ContentDisplay.LEFT, tooltipTranslationID);
    }

    public static Button createRightTextButton(final String label, final String imageURL, final String tooltipTranslationID) {
        return UIUtils.createRightTextButton(label, new ImageView(IconManager.get(imageURL, -1, UIUtils.BUTTON_MAX_HEIGHT, true, true)),
                tooltipTranslationID);
    }

    public static Button createRightTextButton(final String label, final Node graphics, final String tooltipTranslationID) {
        return UIUtils.createSideTextButton(label, graphics, TextAlignment.RIGHT, Pos.CENTER_RIGHT, ContentDisplay.RIGHT, tooltipTranslationID);
    }

    private static Button createSideTextButton(final String label, final Node graphics, final TextAlignment ta, final Pos pos,
                                               final ContentDisplay cd, final String tooltipTranslationID) {
        Button textBtn = UIUtils.createTextButtonWithGraphics(label, graphics, tooltipTranslationID);
        textBtn.setTextAlignment(ta);
        textBtn.setAlignment(pos);
        textBtn.setGraphicTextGap(10.0);
        textBtn.setContentDisplay(cd);
        return textBtn;
    }

    public static Button createTextButtonWithIcon(final String label, final String imageURL, final String tooltipTranslationID) {
        return UIUtils.createTextButtonWithGraphics(label, new ImageView(IconManager.get(imageURL, -1, UIUtils.BUTTON_MAX_HEIGHT, true, true)),
                tooltipTranslationID);
    }

    public static ToggleButton createGraphicsToggleButton(final String label, final Node graphics, final String tooltipTranslationID) {
        ToggleButton button = new ToggleButton(label, graphics);
        button.getStyleClass().add("text-base-button");
        button.setContentDisplay(ContentDisplay.TOP);
        UIUtils.createAndAttachTooltip(button, tooltipTranslationID);
        return button;
    }

    public static ToggleButton createTextToggleButton(final String label, final String imageURL, final String tooltipTranslationID) {
        return UIUtils.createGraphicsToggleButton(label, new ImageView(IconManager.get(imageURL, -1, UIUtils.BUTTON_MAX_HEIGHT, true, true)),
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
            button.setTooltip(UIUtils.createTooltip(Translation.getText(tooltipTranslationID)));
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
    //========================================================================

    public static Label createTitleLabel(String titleId) {
        Label label = new Label(Translation.getText(titleId));
        label.getStyleClass().add("generic-part-title");
        label.setTextAlignment(TextAlignment.LEFT);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    // Class part : "Spinner"
    //========================================================================
    public static Spinner<Integer> createIntSpinner(final int min, final int max, final int initialValue, final int step, final double width) {
        Spinner<Integer> spinner = UIUtils.createBaseSpinner(width);
        SpinnerValueFactory.IntegerSpinnerValueFactory integerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max,
                initialValue, step);
        integerValueFactory.setConverter(new BoundIntConverter(min, max));
        spinner.setValueFactory(integerValueFactory);
        return spinner;
    }

    public static Spinner<Double> createDoubleSpinner(final double min, final double max, final double initialValue, final double step,
                                                      final double width) {
        Spinner<Double> spinner = UIUtils.createBaseSpinner(width);
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
                                UIUtils.LOGGER.warn("Can't parse spinner value after focus loss {}", text, e);
                            }
                        }
                    }
                }
            }
        });
        return spinner;
    }
    //========================================================================


    // Class part : "Images"
    //========================================================================

    /**
     * Return a view port to contains the full image without any border and without breaking the ratio
     *
     * @param image the image we want to create the view port
     * @return the view port, or null if given image is null
     */
    public static Rectangle2D computeFullImageViewPort(final Image image) {
        if (image != null) {
            double minSize = Math.min(image.getWidth(), image.getHeight());
            Rectangle2D viewPort = new Rectangle2D(0.0, 0.0, minSize, minSize);
            return viewPort;
        } else {
            return null;
        }
    }

    public static Image takeNodeSnapshot(final Node node, final double wantedWidth, final double wantedHeight) {
        return takeNodeSnapshot(node, wantedWidth, wantedHeight, false);
    }

    /**
     * Take a snapshot of the given node, even if the node is not currently display.<br>
     * The snapshot ratio is kept, with a resulting size depending on wantedWidth and wantedHeight.<br>
     * The resulting size can be scaled down from original, but never scaled up.
     *
     * @param wantedWidth  the result snapshot width (-1 to keep original, or to compute from height)
     * @param wantedHeight the result snapshot height (-1 to keep original, or to compute from width)
     * @param node         the node we should take a snapshot
     * @return the snapshot for the given node
     */
    public static Image takeNodeSnapshot(final Node node, final double wantedWidth, final double wantedHeight, boolean canScaleUp) {
        Image snapshot = null;
        //Check if node has a parent
        if (node.getParent() != null) {
            snapshot = UIUtils.executeSnapshot(node, wantedWidth, wantedHeight, canScaleUp);
        } else {
            //Init group if needed
            Group group = new Group();
            new Scene(group);
            //Take snapshot
            group.getChildren().add(node);
            snapshot = UIUtils.executeSnapshot(node, wantedWidth, wantedHeight, canScaleUp);
            group.getChildren().remove(node);
        }
        return snapshot;
    }

    private static Image executeSnapshot(final Node node, double wantedWidth, double wantedHeight, boolean canScaleUp) {
        Bounds nodeBounds = node.getBoundsInParent();
        SnapshotParameters snapParams = null;
        // Fix only width or height ? (if needed)
        if (wantedHeight > 0 || wantedWidth > 0) {
            wantedWidth = wantedWidth <= 0 ? nodeBounds.getWidth() : wantedWidth;
            wantedHeight = wantedHeight <= 0 ? nodeBounds.getHeight() : wantedHeight;
            // Compute scale to keep ratio
            double originalRatio = nodeBounds.getWidth() / nodeBounds.getHeight();
            double scale = wantedWidth / wantedHeight > originalRatio ? wantedHeight / wantedWidth : wantedWidth / wantedHeight;
            // Only scale down if wanted (keep lowest memory footprint)
            if (scale < 1 || canScaleUp) {
                snapParams = new SnapshotParameters();
                snapParams.setTransform(new Scale(scale, scale));
            }
        }
        return node.snapshot(snapParams, null);
    }
    //========================================================================

    // Class part : "Performance"
    //========================================================================
    public static void applyPerformanceConfiguration(final Node node) {
        node.setCache(LCGraphicStyle.ENABLE_NODE_CACHE_CONFIG_MODE);
        node.setCacheHint(LCGraphicStyle.CACHE_HINT_CONFIG_MODE);
    }
    //========================================================================

    // Class part : "Color"
    //========================================================================
    private static final double WHITE_COLOR_THRESHOLD = 1.0;
    private static final Color COLOR_DARK = Color.web("#181818");

    public static Color getConstratColor(final Color color) {
        if (color != null && color.getOpacity() >= 0.4 && color.getBlue() + color.getRed() + color.getGreen() < UIUtils.WHITE_COLOR_THRESHOLD) {
            return Color.LIGHTGRAY;
        } else {
            return UIUtils.COLOR_DARK;
        }
    }
    //========================================================================

    public static boolean openUrlInDefaultBrowser(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(url));
                return true;
            } catch (Exception e) {
                LOGGER.warn("Couldn't open default browser to {}", url, e);
            }
        }
        return false;
    }

    public static Window getSourceWindow(Node source) {
        if (source != null) {
            if (source.getScene() != null) {
                Window window = source.getScene().getWindow();
                // FIXME : should test if visible and not null and fall back to known windows...
                return window;
            }
        }
        return null;
    }

    public static Node getSourceFromEvent(Event event) {
        if (event != null) {
            if (event.getSource() instanceof Node) {
                return (Node) event.getSource();
            } else if (event.getTarget() instanceof Node) {
                return (Node) event.getTarget();
            }
        }
        return AppController.INSTANCE.getMainStage().getScene().getRoot();
    }

    // MANUAL SIZING
    //========================================================================
    public static void setFixedWidth(Region region, double width) {
        region.setMinWidth(width);
        region.setPrefWidth(width);
        region.setMaxWidth(width);
    }

    public static void setFixedHeight(Region region, double height) {
        region.setMinHeight(height);
        region.setPrefHeight(height);
        region.setMaxHeight(height);
    }

    public static void setFixedSize(Region region, double width, double height) {
        setFixedWidth(region, width);
        setFixedHeight(region, height);
    }
    //========================================================================
}
