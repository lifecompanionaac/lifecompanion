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

package org.lifecompanion.base.data.control.refacto;

import javafx.beans.value.ChangeListener;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.mode.LCStateListener;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.control.AsyncExecutorController;
import org.lifecompanion.base.data.control.UserActionController;
import org.lifecompanion.base.data.control.prediction.AutoCharPredictionController;
import org.lifecompanion.base.data.control.prediction.CustomCharPredictionController;
import org.lifecompanion.base.data.control.prediction.WordPredictionController;
import org.lifecompanion.base.data.control.stats.SessionStatsController;
import org.lifecompanion.base.data.control.update.InstallationController;
import org.lifecompanion.base.data.image2.ImageDictionaries;
import org.lifecompanion.base.data.plugins.PluginManager;
import org.lifecompanion.base.data.voice.VoiceSynthesizerController;
import org.lifecompanion.config.data.control.ErrorHandlingController;
import org.lifecompanion.config.data.control.LCStateController;
import org.lifecompanion.config.data.control.usercomp.UserCompController;
import org.lifecompanion.config.view.pane.main.notification2.LCNotificationController;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

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
            UserActionController.INSTANCE,
            WordPredictionController.INSTANCE,
            AutoCharPredictionController.INSTANCE,
            CustomCharPredictionController.INSTANCE,
            ImageDictionaries.INSTANCE,
            InstallationController.INSTANCE,
            SessionStatsController.INSTANCE,

            // COPIED FROM CONFIG
            LCStateController.INSTANCE,
            PluginManager.INSTANCE,
            UserCompController.INSTANCE,
            LCNotificationController.INSTANCE,
            ErrorHandlingController.INSTANCE
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
