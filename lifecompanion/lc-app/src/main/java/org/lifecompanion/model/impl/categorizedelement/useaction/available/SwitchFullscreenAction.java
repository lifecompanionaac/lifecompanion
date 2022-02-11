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
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;

import java.util.Map;

public class SwitchFullscreenAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public SwitchFullscreenAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.FRAME;
        this.nameID = "action.switch.fullscreen.frame.name";
        this.staticDescriptionID = "action.switch.fullscreen.frame.description";
        this.configIconPath = "configuration/icon_enable_fullscreen.png";
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        LCUtils.runOnFXThread(() -> {
            final Stage stage = AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
            stage.setMaximized(!stage.isMaximized());
        });
    }
}
