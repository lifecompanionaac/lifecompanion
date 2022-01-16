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

package org.lifecompanion.config.view.pane.compselector;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import org.lifecompanion.api.component.definition.DisplayableComponentI;
import org.lifecompanion.api.ui.ComponentViewI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.use.data.ui.UseViewProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public enum NodeSnapshotCache {
    INSTANCE;

    private static final String LOAD_REQUEST_ID = "displayable-component-list-cell";

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeSnapshotCache.class);

    private static final long CLEAR_AFTER_LAST_USE = 10_000;

    private final ExecutorService loadingService;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<ComponentSnapshotTask>> runningLoadingTasks;
    private final ConcurrentHashMap<String, CachedSnapshot> snapshotCache;

    private final Timer cleanupTimer;

    NodeSnapshotCache() {
        this.loadingService = Executors.newSingleThreadExecutor(LCNamedThreadFactory.daemonThreadFactory("Node-Snapshot-Cache"));
        this.runningLoadingTasks = new ConcurrentHashMap<>();
        this.snapshotCache = new ConcurrentHashMap<>();
        this.cleanupTimer = new Timer(true);
        this.cleanupTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                snapshotCache.keySet().removeIf(id -> {
                    final CachedSnapshot cachedSnapshot = snapshotCache.get(id);
                    if (cachedSnapshot == null) return false;
                    return System.currentTimeMillis() - cachedSnapshot.lastUsed >= CLEAR_AFTER_LAST_USE;
                });
                LOGGER.info("Node snapshot cache size : {}", snapshotCache.size());
                if (AppController.INSTANCE.currentConfigConfigurationProperty().get() != null) {
                    //                    LOGGER.info("Initialized : {}/{}",
                    //                            AppController.INSTANCE.currentConfigConfigurationProperty().get().getAllComponent().values().stream().filter(DisplayableComponentI::isDisplayInitialized).count(), AppController.INSTANCE.currentConfigConfigurationProperty().get().getAllComponent().size());
                }
            }
        }, 1000, 1000);
    }

    public void cancelRequestSnapshot(DisplayableComponentI component) {
        cancelRequestSnapshot(component.getID());
    }

    public void cancelRequestSnapshot(String componentId) {
        CopyOnWriteArrayList<ComponentSnapshotTask> previousTasks = runningLoadingTasks.remove(componentId);
        if (previousTasks != null) {
            List<ComponentSnapshotTask> clearTasks = new ArrayList<>(previousTasks);
            previousTasks.removeAll(clearTasks);
            clearTasks.forEach(ComponentSnapshotTask::cancel);
        }
    }

    public void cancelAllSnapshotRequest() {
        Set<String> toRemove = new HashSet<>(runningLoadingTasks.keySet());
        toRemove.forEach(this::cancelRequestSnapshot);
    }

    public void requestSnapshot(DisplayableComponentI component, double w, double h, Consumer<Image> callback) {
        final CachedSnapshot cachedSnapshot = snapshotCache.get(component.getID());
        if (cachedSnapshot != null) {
            cachedSnapshot.updateLastUsed();
            callback.accept(cachedSnapshot.image);
        } else {
            if (!this.runningLoadingTasks.containsKey(component.getID())) {
                ComponentSnapshotTask task = new ComponentSnapshotTask(component, w, h);
                this.runningLoadingTasks.computeIfAbsent(component.getID(), id -> new CopyOnWriteArrayList<>()).add(task);
                EventHandler<WorkerStateEvent> eventHandlerTaskFinished = e -> {
                    CopyOnWriteArrayList<ComponentSnapshotTask> loadingTasks = runningLoadingTasks.get(component.getID());
                    if (loadingTasks != null) {
                        loadingTasks.remove(task);
                    }
                    LCUtils.unloadAllImagesIn(LOAD_REQUEST_ID, component);
                    if (task.getException() != null) {
                        LOGGER.error("Problem on snaphsot", task.getException());
                    }
                };
                task.setOnCancelled(eventHandlerTaskFinished);
                task.setOnFailed(eventHandlerTaskFinished);
                task.setOnSucceeded(e -> {
                    eventHandlerTaskFinished.handle(e);
                    final Image img = task.getValue();
                    if (img != null) {
                        snapshotCache.put(component.getID(), new CachedSnapshot(img));
                        callback.accept(img);
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

        /*
         Fix note : inject viewprovider in getDisplay and also inject "ignoreCache" param.
         Both params will be stored in view and injected to children.
         /!\ this can only work if the view doesn't created gc dependency to the model > view is stored in model, but view should not store the model >> wtf ?
         */

        @Override
        protected Image call() throws Exception {
            if (!isCancelled()) {
                LCUtils.loadAllImagesIn(LOAD_REQUEST_ID, 1000, component);
                return LCUtils.runOnFXThreadAndWaitFor(() -> {
                    ComponentViewI display = component.getDisplay(new UseViewProvider(), false);
                    Image r = UIUtils.takeNodeSnapshot(display.getView(), w, h);
                    LCUtils.unloadAllImagesIn(LOAD_REQUEST_ID, component);
                    display.unbindComponentAndChildren();
                    return r;
                });
            }
            return null;
        }
    }
}
