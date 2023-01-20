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
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.plugin.PluginInfo;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadAvailableDefaultConfigurationTask extends LCTask<List<Pair<String, List<Pair<LCConfigurationDescriptionI, File>>>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadAvailableDefaultConfigurationTask.class);

    public LoadAvailableDefaultConfigurationTask() {
        super("task.load.available.default.configuration.list");
    }

    @Override
    protected List<Pair<String, List<Pair<LCConfigurationDescriptionI, File>>>> call() throws Exception {
        List<Pair<String, List<Pair<LCConfigurationDescriptionI, File>>>> configurations = new ArrayList<>();

        // Load configurations included in LifeCompanion
        List<Pair<LCConfigurationDescriptionI, File>> forLifeCompanion = new ArrayList<>();
        File configurationRootDirectory = new File(LCConstant.EXT_PATH_DEFAULT_CONFIGURATIONS + File.separator + UserConfigurationController.INSTANCE.userLanguageProperty().get() + File.separator);
        File[] configurationFiles = configurationRootDirectory.listFiles();
        if (configurationFiles != null) {
            for (File configurationFile : configurationFiles) {
                String ext = FileNameUtils.getExtension(configurationFile);
                if (LCConstant.CONFIG_FILE_EXTENSION.equalsIgnoreCase(ext)) {
                    loadAndAddConfigurationTo(forLifeCompanion, configurationFile);
                }
            }
        }
        configurations.add(Pair.of(Translation.getText("default.configuration.lifecompanion.list.name"), forLifeCompanion));

        // Load configuration for each loaded plugin
        File pluginConfigurationDir = IOUtils.getTempDir("plugin-configurations");
        for (PluginInfo pluginInfo : PluginController.INSTANCE.getPluginInfoList()) {
            if (PluginController.INSTANCE.isPluginLoaded(pluginInfo.getPluginId())) {
                List<Pair<LCConfigurationDescriptionI, File>> preparedDefaultConfigurations = new ArrayList<>();
                List<Pair<String, InputStream>> defaultConfigurations = PluginController.INSTANCE.getDefaultConfigurationsFor(pluginInfo.getPluginId());
                for (Pair<String, InputStream> defaultConfiguration : defaultConfigurations) {
                    File configPath = new File(pluginConfigurationDir.getPath() + File.separator + StringUtils.getNewID() + ".lcc");
                    configPath.getParentFile().mkdirs();
                    try (InputStream is = defaultConfiguration.getRight()) {
                        try (FileOutputStream fos = new FileOutputStream(configPath)) {
                            org.lifecompanion.framework.commons.utils.io.IOUtils.copyStream(is, fos);
                        }
                        loadAndAddConfigurationTo(preparedDefaultConfigurations, configPath);
                    } catch (Exception e) {
                        LOGGER.error("Couldn't load plugin {} default configuration, check the given path", pluginInfo.getPluginId(), e);
                    }
                }
                if (!preparedDefaultConfigurations.isEmpty()) {
                    configurations.add(Pair.of(Translation.getText("default.configuration.list.for.plugin", pluginInfo.getPluginName()), preparedDefaultConfigurations));
                }
            }
        }

        return configurations;
    }

    private static void loadAndAddConfigurationTo
            (List<Pair<LCConfigurationDescriptionI, File>> forLifeCompanion, File configurationFile) {
        try {
            final ConfigurationImportTask customConfigurationImport = IOHelper.createCustomConfigurationImport(new File(LCConstant.EXT_PATH_DEFAULT_CONFIGURATIONS_EXTRACTED), configurationFile, false);
            final javafx.util.Pair<LCConfigurationDescriptionI, LCConfigurationI> importValue = ThreadUtils.executeInCurrentThread(customConfigurationImport);
            forLifeCompanion.add(Pair.of(importValue.getKey(), customConfigurationImport.getImportDirectory()));
        } catch (Exception e) {
            LOGGER.warn("Couldn't load the default configuration from {}", configurationFile, e);
        }
    }
}
