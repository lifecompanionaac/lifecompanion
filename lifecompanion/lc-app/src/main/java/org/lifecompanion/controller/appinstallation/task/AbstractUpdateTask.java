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

import org.lifecompanion.framework.client.http.AppServerClient;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public abstract class AbstractUpdateTask<V> extends LCTask<V> {
    protected final long TASK_START_LONG_DELAY;
    protected final long TASK_START_SHORT_DELAY;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUpdateTask.class);

    protected final AppServerClient client;
    protected final String applicationId;
    protected final boolean enablePreviewUpdates;

    protected AbstractUpdateTask(AppServerClient client, String applicationId, boolean enablePreviewUpdates, boolean pauseOnStart) {
        super("update.generic.task.title");
        this.client = client;
        this.applicationId = applicationId;
        this.enablePreviewUpdates = enablePreviewUpdates;
        this.TASK_START_LONG_DELAY = pauseOnStart ? 30_000 : 0;
        this.TASK_START_SHORT_DELAY = pauseOnStart ? 5_000 : 0;
        updateProgress(-1, -1);
    }

    protected void saveJson(Object obj, File destFile) {
        try (PrintStream fos = new PrintStream(destFile, StandardCharsets.UTF_8)) {
            client.gson().toJson(obj, fos);
        } catch (Exception e) {
            LOGGER.error("Couldn't save JSON to {}", destFile, e);
        }
    }

    protected <T> T readJson(File srcFile, Class<T> typeClass) {
        try (InputStreamReader is = new InputStreamReader(new FileInputStream(srcFile), StandardCharsets.UTF_8)) {
            return client.gson().fromJson(is, typeClass);
        } catch (Exception e) {
            LOGGER.error("Couldn't read JSON from {}", srcFile, e);
            return null;
        }
    }

    protected void setLauncherExecutable(File launcherFile) {
        if (SystemType.current() == SystemType.MAC || SystemType.current() == SystemType.UNIX) {
            launcherFile.setExecutable(true);
        }
    }

}
