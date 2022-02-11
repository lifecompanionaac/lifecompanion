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

package org.lifecompanion.ui;

import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.controller.resource.IconManager;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.util.javafx.StageUtils;
import org.lifecompanion.controller.editaction.GlobalActions;

public class UseModeStage extends Stage {

    public UseModeStage(LCProfileI profile, LCConfigurationI configuration, LCConfigurationDescriptionI configurationDescription, ConfigUseScene configUseScene) {
        this.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.initStyle(StageStyle.DECORATED);
        this.setTitle(StageUtils.getStageDefaultTitle() +
                (profile != null ? " - " + profile.nameProperty().get() : "") +
                (configurationDescription != null ? " - " + configurationDescription.configurationNameProperty().get() : "")
        );
        this.setScene(configUseScene);
        this.setWidth(configuration.computedFrameWidthProperty().get());
        this.setHeight(configuration.computedFrameHeightProperty().get());
        this.opacityProperty().bind(configuration.frameOpacityProperty());
        this.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        this.getIcons().add(IconManager.get(LCConstant.LC_ICON_PATH));
        this.setAlwaysOnTop(true);
        this.setOnCloseRequest((we) -> {
            we.consume();
            GlobalActions.HANDLER_CANCEL.handle(null);
        });
        if (configuration.fullScreenOnLaunchProperty().get()) {
            this.setMaximized(true);
        } else {
            StageUtils.moveStageTo(this, configuration.framePositionOnLaunchProperty().get());
        }
        if (configuration.virtualKeyboardProperty().get()) {
            //LOGGER.info("Virtual keyboard detected for the stage, will change the focusable state for main stage");
            LCUtils.setFocusableSafe(this, false);
        }
        this.setOnHidden(e -> {
            this.opacityProperty().unbind();
            configUseScene.unbindAndClean();
        });
    }
}
