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

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum UseTimerController implements ModeListenerI {
    INSTANCE;
    private final static Logger LOGGER = LoggerFactory.getLogger(UseTimerController.class);
    private final DoubleProperty progressProperty = new SimpleDoubleProperty();
    private final UseModeProgressDisplayerController progressController = UseModeProgressDisplayerController.INSTANCE;
    private long endTime;

    public void startTimer(int time){
        LOGGER.info("startTimer");
        Platform.runLater(() -> progressController.hideAllProgress());
        this.endTime = System.currentTimeMillis();

        long startTime = System.currentTimeMillis();
        long durationInMillis = time;
        this.endTime = startTime + durationInMillis;
        progressController.launchTimer(durationInMillis, () -> {});

        while (System.currentTimeMillis() < endTime) {
            double progress = (System.currentTimeMillis() - startTime) / (double) durationInMillis;
            progressProperty.set(progress);
        }

        progressProperty.set(1.0);
        Platform.runLater(() -> progressController.hideAllProgress());
    }

    public void stopTimer(){
        LOGGER.info("stopTimer");
        Platform.runLater(() -> progressController.hideAllProgress());
        this.endTime = System.currentTimeMillis();
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
    }
    //========================================================================
}
