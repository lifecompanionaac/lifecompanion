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

import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.model.server.update.ApplicationPluginUpdate;
import org.lifecompanion.framework.utils.Pair;

import java.io.File;

public class DownloadPluginTask extends AbstractPluginDownloadTask<Pair<ApplicationPluginUpdate, File>> {
    private final String pluginId;

    public DownloadPluginTask(AppServerClient client, String applicationId, boolean enablePreviewUpdates, boolean pauseOnStart, String pluginId) {
        super(client, applicationId, enablePreviewUpdates, pauseOnStart);
        this.pluginId = pluginId;
        this.updateTitle(Translation.getText("download.plugin.task.title", pluginId));
    }

    @Override
    protected Pair<ApplicationPluginUpdate, File> call() throws Exception {
        return this.tryToDownloadPlugin(pluginId, null);
    }
}
