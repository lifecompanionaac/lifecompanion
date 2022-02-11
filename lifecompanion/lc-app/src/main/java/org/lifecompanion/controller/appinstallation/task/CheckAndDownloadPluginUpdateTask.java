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
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.plugin.PluginInfo;
import org.lifecompanion.model.impl.plugin.PluginInfoState;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.model.server.update.ApplicationPluginUpdate;
import org.lifecompanion.framework.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CheckAndDownloadPluginUpdateTask extends AbstractPluginDownloadTask<List<Pair<ApplicationPluginUpdate, File>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckAndDownloadPluginUpdateTask.class);

    public CheckAndDownloadPluginUpdateTask(AppServerClient client, String applicationId, boolean enablePreviewUpdates, boolean pauseOnStart) {
        super(client, applicationId, enablePreviewUpdates, pauseOnStart);
        updateMessage(Translation.getText("update.task.check.plugin.update"));
    }

    @Override
    protected List<Pair<ApplicationPluginUpdate, File>> call() throws Exception {
        List<Pair<ApplicationPluginUpdate, File>> updatedPluginFiles = new ArrayList<>();

        // Check last update check date : check updates every X days (skip this check if an update is downloading)
        Date lastUpdateCheckDate = InstallationController.INSTANCE.readLastPluginUpdateCheckDate();
        if (lastUpdateCheckDate != null && System.currentTimeMillis() - lastUpdateCheckDate.getTime() < LCConstant.UPDATE_CHECK_DELAY) {
            LOGGER.info("Last plugin update check was on {}, will not check again for update", lastUpdateCheckDate);
            return updatedPluginFiles;
        }

        LCUtils.safeSleep(TASK_START_LONG_DELAY);

        // Copy current plugin list and check/download update for each plugin
        List<PluginInfo> pluginInfoList = new ArrayList<>(PluginController.INSTANCE.getPluginInfoList());
        int progress = 0;
        for (PluginInfo pluginInfo : pluginInfoList) {
            try {
                if (pluginInfo.stateProperty().get() != PluginInfoState.REMOVED) {
                    Pair<ApplicationPluginUpdate, File> pluginAndFile = this.tryToDownloadPlugin(pluginInfo.getPluginId(), pluginInfo.getPluginVersion());
                    if (pluginAndFile != null) {
                        updatedPluginFiles.add(pluginAndFile);
                    }
                }
            } catch (Throwable t) {
                LOGGER.warn("Can't check plugin {} - {} updates", pluginInfo.getPluginId(), pluginInfo.getPluginVersion(), t);
            }
            updateProgress(++progress, pluginInfoList.size());
        }
        InstallationController.INSTANCE.writeLastPluginUpdateCheckDate(new Date());
        return updatedPluginFiles;
    }

}
