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

package org.lifecompanion.installer.controller;

import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.installer.ui.InstallerUIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class UnixSystemInstallation extends DefaultSystemInstallation {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnixSystemInstallation.class);

    @Override
    public File getDefaultSoftwareDirectory(String name) {
        return new File(System.getProperty("user.home") + "/" + name);
    }

    @Override
    public File getDefaultDataDirectory(String name) {
        return new File(System.getProperty("user.home") + "/Documents/LifeCompanion");
    }

    @Override
    public void runSystemSpecificInstallationTask(InstallerUIConfiguration configuration, Consumer<String> logAppender) throws Exception {
        // Add the logo to launcher directory
        try (FileOutputStream fos = new FileOutputStream(configuration.getInstallationSoftwareDirectory().getPath() + File.separator + "launcher" + File.separator + "lifecompanion.png")) {
            try (InputStream is = UnixSystemInstallation.class.getResourceAsStream("/lifecompanion_icon_64px.png")) {
                IOUtils.copyStream(is, fos);
            }
        }

        // Create the desktop entry (replace install path)
        File destkopEntryDestination = new File(System.getProperty("user.home") + File.separator + ".local/share/applications/LifeCompanion.desktop");
        try (PrintWriter pw = new PrintWriter(destkopEntryDestination, StandardCharsets.UTF_8)) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(UnixSystemInstallation.class.getResourceAsStream("/LifeCompanion.desktop"), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    pw.println(line.replace("INSTALL_PATH", configuration.getInstallationSoftwareDirectory().getAbsolutePath()));
                }
            }
        }

        // Exec rights on launcher
        File launcherFile = new File(configuration.getInstallationSoftwareDirectory().getAbsolutePath() + File.separator + "launcher" + File.separator + "lifecompanion.sh");
        final boolean setExecutable = launcherFile.setExecutable(true);
        LOGGER.info("Launcher set executable result : {}", setExecutable);
    }

    @Override
    public void directoriesInitialized(InstallerUIConfiguration configuration) throws Exception {
    }
}
