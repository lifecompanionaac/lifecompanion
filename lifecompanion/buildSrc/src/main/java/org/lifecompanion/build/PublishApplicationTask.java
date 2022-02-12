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
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.model.server.dto.*;
import org.lifecompanion.framework.model.server.update.TargetType;
import org.lifecompanion.framework.model.server.update.UpdateVisibility;
import org.lifecompanion.framework.utils.FluentHashMap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class PublishApplicationTask extends DefaultTask {
    private static final Logger LOGGER = Logging.getLogger(PublishApplicationTask.class);

    private static final Map<SystemType, String> PATH_TO_BUILD = FluentHashMap
            .map(SystemType.WINDOWS, "win_x64")
            .with(SystemType.UNIX, "linux_x64")
            .with(SystemType.MAC, "mac_x64");

    private static final Map<SystemType, String> PATH_TO_LAUNCHER = FluentHashMap
            .map(SystemType.WINDOWS, "launchers/WINDOWS/LifeCompanion.exe")
            .with(SystemType.UNIX, "launchers/UNIX/lifecompanion.sh")
            .with(SystemType.MAC, "launchers/MAC/lifecompanion.sh");

    private static final Map<SystemType, String> LAUNCHER_PATH = FluentHashMap
            .map(SystemType.WINDOWS, "LifeCompanion.exe")
            .with(SystemType.MAC, "MacOS/lifecompanion.sh")
            .with(SystemType.UNIX, "launcher/lifecompanion.sh");

    private static final Set<String> TO_UNZIP_PATH = Set.of(
            "resources/images/arasaac.zip",
            "resources/images/sclera.zip",
            "resources/images/fontawesome.zip",
            "resources/images/parlerpictos.zip",
            "resources/images/mulberry-symbols.zip"
    );

    private static final Map<String, String> PRESET_STORAGE_IDS = FluentHashMap
            .map("resources/images/arasaac.zip", "resources/arasaac.zip")
            .with("resources/images/sclera.zip", "resources/sclera.zip")
            .with("p4a-word-predictor/ngrams.bin", "resources/ngrams.bin");

    @TaskAction
    void publishApplicationUpdate() throws Exception {
        File buildDir = getProject().getBuildDir();
        String appId = BuildToolUtils.checkAndGetProperty(getProject(), "appId");
        String buildResourceDir = BuildToolUtils.checkAndGetProperty(getProject(), "lifecompanion.build.resources.directory");
        String version = String.valueOf(getProject().getVersion());
        String env = BuildToolUtils.getEnvValueLowerCase(getProject());
        UpdateVisibility visibility = UpdateVisibility.valueOf(BuildToolUtils.checkAndGetProperty(getProject(), "visibility"));

        for (SystemType system : PATH_TO_BUILD.keySet()) {
            LOGGER.lifecycle("publishApplicationUpdate : env = {}, appId = {}, system = {}, version = {}, visibility = {} ", env, appId, system, version, visibility);

            String serverURL = BuildToolUtils.getServerURL(getProject());
            try (AppServerClient client = new AppServerClient(serverURL)) {
                BuildToolUtils.loginOnServerOrFail(client, getProject());

                Map<String, ApplicationUpdateFileDto> files = new HashMap<>();
                File softwareDataRoot = new File(buildDir.getPath() + File.separator + "image/lc-app-" + PATH_TO_BUILD.get(system));
                if (!softwareDataRoot.exists()) throw new IllegalArgumentException("Incorrect build directory : " + softwareDataRoot.getAbsolutePath());
                exploreAndHashFiles(softwareDataRoot, TargetType.SOFTWARE_DATA, system, softwareDataRoot, files, TO_UNZIP_PATH, PRESET_STORAGE_IDS);
                int beforeSize = files.size();
                LOGGER.lifecycle("Found {} software data files in directory ({})", files.size(), softwareDataRoot);

                File softwareResourcesRoot = new File(buildResourceDir.startsWith(".") ? getProject().getProjectDir() + File.separator + buildResourceDir : buildResourceDir);
                if (!softwareResourcesRoot.exists()) throw new IllegalArgumentException("Incorrect build resource directory : " + softwareResourcesRoot.getAbsolutePath());
                exploreAndHashFiles(softwareResourcesRoot, TargetType.SOFTWARE_RESOURCES, system, softwareResourcesRoot, files, TO_UNZIP_PATH, PRESET_STORAGE_IDS);
                LOGGER.lifecycle("Found {} software resources files in directory ({})", files.size() - beforeSize, softwareResourcesRoot);

                File launcherFile = new File(getProject().getRootProject().getProjectDir().getAbsolutePath() + File.separator + "lc-app-launcher" + File.separator + "build" + File.separator + PATH_TO_LAUNCHER.get(system));
                if (!launcherFile.exists())
                    throw new IllegalArgumentException("Launcher wasn't prepared before update, run `prepareLaunchers` task on 'lc-app-launcher' before (expecting launcher file : " + launcherFile.getAbsolutePath() + ")");
                addFileTo(TargetType.LAUNCHER, system, launcherFile, LAUNCHER_PATH.get(system), files, TO_UNZIP_PATH, PRESET_STORAGE_IDS);
                LOGGER.lifecycle("Launcher file got for {} : {}", system, launcherFile.getName());

                // Initialize update
                InitializeApplicationUpdateDto initializeApplicationUpdateDto = new InitializeApplicationUpdateDto(appId, system, null, null, version, files.values());

                ApplicationUpdateInitializedDto updateInitializedDto = client.post("/api/admin/application-update/initialize-update",
                        initializeApplicationUpdateDto, ApplicationUpdateInitializedDto.class);
                LOGGER.lifecycle("Update initialized, server request {} files to be uploaded", updateInitializedDto.getFilesToUpload().size());

                int fIndex = 0;
                for (ApplicationUpdateFileDto file : updateInitializedDto.getFilesToUpload()) {
                    LOGGER.lifecycle("{}/{} - Will upload, {} - {}", ++fIndex, updateInitializedDto.getFilesToUpload().size(), file.getTargetPath(), FileNameUtils.getFileSize(file.getFileSize()));
                    File sourceFile = files.get(file.getTargetPath()).getSourceFile();
                    if (sourceFile.length() < 100_000_000) {
                        client.postWithFile("/api/admin/application-update/upload-file",
                                new UploadUpdateFileDto(sourceFile.getName(), file.getTargetPath(), file.getFileSize(), updateInitializedDto.getApplicationUpdateId(), file.getApplicationUpdateFileIdInDb(), file.getSystem()),
                                sourceFile);
                    } else {
                        LOGGER.warn("File {} not uploaded because too big : {}, should be uploaded manually !", file.getTargetPath(), FileNameUtils.getFileSize(file.getFileSize()));
                    }
                }
                LOGGER.lifecycle("Every files uploaded, will now finish update");

                client.post("/api/admin/application-update/finish-update", new FinishApplicationUpdateDto(updateInitializedDto.getApplicationUpdateId(), visibility));
                LOGGER.lifecycle("Update finished");
            }
        }
    }

    private static void exploreAndHashFiles(File root, TargetType targetType, SystemType system, File file, Map<String, ApplicationUpdateFileDto> files, Set<String> toUnzipPathSet, Map<String, String> presetStorageIdsMap)
            throws IOException {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File f : listFiles) {
                    exploreAndHashFiles(root, targetType, system, f, files, toUnzipPathSet, presetStorageIdsMap);
                }
            }
        } else {
            String targetPath = IOUtils.getRelativePath(file.getAbsolutePath(), root.getAbsolutePath());
            addFileTo(targetType, system, file, targetPath, files, toUnzipPathSet, presetStorageIdsMap);
        }
    }

    private static void addFileTo(TargetType targetType, SystemType system, File file, String targetPath, Map<String, ApplicationUpdateFileDto> files, Set<String> toUnzipPathSet, Map<String, String> presetStorageIdsMap) throws IOException {

        files.put(targetPath, new ApplicationUpdateFileDto(
                        targetPath,
                        IOUtils.fileSha256HexToString(file),
                        targetType,
                        file.length(),
                        targetType.isSystemTypeDependant() ? system : null,
                        toUnzipPathSet.contains(targetPath),
                        presetStorageIdsMap.get(targetPath),
                        file
                )
        );
    }
}
