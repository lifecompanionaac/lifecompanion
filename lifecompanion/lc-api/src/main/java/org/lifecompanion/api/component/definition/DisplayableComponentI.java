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

import javafx.beans.property.ReadOnlyBooleanProperty;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.io.XMLSerializable;
import org.lifecompanion.api.ui.ComponentViewI;
import org.lifecompanion.api.ui.ViewProviderI;
import org.lifecompanion.framework.utils.Pair;

import java.util.function.Function;

/**
 * This interface represent the base for each component that can be displayed in a configuration.<br>
 * Each configuration graphic element must implements this interface.<br>
 * Each displayable component can be saved in XML.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface DisplayableComponentI extends XMLSerializable<IOContextI>, TreeDisplayableComponentI, IdentifiableComponentI,
        ConfigurationChildComponentI, UserNamedComponentI, UseInformationSerializableI {

    /**
     * The display for this component.<br>
     * The component should call the current {@link ViewProviderI} to get this display.<br>
     * A display musn't change for a component and must be unique.
     *
     * @return the component view.
     */
    ComponentViewI<?> getDisplay(ViewProviderI viewProvider, boolean useCache);

    public void clearViewCache();

    //    /**
    //     * To clear cached display on this component.
    //     */
    //    void clearCachedDisplay();
    //
    //    boolean isDisplayInitialized();

    /**
     * @return a type name for this component that can be shown to user
     */
    String getDisplayableTypeName();

    /**
     * This method must show this component to the top of other component.<br>
     * This method must handle the case of component into other.<br>
     * E.g. : a component into a stack must become the displayed component. A recursive call to parent is a good way to achieve this.
     */
    void showToFront(ViewProviderI viewProvider, boolean useCache);//FIXME ; inject ViewProviderType here (or view provider if the view wasn't initialized yet ?)

    /**
     * @return a property that indicate if this component is currently displayed in stack (or in configuration view), this is useful for stack child that doesn't know the direct stack child of their stack parent.<br>
     * A basic use case of this property could be for a stack child, to know if it is currently displayed.<br>
     * The default value of this property will always be true, the value should be false only if the component is not displayed because of the configuration logic.<br>
     * Changing this property doesn't have any effect.
     */
    ReadOnlyBooleanProperty displayedProperty();

    /**
     * To dispatch a value of the displayed property to this component and its children.
     *
     * @param displayed the displayed property to dispatch
     */
    void dispatchDisplayedProperty(boolean displayed);
}
