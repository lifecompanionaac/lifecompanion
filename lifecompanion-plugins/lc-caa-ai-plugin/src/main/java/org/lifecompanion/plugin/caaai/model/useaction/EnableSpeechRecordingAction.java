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
package org.lifecompanion.plugin.caaai.model.useaction;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.caaai.controller.CAAAIController;

import java.util.Map;

public class EnableSpeechRecordingAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

    public EnableSpeechRecordingAction() {
        super(GridPartKeyComponentI.class);
        this.category = CAAAIActionSubCategories.TODO;
        this.nameID = "caa.ai.plugin.actions.enable_speech_recording.name";
        this.staticDescriptionID = "caa.ai.plugin.actions.enable_speech_recording.description";
        this.configIconPath = "filler_icon_32px.png";
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    // Class part : "Execute"
    // ========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        CAAAIController.INSTANCE.enableSpeechToTextRecognition();
    }
    // ========================================================================
}
