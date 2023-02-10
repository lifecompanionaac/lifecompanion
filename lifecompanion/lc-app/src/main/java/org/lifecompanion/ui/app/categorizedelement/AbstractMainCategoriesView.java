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
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import org.lifecompanion.model.api.categorizedelement.MainCategoryI;
import org.lifecompanion.ui.common.pane.specific.cell.AbstractMainCategoryItemView;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.binding.BindingUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractMainCategoriesView<T extends MainCategoryI<?>> extends ScrollPane implements LCViewInitHelper {
    private static final double CELL_WIDTH = 150, CELL_HEIGHT = 120;
    private static final double V_SPACING = 5.0, H_SPACING = 2.0;

    private final Consumer<T> categorySelectionCallback;

    private Map<T, AbstractMainCategoryItemView<T>> views;

    private TilePane tilePaneCategories;

    /**
     * Create the view to display every main categories
     */
    public AbstractMainCategoriesView(final Consumer<T> categorySelectionCallbackP) {
        this.categorySelectionCallback = categorySelectionCallbackP;
        this.views = new HashMap<>();
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        tilePaneCategories = new TilePane();
        tilePaneCategories.setPrefTileWidth(CELL_WIDTH);
        tilePaneCategories.setPrefTileHeight(CELL_HEIGHT);
        tilePaneCategories.setVgap(V_SPACING);
        tilePaneCategories.setHgap(H_SPACING);
        tilePaneCategories.setAlignment(Pos.CENTER_LEFT);

        this.getStyleClass().addAll("background-transparent","border-transparent");
        this.setContent(this.tilePaneCategories);
        this.setFitToWidth(true);
    }

    protected abstract AbstractMainCategoryItemView<T> createCell(T item, Consumer<T> categorySelectionCallback);

    protected abstract ObservableList<T> getMainCategories();
    //========================================================================

    // Class part : "Binding"
    //========================================================================
    @Override
    public void initBinding() {
        for (T action : this.getMainCategories()) {
            tilePaneCategories.getChildren().add(this.createCell(action));
        }
        //On list change
        this.getMainCategories().addListener(BindingUtils.createListChangeListener((added) -> {
            //Get view
            AbstractMainCategoryItemView<T> addedView;
            if (this.views.containsKey(added)) {
                addedView = this.views.get(added);
            } else {
                addedView = this.createCell(added);
            }
            //Check
            if (!this.getChildren().contains(addedView)) {
                tilePaneCategories.getChildren().add(addedView);
            }
        }, (removed) -> {
            this.getChildren().remove(this.views.get(removed));
        }));
    }

    private AbstractMainCategoryItemView<T> createCell(final T element) {
        AbstractMainCategoryItemView<T> cell = createCell(element, this.categorySelectionCallback);
        cell.setPrefWidth(CELL_WIDTH);
        cell.setPrefHeight(CELL_HEIGHT);
        this.views.put(element, cell);
        return cell;
    }
    //========================================================================

}
