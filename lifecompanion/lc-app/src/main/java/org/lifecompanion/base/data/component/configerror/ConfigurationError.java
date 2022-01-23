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

package org.lifecompanion.base.data.component.configerror;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.lifecompanion.api.definition.configerror.ConfigurationErrorI;
import org.lifecompanion.api.definition.configerror.ErrorLevel;

/**
 * Implementation of configuration error
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class ConfigurationError implements ConfigurationErrorI {
	private String code;
	private ErrorLevel level;
	protected StringProperty name, description;

	ConfigurationError(final String codeP, final ErrorLevel levelP) {
		this.code = codeP;
		this.level = levelP;
		this.name = new SimpleStringProperty(this, "name", null);
		this.description = new SimpleStringProperty(this, "description", null);
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public ErrorLevel getLevel() {
		return this.level;
	}

	@Override
	public ReadOnlyStringProperty nameProperty() {
		return this.name;
	}

	@Override
	public ReadOnlyStringProperty descriptionProperty() {
		return this.description;
	}
}
