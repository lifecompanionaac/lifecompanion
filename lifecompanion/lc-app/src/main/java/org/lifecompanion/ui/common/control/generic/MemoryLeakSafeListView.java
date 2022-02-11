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

package org.lifecompanion.ui.common.control.generic;


import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.lifecompanion.util.LCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ListView implementation to solve JDK-8227619 (memory leak in list view)
 */
public class MemoryLeakSafeListView<T> extends BorderPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryLeakSafeListView.class);

    private final ObjectProperty<T> selectedItem;
    private final IntegerProperty selectedIndex;
    private final ListProperty<T> propertyChildrenList;
    private ListView<T> listView;
    private Callback<ListView<T>, ListCell<T>> cellFactory;

    public MemoryLeakSafeListView() {
        super();
        this.selectedItem = new SimpleObjectProperty<>();
        this.selectedIndex = new SimpleIntegerProperty();
        this.propertyChildrenList = new SimpleListProperty<>();
        this.renewListView();
    }

    private void renewListView() {
        LCUtils.unbindAndSetNull(selectedItem);
        LCUtils.unbindAndSetNull(selectedIndex);
        LCUtils.unbindAndSetNull(propertyChildrenList);
        this.listView = new ListView<>();
        this.selectedItem.bind(this.listView.getSelectionModel().selectedItemProperty());
        this.selectedIndex.bind(this.listView.getSelectionModel().selectedIndexProperty());
        this.propertyChildrenList.bind(this.listView.itemsProperty());
        if (cellFactory != null) {
            listView.setCellFactory(cellFactory);
        }
        this.setCenter(listView);
    }

    public void setItemsFixML(ObservableList<T> items) {
        this.renewListView();
        listView.setItems(items);
    }

    public ReadOnlyObjectProperty<T> selectedItemProperty() {
        return selectedItem;
    }

    public T getSelectedItem() {
        return selectedItem.get();
    }

    public ReadOnlyIntegerProperty selectedIndexProperty() {
        return selectedIndex;
    }

    public void setCellFactory(Callback<ListView<T>, ListCell<T>> cellFactory) {
        this.cellFactory = cellFactory;
        this.listView.setCellFactory(cellFactory);
    }

    public void select(T item) {
        listView.getSelectionModel().select(item);
    }

    public void scrollTo(T item) {
        listView.scrollTo(item);
    }

    public ListProperty<T> getPropertyChildrenList() {
        return propertyChildrenList;
    }
}
