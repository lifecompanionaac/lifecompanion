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

package org.lifecompanion.controller.appinstallation.task;

import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.framework.client.http.ApiException;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.service.AppServerService;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.app.VersionUtils;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.model.server.update.ApplicationPluginUpdate;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.impl.plugin.PluginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lifecompanion.framework.commons.ApplicationConstant.DOWNLOAD_ATTEMPT_COUNT_BEFORE_FAIL;

public abstract class AbstractPluginDownloadTask<T> extends AbstractUpdateTask<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPluginDownloadTask.class);

    private final AppServerService appServerService;
    private final File pluginUpdateDirectory;

    public AbstractPluginDownloadTask(AppServerClient client, String applicationId, boolean enablePreviewUpdates, boolean pauseOnStart) {
        super(client, applicationId, enablePreviewUpdates, pauseOnStart);
        pluginUpdateDirectory = new File(LCConstant.PATH_PLUGIN_UPDATE_DIR);
        pluginUpdateDirectory.mkdirs();
        appServerService = new AppServerService(client);
    }

    protected void downloadAndCheckPluginUpdates(String pluginId) throws ApiException, IOException {
        updateMessage(Translation.getText("update.task.check.plugin.update.check.for", pluginId));

        LOGGER.info("Will try to download all plugin updates for \"{}\" ", pluginId);
        ApplicationPluginUpdate[] pluginUpdates = appServerService.getPluginUpdates(pluginId, enablePreviewUpdates);

        if (pluginUpdates != null) {
            for (ApplicationPluginUpdate pluginUpdate : pluginUpdates) {
                File pluginUpdateFile = new File(pluginUpdateDirectory.getPath() + File.separator + pluginUpdate.getApplicationPluginId() + File.separator + pluginUpdate.getFileName());
                IOUtils.createParentDirectoryIfNeeded(pluginUpdateFile);

                LOGGER.info("Plugin version found {}, will download or check existing file ({})", pluginUpdate.getVersion(), pluginUpdateFile);
                if (!pluginUpdateFile.exists() || !StringUtils.isEquals(IOUtils.fileSha256HexToString(pluginUpdateFile), pluginUpdate.getFileHash())) {
                    updateMessage(Translation.getText("update.task.check.plugin.update.download", pluginId, pluginUpdate.getVersion(), FileNameUtils.getFileSize(pluginUpdate.getFileSize())));
                    try {
                        appServerService.downloadFileAndCheckIt(() -> appServerService.getPluginUpdateDownloadUrl(pluginUpdate.getId()), pluginUpdateFile, pluginUpdate.getFileHash(), DOWNLOAD_ATTEMPT_COUNT_BEFORE_FAIL);
                    } catch (ApiException e) {
                        LOGGER.error("Couldn't download plugin update {} - {}", pluginId, pluginUpdate.getVersion(), e);
                    }
                }
            }
        } else {
            LOGGER.info("No plugin version found for {}", pluginId);
        }
    }

    public File getLastPluginUpdateForAppVersion(String pluginId, String appVersion) {
        File pluginUpdateRoot = new File(pluginUpdateDirectory.getPath() + File.separator + pluginId);
        List<PluginInfo> availablePlugins = new ArrayList<>();

        // Read every available plugin files
        File[] pluginFiles = pluginUpdateRoot.listFiles();
        if (pluginFiles != null) {
            for (File pluginFile : pluginFiles) {
                try {
                    PluginInfo pluginInfoFromJar = PluginInfo.createFromJarManifest(pluginFile);
                    if (StringUtils.isEquals(pluginInfoFromJar.getPluginId(), pluginId)) {
                        availablePlugins.add(pluginInfoFromJar);
                    } else throw new IOException("Plugin ID from jar \"" + pluginInfoFromJar.getPluginId() + "\" doesn't match expected plugin ID value \"" + pluginId + "\"");
                } catch (Exception e) {
                    LOGGER.info("Couldn't read plugin from jar file : {}", pluginFile, e);
                }
            }
        }

        // Find the last plugin that matches the min app version
        PluginInfo lastPluginUpdate = availablePlugins
                .stream()
                .filter(p -> StringUtils.isNotBlank(p.getPluginMinAppVersion()))
                .filter(p -> VersionUtils.compare(appVersion, p.getPluginMinAppVersion()) >= 0)
                .min((p1, p2) -> VersionUtils.compare(p2.getPluginVersion(), p1.getPluginVersion()))
                .orElse(null);

        return lastPluginUpdate != null ? new File(pluginUpdateRoot.getPath() + File.separator + lastPluginUpdate.getFileName()) : null;
    }
}