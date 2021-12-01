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

package org.lifecompanion.base.data.definition.selection.view;

import javafx.util.Pair;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.definition.selection.ScanningDirection;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.definition.selection.impl.ColumnRowScanSelectionMode;

public class ColumnRowScanSelectionModeView extends AbstractPartScanSelectionModeView {

	public ColumnRowScanSelectionModeView(final ColumnRowScanSelectionMode selectionModeP) {
		super(selectionModeP, ScanningDirection.VERTICAL);
	}

	@Override
	protected Pair<Double, Double> getPosition(final int primaryIndex, final int span, final GridComponentI grid) {
		return LCUtils.getColumnPosition(grid, primaryIndex);
	}

	@Override
	protected Pair<Double, Double> getSize(final int primaryIndex, final int span, final GridComponentI grid) {
		return LCUtils.getColumnSize(grid, primaryIndex, span);
	}
}
