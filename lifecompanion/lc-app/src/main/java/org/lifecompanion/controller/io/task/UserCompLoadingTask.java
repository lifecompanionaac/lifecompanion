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

package org.lifecompanion.controller.io.task;

import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.impl.constant.LCConstant;

import java.io.File;


public class UserCompLoadingTask extends AbstractLoadUtilsTask<Void> {
	private final File directory;
	private final UserCompDescriptionI userCompDescription;

	public UserCompLoadingTask(final File directory, final UserCompDescriptionI userCompDescription) {
		super("task.title.user.comp.description.loading");
		this.directory = directory;
		this.userCompDescription = userCompDescription;
	}

	@Override
	protected Void call() throws Exception {
		this.loadElementIn(this.userCompDescription.getUserComponent(), this.directory, LCConstant.USER_COMP_XML_NAME);
		return null;
	}

}
