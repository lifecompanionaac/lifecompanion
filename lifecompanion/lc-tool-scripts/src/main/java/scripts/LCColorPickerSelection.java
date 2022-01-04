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

package scripts;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;


public class LCColorPickerSelection extends VBox implements LCViewInitHelper {
    private final static int MAIN_COLOR_COUNT = 10;
    private final static int COLOR_VARIANT_COUNT = 6;

    private Button customColorButton;
    private final LCColorPicker colorPicker;


    public LCColorPickerSelection(LCColorPicker colorPicker) {
        this.colorPicker = colorPicker;
        initAll();
    }

    @Override
    public void initUI() {
        this.setPadding(new Insets(10.0));
        this.setSpacing(5.0);
        this.setStyle("-fx-background-color: gray, white;-fx-background-insets: 1;");
        this.setEffect(new DropShadow());//FIXME : better effect
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
        final ImageView transparent = new ImageView(new Image("transparent-clean.png"));// FIXME : from IconManager
        transparent.setFitHeight(20);
        transparent.setPreserveRatio(true);
        HBox boxTransparent = new HBox(5.0, transparent, new Text(Translation.getText("Aucune (transparent)")));
        boxTransparent.setAlignment(Pos.CENTER);

        // User defined colors (TODO)

        // Custom color
        customColorButton = new Button("Autre couleur");
        //                UIUtils.createRightTextButton(Translation.getText("Autre couleur"),
        //                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).size(20).color(LCGraphicStyle.MAIN_DARK),
        //                null);


        // Pick a color button (TODO)

        this.getChildren().addAll(tilePaneBaseColors, boxTransparent, new Separator(Orientation.HORIZONTAL), customColorButton);

    }

    private Rectangle createBaseColor(Color color) {
        Rectangle rectangle = new Rectangle(14, 14);
        rectangle.setFill(color);
        rectangle.setOnMouseClicked(me -> {
            colorPicker.colorSelected(color);
        });
        // FIXME : style for "on over" : reduce opacity
        return rectangle;
    }

    @Override
    public void initListener() {
        this.customColorButton.setOnAction(e -> {
            LCColorCustomColorDialog colorCustomColorDialog = new LCColorCustomColorDialog(this.colorPicker, this.colorPicker.valueProperty().get());
            //colorCustomColorDialog.initOwner(UIUtils.getSourceWindow(customColorButton));
            colorCustomColorDialog.initModality(Modality.APPLICATION_MODAL);
            colorCustomColorDialog.show();
        });
    }

    @Override
    public void initBinding() {
        LCViewInitHelper.super.initBinding();
    }

    private Color[][] getBaseColors() {
        Color[][] colors = new Color[MAIN_COLOR_COUNT][COLOR_VARIANT_COUNT];
        for (int i = 0; i < colors.length; i++) {
            Color base = Color.hsb(i * (360.0 / colors.length), 1, 0.80);
            for (int j = 0; j < colors[i].length; j++) {
                Color fColor = base.deriveColor(0.0, (colors[i].length - j) * (1.0 / colors[i].length), 1, 1);
                colors[i][j] = fColor;
            }
        }
        return colors;
    }

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
