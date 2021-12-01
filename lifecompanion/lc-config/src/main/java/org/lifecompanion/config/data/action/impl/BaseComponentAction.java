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

package org.lifecompanion.config.data.action.impl;

import org.lifecompanion.base.data.action.definition.BasePropertyChangeAction;
import org.lifecompanion.api.component.definition.UserNamedComponentI;

/**
 * Class that keep commons action on components.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class BaseComponentAction {
	/**
	 * Undo last action
	 */
	public static class ChangeComponentNameAction extends BasePropertyChangeAction<String> {

		public ChangeComponentNameAction(final UserNamedComponentI component, final String oldValueP, final String wantedValueP) {
			super(component.userNameProperty(), oldValueP, wantedValueP);
		}

		@Override
		public String getNameID() {
			return "action.change.component.name";
		}
	}
}
