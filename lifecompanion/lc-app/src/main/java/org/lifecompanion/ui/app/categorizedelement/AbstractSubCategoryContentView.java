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
package org.lifecompanion.ui.app.categorizedelement;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementI;
import org.lifecompanion.model.api.categorizedelement.SubCategoryI;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.function.Consumer;

public abstract class AbstractSubCategoryContentView<V extends CategorizedElementI<T>, T extends SubCategoryI<?, V>> extends BorderPane
        implements LCViewInitHelper {

    /**
     * Displayed sub category
     */
    private final T subCategory;

    private final Consumer<V> selectionCallback;

    /**
     * Create a view that display every actions of a sub category
     *
     * @param subCategoryP the sub category to display
     */
    public AbstractSubCategoryContentView(final T subCategoryP, final Consumer<V> selectionCallbackP) {
        this.subCategory = subCategoryP;
        this.selectionCallback = selectionCallbackP;
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        //Top : title
        Label labelTitle = FXControlUtils.createTitleLabel(this.subCategory.getName());
        BorderPane.setMargin(labelTitle, new Insets(0.0, 0.0, 4.0, 0.0));
        this.setTop(labelTitle);

        //Center : display actions
        Node useActionGridView = this.getCategorizedItemsView(this.subCategory.getContent(), this.selectionCallback);
        BorderPane.setMargin(useActionGridView, new Insets(4.0));
        BorderPane.setAlignment(useActionGridView, Pos.CENTER);
        this.setCenter(useActionGridView);
    }

    protected abstract Node getCategorizedItemsView(ObservableList<V> content, Consumer<V> selectionCallback);
    //========================================================================
}
