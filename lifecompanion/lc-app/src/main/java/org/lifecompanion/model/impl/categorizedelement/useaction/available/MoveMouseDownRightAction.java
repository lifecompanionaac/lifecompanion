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
import org.lifecompanion.controller.virtualmouse.VirtualMouseController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;

/**
 * Action to write the label of the parent key.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MoveMouseDownRightAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	public MoveMouseDownRightAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.MOUSE_MOVE;
		this.nameID = "action.move.mouse.se.name";
		this.order = 7;
		this.staticDescriptionID = "action.move.mouse.se.static.description";
		this.configIconPath = "computeraccess/icon_arrow_se.png";
		this.parameterizableAction = false;
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		//Start moving
		VirtualMouseController.INSTANCE.startMovingMouseBottomRight();
		//Stop on next
		SelectionModeController.INSTANCE.pauseCurrentScanningUntilNextSelection(() -> {
			VirtualMouseController.INSTANCE.stopMovingMouse();
			return false;
		});
	}
	//========================================================================
}
