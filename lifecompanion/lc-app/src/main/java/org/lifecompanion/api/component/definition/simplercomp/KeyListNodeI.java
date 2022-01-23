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

package org.lifecompanion.api.component.definition.simplercomp;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.function.Consumer;

/**
 * Represent the common content between key list keys and categories.<br>
 * The content representation is then used by the categories themselves and the keys.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
// TODO : UseActionTriggerComponentI to be able to add custom action on keys
public interface KeyListNodeI extends SimplerKeyActionContainerI {
    StringProperty linkedNodeIdProperty();

    /**
     * @return true if this node represent a leaf (then is {@link #getChildren()} can return null)
     */
    boolean isLeafNode();

    /**
     * @return true if this node represent a link (then is {@link #getChildren()} can return null)
     */
    boolean isLinkNode();

    ObservableList<KeyListNodeI> getChildren();

    ReadOnlyObjectProperty<KeyListNodeI> parentProperty();

    ReadOnlyIntegerProperty levelProperty();

    void traverseTreeToBottom(Consumer<KeyListNodeI> nodeConsumer);

    String getHumanReadableText();
}
