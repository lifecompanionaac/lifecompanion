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

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.TilePane;
import org.lifecompanion.api.component.definition.eventaction.CategorizedElementI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.config.view.pane.categorized.cell.AbstractCategorizedItemView;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractCategorizedItemsView<T extends CategorizedElementI<?>> extends TilePane implements LCViewInitHelper {
    private static final double CELL_WIDTH = 150, CELL_HEIGHT = 120;
    private static final double V_SPACING = 5.0, H_SPACING = 2.0;

    private ObservableList<T> items;
    private Map<T, AbstractCategorizedItemView<T>> itemsView;
    private Consumer<T> selectionCallback;

    public AbstractCategorizedItemsView(final ObservableList<T> itemsP, final Consumer<T> selectionCallbackP) {
        this.items = itemsP;
        this.selectionCallback = selectionCallbackP;
        this.itemsView = new HashMap<>();
        this.initAll();
    }

    @Override
    public void initUI() {
        this.setPrefTileWidth(CELL_WIDTH);
        this.setPrefTileHeight(CELL_HEIGHT);
        this.setHgap(AbstractCategorizedItemsView.H_SPACING);
        this.setVgap(AbstractCategorizedItemsView.V_SPACING);
        this.setAlignment(Pos.CENTER_LEFT);
        this.prefHeightProperty().bind(this.minHeightProperty());
    }

    @Override
    public void initBinding() {
        for (T action : this.items) {
            AbstractCategorizedItemView<T> cellView = this.createCell(action);
            this.getChildren().add(cellView);
        }
        //On list change
        this.items.addListener(LCUtils.createListChangeListener((added) -> {
            //Get view
            AbstractCategorizedItemView<T> addedView;
            if (this.itemsView.containsKey(added)) {
                addedView = this.itemsView.get(added);
            } else {
                addedView = this.createCell(added);
            }
            //Check
            if (!this.getChildren().contains(addedView)) {
                this.getChildren().add(addedView);
            }
        }, (removed) -> {
            this.getChildren().remove(this.itemsView.get(removed));
        }));
    }

    /**
     * This method simulate the use of a GridCell.<br>
     * This is done because virtualization have no use with this items.
     *
     * @return the cell that contains the action
     */
    private AbstractCategorizedItemView<T> createCell(final T element) {
        AbstractCategorizedItemView<T> cell = this.createCell(element, this.selectionCallback);
        cell.setPrefWidth(CELL_WIDTH);
        cell.setPrefHeight(CELL_HEIGHT);
        this.itemsView.put(element, cell);
        return cell;
    }

    protected abstract AbstractCategorizedItemView<T> createCell(final T element, Consumer<T> selectionCallback);
}
