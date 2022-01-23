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

import javafx.beans.property.SimpleObjectProperty;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.api.mode.LCStateListener;
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

public enum LifeCompanionController {
    INSTANCE;

    private final SimpleObjectProperty<AppModeV2> mode;

    private final SimpleObjectProperty<LCProfileI> profile;  // FIXME : move to profile manager ?

    private UseModeContext useModeContext;
    private ConfigModeContext configModeContext;

    LifeCompanionController() {
        mode = new SimpleObjectProperty<>();
        profile = new SimpleObjectProperty<>();
    }


    // START/STOP
    //========================================================================
    final LCStateListener[] STATE_LISTENER = {
            // COPIED FROM APP CONTROLLER
            VoiceSynthesizerController.INSTANCE,// TODO : should initialize default voices
            UserActionController.INSTANCE,
            WordPredictionController.INSTANCE,// TODO : should initialize default predictor
            AutoCharPredictionController.INSTANCE, // TODO : should initialize default predictor
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
    // TODO : app modes ?

    public void init(ConfigModeContext configModeContext, UseModeContext useModeContext, AppModeV2 startIn) {
        this.configModeContext = configModeContext;
        this.useModeContext = useModeContext;
    }
    //========================================================================

}
