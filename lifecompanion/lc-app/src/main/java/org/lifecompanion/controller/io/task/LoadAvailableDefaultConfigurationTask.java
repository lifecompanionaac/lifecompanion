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

import org.lifecompanion.controller.io.IOManager;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.util.model.LCTask;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.userconfiguration.UserBaseConfiguration;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoadAvailableDefaultConfigurationTask extends LCTask<List<Pair<LCConfigurationDescriptionI, File>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadAvailableDefaultConfigurationTask.class);

    public LoadAvailableDefaultConfigurationTask() {
        super("task.load.available.default.configuration.list");
    }

    @Override
    protected List<Pair<LCConfigurationDescriptionI, File>> call() throws Exception {
        List<Pair<LCConfigurationDescriptionI, File>> configurationDescriptions = new ArrayList<>();
        File configurationRootDirectory = new File(LCConstant.EXT_PATH_DEFAULT_CONFIGURATIONS + File.separator + UserBaseConfiguration.INSTANCE.userLanguageProperty().get() + File.separator);
        File[] configurationFiles = configurationRootDirectory.listFiles();
        if (configurationFiles != null) {
            for (File configurationFile : configurationFiles) {
                String ext = FileNameUtils.getExtension(configurationFile);
                if (LCConstant.CONFIG_FILE_EXTENSION.equalsIgnoreCase(ext)) {
                    try {
                        final ConfigurationImportTask customConfigurationImport = IOManager.INSTANCE.createCustomConfigurationImport(new File(LCConstant.EXT_PATH_DEFAULT_CONFIGURATIONS_EXTRACTED), configurationFile);
                        final javafx.util.Pair<LCConfigurationDescriptionI, LCConfigurationI> importValue = LCUtils.executeInCurrentThread(customConfigurationImport);
                        configurationDescriptions.add(Pair.of(importValue.getKey(), customConfigurationImport.getImportDirectory()));
                    } catch (Exception e) {
                        LOGGER.warn("Couldn't load the default configuration from {}", configurationFile, e);
                    }
                }
            }
        }
        return configurationDescriptions;
    }
}
