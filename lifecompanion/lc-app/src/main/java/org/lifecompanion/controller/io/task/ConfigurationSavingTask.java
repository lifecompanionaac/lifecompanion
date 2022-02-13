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

import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.profile.ChangelogEntry;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.metrics.SessionEventType;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;


/**
 * Task to save a configuration in a directory.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigurationSavingTask extends AbstractSavingUtilsTask<Void> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationSavingTask.class);

    private final File directory;
    private final LCConfigurationI configuration;
    private final LCConfigurationDescriptionI configurationDescription;
    private final LCProfileI configurationProfile;

    public ConfigurationSavingTask(final File directoryP, final LCConfigurationI configurationP, final LCProfileI configurationProfile) {
        super("task.save.title");
        this.directory = directoryP;
        this.configuration = configurationP;
        this.configurationProfile = configurationProfile;
        this.configurationDescription = configurationProfile.getConfigurationById(configurationP.getID());
        if (this.configurationDescription == null) {
            throw new IllegalArgumentException(
                    "The given profile haven't any configuration description for the configuration with ID " + configurationP.getID());
        }
    }

    @Override
    protected Void call() throws Exception {
        ConfigurationSavingTask.LOGGER.info("Will save the configuration {} to {}", this.configuration.getID(), this.directory);
        //Call parent
        this.saveXmlSerializable(this.configuration, this.directory, LCConstant.CONFIGURATION_XML_NAME);

        // Save key list
        ThreadUtils.executeInCurrentThread(IOHelper.createSaveKeyListTask(configuration, this.directory));

        // Save sequences
        ThreadUtils.executeInCurrentThread(IOHelper.createSaveSequenceTask(configuration, this.directory));

        // Generate changelog entry
        ChangelogEntry changelogEntry = new ChangelogEntry(
                System.getProperty("user.name"),
                configurationProfile != null ? configurationProfile.nameProperty().get() : "unknown",
                null,
                InstallationController.INSTANCE.getBuildProperties().getVersionLabel(),
                configuration.unsavedActionProperty().get(),
                new Date(),
                SystemType.current()
        );
        configurationDescription.getChangelogEntries().add(changelogEntry);
        SessionStatsController.INSTANCE.pushEvent(SessionEventType.CONFIG_UPDATED, FluentHashMap.map("modificationsCount", configuration.unsavedActionProperty().get()));

        //Configuration description update
        this.configurationDescription.configurationLastDateProperty().set(new Date());
        ThreadUtils.executeInCurrentThread(IOHelper.createSaveConfigDescriptionTask(this.configurationDescription, this.directory));

        this.updateProgress(5, 5);
        ConfigurationSavingTask.LOGGER.info("Configuration successfully saved to {}", this.directory);
        return null;
    }
}
