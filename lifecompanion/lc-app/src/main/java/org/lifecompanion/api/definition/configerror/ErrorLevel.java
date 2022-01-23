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

package org.lifecompanion.api.definition.configerror;

import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Error level for configuration error.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum ErrorLevel {
	WARNING("config.error.level.warning"), //
	ERROR("config.error.level.error");

	private String labelID;

	ErrorLevel(final String labelID) {
		this.labelID = labelID;
	}

	public String getLabel() {
		return Translation.getText(this.labelID);
	}
}
