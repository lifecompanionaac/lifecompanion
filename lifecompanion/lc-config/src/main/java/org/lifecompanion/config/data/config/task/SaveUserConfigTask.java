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

import mslinks.ShellLink;
import org.lifecompanion.base.data.common.LCTask;
import org.lifecompanion.base.data.config.UserBaseConfiguration;
import org.lifecompanion.framework.commons.SystemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Task to save {@link UserBaseConfiguration}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SaveUserConfigTask extends LCTask<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveUserConfigTask.class);


    public SaveUserConfigTask() {
        super("task.title.save.user.config");
    }

    @Override
    protected Void call() throws Exception {
        UserBaseConfiguration.INSTANCE.save();
        // Only on Windows : launch on startup property : create or delete the shortcut when needed
        if (SystemType.current() == SystemType.WINDOWS) {
            File startupLauncherShortcutPath = UserConfigUtils.getStartupShortcutPath();
            LOGGER.info("Will check startup shortcut from path : {}, exists {}", startupLauncherShortcutPath, startupLauncherShortcutPath.exists());
            final boolean launchOnStartupEnabled = UserBaseConfiguration.INSTANCE.launchLCSystemStartupProperty().get();
            if (startupLauncherShortcutPath.exists() && !launchOnStartupEnabled) {
                final boolean deleted = startupLauncherShortcutPath.delete();
                LOGGER.info("Startup disabled, removed shortcut : {}", deleted);
            } else if (!startupLauncherShortcutPath.exists() && launchOnStartupEnabled) {
                File launcherFile = UserConfigUtils.getLauncherPath();
                LOGGER.info("Startup enabled, will create shortcut to {} (linked to {})", startupLauncherShortcutPath, launcherFile);
                ShellLink lLink = ShellLink.createLink(launcherFile.getAbsolutePath());
                lLink.saveTo(startupLauncherShortcutPath.getAbsolutePath());
            }
        }
        return null;
    }
}
