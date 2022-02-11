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

package org.lifecompanion.controller.plugin;

import org.lifecompanion.framework.utils.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PluginImplementationLoadingHelper<T> {
    private final T type;
    private final List<Pair<String, T>> cachedElements;
    private BiConsumer<String, T> addListener;

    PluginImplementationLoadingHelper(T type) {
        this.type = type;
        cachedElements = new ArrayList<>();
    }

    public void registerListenerAndDrainCache(Consumer<T> addListener) {
        this.registerListenerAndDrainCache((pluginId, element) -> addListener.accept(element));
    }

    public void registerListenerAndDrainCache(BiConsumer<String, T> addListener) {
        if (this.addListener != null) throw new IllegalStateException("Can't register an add listener twice");
        this.addListener = addListener;
        for (Pair<String, T> cachedElement : cachedElements) {
            addListener.accept(cachedElement.getLeft(), cachedElement.getRight());
        }
        cachedElements.clear();
    }

    void elementAdded(String pluginId, Collection<T> elements) {
        for (T element : elements) {
            if (addListener != null) {
                addListener.accept(pluginId, element);
            } else {
                cachedElements.add(Pair.of(pluginId, element));
            }
        }
    }

    public T getType() {
        return type;
    }
}
