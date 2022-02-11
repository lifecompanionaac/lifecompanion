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

package org.lifecompanion.controller.io;

import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.util.LCTask;
import org.lifecompanion.util.LCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Task to load potential user comp.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MultiUserCompDescriptionLoadingTask extends LCTask<List<UserCompDescriptionI>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MultiUserCompDescriptionLoadingTask.class);

	private final File rootDirectory;

	public MultiUserCompDescriptionLoadingTask(final File rootDirectory) {
		super("task.title.load.multiple.user.comp");
		this.rootDirectory = rootDirectory;
	}

	@Override
	protected List<UserCompDescriptionI> call() throws Exception {
		List<UserCompDescriptionI> resultList = new ArrayList<>();
		long start = System.currentTimeMillis();
		//List each file
		File[] potentialUserCompDirectories = this.rootDirectory.listFiles();
		if (potentialUserCompDirectories != null) {
			for (File userCompDir : potentialUserCompDirectories) {
				MultiUserCompDescriptionLoadingTask.LOGGER.info("Found a user comp to load in {}", userCompDir);
				//Try to load
				try {
					UserCompDescriptionI loadedProfile = LCUtils.executeInCurrentThread(new UserCompDescriptionLoadingTask(userCompDir));
					resultList.add(loadedProfile);
				} catch (Exception e) {
					MultiUserCompDescriptionLoadingTask.LOGGER.warn("Couldn't load the comp in {}", userCompDir, e);
				}
			}
		}
		MultiUserCompDescriptionLoadingTask.LOGGER.info("Every user comp in {} loaded in {} ms", this.rootDirectory,
				System.currentTimeMillis() - start);
		return resultList;
	}
}
