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

package org.lifecompanion.base.data.control;

import org.lifecompanion.framework.commons.configuration.InstallationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.lifecompanion.framework.commons.ApplicationConstant.DIR_NAME_APPLICATION_DATA;
import static org.lifecompanion.framework.commons.ApplicationConstant.INSTALLATION_CONFIG_FILENAME;

public enum InstallationConfigurationController {
    INSTANCE;

    private final Logger LOGGER = LoggerFactory.getLogger(InstallationConfigurationController.class);

    // PATH GETTER
    //========================================================================
    public File getUserDirectory() {
        return getInstallationConfiguration().getUserDataDirectory();
    }
    //========================================================================

    // INSTALLATION CONFIGURATION
    //========================================================================
    private InstallationConfiguration installationConfiguration;

    public InstallationConfiguration getInstallationConfiguration() {
        if (installationConfiguration == null) {
            try {
                installationConfiguration = InstallationConfiguration.read(new File(DIR_NAME_APPLICATION_DATA + File.separator + INSTALLATION_CONFIG_FILENAME));
            } catch (IOException e) {
                LOGGER.error("Can't read installation configuration", e);
            }
        }
        return installationConfiguration;
    }
    //========================================================================
}
