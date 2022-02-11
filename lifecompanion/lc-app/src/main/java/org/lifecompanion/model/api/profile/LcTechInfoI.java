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
package org.lifecompanion.model.api.profile;

import java.io.File;

import org.lifecompanion.model.api.io.XMLSerializable;

/**
 * Represent the technical information about a configuration.<br>
 * It can be used in the future to detect any incompatibilities between version, etc...
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface LcTechInfoI extends XMLSerializable<File> {

    /**
     * Should use the current software information to update this tech information.<br>
     * Most of the time, this method will be called just before saving the tech information.
     */
    void updateInformation();


    /**
     * @return LifeCompanion update version
     */
    String getVersion();

    /**
     * @return the system on the configuration was saved
     */
    String getSystemTypeCode();
}
