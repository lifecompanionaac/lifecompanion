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

import org.apache.commons.io.FilenameUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.model.server.update.ApplicationInstaller;
import org.lifecompanion.framework.model.server.update.UpdateVisibility;

import java.io.File;

public abstract class PublishInstallerTask extends DefaultTask {
    private static final Logger LOGGER = Logging.getLogger(PublishInstallerTask.class);

    public PublishInstallerTask() {
        super();
        this.setGroup("lifecompanion");
        this.setDescription("(INTERNAL TASK) Publish a installer update for a given file and system (from an existing build)");
    }

    @Input
    abstract Property<String> getSystem();

    @Input
    abstract Property<String> getFile();

    @TaskAction
    void publishInstallerUpdate() throws Exception {
        File buildDir = getProject().getBuildDir();
        SystemType system = SystemType.valueOf(this.getSystem().get());
        String appId = BuildToolUtils.checkAndGetProperty(getProject(), "appId");
        UpdateVisibility visibility = UpdateVisibility.valueOf(BuildToolUtils.checkAndGetProperty(getProject(), "visibility"));
        String version = String.valueOf(getProject().getVersion());
        String env = BuildToolUtils.getEnvValueLowerCase(getProject());
        LOGGER.lifecycle("publishInstallerUpdate : env = {}, appId = {}, system = {}, version = {}, visibility = {} ", env, appId, system, version, visibility);

        // Login
        String serverURL = BuildToolUtils.getServerURL(getProject());
        try (AppServerClient client = new AppServerClient(serverURL)) {
            BuildToolUtils.loginOnServerOrFail(client, getProject());
            // Compute hash and file information
            File pathToInstaller = new File(buildDir.getPath() + File.separator + getFile().get());
            long fileLength = pathToInstaller.length();
            String fileHash = IOUtils.fileSha256HexToString(pathToInstaller);
            String extension = FilenameUtils.getExtension(pathToInstaller.getName());

            ApplicationInstaller installer = new ApplicationInstaller();
            installer.setApplicationId(appId);
            installer.setVersion(version);
            installer.setSystem(system);
            installer.setSystemModifier(null);
            installer.setVisibility(visibility);
            installer.setFileNameRoot("LifeCompanion");
            installer.setFileHash(fileHash);
            installer.setFileSize(fileLength);
            installer.setFileNameExtension(extension);
            ApplicationInstaller createdInstaller = client.postWithFile("/api/admin/installer/create-update", installer, pathToInstaller, ApplicationInstaller.class);
            LOGGER.lifecycle("Installer update created for {}_{}_{} : {}", createdInstaller.getApplicationId(), createdInstaller.getVersion(), createdInstaller.getSystem(), createdInstaller.getId());
        }
    }
}
