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

package org.lifecompanion.controller.appinstallation.task;

import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.ApplicationConstant;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;

import java.io.File;

public class DeleteUpdateTempFileTask extends AbstractUpdateTask<Boolean> {

    public DeleteUpdateTempFileTask(AppServerClient client, String applicationId, boolean enablePreviewUpdates, boolean pauseOnStart) {
        super(client, applicationId, enablePreviewUpdates, pauseOnStart);
        updateMessage(Translation.getText("update.task.delete.temp.file.task.message"));
    }

    @Override
    protected Boolean call() throws Exception {
        Thread.sleep(TASK_START_SHORT_DELAY);
        InstallationController.INSTANCE.tryToSendUpdateStats();
        Thread.sleep(TASK_START_LONG_DELAY);
        File updateDirectory = new File("." + File.separator + ApplicationConstant.DIR_NAME_APPLICATION_UPDATE);
        IOUtils.deleteDirectoryAndChildren(updateDirectory);
        return true;
    }
}
