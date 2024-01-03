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

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.props.ApplicationBuildProperties;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.plugin.PluginInfo;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoadAvailableDefaultConfigurationTask extends LCTask<List<Pair<String, List<Pair<LCConfigurationDescriptionI, File>>>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadAvailableDefaultConfigurationTask.class);

    private final ApplicationBuildProperties applicationBuildProperties;

    public LoadAvailableDefaultConfigurationTask(ApplicationBuildProperties applicationBuildProperties) {
        super("task.load.available.default.configuration.list");
        this.applicationBuildProperties = applicationBuildProperties;
    }

    @Override
    protected List<Pair<String, List<Pair<LCConfigurationDescriptionI, File>>>> call() throws Exception {
        List<Pair<String, List<Pair<LCConfigurationDescriptionI, File>>>> configurations = new ArrayList<>();

        OkHttpClient okHttpClient = AppServerClient.initializeClientForExternalCalls().build();
        LOGGER.info("Will try to get default configuration list from app server {}", applicationBuildProperties.getAppServerUrl());
        Request request = new Request.Builder()
                // FIXME : &filter[lcDefaultConfig]=0
                .url(applicationBuildProperties.getAppServerUrl() + "/api/v1/repository-items?page[number]=1&page[size]=100&include=attachments.file&filter[isPublished]=1&sort=-publishedAt")
                .addHeader("Content-Type", "application/vnd.api+json")
                .addHeader("Accept", "application/vnd.api+json")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                AppServerRepositoryResult appServerRepositoryResult = JsonHelper.GSON.fromJson(response.body().string(), AppServerRepositoryResult.class);
                // Get only configuration file
                Map<String, DefaultConfigToDownload> configFromRepo = appServerRepositoryResult.included.stream()
                        .filter(r -> StringUtils.isEquals(r.type, "files"))
                        .filter(file ->
                                file.attributes.metadata.containsKey("configurationId"))
                        .map(file -> new DefaultConfigToDownload((String) file.attributes.metadata.get("configurationId"), file.attributes.url, file.attributes.hash))
                        .collect(Collectors.toMap(c -> c.id, c -> c));
                LOGGER.info("Got {} default configurations from server", configFromRepo.size());

                if (!configFromRepo.isEmpty()) {
                    LOGGER.info("Will check diff with old default configurations");

                    // Check for current cached configurations
                    File destDir = new File(LCConstant.EXT_PATH_DEFAULT_CONFIGURATIONS_CACHE_SOURCE);
                    File[] existingConfigurationFiles = destDir.listFiles();
                    if (existingConfigurationFiles != null) {
                        for (File existingConfigurationFile : existingConfigurationFiles) {
                            String existingConfigId = FileNameUtils.getNameWithoutExtension(existingConfigurationFile);
                            // Old file should be deleted
                            DefaultConfigToDownload configToDownload = configFromRepo.get(existingConfigId);
                            if (configToDownload == null) {
                                LOGGER.info("Delete previous default configuration file {}", existingConfigurationFile);
                                existingConfigurationFile.delete();
                            }
                            // Existing file, will compare hash to know if the file should be downloaded
                            else {
                                // FIXME : waiting for hash update
                                String existingFileHash = IOUtils.fileMd5HexToString(existingConfigurationFile);
                                if (StringUtils.isEquals(existingFileHash, configToDownload.hashMd5)) {
                                    LOGGER.info("Previous default configuration {} is already up to date, will not be downloaded", existingConfigurationFile);
                                    configFromRepo.remove(existingConfigId);
                                }
                            }
                        }
                    }
                    destDir.mkdirs();

                    // Download only needed configuration
                    LOGGER.info("{} default configurations should be updated", configFromRepo.size());
                    for (Map.Entry<String, DefaultConfigToDownload> configToDownload : configFromRepo.entrySet()) {
                        LOGGER.info("Will try to download {} configuration from {}", configToDownload.getKey(), configToDownload.getValue().url);
                        File destConfigFile = new File(destDir.getPath() + File.separator + configToDownload.getKey() + ".lcc");
                        Call call = okHttpClient.newCall(new Request.Builder()
                                .url(configToDownload.getValue().url)
                                .addHeader("Connection", "close")
                                .build());
                        try (Response downloadResponse = call.execute()) {
                            if (downloadResponse.isSuccessful()) {
                                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(destConfigFile))) {
                                    try (InputStream is = new BufferedInputStream(downloadResponse.body().byteStream())) {
                                        IOUtils.copyStreamCounting(is, os, null);
                                    }
                                }
                            }
                        }
                    }
                    LOGGER.info("Default configurations updated from server");
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not update default configuration list, will use only cached configuration", e);
        }

        // Load config list configuration
        List<Pair<LCConfigurationDescriptionI, File>> forLifeCompanion = new ArrayList<>();
        File configListFile = org.lifecompanion.util.IOUtils.getTempFile("configuration", "lcc");
        try (InputStream is = ResourceHelper.getInputStreamForPath("/configurations/profile_config_list.lcc");) {
            try (FileOutputStream fos = new FileOutputStream(configListFile)) {
                IOUtils.copyStream(is, fos);
                loadAndAddConfigurationTo(forLifeCompanion, configListFile);
            }
        }

        // Load configurations included in LifeCompanion (fallback if platform request fails)
        File configurationRootDirectory = new File(LCConstant.EXT_PATH_DEFAULT_CONFIGURATIONS_CACHE_SOURCE);
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
        File pluginConfigurationDir = org.lifecompanion.util.IOUtils.getTempDir("plugin-configurations");
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
            final ConfigurationImportTask customConfigurationImport = IOHelper.createCustomConfigurationImport(new File(LCConstant.EXT_PATH_DEFAULT_CONFIGURATIONS_CACHE_EXTRACTED),
                    configurationFile, false);
            final javafx.util.Pair<LCConfigurationDescriptionI, LCConfigurationI> importValue = ThreadUtils.executeInCurrentThread(customConfigurationImport);
            forLifeCompanion.add(Pair.of(importValue.getKey(), customConfigurationImport.getImportDirectory()));
        } catch (Exception e) {
            LOGGER.warn("Couldn't load the default configuration from {}", configurationFile, e);
        }
    }

    private static class AppServerRepositoryResult {
        private List<AppServerIncludedRelation> included;
    }

    private static class AppServerIncludedRelation {
        private String type;
        private AppServerFileAttributes attributes;
    }

    private static class AppServerFileAttributes {
        private String url;
        private String hash;
        private Map<String, Object> metadata;
    }

    private static class DefaultConfigToDownload {
        private String id;
        private String url;
        private String hashMd5;

        public DefaultConfigToDownload(String id, String url, String hashMd5) {
            this.id = id;
            this.url = url;
            this.hashMd5 = hashMd5;
        }
    }
}
