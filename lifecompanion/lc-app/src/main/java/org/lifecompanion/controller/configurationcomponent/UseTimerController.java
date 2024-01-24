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
import org.lifecompanion.model.impl.categorizedelement.useaction.available.StartTimerAction;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum UseTimerController implements ModeListenerI {
    INSTANCE;
    private final static Logger LOGGER = LoggerFactory.getLogger(UseTimerController.class);
    private final UseModeProgressDisplayerController progressController = UseModeProgressDisplayerController.INSTANCE;
    private final Set<ModeListenerI> listeners = new HashSet<>();


    public void startTimer(int time){
        LOGGER.info("startTimer");
        FXThreadUtils.runOnFXThreadAndWaitFor(() -> progressController.hideAllProgress());
        UseTimerController.INSTANCE.addListener(this);
        progressController.launchTimer(time, () -> {
            stopTimer();
        });
    }

    public void stopTimer(){
        LOGGER.info("stopTimer");
        FXThreadUtils.runOnFXThreadAndWaitFor(() -> progressController.hideAllProgress());
        UseTimerController.INSTANCE.removeListener(this);
    }

    public void addListener(ModeListenerI listener) {
        listeners.add(listener);
    }

    public void removeListener(ModeListenerI listener) {
        listeners.remove(listener);
    }

    // START/STOP
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {
        LOGGER .info("modeStart");
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        LOGGER .info("modeStop");
        listeners.clear();
    }
    //========================================================================
}
