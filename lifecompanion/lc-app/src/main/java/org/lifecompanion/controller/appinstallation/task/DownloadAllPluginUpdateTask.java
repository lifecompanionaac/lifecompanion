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

import com.google.gson.JsonArray;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DownloadAllPluginUpdateTask extends AbstractPluginDownloadTask<List<File>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadAllPluginUpdateTask.class);
    private final String appVersion;
    private final List<String> pluginIds;

    private final boolean manualRequest;

    public DownloadAllPluginUpdateTask(AppServerClient client, String applicationId, boolean enablePreviewUpdates, boolean manualRequest, List<String> pluginIds, String appVersion) {
        super(client, applicationId, enablePreviewUpdates, !manualRequest);
        this.manualRequest = manualRequest;
        this.pluginIds = pluginIds;
        this.appVersion = appVersion;
        updateMessage(Translation.getText("update.task.check.plugin.update"));
    }

    @Override
    protected List<File> call() throws Exception {
        List<File> pluginFiles = new ArrayList<>();

        // Check last update check date : check updates every X days (skip this check if an update is downloading)
        if (!manualRequest) {
            Date lastUpdateCheckDate = InstallationController.INSTANCE.readLastPluginUpdateCheckDate();
            if (lastUpdateCheckDate != null && System.currentTimeMillis() - lastUpdateCheckDate.getTime() < LCConstant.UPDATE_CHECK_DELAY) {
                LOGGER.info("Last plugin update check was on {}, will not check again for update", lastUpdateCheckDate);
                return pluginFiles;
            }
        }

        ThreadUtils.safeSleep(TASK_START_LONG_DELAY);

        for (String pluginId : pluginIds) {
            try {
                this.downloadAndCheckPluginUpdates(pluginId);
                File forPlugin = getLastPluginUpdateForAppVersion(pluginId, appVersion);
                LOGGER.info("Last plugin file found for plugin {} : {}", pluginId, forPlugin);
                if (forPlugin != null) pluginFiles.add(forPlugin);
            } catch (Exception e) {
                LOGGER.warn("Couldn't check plugin updates for {}", pluginId, e);
            }
        }
        InstallationController.INSTANCE.writeLastPluginUpdateCheckDate(new Date());
        return pluginFiles;
    }
}
