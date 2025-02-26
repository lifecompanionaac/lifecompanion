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

package org.lifecompanion.build;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.io.IOUtils;

import java.io.*;

public abstract class PrepareOfflineInstallerTask extends DefaultTask {
    private static final Logger LOGGER = Logging.getLogger(PrepareOfflineInstallerTask.class);

    @Input
    abstract Property<String> getSystem();

    @TaskAction
    void prepareOfflineInstaller() throws Exception {
        String systemStr = getSystem().get();
        SystemType targetSystem = SystemType.valueOf(systemStr);
        LOGGER.lifecycle("Target system : {}", targetSystem);

        File sourceOfflineFolder = PublishApplicationTask.getOfflineDirFor(getProject(), targetSystem);
        File targetZipFile = new File(getProject().getProjectDir() + "/../lc-installer/build/tmp/offline-installation.zip");
        LOGGER.lifecycle("Will ZIP offline ressources to : {}", targetZipFile);
        IOUtils.zipInto(targetZipFile, sourceOfflineFolder, null);

        LOGGER.lifecycle("Offline installer preparation is finished");
    }
}