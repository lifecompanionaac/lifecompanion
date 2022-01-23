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

package org.lifecompanion.base.data.useaction.impl.computera.mousemove;

import java.util.Map;

import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.base.data.control.SelectionModeController;
import org.lifecompanion.base.data.control.virtual.mouse.VirtualMouseController;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;

/**
 * Action to write the label of the parent key.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MoveMouseUpRightAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	public MoveMouseUpRightAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.MOUSE_MOVE;
		this.nameID = "action.move.mouse.ne.name";
		this.order = 5;
		this.staticDescriptionID = "action.move.mouse.ne.static.description";
		this.configIconPath = "computeraccess/icon_arrow_ne.png";
		this.parameterizableAction = false;
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		//Start moving
		VirtualMouseController.INSTANCE.startMovingMouseTopRight();
		//Stop on next
		SelectionModeController.INSTANCE.pauseCurrentScanningUntilNextSelection(() -> {
			VirtualMouseController.INSTANCE.stopMovingMouse();
			return false;
		});
	}
	//========================================================================
}
