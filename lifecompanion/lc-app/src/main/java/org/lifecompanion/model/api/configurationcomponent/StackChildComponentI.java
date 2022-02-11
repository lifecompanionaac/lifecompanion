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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;

/**
 * Represent a component that can be added to a stack, this component can be a direct child of the stack, but also a indirect child (key of a grid in the stack)
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface StackChildComponentI {
	/**
	 * @return the stack parent of this component.<br>
	 * If this component is not inside a stack, the property can be null.<br>
	 * This property must be filled in children of component inside a stack with the nearest stack parent.
	 */
	ObjectProperty<StackComponentI> stackParentProperty();

	/**
	 * @return a property that return true when this stack child is the last component inside the stack
	 */
	BooleanProperty lastStackChildProperty();
}
