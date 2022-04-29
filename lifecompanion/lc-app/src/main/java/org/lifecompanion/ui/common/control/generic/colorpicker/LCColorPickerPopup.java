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

import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.util.javafx.ColorUtils;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class LCColorPickerPopup extends Popup implements LCViewInitHelper {
    private final static int MAIN_COLOR_COUNT = 19;
    private final static int COLOR_VARIANT_COUNT = 6;

    private final static int USER_COLOR_ROWS = 1;

    private Button customColorButton;
    private HBox boxTransparent;
    private TilePane tilePaneUserColors;

    private final LCColorPicker.ColorPickerMode mode;

    private Consumer<Color> onNextSelection;
    private Color previousColor;

    private LCColorCustomColorStage colorCustomColorDialog;

    private final Map<String, Rectangle> baseColorNodes, mostUsedColorNodes;

    public LCColorPickerPopup(LCColorPicker.ColorPickerMode mode) {
        this.mode = mode;
        baseColorNodes = new HashMap<>();
        mostUsedColorNodes = new HashMap<>();
        initAll();
    }

    @Override
    public void initUI() {
        this.setAutoFix(true);
        this.setAutoHide(true);

        VBox total = new VBox();
        total.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        total.setPadding(new Insets(10.0));
        total.setSpacing(5.0);
        total.getStyleClass().addAll("popup-bottom-dropshadow", "base-background-with-gray-border-1");
        total.setAlignment(Pos.CENTER);

        // First part : base colors
        final Color[][] baseColors = getBaseColors();
        TilePane tilePaneBaseColors = new TilePane();
        tilePaneBaseColors.setHgap(2.0);
        tilePaneBaseColors.setVgap(2.0);
        tilePaneBaseColors.setPrefColumns(MAIN_COLOR_COUNT);
        for (int j = 0; j < COLOR_VARIANT_COUNT; j++) {
            for (int i = 0; i < MAIN_COLOR_COUNT; i++) {
                tilePaneBaseColors.getChildren().add(createColorRectangle(baseColors[i][j], baseColorNodes));
            }
        }

        // Transparent button
        final ImageView transparent = new ImageView(IconHelper.get("transparent-background.png"));
        transparent.setFitHeight(COLOR_SQUARE_SIZE);
        transparent.setPreserveRatio(true);
        boxTransparent = new HBox(5.0, transparent, new Text(Translation.getText("lc.colorpicker.transparent.value")));
        boxTransparent.setAlignment(Pos.CENTER);
        boxTransparent.getStyleClass().add("border-hover");
        final Group groupBoxTransparent = new Group(boxTransparent);
        groupBoxTransparent.getStyleClass().addAll("scale-110-hover", "text-font-size-90");

        // User defined colors
        tilePaneUserColors = new TilePane();
        tilePaneUserColors.setHgap(2.0);
        tilePaneUserColors.setVgap(2.0);
        tilePaneUserColors.setPrefColumns(MAIN_COLOR_COUNT);

        // Custom color
        customColorButton = FXControlUtils.createRightTextButton(Translation.getText("lc.colorpicker.custom.color"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.SLIDERS).size(14).color(LCGraphicStyle.MAIN_DARK),
                null);

        // TODO : LATER ?
        // Pick a color button ?
        // Brighter/darker on a color

        total.getChildren().addAll(tilePaneBaseColors, groupBoxTransparent, new Separator(Orientation.HORIZONTAL), tilePaneUserColors, new Separator(Orientation.HORIZONTAL), customColorButton);
        this.getContent().add(total);

        // Custom color dialog
        colorCustomColorDialog = new LCColorCustomColorStage();
    }


    private static final double COLOR_SQUARE_SIZE = 16;

    private Rectangle createColorRectangle(Color color, Map<String, Rectangle> colorMap) {
        Rectangle rectangle = new Rectangle(COLOR_SQUARE_SIZE, COLOR_SQUARE_SIZE);
        rectangle.setFill(color);
        rectangle.setOnMouseClicked(me -> colorSelectedAndHide(color));
        rectangle.getStyleClass().addAll("scale-130-hover", "stroke-hover", "stroke-selected", "scale-130-selected");
        colorMap.put(ColorUtils.toWebColorWithAlpha(color), rectangle);
        return rectangle;
    }

    @Override
    public void initListener() {
        this.customColorButton.setOnAction(e -> colorCustomColorDialog.showCustomDialog(previousColor, this.onNextSelection));
        this.boxTransparent.setOnMouseClicked(me -> colorSelectedAndHide(Color.TRANSPARENT));
        this.setOnHidden(e -> onNextSelection = null);
    }

    @Override
    public void initBinding() {
        LCViewInitHelper.super.initBinding();
    }

    private Color[][] getBaseColors() {
        final List<String> vars = List.of("100", "300", "400", "600", "800", "900");
        final List<List<MaterialColors.MaterialColor>> colorsByTitle = MaterialColors.INSTANCE.getColorsByTitle();
        Color[][] colors = new Color[colorsByTitle.size()][COLOR_VARIANT_COUNT];
        for (int i = 0; i < colorsByTitle.size(); i++) {
            final List<MaterialColors.MaterialColor> colorsForTitle = colorsByTitle.get(i);
            for (int i1 = 0; i1 < vars.size(); i1++) {
                int finalI1 = i1;
                int finalI = i;
                colorsForTitle.stream().filter(c -> StringUtils.isEquals(vars.get(finalI1), c.getSubTitle())).findAny().ifPresent(mc -> {
                    colors[finalI][finalI1] = this.mode == LCColorPicker.ColorPickerMode.BASE ? mc.getColor() : MaterialColors.darker(mc.getColor());
                });
            }
        }
        return colors;
    }

    public void showOnPicker(LCColorPicker lcColorPicker, Consumer<Color> onSelection) {
        this.onNextSelection = onSelection;
        previousColor = lcColorPicker.getValue();
        updateMostUsedColors();
        Scene scene = lcColorPicker.getScene();
        Window window = scene.getWindow();
        Point2D point2D = lcColorPicker.getButtonPick().localToScene(0, 0);
        this.show(lcColorPicker.getButtonPick(), window.getX() + scene.getX() + point2D.getX() - 8.0, window.getY() + scene.getY() + point2D.getY() + lcColorPicker.getButtonPick().getHeight() - 4.0);
        updateSelectedForHex(baseColorNodes, previousColor);
        updateSelectedForHex(mostUsedColorNodes, previousColor);
    }

    private void updateSelectedForHex(Map<String, Rectangle> baseColorNodes, Color color) {
        baseColorNodes.values().forEach(n -> n.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false));
        if (color != null) {
            String colorHex = ColorUtils.toWebColorWithAlpha(color);
            Rectangle rectangle = baseColorNodes.get(colorHex);
            if (rectangle != null) rectangle.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
        }
    }

    private void colorSelectedAndHide(Color color) {
        if (onNextSelection != null) {
            onNextSelection.accept(color);
        }
        this.hide();
    }

    private void updateMostUsedColors() {
        tilePaneUserColors.getChildren().clear();
        mostUsedColorNodes.clear();
        final List<Color> mostUsedColors = LCColorPicker.getMostUsedColorsList();
        if (LangUtils.isNotEmpty(mostUsedColors)) {
            for (Color color : mostUsedColors.subList(0, Math.min(mostUsedColors.size(), USER_COLOR_ROWS * MAIN_COLOR_COUNT))) {
                tilePaneUserColors.getChildren().add(createColorRectangle(color, this.mostUsedColorNodes));
            }
        }
    }

}
