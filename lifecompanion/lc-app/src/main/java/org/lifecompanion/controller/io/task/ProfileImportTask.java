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

import java.io.File;

import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Task to import and load a profile
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProfileImportTask extends AbstractProfileLoadUtilsTask<LCProfileI> {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProfileImportTask.class);
	private final File importDirectory;
	private final File profileFile;
	private final String importedProfileId;

	public ProfileImportTask(final File profileFile, final File importDirectory, final String importedProfileId) {
		super();
		this.importDirectory = importDirectory;
		this.profileFile = profileFile;
		this.importedProfileId = importedProfileId;
	}

	@Override
	protected LCProfileI call() throws Exception {
		//Unzip the profile file
		this.importDirectory.mkdir();
		IOUtils.unzipInto(this.profileFile, this.importDirectory, null);
		ProfileImportTask.LOGGER.info("Profile imported to the directory {}", this.importDirectory);
		//Load the profile
		return this.loadProfileDescription(this.importDirectory);
	}

	public String getImportedProfileId() {
		return this.importedProfileId;
	}
}
