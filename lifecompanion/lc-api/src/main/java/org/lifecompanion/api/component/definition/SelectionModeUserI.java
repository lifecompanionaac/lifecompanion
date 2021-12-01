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

import org.lifecompanion.api.definition.selection.SelectionModeParameterI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Represent a component that can change the parameter of selection mode.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface SelectionModeUserI {
	// Class part : "Parent/usable"
	//========================================================================
	/**
	 * @return if this component use the parent selection mode configuration, and doesn't have its own configuration in the parameters.
	 */
	BooleanProperty useParentSelectionModeProperty();

	/**
	 * @return if the selection mode user is able to use the parent configuration ( e.g. if it's not the top root parent)
	 */
	ReadOnlyBooleanProperty canUseParentSelectionModeConfigurationProperty();

	/**
	 * @return the parameter for selection mode.<br>
	 * This is the properties of this item that should be changed to configuration the selection mode.<br>
	 */
	SelectionModeParameterI getSelectionModeParameter();
	//========================================================================
}
