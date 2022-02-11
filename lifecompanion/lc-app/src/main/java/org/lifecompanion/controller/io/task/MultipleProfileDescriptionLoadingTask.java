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

import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.util.model.LCTask;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Task that will load every existing profiles.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MultipleProfileDescriptionLoadingTask extends LCTask<List<LCProfileI>> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MultipleProfileDescriptionLoadingTask.class);

    private final List<LCProfileI> resultList;
    private final File rootDirectory;

    public MultipleProfileDescriptionLoadingTask(final File rootDirectoryP) {
        super("task.title.load.multiple.profile");
        this.resultList = new ArrayList<>();
        this.rootDirectory = rootDirectoryP;
    }

    @Override
    protected List<LCProfileI> call() throws Exception {
        long start = System.currentTimeMillis();
        //List each file
        File[] potentialProfileDirectories = this.rootDirectory.listFiles();
        if (potentialProfileDirectories != null) {
            for (File profileDirectory : potentialProfileDirectories) {
                if (!LCConstant.BACKUP_DIR.equals(profileDirectory.getName())) {
                    MultipleProfileDescriptionLoadingTask.LOGGER.info("Found a profile to load in {}", profileDirectory);
                    //Try to load
                    try {
                        LCProfileI loadedProfile = LCUtils.executeInCurrentThread(new ProfileDescriptionLoadingTask(profileDirectory));
                        this.resultList.add(loadedProfile);
                    } catch (Exception e) {
                        MultipleProfileDescriptionLoadingTask.LOGGER.warn("Couldn't load the profile in {}", profileDirectory, e);
                    }
                }
            }
        }
        MultipleProfileDescriptionLoadingTask.LOGGER.info("Every profiles in {} loaded in {} ms", this.rootDirectory, System.currentTimeMillis() - start);
        return this.resultList;
    }
}
