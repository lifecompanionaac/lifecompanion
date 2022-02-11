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

package org.lifecompanion.controller.io;

import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LoadConfigurationDescriptionTask extends AbstractLoadUtilsTask<LCConfigurationDescriptionI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadConfigurationDescriptionTask.class);

    private final File configurationFile;

    public LoadConfigurationDescriptionTask(File configurationFile) {
        super("task.title.config.import");//FIXME
        this.configurationFile = configurationFile;
    }

    @Override
    protected LCConfigurationDescriptionI call() throws Exception {
        File tempDir = LCUtils.getTempDir("read-configuration-description");
        tempDir.mkdirs();
        IOUtils.unzipInto(this.configurationFile, tempDir, "^(?!.*lifecompanion-configuration-description\\.xml).*");
        LOGGER.info("Configuration description loaded from temp directory : {}", tempDir);
        return this.loadDescription(tempDir);
    }
}
