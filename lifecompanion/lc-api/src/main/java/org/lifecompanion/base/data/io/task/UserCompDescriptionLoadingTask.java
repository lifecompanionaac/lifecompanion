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

import org.lifecompanion.api.component.definition.usercomp.UserCompDescriptionI;
import org.lifecompanion.base.data.component.usercomp.UserCompDescriptionImpl;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.io.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Task to load {@link UserCompDescriptionI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserCompDescriptionLoadingTask extends AbstractLoadUtilsTask<UserCompDescriptionI> {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserCompDescriptionLoadingTask.class);
    private final File directory;

    public UserCompDescriptionLoadingTask(final File directory) {
        super("task.title.user.comp.description.loading");
        this.directory = directory;
    }

    @Override
    protected UserCompDescriptionI call() throws Exception {
        //Try to load XML
        File configDescriptionXMLPath = new File(this.directory.getPath() + File.separator + LCConstant.USER_COMP_DESCRIPTION_XML_NAME);
        UserCompDescriptionLoadingTask.LOGGER.info("Will try to load user component description from {}", configDescriptionXMLPath);
        if (configDescriptionXMLPath.exists()) {
            UserCompDescriptionI userComp = XMLHelper.loadXMLSerializable(configDescriptionXMLPath, new UserCompDescriptionImpl(), directory);
            this.updateProgress(1, 1);
            return userComp;
        } else {
            throw new IllegalArgumentException(
                    "The given user component description directory doesn't contains any user component description xml file");
        }
    }
}
