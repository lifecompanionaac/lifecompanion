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

import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;

/**
 * Represent a component that can be viewed as a tree node.<br>
 * This is the case for all current component.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface TreeDisplayableComponentI extends UserNamedComponentI, IdentifiableComponentI, TreeIdentifiableComponentI {

    /**
     * The returned {@link ObservableList} must never change, its content can change but on the same component, the list must always be the same.<br>
     *
     * @return a observable list that contains every children of this node.<br>
     * Could return null if {@link #isNodeLeaf()} return true
     */
    <T extends TreeDisplayableComponentI> ObservableList<T> getChildrenNode();

    /**
     * @return a boolean that indicate if this node could have children.<br>
     * This boolean musn't change on two component of the same type.
     */
    boolean isNodeLeaf();

    /**
     * @return the type of this node.<br>
     * Musn't return null
     */
    TreeDisplayableType getNodeType();
}
