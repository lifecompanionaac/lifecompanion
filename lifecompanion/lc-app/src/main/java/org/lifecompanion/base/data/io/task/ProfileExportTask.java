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

package org.lifecompanion.base.data.io.task;

import java.io.File;

import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.base.data.common.LCTask;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * To export the profile to a single file
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProfileExportTask extends LCTask<Void> {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProfileExportTask.class);
	private final LCProfileI profile;
	private final File profileDirectory;
	private final File destinationFile;

	public ProfileExportTask(final LCProfileI profile, final File profileDirectory, final File destinationFile) {
		super("profile.export.title");
		this.profile = profile;
		this.profileDirectory = profileDirectory;
		this.destinationFile = destinationFile;
	}

	@Override
	protected Void call() throws Exception {
		ProfileExportTask.LOGGER.info("Profile will be exported to {}", this.destinationFile);
		IOUtils.zipInto(this.destinationFile, this.profileDirectory, this.profile.getID());
		return null;
	}
}
