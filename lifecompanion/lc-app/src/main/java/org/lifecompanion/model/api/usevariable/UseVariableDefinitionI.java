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

package org.lifecompanion.model.api.usevariable;

/**
 * Represent a possible variable given to use action after on execution.<br>
 * The variable can come from a event that generate it, or from the software, that also generate it.<br>
 * The definition is mostly used as configuration purpose to suggest variables to the user.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface UseVariableDefinitionI {
	/**
	 * Unique ID for this variable.<br>
	 * The user uses this ID when he wants to use a variable (e.g. "Today is {VariableId}")
	 * @return The variable unique id.
	 */
	public String getId();

	/**
	 * @return a name for this variable
	 */
	public String getName();

	/**
	 * @return a description for this variable
	 */
	public String getDescription();

	/**
	 * @return a example value for this variable, that should represent how the variable will look like
	 */
	public String getExampleValueToString();
}
