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

import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListNode;
import org.lifecompanion.base.data.config.LCConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public abstract class AbstractKeyListLoadingTask<T> extends AbstractLoadUtilsTask<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKeyListLoadingTask.class);

    public AbstractKeyListLoadingTask() {
        super("task.load.keylist.title");
    }

    protected final KeyListNodeI loadNodeFromDirectory(File directory) throws Exception {
        KeyListNodeI node = new KeyListNode();
        if (directory.exists() && new File(directory + File.separator + LCConstant.KEYLIST_XML_NAME).exists()) {
            this.loadElementIn(node, directory, LCConstant.KEYLIST_XML_NAME);
        } else {
            LOGGER.info("Didn't load keylist for configuration because there is no keylist directory in {}", directory);
        }
        return node;
    }
}
