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

package org.lifecompanion.controller.editmode;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.ui.configurationcomponent.ComponentViewI;
import org.lifecompanion.model.api.ui.configurationcomponent.ViewProviderI;
import org.lifecompanion.model.impl.ui.configurationcomponent.UseViewProvider;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.SnapshotUtils;
import org.lifecompanion.util.model.ImageDictionaryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public enum DisplayableComponentSnapshotController {
    INSTANCE;

    private static final String LOAD_REQUEST_ID = "displayable-component-snapshot";

    private static final Logger LOGGER = LoggerFactory.getLogger(DisplayableComponentSnapshotController.class);

    private static final ViewProviderI USE_VIEW_PROVIDER = new UseViewProvider();

    private static final long IMAGE_LOADING_TIMEOUT = 3000, CLEAR_AFTER_LAST_USE = 60_000;

    private final ExecutorService loadingService;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<ComponentSnapshotTask>> runningLoadingTasks;
    private final ConcurrentHashMap<String, CachedSnapshot> snapshotCache;

    DisplayableComponentSnapshotController() {
        this.loadingService = Executors.newSingleThreadExecutor(LCNamedThreadFactory.daemonThreadFactory("Node-Snapshot-Cache"));
        this.runningLoadingTasks = new ConcurrentHashMap<>();
        this.snapshotCache = new ConcurrentHashMap<>();
        Timer cleanupTimer = new Timer(true);
        cleanupTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                snapshotCache.keySet().removeIf(id -> {
                    final CachedSnapshot cachedSnapshot = snapshotCache.get(id);
                    if (cachedSnapshot == null) return false;
                    return System.currentTimeMillis() - cachedSnapshot.lastUsed >= CLEAR_AFTER_LAST_USE;
                });
            }
        }, 1000, 5000);
    }

    public void cancelRequestSnapshot(DisplayableComponentI component) {
        cancelRequestSnapshot(component.getID());
    }

    private void cancelRequestSnapshot(String componentId) {
        CopyOnWriteArrayList<ComponentSnapshotTask> previousTasks = runningLoadingTasks.remove(componentId);
        if (previousTasks != null) {
            List<ComponentSnapshotTask> clearTasks = new ArrayList<>(previousTasks);
            previousTasks.removeAll(clearTasks);
            clearTasks.forEach(ComponentSnapshotTask::cancel);
        }
    }

    public void requestSnapshotAsync(DisplayableComponentI component, boolean useCache, double w, double h, BiConsumer<DisplayableComponentI, Image> callback) {
        final CachedSnapshot cachedSnapshot = snapshotCache.get(component.getID());
        if (useCache && cachedSnapshot != null) {
            cachedSnapshot.updateLastUsed();
            callback.accept(component, cachedSnapshot.image);
        } else {
            if (!this.runningLoadingTasks.containsKey(component.getID())) {
                ComponentSnapshotTask task = new ComponentSnapshotTask(component, w, h);
                this.runningLoadingTasks.computeIfAbsent(component.getID(), id -> new CopyOnWriteArrayList<>()).add(task);
                EventHandler<WorkerStateEvent> eventHandlerTaskFinished = e -> {
                    CopyOnWriteArrayList<ComponentSnapshotTask> loadingTasks = runningLoadingTasks.get(component.getID());
                    if (loadingTasks != null) {
                        loadingTasks.remove(task);
                    }
                    ImageDictionaryUtils.unloadAllImagesIn(LOAD_REQUEST_ID, component);
                    if (task.getException() != null) {
                        LOGGER.error("Problem to make a component snaphsot", task.getException());
                    }
                };
                task.setOnCancelled(eventHandlerTaskFinished);
                task.setOnFailed(eventHandlerTaskFinished);
                task.setOnSucceeded(e -> {
                    eventHandlerTaskFinished.handle(e);
                    final Image img = task.getValue();
                    if (img != null) {
                        if (useCache) {
                            snapshotCache.put(component.getID(), new CachedSnapshot(img));
                        }
                        callback.accept(component, img);
                    }
                });
                if (!this.loadingService.isShutdown()) {
                    this.loadingService.submit(task);
                }
            }
        }
    }

    static class CachedSnapshot {
        private long lastUsed;
        private final Image image;

        CachedSnapshot(Image image) {
            this.image = image;
            updateLastUsed();
        }

        public void updateLastUsed() {
            this.lastUsed = System.currentTimeMillis();
        }
    }

    private static class ComponentSnapshotTask extends Task<Image> {
        private final DisplayableComponentI component;
        private final double w, h;

        ComponentSnapshotTask(DisplayableComponentI component, double w, double h) {
            this.component = component;
            this.w = w;
            this.h = h;
        }

        @Override
        protected Image call() throws Exception {
            if (!isCancelled()) {
                return getComponentSnapshot(component, true, w, h);
            }
            return null;
        }
    }

    public static Image getComponentSnapshot(DisplayableComponentI component, boolean loadAllImages, double width, double height) {
        if (loadAllImages) {
            ImageDictionaryUtils.loadAllImagesIn(LOAD_REQUEST_ID, IMAGE_LOADING_TIMEOUT, component);
        }
        return FXThreadUtils.runOnFXThreadAndWaitFor(() -> {
            ComponentViewI<?> display = component.getDisplay(USE_VIEW_PROVIDER, false);
            try {
                return SnapshotUtils.takeNodeSnapshot(display.getView(), width, height, true, 1.0);
            } finally {
                display.unbindComponentAndChildren();
                ImageDictionaryUtils.unloadAllImagesIn(LOAD_REQUEST_ID, component);
            }
        });
    }

}
