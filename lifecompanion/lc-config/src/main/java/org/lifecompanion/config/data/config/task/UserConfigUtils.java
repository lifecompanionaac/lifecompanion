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

package org.lifecompanion.config.data.config.task;

import org.lifecompanion.base.data.control.update.InstallationController;

import java.io.File;

public class UserConfigUtils {
    private static final String USER_STARTUP_PATH = "/AppData/Roaming/Microsoft/Windows/Start Menu/Programs/Startup/LifeCompanion.lnk";

    private UserConfigUtils() {
    }

    static File getStartupShortcutPath() {
        return new File(System.getProperty("user.home") + USER_STARTUP_PATH);
    }

    public static File getLauncherPath() {
        return new File("." + File.separator + InstallationController.INSTANCE.getLauncherProperties().getLauncherPath());
    }
}
