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
package org.lifecompanion.api.component.definition;

/**
 * Interface that define a component that can be identified.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface IdentifiableComponentI {
    /**
     * @return a unique ID for this component.<br>
     * The ID must be set in constructor and musn't change after.<br>
     * A copied object must have a different ID.
     */
    String getID();

    /**
     * This method should generate a new unique identifier for this component.<br>
     * The basic component will call this method in its constructor.<br>
     * The implementation of this method can choose to do nothing if the identifiable component id must never be generated.<br>
     * <strong>Note : </strong> this method is just useful while duplicating object.
     *
     * @return the new generated id
     */
    String generateID();
}
