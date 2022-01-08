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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LCColorPicker extends HBox implements LCViewInitHelper {
    private final ObjectProperty<Color> value;
    private final ObjectProperty<EventHandler<ActionEvent>> onAction;

    private MenuButton buttonPick;
    private Rectangle previewRectangle;
    private final ColorPickerMode mode;

    private static Map<ColorPickerMode, LCColorPickerPopup> colorPickerPopups;

    public LCColorPicker() {
        this(ColorPickerMode.BASE);
    }

    public LCColorPicker(final ColorPickerMode mode) {
        this.mode = mode;
        this.value = new SimpleObjectProperty<>();
        this.onAction = new SimpleObjectProperty<>();
        initAll();
        registerForMostUsedColors(this);
    }

    private synchronized LCColorPickerPopup getColorPickerPopup() {
        if (colorPickerPopups == null) {
            colorPickerPopups = new HashMap<>();
            for (ColorPickerMode colorPickerMode : ColorPickerMode.values()) {
                colorPickerPopups.put(colorPickerMode, new LCColorPickerPopup(colorPickerMode));
            }
        }
        return colorPickerPopups.get(mode);
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

    MenuButton getButtonPick() {
        return buttonPick;
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

        this.buttonPick.setGraphic(panePreview);
        this.buttonPick.setGraphicTextGap(5.0);

        HBox.setHgrow(buttonPick, Priority.ALWAYS);
        this.buttonPick.setMaxWidth(Double.MAX_VALUE);
        this.setPrefWidth(150.0);
        this.getChildren().add(buttonPick);
    }

    private LCColorPickerPopup showingPopup;

    @Override
    public void initListener() {
        this.buttonPick.setOnMouseClicked(e -> {
            e.consume();
            if (showingPopup != null && showingPopup.isShowing()) {
                showingPopup.hide();
                showingPopup = null;
            } else {
                showingPopup = getColorPickerPopup();
                showingPopup.showOnPicker(this, value::set);
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
        this.buttonPick.textProperty().bind(Bindings.createStringBinding(() -> MaterialColors.INSTANCE.getColorName(value.get()), value));
        this.previewRectangle.fillProperty().bind(value);
    }


    public enum ColorPickerMode {
        BASE, DARK;
    }

    // MOST USED VALUES
    //========================================================================
    private static final Map<Color, AtomicInteger> userColors = new HashMap<>();

    private final ChangeListener<Color> valueChangeListener = (obs, ov, nv) -> {
        if (ov != null && userColors.containsKey(ov)) {
            //userColors.get(ov).decrementAndGet();
        }
        if (nv != null) {
            userColors.computeIfAbsent(nv, k -> new AtomicInteger(0)).incrementAndGet();
        }
    };

    static List<Color> getMostUsedColorsList() {
        return userColors.entrySet().stream().sorted((e1, e2) -> Integer.compare(e2.getValue().get(), e1.getValue().get())).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private void registerForMostUsedColors(LCColorPicker colorPicker) {
        colorPicker.valueProperty().addListener(new WeakChangeListener<>(valueChangeListener));
    }
    //========================================================================
}
