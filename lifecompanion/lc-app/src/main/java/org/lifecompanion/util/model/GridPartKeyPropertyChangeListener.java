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

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class GridPartKeyPropertyChangeListener<K, V, N> {
    private final Set<ReadOnlyProperty<? extends V>> propertyList;
    private final Function<K, ? extends ReadOnlyProperty<? extends V>> propGetter;
    private final Function<V, N> valueTransformer;

    private final InvalidationListener propValueListener;
    private final ObjectProperty<N> cachedPropValue;

    public GridPartKeyPropertyChangeListener(Function<K, ? extends ReadOnlyProperty<? extends V>> propGetter, Function<V, N> valueTransformer) {
        this.propGetter = propGetter;
        this.propertyList = new HashSet<>();
        this.cachedPropValue = new SimpleObjectProperty<>();
        this.propValueListener = v -> updateCachedPropValue();
        this.valueTransformer = valueTransformer;
    }

    public GridPartKeyPropertyChangeListener(Function<K, ? extends ReadOnlyProperty<? extends V>> propGetter) {
        this(propGetter, (Function<V, N>) Function.identity());
    }

    private void updateCachedPropValue() {
        HashMap<N, AtomicInteger> countingMap = new HashMap<>();
        int maxCount = 0;
        N mostUsedValue = null;
        for (ReadOnlyProperty<? extends V> prop : propertyList) {
            int currentPropCount = countingMap.computeIfAbsent(valueTransformer.apply(prop.getValue()), v -> new AtomicInteger()).incrementAndGet();
            if (currentPropCount >= maxCount) {
                mostUsedValue = valueTransformer.apply(prop.getValue());
                maxCount = currentPropCount;
            }
        }
        this.cachedPropValue.set(mostUsedValue);
    }

    public void elementAdded(K elementStyle) {
        ReadOnlyProperty<? extends V> prop = propGetter.apply(elementStyle);
        propertyList.add(prop);
        this.updateCachedPropValue();
        prop.addListener(propValueListener);
    }

    public void elementRemoved(K elementStyle) {
        ReadOnlyProperty<? extends V> prop = propGetter.apply(elementStyle);
        propertyList.remove(prop);
        this.updateCachedPropValue();
        prop.removeListener(propValueListener);
    }

    public ReadOnlyObjectProperty<N> cachedPropValueProperty() {
        return cachedPropValue;
    }

}
