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
import org.lifecompanion.model.impl.constant.LCConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Task to save a key list in a directory.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class KeyListSavingTask extends AbstractSavingUtilsTask<Void> {
    private final static Logger LOGGER = LoggerFactory.getLogger(KeyListSavingTask.class);

    protected final File directory;
    protected KeyListNodeI keyListNode;

    public KeyListSavingTask(final File directoryP, final KeyListNodeI keyListNode) {
        super("task.save.keylist.title");
        this.directory = directoryP;
        this.keyListNode = keyListNode;
    }

    @Override
    protected Void call() throws Exception {
        KeyListSavingTask.LOGGER.info("Will save the key list to {}", this.directory);
        this.saveXmlSerializable(keyListNode, this.directory, LCConstant.KEYLIST_XML_NAME);
        KeyListSavingTask.LOGGER.info("Keylist successfully saved to {}", this.directory);
        return null;
    }
}
