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

package org.lifecompanion.controller.editaction;

import javafx.concurrent.Task;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.util.model.LCTask;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum AsyncExecutorController implements LCStateListener {
    INSTANCE;

    /**
     * Thread pool use to do various configuration actions
     */
    private final ExecutorService configurationActionThreadPool;

    /**
     * To listen on task added
     */
    private final List<TaskAddedForExecutionListener> taskAddedForExecutionListeners;

    AsyncExecutorController() {
        this.configurationActionThreadPool = Executors.newFixedThreadPool(LCConstant.CONFIGURATION_ACTION_POOL_SIZE, LCNamedThreadFactory.threadFactory("AsyncExecutorController"));
        this.taskAddedForExecutionListeners = new ArrayList<>();
    }

    public void addTaskAddedForExecutionListener(TaskAddedForExecutionListener taskAddedForExecutionListener) {
        this.taskAddedForExecutionListeners.add(taskAddedForExecutionListener);
    }

    public void addAndExecute(boolean blocking, boolean hideFromNotification, final Runnable task) {
        addAndExecute(blocking, hideFromNotification, task, null);
    }

    public void addAndExecute(boolean blocking, boolean hideFromNotification, final Runnable task, final Runnable onSuccess) {
        final LCTask<Object> wrapperTask = new LCTask<>("task.generic.background.task") {
            @Override
            protected Void call() {
                task.run();
                return null;
            }
        };
        if (onSuccess != null) {
            wrapperTask.setOnSucceeded(e -> onSuccess.run());
        }
        addAndExecute(blocking, hideFromNotification, wrapperTask);
    }

    public void addAndExecute(boolean blocking, boolean hideFromNotification, final Task<?> task) {
        for (TaskAddedForExecutionListener taskAddedForExecutionListener : taskAddedForExecutionListeners) {
            taskAddedForExecutionListener.taskAdded(task, blocking, hideFromNotification);
        }
        this.configurationActionThreadPool.submit(task);
    }

    @Override
    public void lcStart() {
    }

    @Override
    public void lcExit() {
        this.configurationActionThreadPool.shutdown();
    }

    public interface TaskAddedForExecutionListener {
        void taskAdded(Task<?> task, boolean blocking, boolean hideFromNotification);
    }
}
