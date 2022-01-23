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

package org.lifecompanion.base.data.useaction.impl.media.sound;

import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.media.SoundPlayer;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;

import java.util.Map;

public class SwitchSoundActivationAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	public SwitchSoundActivationAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.SOUND;
		this.order = 10;
		this.nameID = "action.switch.sound.player.activation.name";
		this.staticDescriptionID = "action.switch.sound.player.activation.description";
		this.configIconPath = "media/icon_enable_disable_sound.png";
		this.parameterizableAction = false;
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		SoundPlayer.INSTANCE.switchDisableSoundPlayer();
	}
	//========================================================================
}
