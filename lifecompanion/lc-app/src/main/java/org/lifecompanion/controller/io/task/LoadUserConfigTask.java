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

package org.lifecompanion.controller.io.task;

import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.util.model.LCTask;

import java.io.File;

/**
 * Task to load {@link UserConfigurationController}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LoadUserConfigTask extends LCTask<Void> {

    public LoadUserConfigTask() {
        super("task.title.load.user.config");
    }

    @Override
    protected Void call() throws Exception {
        UserConfigurationController.INSTANCE.load();
        // On windows, load startup enabled
        if (SystemType.current() == SystemType.WINDOWS) {
            File startupLauncherShortcutPath = SaveUserConfigTask.getStartupShortcutPath();
            UserConfigurationController.INSTANCE.launchLCSystemStartupProperty().set(startupLauncherShortcutPath.exists());
        }
        return null;
    }
}
