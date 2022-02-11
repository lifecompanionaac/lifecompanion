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

package org.lifecompanion.model.api.ui.editmode;

import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Different profile level (simple, advanced, expert) associated with profile to hide show configuration fields.<br>
 * The enum order represent the level order.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum ConfigurationProfileLevelEnum {
	BEGINNER("profile.level.beginner"), NORMAL("profile.level.normal"), EXPERT("profile.level.expert");

	private String nameID;

	ConfigurationProfileLevelEnum(final String nameID) {
		this.nameID = nameID;
	}

	public String getName() {
		return Translation.getText(this.nameID);
	}
}
