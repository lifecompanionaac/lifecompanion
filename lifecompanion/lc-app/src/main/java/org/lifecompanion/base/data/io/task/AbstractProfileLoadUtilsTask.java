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

import javafx.application.Platform;
import org.lifecompanion.api.component.definition.LCConfigurationDescriptionI;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.base.data.common.LCTask;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.component.profile.LCProfile;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.io.ProfileIOContext;
import org.lifecompanion.base.data.io.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * To mutualize the profile loading methods
 *
 * @param <T> the task return type
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractProfileLoadUtilsTask<T> extends LCTask<T> {

    protected AbstractProfileLoadUtilsTask() {
        super("task.title.profile.loading");
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractProfileLoadUtilsTask.class);

    protected LCProfileI loadProfileDescription(final File directory) throws Exception {
        //Load the profile XML
        LCProfile profile = XMLHelper.loadXMLSerializable(new File(directory.getPath() + File.separator + LCConstant.PROFILE_XML_NAME), new LCProfile(), new ProfileIOContext(directory, false));

        // Count configuration number
        File configurationDirectory = new File(directory.getPath() + File.separator + LCConstant.CONFIGURATION_DIRECTORY + File.separator);
        int configCount = 0;
        File[] configurationsDir = configurationDirectory.listFiles();
        if (configurationsDir != null) {
            for (File configDir : configurationsDir) {
                configCount += new File(configDir.getPath() + File.separator + LCConstant.CONFIGURATION_DESCRIPTION_XML_NAME).exists() ? 1 : 0;
            }
        }
        profile.cachedConfigurationCountProperty().set(configCount);

        return profile;
    }

    protected LCProfileI loadFullProfileAndConfigurationDescription(final File directory, LCProfileI profile) throws Exception {
        //Load the profile XML with the full loading flag
        Platform.runLater(() -> {
            try {
                XMLHelper.loadXMLSerializable(new File(directory.getPath() + File.separator + LCConstant.PROFILE_XML_NAME), profile, new ProfileIOContext(directory, true));
            } catch (Exception e) {
                LOGGER.error("Couldn't load full profile information", e);
                throw new RuntimeException(e);
            }
        });

        // Load each possible configuration description
        File configurationDirectory = new File(directory.getPath() + File.separator + LCConstant.CONFIGURATION_DIRECTORY + File.separator);
        File[] configurationsDir = configurationDirectory.listFiles();
        List<LCConfigurationDescriptionI> configurationDescriptions = new ArrayList<>();
        if (configurationsDir != null) {
            for (File configDir : configurationsDir) {
                AbstractProfileLoadUtilsTask.LOGGER.info("Will try to load the configuration description in the directory {}", configDir);
                try {
                    ConfigurationDescriptionLoadingTask loadDescription = new ConfigurationDescriptionLoadingTask(configDir);
                    LCConfigurationDescriptionI description = LCUtils.executeInCurrentThread(loadDescription);
                    configurationDescriptions.add(description);
                } catch (Exception e) {
                    AbstractProfileLoadUtilsTask.LOGGER.warn("Couldn't load the configuration description in {}", configDir, e);
                }
            }
        }
        //Sort configurations
        Collections.sort(configurationDescriptions, (c1, c2) -> c2.configurationLastDateProperty().get().compareTo(c1.configurationLastDateProperty().get()));
        Platform.runLater(() -> profile.getConfiguration().setAll(configurationDescriptions));
        return profile;
    }
}
