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

import java.util.Map;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;

/**
 * Action that delete all the text.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class DeleteAllTextAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	public DeleteAllTextAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.DELETE_TEXT;
		this.nameID = "action.delete.all.text.name";
		this.staticDescriptionID = "action.delete.all.text.description";
		this.variableDescriptionProperty().set(this.getStaticDescription());
		this.configIconPath = "text/icon_delete_all_text.png";
		this.parameterizableAction = false;
		this.order = 0;
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		WritingStateController.INSTANCE.removeAll(WritingEventSource.USER_ACTIONS);
	}
	//========================================================================
}
