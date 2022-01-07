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
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LCColorPicker extends HBox implements LCViewInitHelper {
    private final ObjectProperty<Color> value;
    private final ObjectProperty<EventHandler<ActionEvent>> onAction;

    private MenuButton buttonPick;
    private Popup popupColorSelection;
    private Rectangle previewRectangle;
    private LCColorPickerSelection colorPickerSelection;

    private final ColorPickerMode mode;

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

    public ColorPickerMode getMode() {
        return mode;
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

        // Popup
        this.popupColorSelection = new Popup();
        popupColorSelection.autoFixProperty().set(true);
        popupColorSelection.autoHideProperty().set(true);
        colorPickerSelection = new LCColorPickerSelection(this);
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
                popupColorSelection.show(buttonPick, window.getX() + scene.getX() + point2D.getX() - 8.0, window.getY() + scene.getY() + point2D.getY() + buttonPick.getHeight() - 4.0);
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
    public void mostUsedColorsUpdated(List<Color> mostUsedColors) {
        colorPickerSelection.mostUsedColorsUpdated(mostUsedColors);
    }

    private static final Map<Color, AtomicInteger> userColors = new HashMap<>();
    private static final Set<WeakReference<Consumer<List<Color>>>> mostUsedColorsListeners = new HashSet<>();
    private static final ChangeListener<Color> colorChangeListener = (obs, ov, nv) -> {
        if (ov != null && userColors.containsKey(ov)) {
            //userColors.get(ov).decrementAndGet();
        }
        if (nv != null) {
            userColors.computeIfAbsent(nv, k -> new AtomicInteger(0)).incrementAndGet();
        }
        final List<Color> mostUsedColors = userColors.entrySet().stream().sorted((e1, e2) -> Integer.compare(e2.getValue().get(), e1.getValue().get())).map(Map.Entry::getKey).collect(Collectors.toList());
        mostUsedColorsListeners.forEach(listenerRef -> {
            final Consumer<List<Color>> listener = listenerRef.get();
            if (listener != null) {
                listener.accept(mostUsedColors);
            }
        });
    };

    private final Consumer<List<Color>> mostUsedColorsListener = this::mostUsedColorsUpdated;

    public void registerForMostUsedColors(LCColorPicker colorPicker) {
        colorPicker.valueProperty().addListener(colorChangeListener);
        mostUsedColorsListeners.add(new WeakReference<>(mostUsedColorsListener));
    }
    //========================================================================
}
