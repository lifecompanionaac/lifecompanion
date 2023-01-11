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
package org.lifecompanion.model.api.configurationcomponent.keyoption;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.layout.Region;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.selectionmode.SelectionModeParameterI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;

/**
 * Represent a option that can be set on a key to restrict its usage to a specific usecase.<br>
 * For example : a prediction option to allow key to display word predictions.<br>
 * Note that there is one instance of KeyOption per key, this means that the {@link #detachFrom(GridPartKeyComponentI)} will always be called on the previous key that was passed to {@link #attachTo(GridPartKeyComponentI)}<br>
 * Be always attentive to the fact that the key option is loaded and then attached, so if for example you modify key in attach, you could duplicate some informations (e.g. : add an action in attach will cause the added to be duplicated on each load,
 * because the added action is serialiazed).
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface KeyOptionI extends XMLSerializable<IOContextI> {

    /**
     * @return a property that disable the ability of setting the text content.
     */
    ReadOnlyBooleanProperty disableTextContentProperty();

    /**
     * @return a property that disable the ability of setting the image content
     */
    ReadOnlyBooleanProperty disableImageProperty();

    /**
     * @return a property that should be true if the associated key should be considered as empty.<br>
     * This mean that selection mode will ignore this key if the {@link SelectionModeParameterI#skipEmptyComponentProperty()} is true
     */
    ReadOnlyBooleanProperty considerKeyEmptyProperty();

    /**
     * @return a property that limit the text length. -1 means that the text length should be limited
     * Note that this property is used only if the {@link #disableTextContentProperty()} return false
     */
    ReadOnlyIntegerProperty maxTextLengthProperty();

    /**
     * Attach the option to a given key.<br>
     * The attach method will be called each time to option is set the to key.<br>
     * For example, this method could add a specific action to the key.
     *
     * @param key the key that this option is attached
     */
    void attachTo(GridPartKeyComponentI key);

    /**
     * Detach the option from a given key.<br>
     * Will be called if we disable the current option on a key.<br>
     * For example, this method could delete a specific action from the key.
     *
     * @param key the key that this option is detached
     */
    void detachFrom(GridPartKeyComponentI key);

    /**
     * Method called once the key is attached and this key option wasn't used before.</br>
     * This method is called only if a key of the specific this type is created, or if the keyoption is changed from an existing key.</br>
     * <strong>This is not called if {@link KeyOptionI} is attached after {@link #deserialize(org.jdom2.Element, IOContextI)}</strong>
     */
    void keyNewlyAttached();

    /**
     * @return the key that this option is attached to.<br>
     * Should always be filled with the last key passed to {@link #attachTo(GridPartKeyComponentI)}, or null if {@link #detachFrom(GridPartKeyComponentI)} was called
     */
    ObjectProperty<GridPartKeyComponentI> attachedKeyProperty();

    /**
     * @return a new instance of this option.<br>
     * The instance should be different that this instance, but there is not need to copy this instance values.
     */
    KeyOptionI getNewOptionInstance();

    /**
     * @return a name for this option to help user choice
     */
    String getOptionName();

    /**
     * @return a description for this option to help user choice
     */
    String getOptionDescription();

    /**
     * The key option icon will be displayed on the top left corner of a key.
     *
     * @return a icon url for this option.<br>
     * Can return null if this option doesn't want to have a specific icon.
     */
    String getIconUrl();

    String NODE_KEY_OPTION = "KeyOption";

    ObjectProperty<Region> keyViewAddedNodeProperty();
}
