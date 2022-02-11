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
package org.lifecompanion.config.view.pane.categorized;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.textfield.TextFields;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementI;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractCategorizedSearchView<T extends CategorizedElementI<?>> extends BorderPane implements LCViewInitHelper {

    /**
     * Grid that display actions
     */
    private Node useActionGridView;

    /**
     * Contains search result
     */
    private ObservableList<T> resultList;

    /**
     * Field to enter search request
     */
    private TextField fieldSearch;

    /**
     * Property on the list
     */
    private SimpleListProperty<T> resultListProperty;

    /**
     * Label to show when the result list is empty
     */
    private Label labelPlaceholder;

    private Consumer<T> selectionCallback;

    /**
     * Create a view that display every actions of a sub category
     *
     * @param subCategoryP the sub category to display
     */
    public AbstractCategorizedSearchView(final Consumer<T> selectionCallbackP) {
        this.selectionCallback = selectionCallbackP;
        this.resultList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        this.resultListProperty = new SimpleListProperty<>(this.resultList);
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        //TODO : specific field for action/event -> subclass need specific placeholder

        //Top : search request
        this.fieldSearch = TextFields.createClearableTextField();
        this.fieldSearch.setPromptText(this.getSearchPromptText());
        this.setTop(this.fieldSearch);

        //PLaceholder
        this.labelPlaceholder = new Label(Translation.getText("use.action.search.no.result"));
        this.setCenter(this.labelPlaceholder);

        //Center : display result
        this.useActionGridView = this.createItemsView(this.resultList, this.selectionCallback);
        BorderPane.setMargin(this.useActionGridView, new Insets(4.0));
    }

    @Override
    public void initListener() {
        this.fieldSearch.textProperty().addListener((inv) -> {
            String terms = this.fieldSearch.getText();
            List<T> result = this.searchForElement(terms);
            this.resultList.clear();
            this.resultList.addAll(result);
        });
    }

    protected abstract Node createItemsView(ObservableList<T> resultList, Consumer<T> selectionCallback);

    protected abstract List<T> searchForElement(String terms);

    protected abstract String getSearchPromptText();
    //========================================================================

    // Class part : "Binding"
    //========================================================================
    @Override
    public void initBinding() {
        this.resultListProperty.emptyProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                this.setCenter(this.labelPlaceholder);
            } else {
                this.setCenter(this.useActionGridView);
            }
        });
    }
    //========================================================================

}
