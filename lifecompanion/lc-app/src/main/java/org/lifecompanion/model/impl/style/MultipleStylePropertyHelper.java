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

package org.lifecompanion.model.impl.style;

import javafx.collections.ObservableList;
import org.lifecompanion.model.api.style.StyleI;
import org.lifecompanion.util.binding.BindingUtils;

import java.util.*;
import java.util.function.Function;

public class MultipleStylePropertyHelper<K, T extends StyleI<?>> {
    private final ObservableList<K> elements;
    private final Function<K, T> styleGetter;
    private final List<PropertyChangeListener<T, ?>> propertyChangeListeners;


    public MultipleStylePropertyHelper(ObservableList<K> elements, Function<K, T> styleGetter, List<PropertyChangeListener<T, ?>> properties) {
        this.elements = elements;
        this.styleGetter = styleGetter;
        this.propertyChangeListeners = properties;
        this.elements.forEach(this::elementAdded);
        this.elements.addListener(BindingUtils.createListChangeListener(this::elementAdded, this::elementRemoved));
    }

    // ABSTRACT
    //========================================================================
    private void elementAdded(K element) {
        T style = styleGetter.apply(element);
        for (PropertyChangeListener<T, ?> propertyChangeListener : propertyChangeListeners) {
            propertyChangeListener.elementAdded(style);
        }
    }

    private void elementRemoved(K element) {
        T style = styleGetter.apply(element);
        for (PropertyChangeListener<T, ?> propertyChangeListener : propertyChangeListeners) {
            propertyChangeListener.elementRemoved(style);
        }
    }

    //========================================================================


}
