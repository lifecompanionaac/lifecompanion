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

package org.lifecompanion.base.data.io.task;

import org.lifecompanion.api.component.definition.simplercomp.KeyListNodeI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Task to save a key list in a directory.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class KeyListImportTask extends AbstractKeyListLoadingTask<List<KeyListNodeI>> {
    private final static Logger LOGGER = LoggerFactory.getLogger(KeyListImportTask.class);

    private final List<File> sourceFiles;

    public KeyListImportTask(final List<File> sourceFiles) {
        super();
        updateTitle(Translation.getText("task.import.keylist.title"));
        this.sourceFiles = sourceFiles;
    }

    @Override
    protected List<KeyListNodeI> call() throws Exception {
        List<KeyListNodeI> result = new ArrayList<>();
        for (File sourceFile : sourceFiles) {
            final File tempDir = LCUtils.getTempDir("keylist-import");
            IOUtils.unzipInto(sourceFile, tempDir, null);
            final KeyListNodeI nodeFromDirectory = this.loadNodeFromDirectory(tempDir);
            result.addAll(nodeFromDirectory.getChildren());
        }
        return result;
    }
}
