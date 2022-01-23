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

import org.lifecompanion.api.component.definition.GridPartComponentI;

/**
 * To keep a component span informations at a location in grid.<br>
 * This is use in undo/redo actions.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridComponentInformation {
	private int row, column;
	private int rowSpan, columnSpan;

	public GridComponentInformation(final int rowP, final int columnP, final int rowSpanP, final int columnSpanP) {
		this.setRow(rowP);
		this.setColumn(columnP);
		this.setRowSpan(rowSpanP);
		this.setColumnSpan(columnSpanP);
	}

	public static GridComponentInformation create(final GridPartComponentI comp) {
		return new GridComponentInformation(comp.rowProperty().get(), comp.columnProperty().get(), comp.rowSpanProperty().get(),
				comp.columnSpanProperty().get());
	}

	public int getRowSpan() {
		return this.rowSpan;
	}

	public void setRowSpan(final int rowSpanP) {
		this.rowSpan = rowSpanP;
	}

	public int getColumnSpan() {
		return this.columnSpan;
	}

	public void setColumnSpan(final int columnSpanP) {
		this.columnSpan = columnSpanP;
	}

	public int getRow() {
		return this.row;
	}

	public void setRow(final int rowP) {
		this.row = rowP;
	}

	public int getColumn() {
		return this.column;
	}

	public void setColumn(final int columnP) {
		this.column = columnP;
	}
}
