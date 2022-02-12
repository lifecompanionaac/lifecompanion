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

package org.lifecompanion.launcher;


import org.lifecompanion.framework.commons.ApplicationConstant;
import org.lifecompanion.framework.commons.configuration.InstallationConfiguration;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.lifecompanion.framework.commons.ApplicationConstant.*;

/**
 * Application launcher : this launcher is used on Windows thanks to Launch4J.<br>
 * It detect and launch the application or the update. It could be replaced on Unix and Mac with bash script.
 */
public class LauncherApplication {
    // Light launcher : shouldn't use logger

    public static void main(String[] args) throws IOException {
        // This is not a set : some args can be duplicated
        List<String> argsCollection = new ArrayList<>(Arrays.asList(args));

        // Write launcher information
        File launcherPropFile = new File(DIR_NAME_APPLICATION_DATA + File.separator + LAUNCHER_PROP_FILENAME);
        if (launcherPropFile.getParentFile() != null) {
            launcherPropFile.getParentFile().mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(launcherPropFile)) {
            try (InputStream is = LauncherApplication.class.getResourceAsStream("/" + LAUNCHER_PROP_FILENAME)) {
                IOUtils.copyStream(is, fos);
            }
        }

        // Read installation configuration
        InstallationConfiguration installationConfiguration = InstallationConfiguration.read(new File(DIR_NAME_APPLICATION_DATA + File.separator + INSTALLATION_CONFIG_FILENAME));
        String xmxParam = installationConfiguration.getXmxConfiguration();

        // Check download state if update not finished yet
        boolean updateDownloadFinished = isUpdateDownloadFinished();

        ArrayList<String> cmds = new ArrayList<>();
        cmds.add((updateDownloadFinished ? DIR_NAME_APPLICATION_UPDATE : DIR_NAME_APPLICATION) + File.separator + RUN_VM_COMMAND);

        // VM memory configuration
        cmds.add("-Xmx" + xmxParam);

        // VM proxy configuration
        cmds.add("-Djava.net.useSystemProxies=true");

        // VM ui scale configuration : solve problems > 100% Windows systems (unfocusable stages, images deletion...)
        cmds.add("-Dglass.win.uiScale=100%");

        // Read classpath configuration
        if (!updateDownloadFinished) {
            String classPathArg = null;
            File pluginClasspathFile = new File(ApplicationConstant.DIR_NAME_APPLICATION_DATA + File.separator + "plugins" + File.separator + "plugin-classpath");
            if (pluginClasspathFile.exists()) {
                try (Scanner scan = new Scanner(pluginClasspathFile, StandardCharsets.UTF_8)) {
                    if (scan.hasNextLine()) {
                        String line = scan.nextLine();
                        if (StringUtils.isNotBlank(line)) {
                            classPathArg = StringUtils.stripToEmpty(line);
                        }
                    }
                }
            }
            if (classPathArg != null) {
                cmds.add("-classpath");
                cmds.add(classPathArg);
            }
        }

        // EasyBind reads javafx
        cmds.add("--add-reads");
        cmds.add("lifecompanion.merged.module=javafx.base");

        // LC word predictor reads slf4j
        cmds.add("--add-reads");
        cmds.add("lifecompanion.merged.module=org.slf4j");

        // Unfocusable stage : need internal sun* API
        cmds.add("--add-exports=javafx.graphics/com.sun.glass.ui=org.lifecompanion.app");

        // ControlsFX : open internal sun* API
        cmds.add("--add-opens=javafx.base/com.sun.javafx.runtime=org.controlsfx.controls");
        cmds.add("--add-opens=javafx.base/com.sun.javafx.collections=org.controlsfx.controls");
        cmds.add("--add-opens=javafx.graphics/com.sun.javafx.css=org.controlsfx.controls");
        cmds.add("--add-opens=javafx.graphics/com.sun.javafx.scene=org.controlsfx.controls");
        cmds.add("--add-opens=javafx.graphics/com.sun.javafx.scene.traversal=org.controlsfx.controls");
        cmds.add("--add-opens=javafx.graphics/javafx.scene=org.controlsfx.controls");
        cmds.add("--add-opens=javafx.controls/com.sun.javafx.scene.control=org.controlsfx.controls");
        cmds.add("--add-opens=javafx.controls/com.sun.javafx.scene.control.behavior=org.controlsfx.controls");
        cmds.add("--add-opens=javafx.controls/javafx.scene.control.skin=org.controlsfx.controls");

        // Splash screen
        cmds.add("-splash:data/lifecompanion_splashscreen.png");

        cmds.add("-m");
        cmds.add("org.lifecompanion.app/org.lifecompanion.LifeCompanion");
        if (updateDownloadFinished) {
            cmds.add(ARG_UPDATE_DOWNLOAD_FINISHED);
        }
        cmds.addAll(argsCollection);
        if (argsCollection.contains(ARG_DEV)) {
            try (PrintWriter pw = new PrintWriter(new File("launcher.txt"))) {
                pw.println(String.join(" ", cmds));
            }
        }
        new ProcessBuilder()//
                .command(cmds)//
                .redirectError(ProcessBuilder.Redirect.DISCARD)//
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)//
                .start();
    }

    private static boolean isUpdateDownloadFinished() {
        return new File("." + File.separator + DIR_NAME_APPLICATION_UPDATE + File.separator + UPDATE_DOWNLOAD_FINISHED_FLAG_FILE).exists();
    }
}