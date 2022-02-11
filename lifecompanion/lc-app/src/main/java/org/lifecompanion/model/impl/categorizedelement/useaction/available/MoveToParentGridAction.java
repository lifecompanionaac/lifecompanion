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
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;

/**
 * Action to change the current display/scanned grid.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MoveToParentGridAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	public MoveToParentGridAction() {
		super(UseActionTriggerComponentI.class);
		this.order = 0;
		this.category = DefaultUseActionSubCategories.MOVE_TO_COMPLEX;
		this.nameID = "go.to.parent.grid.name";
		this.staticDescriptionID = "go.to.parent.grid.description";
		this.configIconPath = "show/icon_move_to_parent.png";
		this.parameterizableAction = false;
		this.movingAction = true;
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		SelectionModeController.INSTANCE.goToParentPartCurrent();
	}

}
