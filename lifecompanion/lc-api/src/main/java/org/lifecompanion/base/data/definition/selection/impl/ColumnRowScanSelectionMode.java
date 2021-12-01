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
import org.lifecompanion.base.data.definition.selection.view.ColumnRowScanSelectionModeView;

import java.util.ArrayList;
import java.util.List;


/**
 * Scanning mode that will create parts with column, and with every keys in the column.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ColumnRowScanSelectionMode extends AbstractPartScanSelectionMode<ColumnRowScanSelectionModeView> {

	@Override
	protected List<ComponentToScan> generateLineToScan(final GridComponentI grid, final boolean byPassEmptyCheck) {
		List<List<GridPartComponentI>> columns = new ArrayList<>();
		ComponentGridI compGrid = grid.getGrid();
		for (int column = 0; column < compGrid.getColumn(); column++) {
			ArrayList<GridPartComponentI> columnComponents = new ArrayList<>();
			columns.add(columnComponents);
			for (int row = 0; row < compGrid.getRow(); row++) {
				columnComponents.add(compGrid.getComponent(row, column));
			}
		}
		return this.generateComponentToScan(columns, byPassEmptyCheck);
	}

	@Override
	protected ColumnRowScanSelectionModeView createView() {
		return new ColumnRowScanSelectionModeView(this);
	}

}
