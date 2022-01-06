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

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

// TODO : handle size to max possible with
// TODO : extends HBox or directly MenuButton ?
public class LCColorPicker extends HBox implements LCViewInitHelper {
    private final ObjectProperty<Color> value;
    private final ObjectProperty<EventHandler<ActionEvent>> onAction;

    private MenuButton buttonPick;
    private Popup popupColorSelection;
    private Rectangle previewRectangle;

    public LCColorPicker() {
        this.value = new SimpleObjectProperty<>();
        this.onAction = new SimpleObjectProperty<>();
        initAll();
    }

    void colorSelectedAndHide(Color color) {
        this.colorSelectedWithoutHide(color);
        this.popupColorSelection.hide();
    }

    void colorSelectedWithoutHide(Color color) {
        this.value.set(color);
    }

    public ObjectProperty<Color> valueProperty() {
        return value;
    }

    public Color getValue() {
        return value.get();
    }

    public void setValue(Color value) {
        this.value.set(value);
    }

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onAction;
    }

    public final void setOnAction(EventHandler<ActionEvent> value) {
        onActionProperty().set(value);
    }

    public final EventHandler<ActionEvent> getOnAction() {
        return onActionProperty().get();
    }

    @Override
    public void initUI() {
        // Pick button
        this.buttonPick = new MenuButton();

        this.previewRectangle = new Rectangle(15.0, 15.0);
        this.previewRectangle.setStrokeWidth(0.2);
        this.previewRectangle.setStroke(Color.DIMGRAY);
        this.previewRectangle.setStrokeType(StrokeType.INSIDE);
        Pane panePreview = new Pane(previewRectangle);
        panePreview.getStyleClass().add("background-image-transparent");

        HBox.setHgrow(buttonPick, Priority.ALWAYS);
        this.buttonPick.setMaxWidth(Double.MAX_VALUE);
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
        this.buttonPick.setOnMouseClicked(e -> {
            e.consume();
            if (popupColorSelection.isShowing()) {
                popupColorSelection.hide();
            } else {
                Scene scene = this.getScene();
                Window window = scene.getWindow();
                Point2D point2D = buttonPick.localToScene(0, 0);
                popupColorSelection.show(buttonPick, window.getX() + scene.getX() + point2D.getX(), window.getY() + scene.getY() + point2D.getY() + buttonPick.getHeight());
            }
        });
        this.value.addListener((obs, ov, nv) -> {
            final EventHandler<ActionEvent> actionEventEventHandler = onAction.get();
            if (actionEventEventHandler != null) {
                actionEventEventHandler.handle(new ActionEvent());
            }
        });
    }

    @Override
    public void initBinding() {
        this.buttonPick.textProperty().bind(Bindings.createStringBinding(() -> {
            final Color color = value.get();
            if (color == null) {
                return Translation.getText("lc.colorpicker.null.value");
            } else if (color.getOpacity() < 0.001) {
                return Translation.getText("lc.colorpicker.transparent.value");
            } else {
                return LCUtils.toWebColor(color);
            }
        }, value));
        this.previewRectangle.fillProperty().bind(value);
    }
}
