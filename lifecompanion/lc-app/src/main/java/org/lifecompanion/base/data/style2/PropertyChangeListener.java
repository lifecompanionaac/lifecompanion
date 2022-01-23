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

package org.lifecompanion.base.data.style2;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import org.lifecompanion.api.style2.property.definition.StylePropertyI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class PropertyChangeListener<V, L> {
    private final Set<StylePropertyI<? extends L>> propertyList;
    private final Function<V, ? extends StylePropertyI<? extends L>> stylePropGetter;

    private final InvalidationListener propValueListener;
    private final ObjectProperty<L> cachedPropValue;
    private final BooleanProperty cachedSelectedNotNull;

    public PropertyChangeListener(Function<V, ? extends StylePropertyI<? extends L>> stylePropGetter) {
        this.stylePropGetter = stylePropGetter;
        this.propertyList = new HashSet<>();
        cachedPropValue = new SimpleObjectProperty<>();
        cachedSelectedNotNull = new SimpleBooleanProperty();
        this.propValueListener = v -> updateCachedPropValue();
    }


    private void updateCachedPropValue() {
        HashMap<L, AtomicInteger> countingMap = new HashMap<>();
        int maxCount = 0;
        L mostUsedValue = null;
        int selectedNotNullCount = 0;
        for (StylePropertyI<? extends L> prop : propertyList) {
            int currentPropCount = countingMap.computeIfAbsent(prop.value().getValue(), v -> new AtomicInteger()).incrementAndGet();
            if (currentPropCount >= maxCount) {
                mostUsedValue = prop.value().getValue();
                maxCount = currentPropCount;
            }
            selectedNotNullCount += prop.isSelectedNotNull().get() ? 1 : 0;
        }
        this.cachedPropValue.set(mostUsedValue);
        this.cachedSelectedNotNull.set(selectedNotNullCount > propertyList.size() / 2);
    }

    public void elementAdded(V elementStyle) {
        StylePropertyI<? extends L> prop = stylePropGetter.apply(elementStyle);
        propertyList.add(prop);
        this.updateCachedPropValue();
        prop.value().addListener(propValueListener);
    }

    public void elementRemoved(V elementStyle) {
        StylePropertyI<? extends L> prop = stylePropGetter.apply(elementStyle);
        propertyList.remove(prop);
        this.updateCachedPropValue();
        prop.value().removeListener(propValueListener);
    }

    public ReadOnlyObjectProperty<L> cachedPropValueProperty() {
        return cachedPropValue;
    }

    public ReadOnlyBooleanProperty cachedSelectedNotNullProperty() {
        return cachedSelectedNotNull;
    }
}
