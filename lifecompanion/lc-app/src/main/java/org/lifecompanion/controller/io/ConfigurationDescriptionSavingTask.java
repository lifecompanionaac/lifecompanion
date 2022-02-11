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

import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.util.LCTask;
import org.lifecompanion.base.data.config.LCConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Task to save a configuration description for a configuration and a profile.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigurationDescriptionSavingTask extends LCTask<Void> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationDescriptionSavingTask.class);
    private File directory;
    private LCConfigurationDescriptionI description;

    public ConfigurationDescriptionSavingTask(final File directoryP, final LCConfigurationDescriptionI descriptionP) {
        super("task.title.save.config.description");
        this.directory = directoryP;
        this.description = descriptionP;
    }

    @Override
    protected Void call() throws Exception {
        this.description.getTechInfo().updateInformation();//Update with current info.
        File configDescriptionXmlFile = new File(this.directory.getPath() + File.separator + LCConstant.CONFIGURATION_DESCRIPTION_XML_NAME);
        XMLHelper.writeXml(configDescriptionXmlFile, this.description.serialize(this.directory));
        ConfigurationDescriptionSavingTask.LOGGER.info("Configuration description saved to {}", configDescriptionXmlFile);
        return null;
    }
}
