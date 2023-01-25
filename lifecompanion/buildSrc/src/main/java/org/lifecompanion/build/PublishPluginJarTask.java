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
import org.gradle.api.tasks.TaskAction;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.server.dto.CreateApplicationPluginUpdate;
import org.lifecompanion.framework.model.server.update.ApplicationPlugin;
import org.lifecompanion.framework.model.server.update.ApplicationPluginUpdate;
import org.lifecompanion.framework.model.server.update.UpdateVisibility;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public abstract class PublishPluginJarTask extends DefaultTask {
    private static final Logger LOGGER = Logging.getLogger(PublishPluginJarTask.class);

    @TaskAction
    void publishPluginJar() throws Exception {
        // Find jar file
        File pluginDirPath = new File(BuildToolUtils.checkAndGetProperty(getProject(), "pluginDir"));
        if (!pluginDirPath.exists()) {
            throw new IllegalArgumentException("Plugin dir " + pluginDirPath.getAbsolutePath() + " doesn't exist");
        }
        // Try to find the built jar file
        File libsDir = new File(pluginDirPath.getPath() + File.separator + "build" + File.separator + "libs");
        File[] files = libsDir.listFiles();
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Can't find any built file in " + libsDir.getAbsolutePath());
        }
        File jarFile = files[0];
        if (!StringUtils.isEqualsIgnoreCase("jar", FileNameUtils.getExtension(jarFile.getPath()))) {
            throw new IllegalArgumentException("Detected jar file could be invalid " + jarFile.getAbsolutePath());
        }

        String appId = BuildToolUtils.checkAndGetProperty(getProject(), "appId");
        UpdateVisibility visibility = UpdateVisibility.valueOf(BuildToolUtils.checkAndGetProperty(getProject(), "visibility"));
        String env = BuildToolUtils.getEnvValueLowerCase(getProject());

        // Compute hash and file information
        long fileLength = jarFile.length();
        String fileHash = IOUtils.fileSha256HexToString(jarFile);

        ApplicationPlugin applicationPlugin = new ApplicationPlugin();
        ApplicationPluginUpdate applicationPluginUpdate = new ApplicationPluginUpdate();
        try (JarInputStream jarStream = new JarInputStream(new FileInputStream(jarFile))) {
            Manifest mf = jarStream.getManifest();
            Attributes attributes = mf.getMainAttributes();

            applicationPlugin.setApplicationId(appId);
            applicationPlugin.setId(attributes.getValue("LifeCompanion-Plugin-Id"));
            applicationPlugin.setAuthor(attributes.getValue("LifeCompanion-Plugin-Author"));
            applicationPlugin.setName(attributes.getValue("LifeCompanion-Plugin-Name"));
            applicationPlugin.setDescription(attributes.getValue("LifeCompanion-Plugin-Description"));

            applicationPluginUpdate.setApplicationPluginId(applicationPlugin.getId());
            applicationPluginUpdate.setVisibility(visibility);
            applicationPluginUpdate.setVersion(attributes.getValue("LifeCompanion-Plugin-Version"));
            applicationPluginUpdate.setFileName(jarFile.getName());
            applicationPluginUpdate.setFileHash(fileHash);
            applicationPluginUpdate.setFileSize(fileLength);
            applicationPluginUpdate.setMinAppVersion(attributes.getValue("LifeCompanion-Min-App-Version"));
            LOGGER.lifecycle("Jar information read from {}", jarFile.getName());
            LOGGER.lifecycle("publishLauncherUpdate : env = {}, appId = {}, version = {}, visibility = {} ", env, appId, applicationPluginUpdate.getVersion(), visibility);

            // Login and send request
            String serverURL = BuildToolUtils.getServerURL(getProject());
            try (AppServerClient client = new AppServerClient(serverURL)) {
                BuildToolUtils.loginOnServerOrFail(client, getProject());
                LOGGER.lifecycle("Connected to update server {}", serverURL);

                ApplicationPluginUpdate createdUpdate = client.postWithFile("/api/admin/application-plugin/create-update",
                        new CreateApplicationPluginUpdate(applicationPlugin, applicationPluginUpdate), jarFile,
                        ApplicationPluginUpdate.class);
                LOGGER.lifecycle("Application plugin update created for {}_{} : {}", createdUpdate.getApplicationPluginId(), createdUpdate.getVersion(), createdUpdate.getId());
            }
        }
    }
}
