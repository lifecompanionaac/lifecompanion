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

package org.lifecompanion.util.model;

import javafx.collections.ObservableList;
import org.lifecompanion.util.binding.BindingUtils;

import java.util.List;

public class GridPartKeyCollectionPropertyHolder<K> {
    private final ObservableList<K> elements;
    private final List<GridPartKeyPropertyChangeListener<K, ?, ?>> propertyChangeListeners;


    public GridPartKeyCollectionPropertyHolder(ObservableList<K> elements, List<GridPartKeyPropertyChangeListener<K, ?, ?>> properties) {
        this.elements = elements;
        this.propertyChangeListeners = properties;
        this.elements.forEach(this::elementAdded);
        this.elements.addListener(BindingUtils.createListChangeListener(this::elementAdded, this::elementRemoved));
    }

    // ABSTRACT
    //========================================================================
    private void elementAdded(K element) {
        for (GridPartKeyPropertyChangeListener<K, ?, ?> propertyChangeListener : propertyChangeListeners) {
            propertyChangeListener.elementAdded(element);
        }
    }

    private void elementRemoved(K element) {
        for (GridPartKeyPropertyChangeListener<K, ?, ?> propertyChangeListener : propertyChangeListeners) {
            propertyChangeListener.elementRemoved(element);
        }
    }
    //========================================================================

}
