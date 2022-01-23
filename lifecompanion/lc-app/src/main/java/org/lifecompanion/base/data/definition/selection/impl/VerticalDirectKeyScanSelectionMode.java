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

package org.lifecompanion.base.data.definition.selection.impl;

import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.api.component.definition.grid.ComponentGridI;
import org.lifecompanion.api.component.definition.grid.GridComponentInformation;
import org.lifecompanion.api.definition.selection.ScanningDirection;
import org.lifecompanion.base.data.definition.selection.view.DirectKeyScanSelectionModeView;

import java.util.ArrayList;
import java.util.List;


public class VerticalDirectKeyScanSelectionMode extends AbstractDirectKeyScanSelectionMode {

	@Override
	protected List<GridComponentInformation> generateComponentToScan(final GridComponentI componentGrid, final boolean byPassEmptyCheck) {
		List<GridComponentInformation> components = new ArrayList<>();
		ComponentGridI grid = componentGrid.getGrid();
		List<GridPartComponentI> componentVertical = grid.getComponentVertical();
		for (GridPartComponentI gridPartComponentI : componentVertical) {
			this.addGridComponentToList2(components, gridPartComponentI, byPassEmptyCheck);
		}
		return components;
	}

	@Override
	protected DirectKeyScanSelectionModeView createView() {
		return new DirectKeyScanSelectionModeView(this, ScanningDirection.VERTICAL);
	}
}
