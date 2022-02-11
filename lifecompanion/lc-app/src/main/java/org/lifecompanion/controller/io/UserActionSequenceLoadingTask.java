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

import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequencesI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class UserActionSequenceLoadingTask extends AbstractUserSequenceLoadingTask<UserActionSequencesI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserActionSequenceLoadingTask.class);

    private final File directory;

    public UserActionSequenceLoadingTask(final File directory) {
        super();
        this.directory = directory;
    }

    @Override
    protected UserActionSequencesI call() throws Exception {
        return this.loadNodeFromDirectory(directory);
    }
}
