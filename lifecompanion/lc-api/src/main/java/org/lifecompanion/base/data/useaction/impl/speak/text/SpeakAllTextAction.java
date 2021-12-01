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

import java.util.Map;

import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.voice.VoiceSynthesizerController;

public class SpeakAllTextAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	public SpeakAllTextAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.SPEAK_TEXT;
		this.nameID = "action.speak.all.text.name";
		this.staticDescriptionID = "action.speak.all.text.description";
		this.configIconPath = "sound/icon_speak_all.png";
		this.parameterizableAction = false;
		this.order = 3;
		this.variableDescriptionProperty().set(Translation.getText("action.speak.all.text.description"));
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		String toSpeak = WritingStateController.INSTANCE.currentTextProperty().get();
		VoiceSynthesizerController.INSTANCE.speakSync(toSpeak);
	}
	//========================================================================
}
