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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadAllPluginUpdateTask extends AbstractPluginDownloadTask<List<File>> {
    private final String appVersion;
    private final List<String> pluginIds;

    public DownloadAllPluginUpdateTask(AppServerClient client, String applicationId, boolean enablePreviewUpdates, boolean pauseOnStart, List<String> pluginIds, String appVersion) {
        super(client, applicationId, enablePreviewUpdates, pauseOnStart);
        this.pluginIds = pluginIds;
        this.appVersion = appVersion;
        //this.updateTitle(Translation.getText("download.plugin.task.title", pluginId));
    }

    @Override
    protected List<File> call() throws Exception {
        List<File> pluginFiles = new ArrayList<>();
        for (String pluginId : pluginIds) {
            this.downloadAndCheckPluginUpdates(pluginId);
            File forPlugin = getLastPluginUpdateForAppVersion(pluginId, appVersion);
            if (forPlugin != null) pluginFiles.add(forPlugin);
        }
        return pluginFiles;
    }
}
