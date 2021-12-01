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
import org.lifecompanion.base.data.definition.selection.view.RowColumnScanSelectionModeView;

import java.util.ArrayList;
import java.util.List;


/**
 * Selection mode that will create part with each line.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RowColumnScanSelectionMode extends AbstractPartScanSelectionMode<RowColumnScanSelectionModeView> {

	@Override
	protected List<ComponentToScan> generateLineToScan(final GridComponentI grid, final boolean byPassEmptyCheck) {
		List<List<GridPartComponentI>> rows = new ArrayList<>();
		ComponentGridI compGrid = grid.getGrid();
		for (int row = 0; row < compGrid.getRow(); row++) {
			ArrayList<GridPartComponentI> rowComponents = new ArrayList<>();
			rows.add(rowComponents);
			for (int column = 0; column < compGrid.getColumn(); column++) {
				rowComponents.add(compGrid.getComponent(row, column));
			}
		}
		return this.generateComponentToScan(rows, byPassEmptyCheck);
	}

	@Override
	protected RowColumnScanSelectionModeView createView() {
		return new RowColumnScanSelectionModeView(this);
	}

}
