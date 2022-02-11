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

package org.lifecompanion.util;

import javafx.beans.WeakListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Copied from Bindings#bindContent and changed to include content mapping.<br>
 * <a href="https://stackoverflow.com/questions/43890528/observablelist-bind-content-with-elements-conversion">Source from StackOverflow</a>
 */
public class ListBindingWithMapper {

    public static <E, F> void mapContent(ObservableList<F> mapped, ObservableList<? extends E> source,
                                         Function<? super E, ? extends F> mapper) {
        map(mapped, source, mapper);
    }

    private static <E, F> Object map(ObservableList<F> mapped, ObservableList<? extends E> source,
                                     Function<? super E, ? extends F> mapper) {
        final ListContentMapping<E, F> contentMapping = new ListContentMapping<E, F>(mapped, mapper);
        mapped.setAll(source.stream().map(mapper).collect(toList()));
        source.removeListener(contentMapping);
        source.addListener(contentMapping);
        return contentMapping;
    }

    private static class ListContentMapping<E, F> implements ListChangeListener<E>, WeakListener {
        private final WeakReference<List<F>> mappedRef;
        private final Function<? super E, ? extends F> mapper;

        public ListContentMapping(List<F> mapped, Function<? super E, ? extends F> mapper) {
            this.mappedRef = new WeakReference<List<F>>(mapped);
            this.mapper = mapper;
        }

        @Override
        public void onChanged(Change<? extends E> change) {
            final List<F> mapped = mappedRef.get();
            if (mapped == null) {
                change.getList().removeListener(this);
            } else {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        mapped.subList(change.getFrom(), change.getTo()).clear();
                        mapped.addAll(change.getFrom(), change.getList().subList(change.getFrom(), change.getTo())
                                .stream().map(mapper).collect(toList()));
                    } else {
                        if (change.wasRemoved()) {
                            mapped.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
                        }
                        if (change.wasAdded()) {
                            mapped.addAll(change.getFrom(), change.getAddedSubList()
                                    .stream().map(mapper).collect(toList()));
                        }
                    }
                }
            }
        }

        @Override
        public boolean wasGarbageCollected() {
            return mappedRef.get() == null;
        }

        @Override
        public int hashCode() {
            final List<F> list = mappedRef.get();
            return (list == null) ? 0 : list.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            final List<F> mapped1 = mappedRef.get();
            if (mapped1 == null) {
                return false;
            }

            if (obj instanceof ListContentMapping) {
                final ListContentMapping<?, ?> other = (ListContentMapping<?, ?>) obj;
                final List<?> mapped2 = other.mappedRef.get();
                return mapped1 == mapped2;
            }
            return false;
        }
    }
}