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
package org.lifecompanion.controller.configurationcomponent;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.util.javafx.FXThreadUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Oscar PAVOINE
 */
public enum UseTimerController implements ModeListenerI {
    INSTANCE;
    private final UseModeProgressDisplayerController progressController;
    private final Set<Runnable> onTimerFinishedListeners;
    private boolean isTimerRunning;

    UseTimerController() {
        progressController = UseModeProgressDisplayerController.INSTANCE;
        this.onTimerFinishedListeners = new HashSet<>();
    }

    public Set<Runnable> getOnTimerFinishedListeners() {
        return onTimerFinishedListeners;
    }

    public void startTimer(int timeInMs) {
        isTimerRunning = true;
        FXThreadUtils.runOnFXThread(() -> progressController.launchTimer(timeInMs, () -> {
            if (isTimerRunning) {
                for (Runnable onTimerFinishedListener : onTimerFinishedListeners) {
                    onTimerFinishedListener.run();
                }
            }
        }));
    }

    public void stopTimer() {
        isTimerRunning = false;
        FXThreadUtils.runOnFXThread(progressController::hideAllProgress);
    }

    // START/STOP
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        onTimerFinishedListeners.clear();
    }
    //========================================================================
}
