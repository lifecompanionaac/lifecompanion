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
import java.util.function.Function;

public class PluginImplementationLoadingHandler<T> {
    private final T type;
    private final List<Pair<String, T>> cachedElements;
    private final BiConsumer<String, Throwable> errorHandler;
    private PluginElementAddedWithIdListener<T> addListener;

    PluginImplementationLoadingHandler(T type, BiConsumer<String, Throwable> errorHandler) {
        this.type = type;
        this.cachedElements = new ArrayList<>();
        this.errorHandler = errorHandler;
    }

    public void registerListenerAndDrainCache(PluginElementAddedListener<T> addListener) {
        this.registerListenerAndDrainCache((pluginId, element) -> addListener.elementAdded(element));
    }

    public void registerListenerAndDrainCache(PluginElementAddedWithIdListener<T> addListener) {
        if (this.addListener != null) throw new IllegalStateException("Can't register an add listener twice");
        this.addListener = addListener;
        for (Pair<String, T> cachedElement : cachedElements) {
            handleListener(addListener, cachedElement.getLeft(), cachedElement.getRight());
        }
        cachedElements.clear();
    }

    private void handleListener(PluginElementAddedWithIdListener<T> listener, String pluginId, T element) {
        try {
            listener.elementAdded(pluginId, element);
        } catch (Throwable t) {
            this.errorHandler.accept(pluginId, t);
        }
    }

    void elementAdded(String pluginId, Collection<T> elements) {
        for (T element : elements) {
            if (addListener != null) {
                handleListener(addListener, pluginId, element);
            } else {
                cachedElements.add(Pair.of(pluginId, element));
            }
        }
    }

    public T getType() {
        return type;
    }
}
