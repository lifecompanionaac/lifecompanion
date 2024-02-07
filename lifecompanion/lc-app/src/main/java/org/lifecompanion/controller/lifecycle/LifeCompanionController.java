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

package org.lifecompanion.controller.lifecycle;

import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editmode.ErrorHandlingController;
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.controller.hub.HubController;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.controller.profile.UserCompController;
import org.lifecompanion.controller.textprediction.AutoCharPredictionController;
import org.lifecompanion.controller.textprediction.CustomCharPredictionController;
import org.lifecompanion.controller.textprediction.WordPredictionController;
import org.lifecompanion.controller.training.TrainingController;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LifeCompanionController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(LifeCompanionController.class);


    private boolean started = false;

    LifeCompanionController() {
    }

    // START/STOP
    //========================================================================
    final LCStateListener[] STATE_LISTENER = {
            AsyncExecutorController.INSTANCE,

            // COPIED FROM APP CONTROLLER
            VoiceSynthesizerController.INSTANCE,
            UseActionController.INSTANCE,
            WordPredictionController.INSTANCE,
            AutoCharPredictionController.INSTANCE,
            CustomCharPredictionController.INSTANCE,
            ImageDictionaries.INSTANCE,
            InstallationController.INSTANCE,
            SessionStatsController.INSTANCE,

            // COPIED FROM CONFIG
            LCStateController.INSTANCE,
            PluginController.INSTANCE,
            UserCompController.INSTANCE,
            LCNotificationController.INSTANCE,
            ErrorHandlingController.INSTANCE,

            HubController.INSTANCE,

            TrainingController.INSTANCE,
    };

    public void lcStart() {
        started = true;
        for (LCStateListener stateListener : STATE_LISTENER) {
            stateListener.lcStart();
        }
    }

    public void lcExit() {
        AppModeController.INSTANCE.clearCurrentMode();
        if (started) {
            for (LCStateListener stateListener : STATE_LISTENER) {
                stateListener.lcExit();
            }
        }
    }
    //========================================================================

}
