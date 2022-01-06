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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class LCColorPickerSelection extends VBox implements LCViewInitHelper {
    private final static int MAIN_COLOR_COUNT = 19;
    private final static int COLOR_VARIANT_COUNT = 10;

    private Button customColorButton;
    private final LCColorPicker colorPicker;
    private HBox boxTransparent;

    private Button buttonDarker, buttonBrighter;


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

        // User defined colors (TODO)

        // Custom color
        customColorButton = UIUtils.createRightTextButton(Translation.getText("lc.colorpicker.custom.color"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.SLIDERS).size(14).color(LCGraphicStyle.MAIN_DARK),
                null);

        //        // Brighter/darker - later ?
        //        buttonDarker = UIUtils.createGraphicButton(LCGlyphFont.FONT_MATERIAL.create('\ue3ab').size(12).color(LCGraphicStyle.MAIN_DARK),
        //                null);
        //        buttonBrighter = UIUtils.createGraphicButton(LCGlyphFont.FONT_MATERIAL.create('\ue3aa').size(12).color(LCGraphicStyle.MAIN_DARK),
        //                null);
        //        HBox boxBrighterDarker = new HBox(3.0,buttonDarker,new Label(Translation.getText("")))


        // Pick a color button (TODO)

        this.getChildren().addAll(tilePaneBaseColors, groupBoxTransparent, new Separator(Orientation.HORIZONTAL), customColorButton);

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
        final List<String> withoutA = List.of("Brown 50", "Gray 50", "Blue Gray 50");
        Color[][] colors = new Color[MAIN_COLOR_COUNT][COLOR_VARIANT_COUNT];

        try {
            int mi = 0;
            List<String> lineList = new ArrayList<>();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("E:\\Desktop\\colors.txt"), StandardCharsets.UTF_8))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    lineList.add(line);
                }
            }
            final String[] lines = lineList.toArray(new String[0]);
            for (int i = 0; i < lines.length; i++) {
                String title = lines[i];
                for (int j = 0; j < 10; j++) {
                    String subTitle = lines[i + j * 2];
                    final String val = lines[i + j * 2 + 1];
                    System.out.println(title + " [" + subTitle + "] = " + val);
                    colors[mi][j] = Color.web(val, 1);
                }
                i += withoutA.contains(title) ? 19 : 27;
                mi++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //        // First : black to white
        //        for (int j = 0; j < colors[0].length; j++) {
        //            colors[0][j] = Color.WHITE.deriveColor(0.0, 1, (colors[0].length - j) * (1.0 / colors[0].length), 1);
        //        }
        //        // Then : others
        //        for (int i = 1; i < colors.length; i++) {
        //            Color base = Color.hsb((i - 1) * (360.0 / (colors.length - 1)), 0.9, 0.70);
        //            for (int j = 0; j < colors[i].length; j++) {
        //                colors[i][j] = base.deriveColor(0.0, (colors[i].length - j) * (0.9 / colors[i].length), 1, 1);
        //            }
        //        }
        return colors;
    }

    // TODO - Add it later ?
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
