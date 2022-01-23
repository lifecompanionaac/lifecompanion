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

package org.lifecompanion.base.data.useaction.impl.selection.scan;

import java.util.Map;

import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.base.data.control.SelectionModeController;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;

/**
 * Action to pause current scanning
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RestartScanningAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	public RestartScanningAction() {
		super(UseActionTriggerComponentI.class);
		this.order = 0;
		this.category = DefaultUseActionSubCategories.SCANNING_MODE;
		this.nameID = "action.restart.current.scanning.name";
		this.staticDescriptionID = "action.restart.current.scanning.description";
		this.configIconPath = "selection/icon_restart_scanning.png";
		this.parameterizableAction = false;
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		SelectionModeController.INSTANCE.restartCurrentScanning();
	}

}
