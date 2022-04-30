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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import org.lifecompanion.model.api.style.GridStyleUserI;
import org.lifecompanion.model.api.style.KeyStyleUserI;

/**
 * Interface that represent a component that keep {@link GridComponentI} as child.<br>
 * It keeps component in a stack, and display one component in the same time : the component on the top.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface StackComponentI extends DisplayableComponentI, TreeDisplayableComponentI, ConfigurationChildComponentI, GridStyleUserI, KeyStyleUserI, UserNamedComponentI {
    /**
     * @return the list of all component inside this component (all stack component)
     */
    ObservableList<GridComponentI> getComponentList();

    /**
     * @return the property that contains the current component on top of the stack : displayed component
     */
    ObjectProperty<GridComponentI> displayedComponentProperty();

    /**
     * To know if a given component is a direct child of this stack.
     *
     * @param component the component that could be a direct child
     * @return true if this child is in {@link #getComponentList()}
     */
    boolean isDirectStackChild(GridComponentI component);

    /**
     * Display the next child in stack list
     * <strong>Because this method in use mode doesn't handle the different selection mode, you should never call directly this method on stack in use mode</strong>
     */
    void displayNextForEditMode();

    /**
     * Display the previous child in stack list
     * <strong>Because this method in use mode doesn't handle the different selection mode, you should never call directly this method on stack in use mode</strong>
     */
    void displayPreviousForEditMode();

    void displayComponentByIdForEditMode(String componentId);

    /**
     * @return the component that comes after the currently displayed component, will return null if the next is not possible
     */
    GridComponentI getNextComponent();

    /**
     * @return the component that comes before the currently displayed component, will return null if the previous is not possible
     */
    GridComponentI getPreviousComponent();

    /**
     * Shift the given component at a higher place in the list (when possible, e.g. when component is not the first in list)
     *
     * @param component the component to shift down
     */
    void shiftUpComponent(GridComponentI component);

    /**
     * Shift the given component at a lower place in the list (when possible, e.g. when component is not the last in list)
     *
     * @param component the component to shift down
     */
    void shiftDownComponent(GridComponentI component);

    /**
     * @return must return the property that can be use to bind the displayed component width to this component width.<br>
     * This can vary with the type of component.
     */
    DoubleProperty bindableDisplayedWidthProperty();

    /**
     * @return must return the property that can be use to bind the displayed component height to this component height.<br>
     * This can vary with the type of component.
     */
    DoubleProperty bindableDisplayedHeightProperty();

    /**
     * @return a property that is true when calling {@link #displayPreviousForEditMode()} will display the previous component (ie action is possible)
     */
    BooleanProperty nextPossibleProperty();

    /**
     * @return a property that is true when calling {@link #displayNextForEditMode()} will display the next component (ie action is possible)
     */
    BooleanProperty previousPossibleProperty();

    /**
     * This method must create the base listener on component list and on displayed component.<br>
     * The default behavior of this method will bind the first list add on displayed, will remove displayed on list remove.<br>
     * This will also bind layout properties of displayed on this component properties.<br>
     * <strong>This method should be called after properties and list initialization</strong>
     */
    void initStackComponent();

    /**
     * Replace a component of the stack with another component
     *
     * @param toReplace the grid to replace
     * @param component the component to use to replace the component
     */
    void replace(GridComponentI toReplace, GridComponentI component);
}
