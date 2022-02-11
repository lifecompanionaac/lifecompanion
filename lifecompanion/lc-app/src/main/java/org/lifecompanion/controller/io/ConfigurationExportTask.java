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
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Task to save a configuration in a file
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigurationExportTask extends LCTask<Void> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationExportTask.class);
    /**
     * Configuration to export
     */
    private final LCConfigurationDescriptionI configurationDescription;

    /**
     * Paths
     */
    private final File configDirectory, configDestFile;

    /**
     * Create the task to export the configuration, configuration must be located in the given directory
     *
     * @param configDirectoryP the configuration directory
     * @param configDestFileP  the destination file for this export
     */
    public ConfigurationExportTask(LCConfigurationDescriptionI configurationDescription, final File configDirectoryP, final File configDestFileP) {
        super("config.export.title");
        this.configDestFile = configDestFileP;
        this.configDirectory = configDirectoryP;
        this.configurationDescription = configurationDescription;
    }

    @Override
    protected Void call() throws Exception {
        //Create a zip from the configuration file
        ConfigurationExportTask.LOGGER.info("Configuration will be export to {}", this.configDestFile);
        IOUtils.zipInto(this.configDestFile, this.configDirectory, this.configurationDescription.getConfigurationId());
        this.updateProgress(1, 1);
        return null;
    }
}
