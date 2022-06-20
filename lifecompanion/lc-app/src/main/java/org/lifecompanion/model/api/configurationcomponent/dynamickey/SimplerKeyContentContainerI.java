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

package org.lifecompanion.model.api.configurationcomponent.dynamickey;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Color;
import org.lifecompanion.model.api.configurationcomponent.DuplicableComponentI;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.TreeIdentifiableComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.api.style.TextPosition;

/**
 * Represent a simpler component to use when key content should be dynamically updated and UI should be simpler to add it. <br>
 * It is for example used by keylist (simpler key list), sequences, calendar...<br>
 * It contains simpler key content to be bound to a key thank to key option.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface SimplerKeyContentContainerI extends ImageUseComponentI, XMLSerializable<IOContextI>, DuplicableComponentI, TreeIdentifiableComponentI {

    // CONTENT DISPLAYED
    //========================================================================

    /**
     * @return text displayed on the key
     */
    StringProperty textProperty();

    ObjectProperty<TextPosition> textPositionProperty();

    ObjectProperty<Color> backgroundColorProperty();

    ObjectProperty<Color> strokeColorProperty();

    //========================================================================

    // OTHER
    //========================================================================
    boolean isEmpty();
    //========================================================================

    // IMAGE
    //========================================================================
    void bindImageDisplayProperties(ImageUseComponentI imageUseComponent);
    //========================================================================
}
