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

package org.lifecompanion.api.component.definition.grid;

import java.util.Map;

import org.lifecompanion.api.component.definition.GridPartComponentI;

/**
 * Class to keep a grid state.<br>
 * This is use in undo/redo actions.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridState {
	private GridPartComponentI[][] grid;
	private Map<GridPartComponentI, GridComponentInformation> spans;

	public GridState(final GridPartComponentI[][] gridP, final Map<GridPartComponentI, GridComponentInformation> spansP) {
		this.setGrid(gridP);
		this.setSpans(spansP);
	}

	public Map<GridPartComponentI, GridComponentInformation> getSpans() {
		return this.spans;
	}

	public void setSpans(final Map<GridPartComponentI, GridComponentInformation> spansP) {
		this.spans = spansP;
	}

	public GridPartComponentI[][] getGrid() {
		return this.grid;
	}

	public void setGrid(final GridPartComponentI[][] gridP) {
		this.grid = gridP;
	}
}
