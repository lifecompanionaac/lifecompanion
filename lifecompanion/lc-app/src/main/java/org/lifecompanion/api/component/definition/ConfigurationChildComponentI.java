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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;

/**
 * A component that is a child of a configuration.<br>
 * This property should be set on every component.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface ConfigurationChildComponentI extends DuplicableComponentI, IdentifiableComponentI {

    /**
     * @return a property that contains the configuration parent of this element.<br>
     * Every component should have this property filled if they are inside a configuration.<br>
     */
    ObjectProperty<LCConfigurationI> configurationParentProperty();

    /**
     * @return A property that indicates if the component is removed from its configuration.
     * If the component if removed, this property should be always set to true. (this will fire a remove event to the previous parent configuration)
     */
    BooleanProperty removedProperty();

    /**
     * Method that should set the given value to its removed property, and that must dispatch the property value to its children.<br>
     * A good way to achieve this is to firstly set the value, and to call {@link #dispatchRemovedPropertyValue(boolean)} on every children.
     * This is use to deeply fire removed event on children.
     *
     * @param value remove value to set
     */
    void dispatchRemovedPropertyValue(boolean value);
}
