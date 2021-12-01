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

package org.lifecompanion.framework.client.props;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

public class LauncherProperties {
    private String versionLabel;
    private Date buildDate;
    private String launcherPath;

    private LauncherProperties() {
    }

    public static LauncherProperties load(File path) {
        try (FileReader fr = new FileReader(path, StandardCharsets.UTF_8)) {
            LauncherProperties properties = new LauncherProperties();
            Properties props = new Properties();
            props.load(fr);
            properties.versionLabel = props.getProperty("version");
            properties.buildDate = new Date(Long.parseLong(props.getProperty("buildDate")));
            properties.launcherPath = props.getProperty("launcherPath");
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getVersionLabel() {
        return versionLabel;
    }

    public Date getBuildDate() {
        return buildDate;
    }

    public String getLauncherPath() {
        return launcherPath;
    }
}
