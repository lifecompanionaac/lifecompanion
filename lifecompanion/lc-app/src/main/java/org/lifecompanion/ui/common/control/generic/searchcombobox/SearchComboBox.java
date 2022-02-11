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

package org.lifecompanion.ui.common.control.generic.searchcombobox;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.resource.LCGlyphFont;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public class SearchComboBox<T> extends HBox implements LCViewInitHelper {
    private final ObjectProperty<T> value;
    private ObservableList<T> items;

    private MenuButton buttonOpenPopup;
    private Button buttonClearValue;

    private final Function<T, String> toStringFunction;
    private final Function<String, Predicate<T>> predicateBuilder;
    private final Function<String, Comparator<T>> comparatorBuilder;
    private final Callback<ListView<T>, ListCell<T>> cellFactory;

    private SearchComboBoxPopup<T> searchComboBoxPopup;

    public SearchComboBox(Callback<ListView<T>, ListCell<T>> cellFactory, Function<String, Predicate<T>> predicateBuilder, Function<T, String> toStringFunction) {
        this(cellFactory, predicateBuilder, toStringFunction, null);
    }

    public SearchComboBox(Callback<ListView<T>, ListCell<T>> cellFactory, Function<String, Predicate<T>> predicateBuilder, Function<T, String> toStringFunction, Function<String, Comparator<T>> comparatorBuilder) {
        this.cellFactory = cellFactory;
        this.predicateBuilder = predicateBuilder;
        this.toStringFunction = toStringFunction;
        this.comparatorBuilder = comparatorBuilder;
        this.value = new SimpleObjectProperty<>();
        this.initAll();
    }

    public void setItems(ObservableList<T> items) {
        this.items = items;
    }

    public T getValue() {
        return value.get();
    }

    public ObjectProperty<T> valueProperty() {
        return value;
    }

    public void setFixedCellSize(double size){
        this.searchComboBoxPopup.setFixedCellSize(size);
    }

    // UI
    //========================================================================
    @Override
    public void initUI() {
        searchComboBoxPopup = new SearchComboBoxPopup<>(this);
        this.buttonOpenPopup = new MenuButton();
        HBox.setHgrow(buttonOpenPopup, Priority.ALWAYS);
        this.buttonOpenPopup.setMaxWidth(Double.MAX_VALUE);

        buttonClearValue = UIUtils.createGraphicButton(LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(14).color(LCGraphicStyle.SECOND_DARK), "tooltip.search.combobox.button.clear");

        this.setSpacing(5.0);
        this.getChildren().addAll(buttonOpenPopup, buttonClearValue);
    }


    @Override
    public void initListener() {
        this.buttonClearValue.setOnAction(e -> {
            value.set(null);
            if (searchComboBoxPopup.isShowing()) {
                searchComboBoxPopup.hide();
            }
        });
        this.buttonOpenPopup.setOnMouseClicked(e -> {
            e.consume();
            if (searchComboBoxPopup.isShowing()) {
                searchComboBoxPopup.hide();
            } else {
                searchComboBoxPopup.showOnSearchCombobox();
            }
        });
    }

    @Override
    public void initBinding() {
        this.buttonOpenPopup.textProperty().bind(Bindings.createStringBinding(() -> toStringFunction.apply(value.get()), value));
    }
    //========================================================================

    // PACKAGE
    //========================================================================
    void itemSelected(T item) {
        value.set(item);
    }

    MenuButton getButtonOpenPopup() {
        return buttonOpenPopup;
    }

    Function<String, Predicate<T>> getPredicateBuilder() {
        return predicateBuilder;
    }

    Function<String, Comparator<T>> getComparatorBuilder() {
        return comparatorBuilder;
    }

    Callback<ListView<T>, ListCell<T>> getCellFactory() {
        return cellFactory;
    }

    ObservableList<T> getItems() {
        return items;
    }
    //========================================================================
}
