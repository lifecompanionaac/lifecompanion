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

package org.lifecompanion.build;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.model.server.update.ApplicationLauncherUpdate;
import org.lifecompanion.framework.model.server.update.UpdateVisibility;
import org.lifecompanion.framework.utils.FluentHashMap;

import java.io.File;
import java.util.Map;

public abstract class PublishLauncherTask extends DefaultTask {
    private static final Logger LOGGER = Logging.getLogger(PublishLauncherTask.class);

    private static final Map<SystemType, String> PATH_FROM_SYSTEM = FluentHashMap
            .map(SystemType.WINDOWS, "LifeCompanion.exe")
            .with(SystemType.MAC, "MacOS/lifecompanion.sh")
            .with(SystemType.UNIX, "launcher/lifecompanion.sh");

    public PublishLauncherTask() {
        super();
        this.setGroup("lifecompanion");
        this.setDescription("(INTERNAL TASK) Publish a launcher update for a given file and system (from an existing build)");
    }

    @Input
    abstract Property<String> getSystem();

    @Input
    abstract Property<String> getFile();

    @TaskAction
    void publishLauncherUpdate() throws Exception {
        String appId = BuildToolUtils.checkAndGetProperty(getProject(), "appId");
        UpdateVisibility visibility = UpdateVisibility.valueOf(BuildToolUtils.checkAndGetProperty(getProject(), "visibility"));
        SystemType system = SystemType.valueOf(this.getSystem().get());
        String version = String.valueOf(getProject().getVersion());
        String env = BuildToolUtils.getEnvValueLowerCase(getProject());
        LOGGER.lifecycle("publishLauncherUpdate : env = {}, appId = {}, system = {}, version = {}, visibility = {} ", env, appId, system, version, visibility);

        File pathToLauncher = new File(getFile().get());

        // Login
        String serverURL = BuildToolUtils.getServerURL(getProject());
        try (AppServerClient client = new AppServerClient(serverURL)) {
            BuildToolUtils.loginOnServerOrFail(client, getProject());
            // Compute hash and file information
            long fileLength = pathToLauncher.length();
            String fileHash = IOUtils.fileSha256HexToString(pathToLauncher);

            ApplicationLauncherUpdate update = new ApplicationLauncherUpdate();
            update.setApplicationId(appId);
            update.setVersion(String.valueOf(getProject().getVersion()));
            update.setSystem(system);
            update.setSystemModifier(null);
            update.setVisibility(visibility);
            update.setFileHash(fileHash);
            update.setFileSize(fileLength);
            update.setFilePath(PATH_FROM_SYSTEM.get(system));

            ApplicationLauncherUpdate createdLauncher = client.postWithFile("/api/admin/launcher/create-update", update, pathToLauncher,
                    ApplicationLauncherUpdate.class);

            LOGGER.lifecycle("Launcher update created for {}_{}_{} : {}", createdLauncher.getApplicationId(), createdLauncher.getVersion(),
                    createdLauncher.getSystem(), createdLauncher.getId());
        }
    }
}
