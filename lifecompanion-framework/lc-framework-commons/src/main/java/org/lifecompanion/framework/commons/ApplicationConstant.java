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

package org.lifecompanion.framework.commons;

import java.io.File;

public class ApplicationConstant {
    public final static String DIR_NAME_APPLICATION = "application";
    public final static String DIR_NAME_APPLICATION_UPDATE = "update";
    public final static String DIR_NAME_APPLICATION_DATA = "data";

    public final static String DIR_NAME_LAUNCHER_UPDATED = "updated-launcher";
    public final static String DIR_NAME_SOFTWARE_RESOURCES_UPDATED = "updated-data";
    public final static String DIR_NAME_USER_DATA_UPDATED = "updated-user-data";

    public final static String RUN_VM_COMMAND = "bin" + File.separator + "java";

    public final static String COMMON_CHARSET = "UTF-8";

    public final static String ARG_UPDATE_DOWNLOAD_FINISHED = "-updateDownloadFinished";
    public final static String ARG_UPDATE_FINISHED = "-updateFinished";
    public final static String ARG_ENABLE_PREVIEW_UPDATES = "-enablePreviewUpdates";
    public final static String ARG_DEV = "-dev";

    public final static String UPDATE_STATE_FILENAME = "state.json";
    public final static String UPDATE_DOWNLOAD_FINISHED_FLAG_FILE = "updated.flag";

    public final static String INSTALLATION_CONFIG_FILENAME = "installation.properties";
    public final static String INSTALLATION_KEY_FILENAME = "installation.key";

    public final static int DOWNLOAD_ATTEMPT_COUNT_BEFORE_FAIL = 3;
    public static final int MAX_PARALLEL_DOWNLOAD = 4;
    public static final long PAUSE_BEFORE_NEXT_ATTEMPT = 10_000; // 10 seconds
}
