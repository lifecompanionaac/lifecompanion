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
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.configuration.InstallationConfiguration;
import org.lifecompanion.framework.commons.utils.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.lifecompanion.framework.commons.ApplicationConstant.DIR_NAME_APPLICATION;
import static org.lifecompanion.framework.commons.ApplicationConstant.DIR_NAME_APPLICATION_DATA;

public abstract class CreateDebTask extends DefaultTask {
    private static final String CMD_PATH = "/usr/local/bin/lifecompanion";
    private static final String APP_DIR_PATH = "/mnt/root2/lifecompanion/";

    // TODO : where to store profile and configuration ?

    private static final Logger LOGGER = Logging.getLogger(CreateDebTask.class);

    @TaskAction
    void createDeb() throws Exception {
        File buildDir = getProject().getBuildDir();
        File debianDir = new File(buildDir + File.separator + "debian");
        File debContentDir = new File(debianDir + File.separator + "lifecompanion_" + getProject().getVersion() + "_x64");
        debContentDir.mkdirs();

        // Create specific launcher
        LOGGER.lifecycle("Prepare specific launcher");
        File unixSrc = new File(getProject().getRootProject().getProjectDir().getAbsolutePath() + File.separator + "lc-app-launcher" + File.separator + "build-src" + File.separator + "lifecompanion.sh");
        String unixLauncherContent = readContent(unixSrc);
        unixLauncherContent = unixLauncherContent.replace("cd ${0%/*}/..", "cd " + APP_DIR_PATH);
        File destCmdFile = new File(debContentDir + CMD_PATH);
        IOUtils.createParentDirectoryIfNeeded(destCmdFile);
        IOUtils.writeToFile(destCmdFile, unixLauncherContent, "utf-8");
        boolean setExecutable = destCmdFile.setExecutable(true);
        LOGGER.lifecycle("Set launcher to be executable : {}", setExecutable);

        // Copy application and data directory
        LOGGER.lifecycle("Copying application and data folders");
        File offlineDirFor = PublishApplicationTask.getOfflineDirFor(getProject(), SystemType.UNIX);
        File destDir = new File(debContentDir + APP_DIR_PATH);
        IOUtils.copyDirectory(new File(offlineDirFor + File.separator + DIR_NAME_APPLICATION), new File(destDir + File.separator + DIR_NAME_APPLICATION));
        File destAppDataDir = new File(destDir + File.separator + DIR_NAME_APPLICATION_DATA);
        IOUtils.copyDirectory(new File(offlineDirFor + File.separator + DIR_NAME_APPLICATION_DATA), destAppDataDir);

        // Remove image directory contents
        File imageDirectoryRoot = new File(destDir + File.separator + DIR_NAME_APPLICATION_DATA + File.separator + "resources" + File.separator + "images");
        File[] imageDirectoryFiles = imageDirectoryRoot.listFiles();
        if (imageDirectoryFiles != null) {
            for (File imageDirectoryFile : imageDirectoryFiles) {
                if (imageDirectoryFile.isDirectory()) {
                    IOUtils.deleteDirectoryAndChildren(imageDirectoryFile);
                }
            }
        }
        // TODO : Remove Windows resources

        // Custom install configuration
        InstallationConfiguration installConfig = new InstallationConfiguration("2048m", "~/Documents/LifeCompanion");
        installConfig.save(new File(destAppDataDir + File.separator + "installation.properties"));

        // Create deb control file
        LOGGER.lifecycle("Prepare deb files");
        File debianControlSrc = new File(getProject().getProjectDir().getAbsolutePath() + File.separator + "build-src" + File.separator + "debian-control-template.txt");
        String debianControlContent = readContent(debianControlSrc);
        debianControlContent = debianControlContent.replace("${appVersion}", "" + getProject().getVersion());
        File debianControlDestFile = new File(debContentDir + "/DEBIAN/control");
        IOUtils.createParentDirectoryIfNeeded(debianControlDestFile);
        IOUtils.writeToFile(debianControlDestFile, debianControlContent, "utf-8");

        // Create the debian package
        LOGGER.lifecycle("Will run dpkg-deb command");
        Process start = new ProcessBuilder().command("dpkg-deb", "--build", debContentDir.getName()).directory(debianDir).redirectError(ProcessBuilder.Redirect.INHERIT).redirectOutput(ProcessBuilder.Redirect.INHERIT).start();
        if (start.waitFor() != 0) throw new Exception("dpkg-deb command failed !");
    }

    private String readContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = bf.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
