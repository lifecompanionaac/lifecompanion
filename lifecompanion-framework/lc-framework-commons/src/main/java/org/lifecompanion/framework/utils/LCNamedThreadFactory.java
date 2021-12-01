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

package org.lifecompanion.framework.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class LCNamedThreadFactory implements ThreadFactory {
    private static final Map<String, AtomicInteger> THREAD_COUNTERS = new HashMap<>();

    private final String threadSuffix;
    private final boolean daemon;
    private final int priority;

    private LCNamedThreadFactory(final String threadSuffix, final boolean daemon, final int priority) {
        this.threadSuffix = threadSuffix;
        this.daemon = daemon;
        this.priority = priority;
        THREAD_COUNTERS.putIfAbsent(threadSuffix, new AtomicInteger(0));
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(daemon);
        t.setName("LifeCompanion-Thread-" + threadSuffix + "-" + THREAD_COUNTERS.get(threadSuffix).getAndIncrement());
        if (priority != Thread.NORM_PRIORITY) {
            t.setPriority(priority);
        }
        return t;
    }

    public static LCNamedThreadFactory daemonThreadFactory(String name) {
        return new LCNamedThreadFactory(name, true, Thread.NORM_PRIORITY);
    }

    public static LCNamedThreadFactory threadFactory(String name) {
        return new LCNamedThreadFactory(name, false, Thread.NORM_PRIORITY);
    }

    public static LCNamedThreadFactory daemonThreadFactoryWithPriority(String name, int priority) {
        return new LCNamedThreadFactory(name, true, priority);
    }
}