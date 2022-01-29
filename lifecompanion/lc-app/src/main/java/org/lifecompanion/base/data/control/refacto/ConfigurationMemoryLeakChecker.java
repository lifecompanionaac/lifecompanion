package org.lifecompanion.base.data.control.refacto;

import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
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
        if (LCUtils.safeParseBoolean(System.getProperty("org.lifecompanion.debug.configuration.memory.leak"))) {
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
                        LCUtils.safeSleep(10_000);
                    }
                }).start();
            }
        }
    }
}
