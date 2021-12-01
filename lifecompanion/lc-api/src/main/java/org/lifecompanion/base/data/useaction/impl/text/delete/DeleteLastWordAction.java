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

package org.lifecompanion.base.data.useaction.impl.text.delete;

import java.util.Map;

import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.control.events.WritingEventSource;
import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;

public class DeleteLastWordAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	public DeleteLastWordAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.DELETE_TEXT;
		this.nameID = "action.delete.last.word.name";
		this.staticDescriptionID = "action.delete.last.word.description";
		this.variableDescriptionProperty().set(this.getStaticDescription());
		this.configIconPath = "text/icon_delete_last_entry.png";
		this.parameterizableAction = false;
		this.order = 1;
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		WritingStateController.INSTANCE.removeLastWord(WritingEventSource.USER_ACTIONS);
	}
	//========================================================================
}
