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

package org.lifecompanion.util.debug;

import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.LangUtils;
import org.lifecompanion.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigurationMemoryLeakChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationMemoryLeakChecker.class);

    private static final CopyOnWriteArrayList<WeakReference<LCConfigurationI>> CONFIGURATIONS = new CopyOnWriteArrayList<>();
    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    public static void registerConfiguration(LCConfigurationI configuration) {
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.PROP_DEBUG_LOADED_CONFIGURATION)) {
            CONFIGURATIONS.add(new WeakReference<>(configuration));
            if (!RUNNING.getAndSet(true)) {
                LOGGER.info("Configuration memory leak debug enabled");
                LCNamedThreadFactory.daemonThreadFactory("ConfigurationMemoryLeakDebug").newThread(() -> {
                    while (true) {
                        System.gc();
                        final long count = new ArrayList<>(CONFIGURATIONS)
                                .stream()
                                .map(WeakReference::get)
                                .filter(Objects::nonNull)
                                .distinct()
                                .count();
                        LOGGER.info("Loaded configuration in memory (from contexts) : {}", count);
                        ThreadUtils.safeSleep(5_000);
                    }
                }).start();
            }
        }
    }
}
