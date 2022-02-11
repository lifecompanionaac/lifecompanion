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

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;

import java.util.Map;

/**
 * Action that should have the same effect as the back space key on a keyboard.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MoveCaretBackAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	public MoveCaretBackAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.CARET;
		this.nameID = "action.move.caret.backward.name";
		this.staticDescriptionID = "action.move.caret.backward.description";
		this.variableDescriptionProperty().set(this.getStaticDescription());
		this.configIconPath = "text/icon_move_caret_backward.png";
		this.parameterizableAction = false;
		this.order = 0;
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		WritingStateController.INSTANCE.moveCaretBackward(WritingEventSource.USER_ACTIONS);
	}
	//========================================================================
}
