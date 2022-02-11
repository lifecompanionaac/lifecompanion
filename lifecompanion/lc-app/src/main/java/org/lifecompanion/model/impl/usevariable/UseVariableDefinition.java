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
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Implementation for {@link UseVariableDefinitionI}
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseVariableDefinition implements UseVariableDefinitionI {
	private String id;
	private String nameId;
	private String descriptionId;
	private String exampleValueId;

	public UseVariableDefinition(final String id, final String nameId, final String descriptionId, final String exampleValueId) {
		this.id = id;
		this.nameId = nameId;
		this.descriptionId = descriptionId;
		this.exampleValueId = exampleValueId;
	}

	public UseVariableDefinition(final String id) {
		this(id, null, null, null);
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return Translation.getText(this.nameId);
	}

	@Override
	public String getDescription() {
		return Translation.getText(this.descriptionId);
	}

	@Override
	public String getExampleValueToString() {
		return Translation.getText(this.exampleValueId);
	}

}
