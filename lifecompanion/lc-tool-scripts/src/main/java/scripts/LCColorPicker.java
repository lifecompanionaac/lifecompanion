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

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;


public class LCColorPicker extends HBox implements LCViewInitHelper {
    private final ObjectProperty<Color> value;
    private Button buttonPick;
    private Popup popupColorSelection;
    private Rectangle previewRectangle;

    public LCColorPicker() {
        this.value = new SimpleObjectProperty<>();
        initAll();
    }

    void colorSelected(Color color) {
        this.value.set(color);
        this.popupColorSelection.hide();
    }

    public ObjectProperty<Color> valueProperty() {
        return value;
    }

    @Override
    public void initUI() {
        // Pick button
        this.buttonPick = new Button();

        this.previewRectangle = new Rectangle(15.0, 15.0);
        Pane panePreview = new Pane(previewRectangle);
        panePreview.getStyleClass().add("background-image-transparent");

        this.buttonPick.setGraphic(panePreview);
        this.buttonPick.setGraphicTextGap(5.0);

        this.getChildren().add(buttonPick);

        // Popup
        this.popupColorSelection = new Popup();
        popupColorSelection.autoFixProperty().set(true);
        popupColorSelection.autoHideProperty().set(true);
        LCColorPickerSelection colorPickerSelection = new LCColorPickerSelection(this);
        popupColorSelection.getContent().add(colorPickerSelection);
    }

    @Override
    public void initListener() {
        this.buttonPick.setOnAction(e -> {
            if (popupColorSelection.isShowing()) {
                popupColorSelection.hide();
            } else {
                Scene scene = this.getScene();
                Window window = scene.getWindow();
                Point2D point2D = buttonPick.localToScene(0, 0);
                popupColorSelection.show(buttonPick, window.getX() + scene.getX() + point2D.getX(), window.getY() + scene.getY() + point2D.getY() + buttonPick.getHeight());
            }
        });
    }

    @Override
    public void initBinding() {
        // FIXME : special message if fully transparent
        this.buttonPick.textProperty().bind(Bindings.createStringBinding(() -> value.get() != null ? LCUtils.toWebColor(value.get()) : "AUCUN", value));
        this.previewRectangle.fillProperty().bind(value);
    }
}
