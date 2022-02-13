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

package org.lifecompanion.util.javafx;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class FXThreadUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FXThreadUtils.class);

    /**
     * If the calling thread is on FX Thread, will execute the runnable directly, else, will call {@link Platform#runLater(Runnable)}
     *
     * @param runnable the runnable to execute
     */
    public static void runOnFXThread(final Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    public static void runOnFXThreadAndWaitFor(final Runnable runnable) {
        runOnFXThreadAndWaitFor(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T runOnFXThreadAndWaitFor(final Supplier<T> toExecute) {
        if (Platform.isFxApplicationThread()) return toExecute.get();
        else {
            AtomicReference<T> ref = new AtomicReference<>();
            Semaphore semaphore = new Semaphore(0);
            runOnFXThread(() -> {
                try {
                    ref.set(toExecute.get());
                } finally {
                    semaphore.release();
                }
            });
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                LOGGER.warn("Couldn't wait for Platform.runLater(...) call to be finished", e);
            }
            return ref.get();
        }
    }
}
