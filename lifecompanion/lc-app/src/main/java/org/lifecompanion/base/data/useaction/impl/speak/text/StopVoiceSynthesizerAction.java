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
package org.lifecompanion.base.data.useaction.impl.speak.text;

import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.base.data.voice.VoiceSynthesizerController;

import java.util.Map;

public class StopVoiceSynthesizerAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public StopVoiceSynthesizerAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.SPEAK_PARAMETERS;
        this.order = 15;
        this.nameID = "action.speak.stop.voice.synthesizer.name";
        this.staticDescriptionID = "action.speak.stop.voice.synthesizer.description";
        this.configIconPath = "sound/icon_stop_voice_synthesizer.png";
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        VoiceSynthesizerController.INSTANCE.stopCurrentSpeakAndClearQueue();
    }
}
