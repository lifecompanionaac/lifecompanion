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

import java.io.PrintStream;
import java.util.concurrent.*;

public class ThreadUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadUtils.class);

    private static ConcurrentHashMap<String, Future<?>> runningDebounceCalls;
    private static ExecutorService debounceExecutorService;

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
        if (debounceExecutorService == null) {
            runningDebounceCalls = new ConcurrentHashMap<>(5);
            debounceExecutorService = Executors.newSingleThreadExecutor(LCNamedThreadFactory.daemonThreadFactory("Debounce-Feature"));
        }
        Future<?> previousCall = runningDebounceCalls.get(callId);
        if (previousCall != null) previousCall.cancel(true);
        runningDebounceCalls.put(callId, debounceExecutorService.submit(() -> {
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
        printCurrentThreadStackTraceOn(System.err);
    }

    public static void printCurrentThreadStackTraceOnOut() {
        printCurrentThreadStackTraceOn(System.out);
    }

    public static void printCurrentThreadStackTraceOn(PrintStream pw) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTrace.length && i < 50; i++) {
            pw.println("\t" + stackTrace[i]);
        }
    }


    public static void runAfter(long delay, Runnable runnable) {
        Thread delayThread = new Thread(() -> {
            safeSleep(delay);
            runnable.run();
        });
        delayThread.setDaemon(true);
        delayThread.start();
    }
}
