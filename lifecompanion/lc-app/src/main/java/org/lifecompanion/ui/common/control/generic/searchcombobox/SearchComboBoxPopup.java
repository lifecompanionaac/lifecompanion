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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.lifecompanion.controller.editaction.GridStackActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.controlsfx.control.textfield.TextFields;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.FXUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SearchComboBoxPopup<T> extends Popup implements LCViewInitHelper {
    private Button buttonAddElement;
    private TextField fieldSearch;
    private ListView<T> listView;

    private VBox content;

    private final SearchComboBox<T> searchComboBox;

    public SearchComboBoxPopup(SearchComboBox<T> searchComboBox) {
        this.searchComboBox = searchComboBox;
        initAll();
    }

    // UI
    //========================================================================
    @Override
    public void initUI() {
        this.setAutoFix(true);
        this.setAutoHide(true);

        content = new VBox();
        content.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        content.setPadding(new Insets(10.0));
        content.setSpacing(5.0);
        content.getStyleClass().addAll("popup-bottom-dropshadow", "base-background-with-gray-border-1");
        content.setAlignment(Pos.CENTER);

        fieldSearch = TextFields.createClearableTextField();
        fieldSearch.setPromptText(Translation.getText("tooltip.search.combobox.prompt.text"));

        this.listView = new ListView<>();
        this.listView.setPlaceholder(new Label(Translation.getText("tooltip.search.combobox.placeholder.empty.list")));
        this.listView.setCellFactory(searchComboBox.getCellFactory());
        listView.setFixedCellSize(150.0);
        FXUtils.setFixedHeight(listView, 300);

        content.getChildren().addAll(fieldSearch, listView);
        this.getContent().add(content);

        SystemVirtualKeyboardController.INSTANCE.registerScene(this.getScene());
    }

    @Override
    public void initListener() {
        this.fieldSearch.textProperty().addListener(inv -> searchUpdated());
        // Implementation note : we are forced to use mouse clicked event as the selection item listener cause list view crash
        // if we clear items on selection (because popup is hidden)
        this.listView.setOnMouseClicked(e -> {
            final T item = this.listView.getSelectionModel().getSelectedItem();
            if (item != null) {
                searchComboBox.itemSelected(item);
                this.hide();
            }
        });
        this.setOnHidden(e -> {
            setItems(null);
            fieldSearch.clear();
            SystemVirtualKeyboardController.INSTANCE.unregisterScene(this.getScene());
        });
    }

    @Override
    public void initBinding() {
    }
    //========================================================================


    // POPUP
    //========================================================================
    public void showOnSearchCombobox() {
        this.content.setPrefWidth(searchComboBox.getButtonOpenPopup().getWidth());
        Scene scene = searchComboBox.getScene();
        Window window = scene.getWindow();
        Point2D point2D = searchComboBox.getButtonOpenPopup().localToScene(0, 0);
        this.show(searchComboBox.getButtonOpenPopup(),
                window.getX() + scene.getX() + point2D.getX() - 8.0,
                window.getY() + scene.getY() + point2D.getY() + searchComboBox.getButtonOpenPopup().getHeight() - 4.0);
        //this.fieldSearch.requestFocus();
        setItems(searchComboBox.getItems());
    }
    //========================================================================

    // SEARCH
    //========================================================================
    private void searchUpdated() {
        // TODO : loading indicator ?
        String text = fieldSearch.getText();
        ThreadUtils.debounce(300, "search-combobox-popup" + hashCode(), () -> {
            if (LangUtils.isNotEmpty(sourceItems)) {
                ArrayList<T> copy = new ArrayList<>(sourceItems);
                final Predicate<T> builtPredicate = this.searchComboBox.getPredicateBuilder().apply(text);
                final Predicate<T> predicate = builtPredicate != null ? builtPredicate : c -> true;
                final List<T> itemsFiltered = copy.stream().filter(predicate).collect(Collectors.toList());
                if (searchComboBox.getComparatorBuilder() != null) {
                    final Comparator<T> comparator = searchComboBox.getComparatorBuilder().apply(text);
                    if (comparator != null) {
                        itemsFiltered.sort(comparator);
                    }
                }
                final ObservableList<T> itemsToSet = FXCollections.observableArrayList(itemsFiltered);
                FXThreadUtils.runOnFXThread(() -> {
                    if (sourceItems != null) {// To avoid update while popup is disposed
                        listView.setItems(itemsToSet);
                        this.listView.scrollTo(0);
                    }
                });
            }
        });
    }
    //========================================================================

    // ITEMS
    //========================================================================
    private ObservableList<T> sourceItems;

    private void setItems(ObservableList<T> items) {
        sourceItems = items;
        if (items != null) {
            this.searchUpdated();
        } else {
            this.listView.setItems(null);
        }
    }

    public void setFixedCellSize(double size) {
        listView.setFixedCellSize(size);
    }

    public void enableAddButton(String addButtonText, Function<Node, T> addButtonHandler) {
        if (this.buttonAddElement == null) {
            this.buttonAddElement = FXControlUtils.createRightTextButton(Translation.getText(addButtonText),
                    GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(18).color(LCGraphicStyle.MAIN_PRIMARY), null);
            this.buttonAddElement.setOnAction(e -> {
                T added = addButtonHandler.apply(this.searchComboBox);
                if (added != null) {
                    searchComboBox.itemSelected(added);
                    this.hide();
                }
            });
            this.content.getChildren().add(this.buttonAddElement);
        }
    }
    //========================================================================
}
