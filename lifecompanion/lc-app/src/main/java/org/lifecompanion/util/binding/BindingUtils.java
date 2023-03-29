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
import org.lifecompanion.util.model.CountingMap;

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

    /**
     * Helper to create a list change listener.
     *
     * @param forEachAdd    called on each added element
     * @param forEachRemove called on each removed element
     * @param <T>           list type
     * @return the listener
     * @deprecated see {@link #createListChangeListenerV2(Consumer, Consumer)} instead
     */
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

    /**
     * Helper to create a list change listener, but that will take the element counts into account.<br>
     * This means that if the element is added twice, only one call to onAdd will be made. Following the same principle, onRemove will be called only the element is not in the list anymore.<br>
     * <br> Note that even if the list is given to this method, listener should be added later with {@link ObservableList#addListener(ListChangeListener)}
     *
     * @param list     the list where binding will be added
     * @param onAdd    called on each unique add (element was not in the list before)
     * @param onRemove called on each unique remove (element is not in the list anymore)
     * @param <T>      the list type
     * @return the change listener
     */
    public static <T> ListChangeListener<T> createUniqueAddOrRemoveListener(ObservableList<T> list, Consumer<T> onAdd, Consumer<T> onRemove) {
        CountingMap<T> counts = new CountingMap<>();
        counts.setCountsFrom(list);
        return c -> {
            CountingMap<T> countsBeforeChange = counts.clone();
            while (c.next()) {
                if (c.wasPermutated() || c.wasUpdated()) {
                    // Don't do anything
                } else {
                    LangUtils.consumeEachIn(c.getRemoved(), counts::decrement);
                    LangUtils.consumeEachIn(c.getAddedSubList(), counts::increment);
                }
            }
            countsBeforeChange.forEach((e, v) -> {
                if (counts.getCount(e) == 0) {
                    onRemove.accept(e);
                }
            });
            counts.forEach((e, v) -> {
                if (countsBeforeChange.getCount(e) == 0) {
                    onAdd.accept(e);
                }
            });
        };
    }

    /**
     * See {@link #createListChangeListenerV2(Consumer, Consumer, Runnable)}
     */
    public static <T> ListChangeListener<T> createListChangeListenerV2(final Consumer<T> forEachAdd, final Consumer<T> forEachRemove) {
        return createListChangeListenerV2(forEachAdd, forEachRemove, null);
    }

    /**
     * Helper to create list change listener.<br>
     * Note that this will create exact change listener : if an element is added/removed twice in the list, the changes will be propagated.<br>
     * If you just need to detect if an element is contained in the list no matter its contains count, you should use {@link #createUniqueAddOrRemoveListener(ObservableList, Consumer, Consumer)}
     *
     * @param forEachAdd        called on each element added
     * @param forEachRemove     called on each element remove
     * @param onChangeProcessed called once every change are processed
     * @param <T>               list type
     * @return the listener
     */
    public static <T> ListChangeListener<T> createListChangeListenerV2(final Consumer<T> forEachAdd, final Consumer<T> forEachRemove, Runnable onChangeProcessed) {
        return (c) -> {
            while (c.next()) {
                if (c.wasPermutated() || c.wasUpdated()) {
                    // Don't do anything
                } else {
                    // Order is important here : removed should be handled before added
                    LangUtils.consumeEachIn(c.getRemoved(), forEachRemove);
                    LangUtils.consumeEachIn(c.getAddedSubList(), forEachAdd);
                }
            }
            if (onChangeProcessed != null) onChangeProcessed.run();
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
