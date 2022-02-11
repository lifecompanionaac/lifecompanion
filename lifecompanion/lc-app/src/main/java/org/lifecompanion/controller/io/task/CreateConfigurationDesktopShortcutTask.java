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

import mslinks.ShellLink;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.util.model.LCTask;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.system.WindowsRegUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CreateConfigurationDesktopShortcutTask extends LCTask<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateConfigurationDesktopShortcutTask.class);

    private final LCProfileI profile;
    private final LCConfigurationDescriptionI configuration;

    public CreateConfigurationDesktopShortcutTask(LCProfileI profile, LCConfigurationDescriptionI configuration) {
        super("task.create.configuration.desktop.shortcut");
        this.profile = profile;
        this.configuration = configuration;
    }

    @Override
    protected Void call() throws Exception {
        // Only on Windows : launch on startup property : create or delete the shortcut when needed
        if (SystemType.current() == SystemType.WINDOWS) {
            File desktopDirectory;
            try {
                final String[] queryResult = WindowsRegUtils.executeRegeditCmd("query", Arrays.asList("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders", "/v", "Desktop"), true);
                final String userProfileFromEnv = System.getenv("USERPROFILE");
                final String pathFromRegWithEnvInserted = queryResult[queryResult.length - 1].replace("%USERPROFILE%", userProfileFromEnv).replace("%userprofile%", userProfileFromEnv);
                desktopDirectory = new File(pathFromRegWithEnvInserted);
                if (!desktopDirectory.exists()) {
                    throw new IOException("Invalid desktop path from registry : " + pathFromRegWithEnvInserted);
                }
            } catch (Exception e) {
                LOGGER.warn("Couldn't read desktop path, will fallback to default location", e);
                desktopDirectory = new File(System.getProperty("user.home") + File.separator + "Desktop");
            }
            File desktopShortcutPath = new File(desktopDirectory.getAbsolutePath() + File.separator + LCUtils.getValidFileName(configuration.configurationNameProperty().get()) + ".lnk");
            if (!desktopShortcutPath.exists()) {
                ShellLink lLink = ShellLink.createLink(InstallationController.INSTANCE.getLauncherPath().getAbsolutePath());
                lLink.setCMDArgs(LCConstant.ARG_LAUNCH_CONFIG + " " + profile.getID() + " " + configuration.getConfigurationId());
                lLink.saveTo(desktopShortcutPath.getAbsolutePath());
                LOGGER.info("Configuration direct use desktop shortcut created : {}", desktopShortcutPath);
            } else {
                LOGGER.info("Shortcut {} already existed, didn't create new", desktopShortcutPath);
            }
        }
        return null;
    }
}
