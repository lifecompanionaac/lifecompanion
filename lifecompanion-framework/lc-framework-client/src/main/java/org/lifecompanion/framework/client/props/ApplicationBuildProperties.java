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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class ApplicationBuildProperties {
    private final String appId;
    private final String versionLabel;
    private final Date buildDate;
    private final String updateServerUrl;
    private final String appServerUrl;
    private final String appServerQueryParameters;
    private final String installationPublicKey;

    private ApplicationBuildProperties(String appId, String versionLabel, Date buildDate, String updateServerUrl, String appServerUrl, String appServerQueryParameters, String installationPublicKey) {
        this.appId = appId;
        this.versionLabel = versionLabel;
        this.buildDate = buildDate;
        this.updateServerUrl = updateServerUrl;
        this.appServerUrl = appServerUrl;
        this.appServerQueryParameters = appServerQueryParameters;
        this.installationPublicKey = installationPublicKey;
    }

    public static ApplicationBuildProperties load(InputStream is) {
        try (is) {
            Properties props = new Properties();
            props.load(is);
            return new ApplicationBuildProperties(
                    props.getProperty("appId"),
                    props.getProperty("version"),
                    new Date(Long.parseLong(props.getProperty("buildDate"))),
                    props.getProperty("updateServerUrl"),
                    props.getProperty("appServerUrl"),
                    props.getProperty("appServerQueryParameters", ""),
                    props.getProperty("installationPublicKey")
            );
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

    public String getUpdateServerUrl() {
        return updateServerUrl;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppServerUrl() {
        return appServerUrl;
    }

    public String getInstallationPublicKey() {
        return installationPublicKey;
    }

    public String getAppServerQueryParameters() {
        return appServerQueryParameters;
    }
}
