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

package org.lifecompanion.ui.common.control.generic.colorpicker;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.LangUtils;
import org.lifecompanion.util.javafx.ColorUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.StageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class LCColorCustomColorStage extends Stage implements LCViewInitHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(LCColorCustomColorStage.class);

    private static final Color DEFAULT_COLOR = Color.TRANSPARENT;
    private static final double SELECTION_SIZE = 10;
    private static final double BAND_SIZE = 20;
    private static final double TOTAL_SIZE = 200;

    private final DoubleProperty hue;
    private final DoubleProperty saturation;
    private final DoubleProperty brightness;
    private final DoubleProperty opacity;

    private Rectangle hueSelectionRect, colorSelectionRect, opacitySelectionRect;

    private Pane hueSelectorPane, opacityShowPane, opacitySelectorPane, colorSelectionPane;
    private Rectangle resultRectangle, previousColorRectangle;

    private Button buttonOk, buttonCancel, buttonCopyHex;

    private TextField fieldColorHex;

    private final ObjectProperty<Color> selectedColor;

    private Consumer<Color> onNextSelection;

    public LCColorCustomColorStage() {
        this.selectedColor = new SimpleObjectProperty<>();
        this.hue = new ColorDoubleProperty(0);
        this.saturation = new ColorDoubleProperty(1);
        this.brightness = new ColorDoubleProperty(1);
        this.opacity = new ColorDoubleProperty(1.0);
        initAll();
    }

    private void setCurrentColorTo(Color color) {
        this.hue.set(color.getHue());
        this.saturation.set(color.getSaturation());
        this.brightness.set(color.getBrightness());
        this.opacity.set(color.getOpacity());
    }

    private class ColorDoubleProperty extends SimpleDoubleProperty {
        public ColorDoubleProperty(double v) {
            super(v);
        }

        @Override
        protected void invalidated() {
            selectedColor.set(Color.hsb(hue.get(), saturation.get(), brightness.get(), opacity.get()));
        }
    }

    @Override
    public void initUI() {
        VBox totalVbox = new VBox(5.0);
        totalVbox.setPadding(new Insets(10.0));

        // Hue selection pane
        hueSelectorPane = new Pane();
        hueSelectorPane.setBackground(new Background(new BackgroundFill(createHueGradient(), CornerRadii.EMPTY, Insets.EMPTY)));
        hueSelectorPane.setPrefSize(TOTAL_SIZE, BAND_SIZE);
        hueSelectionRect = createShowSelectionRectangle(SELECTION_SIZE, BAND_SIZE);
        hueSelectorPane.getChildren().add(hueSelectionRect);

        // Color selection pane
        colorSelectionPane = new Pane();
        colorSelectionPane.setPrefSize(TOTAL_SIZE - BAND_SIZE, TOTAL_SIZE - BAND_SIZE);
        Pane overlayPane1 = createOverlayPane(Color.WHITE, 1, 0, 1, 0);
        Pane overlayPane2 = createOverlayPane(Color.BLACK, 0, 1, 0, 1);
        colorSelectionRect = createShowSelectionRectangle(SELECTION_SIZE, SELECTION_SIZE);
        colorSelectionPane.getChildren().addAll(overlayPane1, overlayPane2, colorSelectionRect);

        // Opacity selection
        opacityShowPane = new Pane();
        opacityShowPane.setPrefSize(BAND_SIZE, TOTAL_SIZE - BAND_SIZE);
        opacitySelectorPane = new Pane();
        opacitySelectorPane.getStyleClass().add("background-image-transparent");
        opacitySelectorPane.setPrefSize(BAND_SIZE, TOTAL_SIZE - BAND_SIZE);
        opacitySelectionRect = createShowSelectionRectangle(BAND_SIZE, SELECTION_SIZE);
        opacitySelectorPane.getChildren().addAll(opacityShowPane, opacitySelectionRect);

        BorderPane borderPaneSelector = new BorderPane();
        borderPaneSelector.setTop(hueSelectorPane);
        borderPaneSelector.setCenter(colorSelectionPane);
        borderPaneSelector.setRight(opacitySelectorPane);
        BorderPane.setMargin(hueSelectorPane, new Insets(5));
        BorderPane.setMargin(colorSelectionPane, new Insets(5));
        BorderPane.setMargin(opacitySelectorPane, new Insets(5));

        resultRectangle = new Rectangle(BAND_SIZE, BAND_SIZE);
        Pane paneResult = new Pane(resultRectangle);
        paneResult.getStyleClass().add("background-image-transparent");
        previousColorRectangle = new Rectangle(BAND_SIZE, BAND_SIZE);
        Pane paneResultP = new Pane(previousColorRectangle);
        paneResultP.getStyleClass().add("background-image-transparent");

        final Label labelPreviousColor = new Label(Translation.getText("lc.colorpicker.previous.value"));
        labelPreviousColor.getStyleClass().add(".text-label-right");
        final Label labelCurrentColor = new Label(Translation.getText("lc.colorpicker.current.value"));
        HBox boxResult = new HBox(5.0, labelPreviousColor, paneResultP, paneResult, labelCurrentColor);
        boxResult.setAlignment(Pos.CENTER);

        fieldColorHex = new TextField();
        buttonCopyHex = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.COPY).size(14).color(LCGraphicStyle.MAIN_DARK), null);
        HBox boxFieldColorHex = new HBox(5.0, fieldColorHex, buttonCopyHex);
        boxFieldColorHex.setAlignment(Pos.CENTER);
        boxFieldColorHex.getStyleClass().add("text-font-size-90");

        buttonOk = FXControlUtils.createLeftTextButton(Translation.getText("general.configuration.scene.ok.button"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHECK).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        buttonCancel = FXControlUtils.createLeftTextButton(Translation.getText("general.configuration.scene.cancel.button"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TIMES).size(16).color(LCGraphicStyle.SECOND_DARK), null);

        HBox boxButton = new HBox(8.0, buttonCancel, buttonOk);
        boxButton.setAlignment(Pos.CENTER);
        VBox.setMargin(boxButton, new Insets(10, 0, 0, 0));

        totalVbox.getChildren().addAll(boxFieldColorHex, borderPaneSelector, boxResult, boxButton);

        // Stage
        this.initStyle(StageStyle.UTILITY);
        this.initModality(Modality.APPLICATION_MODAL);
        StageUtils.applyDefaultStageConfiguration(this);
        this.setResizable(false);

        Scene sceneContent = new Scene(totalVbox);
        sceneContent.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);

        this.setScene(sceneContent);
    }

    void showCustomDialog(Color previousColor, Consumer<Color> onSelection) {
        this.onNextSelection = onSelection;
        final Color previousColorClean = previousColor != null ? previousColor : DEFAULT_COLOR;
        previousColorRectangle.setFill(previousColorClean);
        setCurrentColorTo(previousColorClean);
        this.hueSelectorPane.requestFocus();
        StageUtils.centerOnOwnerOrOnCurrentStageAndShow(this);
        this.toFront();
    }

    private Rectangle createShowSelectionRectangle(double w, double h) {
        Rectangle rectangle = new Rectangle(w, h);
        rectangle.setMouseTransparent(true);
        rectangle.setManaged(false);
        rectangle.setFill(Color.WHITE.deriveColor(0, 1, 1, 0.3));
        rectangle.setStrokeWidth(1);
        rectangle.setStroke(Color.GREY);
        return rectangle;
    }

    private Pane createOverlayPane(Color baseColor, double endX, double endY, double o1, double o2) {
        Pane overlayPane = new Pane();
        overlayPane.setPrefSize(colorSelectionPane.getPrefWidth(), colorSelectionPane.getPrefHeight());
        overlayPane.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, endX, endY, true, CycleMethod.NO_CYCLE,
                        new Stop(0, baseColor.deriveColor(0, 1, 1, o1)),
                        new Stop(1, baseColor.deriveColor(0, 1, 1, o2))),
                CornerRadii.EMPTY, Insets.EMPTY)));
        return overlayPane;
    }

    private static LinearGradient createHueGradient() {
        double offset;
        Stop[] stops = new Stop[255];
        for (int x = 0; x < 255; x++) {
            offset = (1.0 / 255) * x;
            int h = (int) ((x / 255.0) * 360);
            stops[x] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
        }
        return new LinearGradient(0f, 0f, 1f, 0f, true, CycleMethod.NO_CYCLE, stops);
    }

    @Override
    public void initListener() {
        final EventHandler<MouseEvent> hueEH = me -> {
            hue.set(360.0 * LangUtils.toBoundDouble(me.getX() / hueSelectorPane.getWidth(), 0, 1));
            hueSelectorPane.requestFocus();
        };
        hueSelectorPane.setOnMousePressed(hueEH);
        hueSelectorPane.setOnMouseDragged(hueEH);

        final EventHandler<MouseEvent> colorEH = me -> {
            saturation.set(LangUtils.toBoundDouble(me.getX() / colorSelectionPane.getWidth(), 0, 1));
            brightness.set(LangUtils.toBoundDouble((colorSelectionPane.getHeight() - me.getY()) / colorSelectionPane.getHeight(), 0, 1));
            colorSelectionPane.requestFocus();
        };
        colorSelectionPane.setOnMousePressed(colorEH);
        colorSelectionPane.setOnMouseDragged(colorEH);

        final EventHandler<MouseEvent> opacityEH = me -> {
            opacity.set(LangUtils.toBoundDouble((opacityShowPane.getHeight() - me.getY()) / opacityShowPane.getHeight(), 0, 1));
            colorSelectionPane.requestFocus();
        };
        opacitySelectorPane.setOnMousePressed(opacityEH);
        opacitySelectorPane.setOnMouseDragged(opacityEH);

        buttonCancel.setOnAction(e -> this.hide());
        buttonOk.setOnAction(e -> {
            if (onNextSelection != null) {
                onNextSelection.accept(selectedColor.get());
            }
            this.hide();
        });
        buttonCopyHex.setOnAction(event -> {
            if (selectedColor.get() != null) {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(ColorUtils.toWebColorWithoutAlpha(selectedColor.get()));
                clipboard.setContent(content);
                LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("lc.colorpicker.web.color.copied").withMsDuration(LCGraphicStyle.SHORT_NOTIFICATION_DURATION_MS));
            }
        });
        this.setOnShowing(e -> {
            Scene scene = this.getScene();
            SystemVirtualKeyboardController.INSTANCE.registerScene(scene);
            SessionStatsController.INSTANCE.registerScene(scene);
        });
        this.setOnHidden(e -> {
            Scene scene = this.getScene();
            SystemVirtualKeyboardController.INSTANCE.unregisterScene(scene);
            SessionStatsController.INSTANCE.unregisterScene(scene);
            onNextSelection = null;
        });
    }


    @Override
    public void initBinding() {
        hueSelectionRect.layoutXProperty().bind(hue.divide(360.0).multiply(hueSelectorPane.widthProperty()).subtract(SELECTION_SIZE / 2.0));
        colorSelectionRect.layoutXProperty().bind(saturation.multiply(colorSelectionPane.widthProperty()).subtract(SELECTION_SIZE / 2.0));
        colorSelectionRect.layoutYProperty().bind(brightness.negate().add(1).multiply(colorSelectionPane.heightProperty()).subtract(SELECTION_SIZE / 2.0));
        opacitySelectionRect.layoutYProperty().bind(opacity.negate().add(1).multiply(opacityShowPane.heightProperty()).subtract(SELECTION_SIZE / 2.0));
        resultRectangle.fillProperty().bind(selectedColor);
        colorSelectionPane.backgroundProperty().bind(Bindings.createObjectBinding(() -> new Background(new BackgroundFill(Color.hsb(hue.get(), 1.0, 1.0), CornerRadii.EMPTY, Insets.EMPTY)), hue));
        opacityShowPane.backgroundProperty().bind(Bindings.createObjectBinding(() -> {
            Color c = makeColorOpaque(selectedColor.get() != null ? selectedColor.get() : DEFAULT_COLOR);
            return new Background(new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                            new Stop(0, c.deriveColor(0, 1, 1, 1)),
                            new Stop(1, c.deriveColor(0, 1, 1, 0))),
                    CornerRadii.EMPTY, Insets.EMPTY));
        }, selectedColor));
        selectedColor.addListener((obs, ov, nv) -> fieldColorHex.setText(ColorUtils.toWebColorWithoutAlpha(nv)));
        fieldColorHex.focusedProperty().addListener(inv -> updateAfterFieldUpdate());
        fieldColorHex.setOnAction(e -> updateAfterFieldUpdate());
    }

    boolean updatingColorFromText = false;

    private void updateAfterFieldUpdate() {
        updatingColorFromText = true;
        String text = fieldColorHex.getText();
        try {
            if (StringUtils.isNotBlank(text)) {
                text = text.trim();
                setCurrentColorTo(Color.web(text, opacity.get()));
            }
        } catch (Exception e) {
            LOGGER.info("Couldn't update color from text {} : {} - {}", text, e.getClass().getSimpleName(), e.getMessage());
        } finally {
            updatingColorFromText = false;
        }
    }

    private Color makeColorOpaque(Color color) {
        return Color.hsb(color.getHue(), color.getSaturation(), color.getBrightness(), 1.0);
    }
}
