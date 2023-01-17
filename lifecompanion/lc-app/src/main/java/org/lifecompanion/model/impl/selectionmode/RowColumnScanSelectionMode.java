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

package org.lifecompanion.model.impl.selectionmode;

import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.ComponentGridI;
import org.lifecompanion.model.api.selectionmode.ComponentToScanI;
import org.lifecompanion.ui.selectionmode.RowColumnScanSelectionModeView;
import org.lifecompanion.util.model.SelectionModeUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Selection mode that will create part with each line.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RowColumnScanSelectionMode extends AbstractPartScanSelectionMode<RowColumnScanSelectionModeView> {

    @Override
    protected RowColumnScanSelectionModeView createView() {
        return new RowColumnScanSelectionModeView(this);
    }

    @Override
    protected List<ComponentToScanI> generateComponentsToScan(GridComponentI grid, boolean byPassEmptyCheck) {
        return SelectionModeUtils.getRowColumnScanningComponents(grid, byPassEmptyCheck);
    }
}
