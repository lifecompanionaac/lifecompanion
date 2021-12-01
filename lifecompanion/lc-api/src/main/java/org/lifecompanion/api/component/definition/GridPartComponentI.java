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

package org.lifecompanion.api.component.definition;

import org.lifecompanion.api.style2.definition.GridStyleUserI;
import org.lifecompanion.api.style2.definition.KeyStyleUserI;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;

/**
 * Represent a component that is a part of a GridComponentI.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface GridPartComponentI extends DisplayableComponentI, SelectableComponentI, GridChildComponentI, SpanModifiableComponentI,
		StackChildComponentI, RootChildComponentI, GridStyleUserI, KeyStyleUserI {

	/**
	 * @return the property that place component on x axis relative to its properties and its parent properties
	 */
	DoubleProperty layoutXProperty();

	/**
	 * @return the property that place component on y axis relative to its properties and its parent properties
	 */
	DoubleProperty layoutYProperty();

	/**
	 * @return the property that define this component width relative to its properties and its parent properties
	 */
	DoubleProperty layoutWidthProperty();

	/**
	 * @return the property that define this component height relative to its properties and its parent properties
	 */
	DoubleProperty layoutHeightProperty();

	/**
	 * <strong>This property musn't be edited by a other component than a layout component.</strong>
	 * @return the property that place this component on row in its parent
	 */
	IntegerProperty rowProperty();

	/**
	 * <strong>This property musn't be edited by a other component than a layout component.</strong>
	 * @return the property that place this component on column in its parent
	 */
	IntegerProperty columnProperty();

	/**
	 * @return true if this grid part can't have any other grid part or component inside, and false if the grid part have children inside
	 */
	boolean isLeaf();
}
