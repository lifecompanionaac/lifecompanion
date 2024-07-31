/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.controller.appinstallation;

import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.impl.appinstallation.OptionalResourceEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum OptionalResourceController implements LCStateListener {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionalResourceController.class);

    OptionalResourceController() {
    }

    @Override
    public void lcStart() {
        checkForInstalledResources();
    }

    private void checkForInstalledResources() {
        for (OptionalResourceEnum optionalResourceEnum : OptionalResourceEnum.values()) {
            LOGGER.info("Will check if optional resource {} is installed", optionalResourceEnum.getId());
            boolean installed = optionalResourceEnum.getResource().isInstalled();
            if (installed) {
                LOGGER.info("{} is installed, will be validated", optionalResourceEnum.getId());
                if (!optionalResourceEnum.getResource().validateInstallation()) {
                    LOGGER.info("{} is installed but could not be validated, resource will be uninstalled", optionalResourceEnum.getId());
                    optionalResourceEnum.getResource().uninstall();
                    // TODO store invalidated installation in a list to display it to user
                }
            }

        }
    }

    @Override
    public void lcExit() {

    }
}
