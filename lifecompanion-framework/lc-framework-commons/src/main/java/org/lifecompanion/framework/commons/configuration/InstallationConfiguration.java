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

package org.lifecompanion.framework.commons.configuration;

import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class InstallationConfiguration {
    private final String xmxConfiguration;
    private final String userDataDirectory;

    public InstallationConfiguration(String xmxConfiguration, File userDataDirectory) {
        this(xmxConfiguration, userDataDirectory.getPath());
    }

    public InstallationConfiguration(String xmxConfiguration, String userDataDirectory) {
        this.xmxConfiguration = xmxConfiguration;
        this.userDataDirectory = userDataDirectory;
    }


    public String getXmxConfiguration() {
        return xmxConfiguration;
    }

    public File getUserDataDirectory() {
        return new File(StringUtils.trimToEmpty(userDataDirectory).replace("~", System.getProperty("user.home")));
    }

    public static InstallationConfiguration read(File file) throws IOException {
        try (final FileInputStream fis = new FileInputStream(file)) {
            Properties prop = new Properties();
            prop.load(fis);
            final String xmxConfiguration = prop.getProperty("xmxConfiguration");
            final File userDataDirectory = new File(prop.getProperty("userDataDirectory"));
            InstallationConfiguration config = new InstallationConfiguration(xmxConfiguration, userDataDirectory);
            return config;
        }
    }

    public void save(File file) throws IOException {
        Properties prop = new Properties();
        prop.setProperty("xmxConfiguration", this.xmxConfiguration);
        prop.setProperty("userDataDirectory", this.userDataDirectory);
        IOUtils.createParentDirectoryIfNeeded(file);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            prop.store(fos, "Installation configuration");
        }
    }
}
