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

import org.lifecompanion.api.component.definition.simplercomp.UserActionSequencesI;
import org.lifecompanion.base.data.config.LCConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Task to save a key list in a directory.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserActionSequenceSavingTask extends AbstractSavingUtilsTask<Void> {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserActionSequenceSavingTask.class);

    protected final File directory;
    protected final UserActionSequencesI userActionSequences;

    public UserActionSequenceSavingTask(final File directoryP, UserActionSequencesI userActionSequences) {
        super("task.save.user.action.sequences.title");
        this.directory = directoryP;
        this.userActionSequences = userActionSequences;
    }

    @Override
    protected Void call() throws Exception {
        UserActionSequenceSavingTask.LOGGER.info("Will save the sequences to {}", this.directory);
        this.saveXmlSerializable(userActionSequences, this.directory, LCConstant.SEQUENCE_XML_NAME);
        UserActionSequenceSavingTask.LOGGER.info("Sequences successfully saved to {}", this.directory);
        return null;
    }
}
