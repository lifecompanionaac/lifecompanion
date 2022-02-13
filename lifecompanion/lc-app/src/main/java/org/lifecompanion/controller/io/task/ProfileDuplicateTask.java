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
import org.lifecompanion.util.CopyUtils;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.model.LCTask;
import org.lifecompanion.model.impl.profile.LCProfile;
import org.lifecompanion.model.impl.io.ProfileIOContext;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Task to duplicate a configuration from profile.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProfileDuplicateTask extends LCTask<LCProfileI> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProfileDuplicateTask.class);

    private final LCProfileI profileToDuplicate;
    private final String newProfileId;
    private final File profileDestDirectory;
    private final File profileSourceDirectory;
    private final boolean addDuplicatedPrefix;


    public ProfileDuplicateTask(LCProfileI profileToDuplicate, File profileSourceDirectory, String newProfileId, File profileDestDirectory, boolean addDuplicatedPrefix) {
        super("profile.duplicate.task.title");
        this.profileToDuplicate = profileToDuplicate;
        this.newProfileId = newProfileId;
        this.profileDestDirectory = profileDestDirectory;
        this.profileSourceDirectory = profileSourceDirectory;
        this.addDuplicatedPrefix = addDuplicatedPrefix;
    }

    @Override
    protected LCProfileI call() throws Exception {
        // Copy files
        IOUtils.copyDirectory(profileSourceDirectory, profileDestDirectory);
        LOGGER.info("Profile directory duplicated in {}", profileDestDirectory);

        // Duplicate profile
        ProfileIOContext profileContext = new ProfileIOContext(profileDestDirectory, false);
        LCProfileI duplicated = CopyUtils.createSimpleCopy(profileToDuplicate, profileContext, LCProfile::new);

        // Change values
        duplicated.nameProperty().set((addDuplicatedPrefix ? (Translation.getText("configuration.duplicated.prefix") + " ") : "") + profileToDuplicate.nameProperty().get());
        duplicated.setID(newProfileId);
        duplicated.cachedConfigurationCountProperty().set(profileToDuplicate.configurationCountProperty().get());

        // Save new profile
        ThreadUtils.executeInCurrentThread(new ProfileSavingTask(profileDestDirectory, duplicated));

        return duplicated;
    }
}
