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

package org.lifecompanion.installer.task;

import javafx.concurrent.Task;
import javafx.util.Pair;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.client.service.AppServerService;
import org.lifecompanion.framework.commons.doublelaunch.DoubleLaunchController;
import org.lifecompanion.framework.commons.doublelaunch.NoopDoubleLaunchListener;
import org.lifecompanion.installer.controller.InstallerManager;
import org.lifecompanion.installer.ui.InstallerUIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitializeInstallationTask extends Task<InitializeResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitializeInstallationTask.class);
    private final InstallerUIConfiguration configuration;
    private final AppServerClient client;

    public InitializeInstallationTask(InstallerUIConfiguration configuration, AppServerClient client) {
        this.configuration = configuration;
        this.client = client;
        this.updateProgress(-1, 0);
    }

    @Override
    protected InitializeResult call() throws Exception {
        // Get installation directories
        configuration.setInstallationUserDataDirectory(InstallerManager.INSTANCE.getSpecificOrDefault().getDefaultDataDirectory("LifeCompanion"));
        configuration.setInstallationSoftwareDirectory(InstallerManager.INSTANCE.getSpecificOrDefault().getDefaultSoftwareDirectory("LifeCompanion"));
        LOGGER.info("Default directories set to installation configuration");

        // Check internet connection
        boolean connectedToInternet = client.isConnectedToInternet();
        LOGGER.info("Internet connection checked : {}", connectedToInternet);

        // Wakeup server
        try {
            new AppServerService(client).wakeup();
            LOGGER.info("Server woken up");
        } catch (Exception e) {
            LOGGER.error("Couldn't wake the update server (or timeout ?)", e);
            //Ignore
        }

        // Double launch
        boolean doubleStart = DoubleLaunchController.INSTANCE.startAndDetect(new NoopDoubleLaunchListener(), false, null);

        return new InitializeResult(connectedToInternet, !doubleStart);
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        updateProgress(1, 1);
    }

    @Override
    protected void failed() {
        super.failed();
        updateProgress(1, 1);
    }
}
