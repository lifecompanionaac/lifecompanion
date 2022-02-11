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
package org.lifecompanion.base.data.control;

import javafx.concurrent.Task;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.ProgressDisplayKeyOption;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum UseModeProgressDisplayerController implements ModeListenerI {
    INSTANCE;
    private final static Logger LOGGER = LoggerFactory.getLogger(UseModeProgressDisplayerController.class);

    private List<ProgressDisplayKeyOption> progressDisplayKeyOptions;
    private ExecutorService timerTaskExecutor;
    private final AtomicReference<Task<?>> currentTimerTask;

    UseModeProgressDisplayerController() {
        this.currentTimerTask = new AtomicReference<>();
    }

    public void launchTimer(long timeInMs, Runnable onFinished) {
        if (timerTaskExecutor != null) {
            this.hideAllProgress();
            // Prepare the timer task
            final Task<Void> timerTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    final long startTime = System.currentTimeMillis();
                    while ((System.currentTimeMillis() - startTime) <= timeInMs && !isCancelled()) {
                        updateProgress(System.currentTimeMillis() - startTime, timeInMs - 10);
                        Thread.sleep((long) (1000.0 / 30.0));// 30 FPS
                    }
                    updateProgress(1, 1);
                    return null;
                }
            };
            final Task<?> previousTask = currentTimerTask.getAndSet(timerTask);
            if (previousTask != null) {
                previousTask.cancel();
            }
            // Update the progress task for each progress display
            progressDisplayKeyOptions.forEach(p -> LCUtils.runOnFXThread(() -> p.bindAndShowProgress(timerTask.progressProperty())));
            // Callback on wait finished and launch timer
            timerTask.setOnSucceeded(e -> onFinished.run());
            timerTaskExecutor.submit(timerTask);
        }
    }

    public void hideAllProgress() {
        if (progressDisplayKeyOptions != null) {
            for (ProgressDisplayKeyOption progressDisplayKeyOption : progressDisplayKeyOptions) {
                progressDisplayKeyOption.hideProgress();
            }
        }
    }

    // START/STOP
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {
        Map<GridComponentI, List<ProgressDisplayKeyOption>> progressDisplayKeyOption = new HashMap<>();
        LCUtils.findKeyOptionsByGrid(ProgressDisplayKeyOption.class, configuration, progressDisplayKeyOption, null);
        progressDisplayKeyOptions = progressDisplayKeyOption.values().stream().flatMap(List::stream).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(this.progressDisplayKeyOptions)) {
            this.timerTaskExecutor = Executors.newSingleThreadScheduledExecutor(LCNamedThreadFactory.daemonThreadFactory("UseModeProgressDisplayerController"));
        }
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        hideAllProgress();
        progressDisplayKeyOptions = null;
        if (this.timerTaskExecutor != null) {
            timerTaskExecutor.shutdownNow();
            timerTaskExecutor = null;
        }
    }
    //========================================================================
}
