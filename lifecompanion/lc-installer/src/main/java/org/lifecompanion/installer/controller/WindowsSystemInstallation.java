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

import mslinks.ShellLink;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.system.WindowsRegUtils;
import org.lifecompanion.installer.ui.InstallerUIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WindowsSystemInstallation extends DefaultSystemInstallation {
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsSystemInstallation.class);

    @Override
    public File getDefaultSoftwareDirectory(String name) {
        try {
            String[] results = WindowsRegUtils.executeRegeditCmd("query",
                    Arrays.asList("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "/v", "Common AppData"),
                    true);
            return new File(results[results.length - 1] + File.separator + name);
        } catch (Exception e) {
            LOGGER.error("Couldn't get Windows default software directory", e);
            return super.getDefaultDataDirectory(name);
        }
    }

    @Override
    public File getDefaultDataDirectory(String name) {
        try {
            return new File(System.getenv("PUBLIC") + File.separator + "Documents" + File.separator + name);
        } catch (Exception e) {
            LOGGER.error("Couldn't get Windows default data directory", e);
            return super.getDefaultDataDirectory(name);
        }
    }

    @Override
    public void runSystemSpecificInstallationTask(InstallerUIConfiguration configuration, Consumer<String> logAppender) throws Exception {
        // File association
        logAppender.accept(Translation.getText("lc.installer.task.installing.progress.detail.file.association"));
        File launcherPath = new File(configuration.getInstallationSoftwareDirectory() + File.separator + "LifeCompanion.exe");
        WindowsRegUtils.createFileAssociation("lcc", launcherPath, "LifeCompanion", Translation.getText("lc.installer.file.association.config"));
        WindowsRegUtils.createFileAssociation("lcp", launcherPath, "LifeCompanion", Translation.getText("lc.installer.file.association.profile"));
        LOGGER.info("Windows file associations created");

        // Create link on user desktop
        logAppender.accept(Translation.getText("lc.installer.task.installing.progress.detail.file.desktop.shortcut"));
        File desktopDirectory = new File(System.getenv("PUBLIC") + File.separator + "Desktop");
        ShellLink lLink = ShellLink.createLink(launcherPath.getAbsolutePath());
        File desktopShortcutPath = new File(desktopDirectory.getAbsolutePath() + File.separator + "LifeCompanion" + ".lnk");
        lLink.saveTo(desktopShortcutPath.getAbsolutePath());
        LOGGER.info("Windows desktop shortcut created : {}", desktopShortcutPath);

        // Create link in start menu
        logAppender.accept(Translation.getText("lc.installer.task.installing.progress.detail.file.menu.shortcut"));
        String[] programResult = WindowsRegUtils.executeRegeditCmd("query",
                Arrays.asList("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "/v", "Common Programs"),
                true);
        File copyShortcutTarget = new File(programResult[programResult.length - 1] + File.separator + "LifeCompanion" + File.separator + desktopShortcutPath.getName());
        IOUtils.createParentDirectoryIfNeeded(copyShortcutTarget);
        lLink.saveTo(copyShortcutTarget.getAbsolutePath());
        LOGGER.info("Windows program shortcut created : {}", copyShortcutTarget);
    }

    @Override
    public void directoriesInitialized(InstallerUIConfiguration configuration) throws Exception {
        Path file = configuration.getInstallationSoftwareDirectory().toPath();
        AclFileAttributeView view = Files.getFileAttributeView(file, AclFileAttributeView.class);
        // show current permissions for authenticated users
        List<AclEntry> acls = view.getAcl();
        Set<UserPrincipal> principals = acls.stream().map(AclEntry::principal).collect(Collectors.toSet());
        for (UserPrincipal principal : principals) {
            AclEntry entry = AclEntry.newBuilder()
                    .setType(AclEntryType.ALLOW)//
                    .setPrincipal(principal)//
                    .setPermissions(AclEntryPermission.values())//
                    .setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT)//
                    .build();
            acls.add(0, entry);
            LOGGER.info("ACL entry created for {}", principal);
        }
        view.setAcl(acls);
    }
}
