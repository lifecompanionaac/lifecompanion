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

import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequencesI;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.UserActionSequences;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public abstract class AbstractUserSequenceLoadingTask<T> extends AbstractLoadUtilsTask<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUserSequenceLoadingTask.class);

    public AbstractUserSequenceLoadingTask() {
        super("task.load.user.sequence.title");
    }

    protected final UserActionSequencesI loadNodeFromDirectory(File directory) throws Exception {
        UserActionSequencesI node = new UserActionSequences();
        if (directory.exists() && new File(directory + File.separator + LCConstant.SEQUENCE_XML_NAME).exists()) {
            this.loadElementIn(node, directory, LCConstant.SEQUENCE_XML_NAME);
        } else {
            LOGGER.info("Didn't load sequence for configuration because there is no sequence directory in {}", directory);
        }
        return node;
    }
}
