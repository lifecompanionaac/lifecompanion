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

package org.lifecompanion.config.view.reusable;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public class SearchComboBox<T> extends HBox implements LCViewInitHelper {
    private FilteredList<T> filteredItems;
    private SortedList<T> sortedItems;

    private final ObjectProperty<T> value;

    private ComboBox<T> comboBoxItems;
    private TextField textFieldSearch;
    private Button buttonClearValue;
    private Label labelButtonSearch;
    private HBox boxSearch;

    private final Function<String, Predicate<T>> predicateBuilder;
    private final Function<T, String> toStringFunction;
    private final Function<String, Comparator<T>> comparatorBuilder;

    public SearchComboBox(Function<String, Predicate<T>> predicateBuilder, Function<T, String> toStringFunction) {
        this(predicateBuilder, toStringFunction, null);
    }

    public SearchComboBox(Function<String, Predicate<T>> predicateBuilder, Function<T, String> toStringFunction, Function<String, Comparator<T>> comparatorBuilder) {
        this.predicateBuilder = predicateBuilder;
        this.toStringFunction = toStringFunction;
        this.comparatorBuilder = comparatorBuilder;
        this.value = new SimpleObjectProperty<>();
        this.initAll();
    }

    // Public API
    //========================================================================
    public void setItems(ObservableList<T> items) {
        if (items != null) {
            this.filteredItems = new FilteredList<>(items);
            this.sortedItems = new SortedList<>(this.filteredItems);
            this.comboBoxItems.setItems(sortedItems);
            this.searchUpdated();
        } else {
            this.filteredItems = null;
            this.sortedItems = null;
            this.comboBoxItems.setItems(null);
        }
    }

    public void setCellFactory(Callback<ListView<T>, ListCell<T>> cellFactory) {
        this.comboBoxItems.setCellFactory(cellFactory);
    }

    public ObjectProperty<T> valueProperty() {
        return value;
    }

    public void setVisibleRowCount(int value) {
        this.comboBoxItems.setVisibleRowCount(value);
    }

    public ObservableList<T> getItems() {
        return (ObservableList<T>) filteredItems.getSource();
    }
    //========================================================================

    //
    //========================================================================
    @Override
    public void initUI() {
        this.comboBoxItems = new ComboBox<>();
        this.comboBoxItems.setVisible(false);
        StackPane placeHolderEmptyList = new StackPane(new Label(Translation.getText("tooltip.search.combobox.placeholder.empty.list")));
        placeHolderEmptyList.setStyle("-fx-background-color: white;");
        placeHolderEmptyList.setMinHeight(100.0);
        this.comboBoxItems.setPlaceholder(placeHolderEmptyList);

        // FIX : if this property is not set, updateItem will be called on all items displayed.
        // Could be time consuming...
        // WARNING : this property should be checked on JavaFX update as it is not public API
        this.comboBoxItems.getProperties().put("comboBoxRowsToMeasureWidth", 5);

        this.textFieldSearch = new TextField();
        this.textFieldSearch.setVisible(false);

        buttonClearValue = UIUtils.createGraphicButton(LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(14).color(LCGraphicStyle.SECOND_DARK), "tooltip.search.combobox.button.clear");

        labelButtonSearch = new Label();
        labelButtonSearch.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelButtonSearch, Priority.ALWAYS);
        boxSearch = new HBox(labelButtonSearch, LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CARET_DOWN).size(14).color(LCGraphicStyle.MAIN_PRIMARY));
        boxSearch.getStyleClass().addAll("combo-box-base", "search-combo-box");
        Tooltip.install(boxSearch, UIUtils.createTooltip(Translation.getText("tooltip.search.combobox.button.select")));

        setWidthOnButton(comboBoxItems);
        setWidthOnButton(textFieldSearch);

        StackPane stackPaneSearchBox = new StackPane(comboBoxItems, textFieldSearch, boxSearch);
        stackPaneSearchBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(stackPaneSearchBox, Priority.SOMETIMES);

        this.setSpacing(5.0);
        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(stackPaneSearchBox, buttonClearValue);
    }

    private void setWidthOnButton(Region region) {
        region.minWidthProperty().bind(boxSearch.widthProperty());
        region.maxWidthProperty().bind(boxSearch.widthProperty());
        region.prefWidthProperty().bind(boxSearch.widthProperty());
    }

    @Override
    public void initListener() {
        this.textFieldSearch.textProperty().addListener(inv -> searchUpdated());
        this.comboBoxItems.setOnHidden(e -> showSearch(false));
        this.boxSearch.setOnMouseClicked(e -> showSearch(true));
        this.buttonClearValue.setOnAction(e -> {
            comboBoxItems.getSelectionModel().clearSelection();
            showSearch(false);
        });
    }

    private void searchUpdated() {
        String text = textFieldSearch.getText();
        this.filteredItems.setPredicate(this.predicateBuilder.apply(text));
        this.sortedItems.setComparator(comparatorBuilder != null ? comparatorBuilder.apply(text) : null);
    }

    private void showSearch(boolean show) {
        this.boxSearch.setVisible(!show);
        this.textFieldSearch.setVisible(show);
        if (show) {
            this.comboBoxItems.show();
            this.textFieldSearch.requestFocus();
        } else {
            this.comboBoxItems.hide();
            this.value.set(this.comboBoxItems.getValue());
        }
    }

    @Override
    public void initBinding() {
        this.labelButtonSearch.textProperty().bind(Bindings.createStringBinding(() -> toStringFunction.apply(value.get()), value));
    }
    //========================================================================
}
