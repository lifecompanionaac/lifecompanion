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

package org.lifecompanion.util.binding;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import org.lifecompanion.util.LangUtils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BindingUtils {
    public static <K, V> MapChangeListener<K, V> createBindMapValue(final ObservableList<V> list) {
        return (change) -> {
            V added = change.getValueAdded();
            if (added != null) {
                list.add(added);
            }
            V removed = change.getValueRemoved();
            if (removed != null) {
                list.remove(removed);
            }
        };
    }

    // Should be checked and replaced with V2 implementation
    @Deprecated
    public static <T> ListChangeListener<T> createListChangeListener(final Consumer<T> forEachAdd, final Consumer<T> forEachRemove) {
        ListChangeListener<T> changeListener = (change) -> {
            while (change.next()) {
                if (change.wasAdded() && forEachAdd != null) {
                    List<? extends T> addeds = change.getAddedSubList();
                    for (T added : addeds) {
                        forEachAdd.accept(added);
                    }
                }
                if (change.wasRemoved() && forEachRemove != null) {
                    List<? extends T> removeds = change.getRemoved();
                    for (T removed : removeds) {
                        forEachRemove.accept(removed);
                    }
                }
            }
        };
        return changeListener;
    }

    // This version is correctly implemented for other actions that simple add/remove
    public static <T> ListChangeListener<T> createListChangeListenerV2(final Consumer<T> forEachAdd, final Consumer<T> forEachRemove) {
        return (c) -> {
            while (c.next()) {
                if (c.wasPermutated() || c.wasUpdated()) {
                    // Don't do anything
                } else {
                    // Order is important here : removed should be handled before added
                    // this is related to ModifiableObservableListBase.setAll implementation (it does clear() then addAll(...) in the same Change)
                    LangUtils.consumeEachIn(c.getRemoved(), forEachRemove);
                    LangUtils.consumeEachIn(c.getAddedSubList(), forEachAdd);
                }
            }
        };
    }

    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static StringBinding createFormatedIntBinding(ReadOnlyIntegerProperty longProperty, Function<Integer, Double> transformFunction) {
        return Bindings.createStringBinding(() -> {
            final int value = longProperty.get();
            return DOUBLE_DECIMAL_FORMAT.format((double) (transformFunction != null ? transformFunction.apply(value) : value));
        }, longProperty);
    }

    public static StringBinding createDivide1000Binding(ReadOnlyIntegerProperty longProperty) {
        return createFormatedIntBinding(longProperty, v -> v / 1000.0);
    }

    public static void unbindAndSetNull(final Property<?> prop) {
        prop.unbind();
        prop.setValue(null);
    }

    public static <T> void unbindAndSet(final Property<T> prop, T val) {
        prop.unbind();
        prop.setValue(val);
    }

    public static DoubleBinding bindToValueOrIfInfinityOrNan(ObservableDoubleValue binding, double or) {
        return Bindings.createDoubleBinding(() -> {
            double val = binding.get();
            return Double.isFinite(val) ? val : or;
        }, binding);
    }
}
