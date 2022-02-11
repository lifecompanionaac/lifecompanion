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

package org.lifecompanion.model.impl.usevariable;


import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;

/**
 * Base implementation for {@link UseVariableI}
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractUseVariable<T> implements UseVariableI<T> {
	private final UseVariableDefinitionI definition;
	protected T value;

	public AbstractUseVariable(final UseVariableDefinitionI definition) {
		this.definition = definition;
	}

	public AbstractUseVariable(final UseVariableDefinitionI definition, final T valueP) {
		this(definition);
		this.value = valueP;
	}

	@Override
	public UseVariableDefinitionI getDefinition() {
		return this.definition;
	}

	@Override
	public T getValue() {
		return this.value;
	}

	@Override
	public String toStringValue() {
		return this.value != null ? this.value.toString() : "";
	}
}
