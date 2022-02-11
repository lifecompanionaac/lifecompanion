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

import org.jdom2.Element;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.util.CopyUtils;
import org.lifecompanion.util.LCTask;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.model.impl.profile.LCConfigurationDescription;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.model.impl.textprediction.predict4all.Predict4AllWordPredictorHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;


/**
 * Task to duplicate a configuration from profile.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigurationDuplicateTask extends LCTask<LCConfigurationDescriptionI> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationDuplicateTask.class);

    private final LCConfigurationDescriptionI configurationToDuplicate;
    private final String newConfigurationId;
    private final File configurationDestDirectory;
    private final File configurationCurrentDirectory;
    private final DuplicateMode mode;

    public ConfigurationDuplicateTask(LCConfigurationDescriptionI configurationToDuplicate, String newConfigurationId, File configurationDestDirectory, File configurationCurrentDirectory, DuplicateMode mode) {
        super("config.duplicate.task.title");
        this.configurationToDuplicate = configurationToDuplicate;
        this.newConfigurationId = newConfigurationId;
        this.configurationDestDirectory = configurationDestDirectory;
        this.configurationCurrentDirectory = configurationCurrentDirectory;
        this.mode = mode;
    }

    @Override
    protected LCConfigurationDescriptionI call() throws Exception {
        // Copy files
        IOUtils.copyDirectory(configurationCurrentDirectory, configurationDestDirectory);
        LOGGER.info("Configuration directory duplicated in {}", configurationDestDirectory);

        // Remove use information
        if (mode != DuplicateMode.CHANGE_ID_ONLY) {
            File useInfoFile = new File(configurationDestDirectory + File.separator + LCConstant.CONFIGURATION_USE_INFO_XML_NAME);
            useInfoFile.delete();
        }

        // Remove word predictor configuration when needed
        if (mode == DuplicateMode.FROM_DEFAULT) {
            IOUtils.deleteDirectoryAndChildren(new File(configurationDestDirectory + File.separator + Predict4AllWordPredictorHelper.P4A_PROFILE_DIR_NAME));
        }

        //Duplicate description
        LCConfigurationDescription configDescription = (LCConfigurationDescription) CopyUtils.createSimpleCopy(configurationToDuplicate, configurationDestDirectory, LCConfigurationDescription::new);

        // Change values in config description
        configDescription.setConfigurationId(newConfigurationId);
        configDescription.configurationNameProperty().set(generateName());
        if (mode != DuplicateMode.CHANGE_ID_ONLY) {
            configDescription.configurationLastDateProperty().set(new Date());
            configDescription.getChangelogEntries().clear();
        }

        // Save new description
        LCUtils.executeInCurrentThread(new ConfigurationDescriptionSavingTask(this.configurationDestDirectory, configDescription));

        //Load configuration ID, and change its ID
        File destConfigFile = new File(this.configurationDestDirectory.getPath() + File.separator + LCConstant.CONFIGURATION_XML_NAME);
        Element root = XMLHelper.readXml(destConfigFile);
        root.setAttribute("id", this.newConfigurationId);
        XMLHelper.writeXml(destConfigFile, root);

        LOGGER.info("Configuration xml loaded, changed and saved to in {}", destConfigFile);

        return configDescription;
    }

    private String generateName() {
        if (mode == DuplicateMode.IN_PROFILE) return Translation.getText("configuration.duplicated.prefix") + " " + configurationToDuplicate.configurationNameProperty().get();
        else if (mode == DuplicateMode.CHANGE_ID_ONLY) return Translation.getText("config.duplicated.for.import.prefix") + " " + configurationToDuplicate.configurationNameProperty().get();
        else return configurationToDuplicate.configurationNameProperty().get();
    }

    public enum DuplicateMode {
        FROM_DEFAULT,
        CHANGE_ID_ONLY,
        IN_PROFILE
    }
}
