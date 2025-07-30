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

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import javafx.beans.binding.Bindings;

public class ChangeVoiceParameterAndRestoreAction extends ChangeVoiceParameterAction {

	public ChangeVoiceParameterAndRestoreAction() {
		super();
		this.restore = true;
		this.nameID = "action.change.voice.and.restore.parameter.name";
		this.staticDescriptionID = "action.change.voice.and.restore.parameter.static.description";
		this.configIconPath = "sound/icon_change_voice_and_restore.png";
		this.order = 1;
		this.variableDescriptionProperty()
				.bind(TranslationFX.getTextBinding("action.change.voice.and.restore.parameter.variable.description", Bindings.createStringBinding(() -> {
					return this.selectedVoice.get() != null ? this.selectedVoice.get().getDisplayableLabel() : "null";
				}, this.selectedVoice)));
		this.allowSystems = SystemType.allExpectMobile();
	}
}
