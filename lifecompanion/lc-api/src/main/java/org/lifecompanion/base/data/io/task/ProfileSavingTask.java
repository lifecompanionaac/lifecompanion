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

import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.base.data.common.LCTask;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.io.ProfileIOContext;
import org.lifecompanion.base.data.io.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Unit task to save profile.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProfileSavingTask extends LCTask<Void> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProfileSavingTask.class);
    private LCProfileI profile;
    private File directory;

    public ProfileSavingTask(final File directoryP, final LCProfileI profileP) {
        super("task.title.profile.saving");
        this.profile = profileP;
        this.directory = directoryP;
    }

    @Override
    protected Void call() throws Exception {
        //Create directory
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }
        //Save XML for profile
        final File xmlFile = new File(this.directory.getPath() + File.separator + LCConstant.PROFILE_XML_NAME);
        XMLHelper.writeXml(xmlFile, this.profile.serialize(new ProfileIOContext(this.directory, true)));
        ProfileSavingTask.LOGGER.info("Profile XML saved to {}", xmlFile);
        return null;
    }

}
