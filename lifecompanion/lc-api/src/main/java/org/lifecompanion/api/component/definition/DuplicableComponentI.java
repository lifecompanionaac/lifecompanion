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

import java.util.Map;

/**
 * Represent a component that can be copied.<br>
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface DuplicableComponentI {

	/**
	 * This method creates a deep copy of this object.
	 * @param changeID if change ID is true, the duplicated component musn't have the same ID that previous component (typically used by copy/paste), if false, ID should be the same.
	 * @return a new instance of this component, all the children must also be new instance.<br>
	 * If this component is a {@link IdentifiableComponentI}, the ID of the returned copy musn't be the same.<br>
	 */
	DuplicableComponentI duplicate(boolean changeID);

	/**
	 * This method is called when this component or depending component have their ids changed.<br>
	 * This method is useful if this component depends on other component by id.
	 * @param changes the map that match previous id with the new one
	 */
	void idsChanged(Map<String, String> changes);
}
