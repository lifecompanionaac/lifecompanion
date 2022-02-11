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

package org.lifecompanion.model.api.io;

import java.io.File;

public interface ProfileIOContextI {
    /**
     * Directory where associated XML file is saved/loaded.<br>
     *
     * @return the directory where the xml file will be saved/loaded
     */
    File getDirectory();

    /**
     * @return true if the profile should be fully loaded (with its styles etc..) or false if this should only load description information.
     */
    boolean isFullLoading();
}
