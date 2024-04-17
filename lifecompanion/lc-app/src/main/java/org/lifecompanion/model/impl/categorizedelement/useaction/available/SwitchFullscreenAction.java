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

package org.lifecompanion.model.impl.categorizedelement.useaction.available;

import javafx.stage.Stage;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Note : this action is called SwitchFullscreen while it should be called SwitchMaximized[...] but we keep it that way for backward compatibilities.<br>
 * See SwitchRealFullscreenAction if needed.
 */
public class SwitchFullscreenAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwitchFullscreenAction.class);

    public SwitchFullscreenAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.FRAME;
        this.nameID = "action.switch.maximized.name";
        this.staticDescriptionID = "action.switch.maximized.description";
        this.configIconPath = "configuration/icon_enable_fullscreen.png";
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        List<GlobalRuntimeConfiguration> shouldBeNotActivated = List.of(GlobalRuntimeConfiguration.FORCE_WINDOW_LOCATION,
                GlobalRuntimeConfiguration.FORCE_WINDOW_SIZE,
                GlobalRuntimeConfiguration.DISABLE_WINDOW_FULLSCREEN);
        if (shouldBeNotActivated.stream().anyMatch(GlobalRuntimeConfigurationController.INSTANCE::isPresent)) {
            LOGGER.info("SwitchFullscreenAction action ignored because one of the following configuration {} is enabled", shouldBeNotActivated);
        } else {
            FXThreadUtils.runOnFXThread(() -> {
                final Stage stage = AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
                if (stage != null) {
                    stage.setMaximized(!stage.isMaximized());
                }
            });
        }

    }
}
