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

import javafx.concurrent.Task;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class ThreadUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadUtils.class);

    private static ConcurrentHashMap<String, Future<?>> runningCalls;
    private static ExecutorService executorService;

    /**
     * Will run the given task without any executor.
     *
     * @param task the task to be run
     * @throws Exception if the task execution produce a exception
     */

    public static <T> T executeInCurrentThread(final Task<T> task) throws Exception {
        task.run();
        try {
            return task.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            throw cause instanceof Exception ? (Exception) cause : e;
        }
    }

    public static void safeSleep(final long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (Throwable t) {
            LOGGER.warn("Couldn't sleep the thread {} for {} ms", Thread.currentThread().getName(), sleep, t);
        }
    }

    public static void debounce(long ms, String callId, Runnable call) {
        if (executorService == null) {
            runningCalls = new ConcurrentHashMap<>(5);
            executorService = Executors.newSingleThreadExecutor(LCNamedThreadFactory.daemonThreadFactory("Debounce-Feature"));
        }
        Future<?> previousCall = runningCalls.get(callId);
        if (previousCall != null) previousCall.cancel(true);
        runningCalls.put(callId, executorService.submit(() -> {
            Thread.sleep(ms);
            try {
                call.run();
            } catch (Throwable t) {
                LOGGER.error("Problem in debounce call for {}", callId, t);
            }
            return null;
        }));
    }

    public static void printCurrentThreadStackTraceOnErr() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stackTrace.length && i < 50; i++) {
            System.err.println(stackTrace[i]);
        }
    }
}
