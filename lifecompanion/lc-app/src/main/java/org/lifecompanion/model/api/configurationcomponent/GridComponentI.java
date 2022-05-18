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

import org.lifecompanion.model.api.style.GridStyleUserI;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;

/**
 * Component that is composed of a grid.<br>
 * A grid is a component that contains multiple components inside its key.<br>
 * There is row*column keys in a grid.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface GridComponentI
		extends DisplayableComponentI, GridChildComponentI, SelectableComponentI, GridPartComponentI, SelectionModeUserI, GridStyleUserI {

	/**
	 * @return property that represent number of row inside the grid
	 */
	ReadOnlyIntegerProperty rowCountProperty();

	/**
	 * @return property that represent number of column inside the grid
	 */
	ReadOnlyIntegerProperty columnCountProperty();

	/**
	 * <strong>Never change this property manually, this property its automatically computed with column count and width</strong>
	 * @return the property that represent a case width
	 */
	ReadOnlyDoubleProperty caseWidthProperty();

	/**
	 * <strong>Never change this property manually, this property its automatically computed with row count and height</strong>
	 * @return the property that represent a case height
	 */
	ReadOnlyDoubleProperty caseHeightProperty();

	/**
	 * The grid behind this component.<br>
	 * The grid is the component that organize all the component of this component
	 * @return the grid that contains all this component childrens, will never return null
	 */
	ComponentGridI getGrid();
}
