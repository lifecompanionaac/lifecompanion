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

import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListNode;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;


/**
 * Task to save a key list in a directory.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class KeyListExportTask extends KeyListSavingTask {
    private final static Logger LOGGER = LoggerFactory.getLogger(KeyListExportTask.class);

    private final File destFile;
    private final List<KeyListNodeI> keyListNodes;

    public KeyListExportTask(final File destFile, final List<KeyListNodeI> keyListNodes) {
        super(LCUtils.getTempDir("keylist-export"), null);
        updateTitle(Translation.getText("task.export.keylist.title"));
        this.destFile = destFile;
        this.keyListNodes = keyListNodes;
    }

    @Override
    protected Void call() throws Exception {
        this.keyListNode = new KeyListNode();
        this.keyListNode.getChildren().addAll(keyListNodes);
        super.call();
        IOUtils.zipInto(this.destFile, this.directory, null);
        return null;
    }
}
