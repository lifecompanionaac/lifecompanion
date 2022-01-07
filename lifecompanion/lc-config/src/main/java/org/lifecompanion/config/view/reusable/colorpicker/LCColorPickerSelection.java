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

package org.lifecompanion.config.view.reusable.colorpicker;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.List;


public class LCColorPickerSelection extends VBox implements LCViewInitHelper {
    private final static int MAIN_COLOR_COUNT = 19;
    private final static int COLOR_VARIANT_COUNT = 6;

    private final static int USER_COLOR_ROWS = 1;


    private Button customColorButton;
    private final LCColorPicker colorPicker;
    private HBox boxTransparent;
    private TilePane tilePaneUserColors;

    public LCColorPickerSelection(LCColorPicker colorPicker) {
        this.colorPicker = colorPicker;
        initAll();
    }

    @Override
    public void initUI() {
        this.setPadding(new Insets(10.0));
        this.setSpacing(5.0);
        this.getStyleClass().addAll("popup-bottom-dropshadow", "base-background-with-gray-border-1");
        this.setAlignment(Pos.CENTER);

        // First part : base colors
        final Color[][] baseColors = getBaseColors();
        TilePane tilePaneBaseColors = new TilePane();
        tilePaneBaseColors.setHgap(2.0);
        tilePaneBaseColors.setVgap(2.0);
        tilePaneBaseColors.setPrefColumns(MAIN_COLOR_COUNT);
        for (int j = 0; j < COLOR_VARIANT_COUNT; j++) {
            for (int i = 0; i < MAIN_COLOR_COUNT; i++) {
                tilePaneBaseColors.getChildren().add(createBaseColor(baseColors[i][j]));
            }
        }

        // Transparent button
        final ImageView transparent = new ImageView(IconManager.get("transparent-background.png"));
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
        customColorButton = UIUtils.createRightTextButton(Translation.getText("lc.colorpicker.custom.color"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.SLIDERS).size(14).color(LCGraphicStyle.MAIN_DARK),
                null);

        // LATER ?
        // Pick a color button ?
        // Brighter/darker on a color

        this.getChildren().addAll(tilePaneBaseColors, groupBoxTransparent, new Separator(Orientation.HORIZONTAL), tilePaneUserColors, new Separator(Orientation.HORIZONTAL), customColorButton);
    }

    public void mostUsedColorsUpdated(List<Color> mostUsedColors) {
        tilePaneUserColors.getChildren().clear();
        if (LangUtils.isNotEmpty(mostUsedColors)) {
            for (Color color : mostUsedColors.subList(0, Math.min(mostUsedColors.size(), USER_COLOR_ROWS * MAIN_COLOR_COUNT))) {
                tilePaneUserColors.getChildren().add(createBaseColor(color));
            }
        }
    }

    private static final double COLOR_SQUARE_SIZE = 16;

    private Rectangle createBaseColor(Color color) {
        Rectangle rectangle = new Rectangle(COLOR_SQUARE_SIZE, COLOR_SQUARE_SIZE);
        rectangle.setFill(color);
        rectangle.setOnMouseClicked(me -> {
            colorPicker.colorSelectedAndHide(color);
        });
        rectangle.getStyleClass().addAll("scale-130-hover", "stroke-hover");
        return rectangle;
    }

    @Override
    public void initListener() {
        this.customColorButton.setOnAction(e -> {
            LCColorCustomColorDialog colorCustomColorDialog = new LCColorCustomColorDialog(this.colorPicker, this.colorPicker.valueProperty().get());
            colorCustomColorDialog.initModality(Modality.APPLICATION_MODAL);
            colorCustomColorDialog.show();
        });
        this.boxTransparent.setOnMouseClicked(me -> {
            colorPicker.colorSelectedAndHide(Color.TRANSPARENT);
        });
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
                    //System.out.println(finalI+","+finalI1+" = "+mc.getTitle()+", "+mc.getSubTitle()+" = "+mc.getColor());
                    colors[finalI][finalI1] = colorPicker.getMode() == LCColorPicker.ColorPickerMode.BASE ? mc.getColor() : mc.getColor().darker();
                });
            }

        }
        return colors;
    }


    // TEST : for pick color button feature
    //    Button buttonPick = new Button("Pick");
    //            buttonPick.setOnAction(e -> {
    //        Stage stage = new Stage();
    //        stage.initStyle(StageStyle.TRANSPARENT);
    //        Pane wholePaneTransp = new Pane();
    //        wholePaneTransp.setBackground(new Background(new BackgroundFill(Color.WHITE.deriveColor(0, 1, 1, 0.01), CornerRadii.EMPTY, Insets.EMPTY)));
    //        wholePaneTransp.setOnMouseClicked(me -> {
    //            final Robot robot = new Robot();
    //            final Color pixelColor = robot.getPixelColor(me.getScreenX(), me.getScreenY());
    //            rectangle.fillProperty().set(pixelColor);
    //            stage.hide();
    //        });
    //        final Scene value = new Scene(wholePaneTransp);
    //        value.setFill(null);
    //        stage.setScene(value);
    //        stage.setFullScreen(true);
    //        stage.show();
    //    });

}
