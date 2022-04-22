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

package org.lifecompanion.model.api.configurationcomponent;

import java.util.List;
import java.util.Set;

import org.lifecompanion.model.impl.configurationcomponent.GridState;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;

/**
 * Represent a component grid into a {@link GridComponentI}.<br>
 * This handle the implementation of component location and size in a grid.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface ComponentGridI extends XMLSerializable<IOContextI> {

	// Class part : "Expand,collapse right/left"
	//========================================================================
	/**
	 * Expand a component on a right side
	 * @param component the component to expand
	 */
	public void expandSpanRight(GridPartComponentI component);

	/**
	 * Collapse a component by the right side
	 * @param component the component to collapse
	 */
	public void collapseSpanRight(GridPartComponentI component);

	/**
	 * Expand a component on a left side
	 * @param component the component to expand
	 */
	public void expandSpanLeft(GridPartComponentI component);

	/**
	 * Collapse a component by the left side
	 * @param component the component to collapse
	 */
	public void collapseSpanLeft(GridPartComponentI component);

	//========================================================================

	// Class part : "Expand,collapse top/bottom"
	//========================================================================
	/**
	 * Expand a component on a top side
	 * @param component the component to expand
	 */
	public void expandSpanTop(GridPartComponentI component);

	/**
	 * Collapse a component by the top side
	 * @param component the component to collapse
	 */
	public void collapseSpanTop(GridPartComponentI component);

	/**
	 * Collapse a component by the bottom side
	 * @param component the component to collapse
	 */
	public void collapseSpanBottom(GridPartComponentI component);

	/**
	 * Expand a component on a bottom side
	 * @param component the component to expand
	 */
	public void expandSpanBottom(GridPartComponentI component);

	//========================================================================

	// Class part : "Component change..."
	//========================================================================
	/**
	 * Change the component at the given place to another one.
	 * @param row the row of the new component place
	 * @param column the column of the new component place
	 * @param component the component that must be set at the given place
	 */
	public void setComponent(int row, int column, GridPartComponentI component);

	/**
	 * Remove the component at the given place (replace with a empty key)
	 * @param rowP the component to remove row place
	 * @param columnP the component to remove column place
	 */
	public void removeComponent(int rowP, int columnP);

	/**
	 * This will replace the given component (that should be in the grid) with another component
	 * @param toReplace the component to replace
	 * @param component the component to use to replace the toReplace component
	 */
	public void replaceComponent(GridPartComponentI toReplace, GridPartComponentI component);

	/**
	 * Try to split the given component into multiple keys if the component have span on row or column
	 * @param component the component to split, should be a child of this grid.
	 */
	public void splitComponentIntoKeys(GridPartComponentI component);

	/**
	 * Insert a key to replace the given element and shift the source element and all component on right.<br>
	 * The last component (bottom right) is removed.
	 * @param source the source element
	 */
	public void addKeyOnLeft(GridPartComponentI source);

	/**
	 * Insert a key to replace the given element and shift the source element and all component on left.<br>
	 * The first component (top left) is removed.
	 * @param source the source element
	 */
	public void addKeyOnRight(GridPartComponentI source);

	/**
	 * Insert a key to replace the given element and shift the source element and all component on bottom.<br>
	 * The last component (bottom right) is removed.
	 * @param source the source element
	 */
	public void addKeyOnTop(GridPartComponentI source);

	/**
	 * Insert a key to replace the given element and shift the source element and all component on top.<br>
	 * The first component (top left) is removed.
	 * @param source the source element
	 */
	public void addKeyOnBottom(GridPartComponentI source);

	//========================================================================

	// Class part : "Grid content"
	//========================================================================

	/**
	 * This method must return a {@link ObservableList} that contains every current key of this grid.<br>
	 * The internal representation of the grid must update the backed list.
	 * @return a observable list that contains all grid keys.
	 */
	public ObservableList<GridPartComponentI> getGridContent();

	/**
	 * To get the component from the grid.<br>
	 * If given place is a component that span on multiple row/column, return the base component
	 * @param row the row location the of the wanted component
	 * @param column the column location the of the wanted component
	 * @return the component located at the given place
	 * @throws IllegalArgumentException if the given row/column is out of grid size
	 */
	public GridPartComponentI getComponent(int row, int column) throws IllegalArgumentException;

	/**
	 * To get a list of all the keys between (and including if they are keys) the two given part.</br>
	 * This should select the sub grid where one of the part is the top-left corner, and the other is the bottom-right corner (or the same with top-right and bottom-left).</br>
	 * If the given parameters are swapped, returned list should be the same.
	 * @param part1 one of the sub grid corner
	 * @param part2 the other sub grid corner
	 * @return the set containing the wanted subgrid
	 */
	public Set<GridPartKeyComponentI> getKeysBetween(GridPartComponentI part1, GridPartComponentI part2);
	//========================================================================

	// Class part : "Grid save/restore"
	//========================================================================
	/**
	 * Restore a previous saved state to this grid
	 * @param state the state to restore.
	 */
	public void restoreGrid(GridState stateBeforeUndoP);

	/**
	 * Give a unique grid state that can be restored with {@link #restoreGrid(GridState)}
	 * @return the grid state for the current grid content
	 */
	public GridState saveGrid();

	//========================================================================

	// Class part : "For each"
	//========================================================================
	/**
	 * @return a list of all first level grid child from top left to bottom right line by line
	 */
	public List<GridPartComponentI> getComponentHorizontal();

	/**
	 * @return a list of all first level grid child from top left to bottom right column by column
	 */
	public List<GridPartComponentI> getComponentVertical();
	//========================================================================

	// Class part : "Row/columns"
	//========================================================================
	/**
	 * @return the current number of row in the grid
	 */
	public int getRow();

	/**
	 * @return the current number of column in the grid
	 */
	public int getColumn();

	/**
	 * @return the row count property (same as {@link #getRow()})
	 */
	public ReadOnlyIntegerProperty rowProperty();

	/**
	 * @return the column count property (same as {@link #getColumn()})
	 */
	public ReadOnlyIntegerProperty columnProperty();

	/**
	 * To change the number of row present in this grid.<br>
	 * This will add/remove the first row in grid.
	 * @param rowP the wanted number of row
	 */
	public void setRow(int rowP);

	/**
	 * To change the number of column present in this grid.<br>
	 * This will add/remove the first column in grid.
	 * @param columnP the wanted number of column
	 */
	public void setColumn(int columnP);

	/**
	 * Add a row at the given index
	 * @param wantedIndex the index, must be included in current row interval
	 */
	public void addRow(final int wantedIndex);

	/**
	 * Add a column at the given index
	 * @param wantedIndex the index, must be included in current column interval
	 */
	public void addColumn(final int wantedIndex);

	/**
	 * Remove a column by its index
	 * @param wantedIndex the column index to remove
	 */
	public void removeColumn(final int wantedIndex);

	/**
	 * Remove a row by its index
	 * @param wantedIndex the index of the row we want to remove
	 */
	public void removeRow(final int wantedIndex);
	//========================================================================

}
