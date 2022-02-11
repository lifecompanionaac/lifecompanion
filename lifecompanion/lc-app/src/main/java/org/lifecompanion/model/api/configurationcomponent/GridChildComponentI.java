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

package org.lifecompanion.model.api.configurationcomponent;

import javafx.beans.property.ObjectProperty;

import java.util.function.Consumer;

/**
 * Define a component that can be added to a grid.<br>
 * This component can be a simple leaf element like a key, but can also be a more complex element like another grid.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface GridChildComponentI {
    /**
     * @return the child level, return 0 if this child doesn't have parent, if this child have a parent, must return the distance from top level component.
     */
    int getLevel();

    /**
     * @return true if this child component is not at root level.
     */
    boolean isParentExist();

    /**
     * @return the parent of this child component, can return null if {@link #isParentExist()} return false
     */
    ObjectProperty<GridComponentI> gridParentProperty();

    /**
     * To execute an action on all this grid key and children.<br>
     * This will make a recursive call to grid children.
     *
     * @param action the action to execute, shouldn't be null
     */
    void forEachKeys(Consumer<GridPartKeyComponentI> action);
}
