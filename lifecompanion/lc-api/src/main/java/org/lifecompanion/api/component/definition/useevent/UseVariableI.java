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

package org.lifecompanion.api.component.definition.useevent;

/**
 * Represent the value of a {@link UseVariableDefinitionI}.<br>
 * This can be instanced multiple times, unlike its definition that is static.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 * @param <T> the value type
 */
public interface UseVariableI<T> {
	/**
	 * The associated definition
	 * @return the variable definition, can't be null
	 */
	public UseVariableDefinitionI getDefinition();

	/**
	 * @return the real value of this variable
	 */
	public T getValue();

	/**
	 * @return the displayable form for this variable's value.<br>
	 * Basic implementation will call the {@link #toString()} method of {@link #getValue()}
	 */
	public String toStringValue();
}
