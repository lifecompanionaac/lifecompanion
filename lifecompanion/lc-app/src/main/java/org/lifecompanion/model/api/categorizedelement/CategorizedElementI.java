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
package org.lifecompanion.model.api.categorizedelement;

import javafx.beans.property.StringProperty;
import org.lifecompanion.model.api.configurationcomponent.UseInformationSerializableI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.framework.commons.SystemType;

/**
 * Represent a element in a category.<br>
 * This is use to be able to display both action and event in the same UI.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface CategorizedElementI<K extends SubCategoryI<?, ?>> extends XMLSerializable<IOContextI>, UseInformationSerializableI {

    // Class part : "Configuration"
    //========================================================================

    /**
     * @return the static description of this element.<br>
     * This is use in configuration and it should describe how this element is working.
     */
    public String getStaticDescription();

    /**
     * @return the description of this element, but that could vary with the parameters of this element.<br>
     * This is why this is a StringProperty and not just a string (because it can change with the element configuration)
     */
    public StringProperty variableDescriptionProperty();

    /**
     * @return the static name of this element
     */
    public String getName();

    /**
     * @return the icon path of this element
     */
    public String getConfigIconPath();

    /**
     * @return an array that contains every system that could use/execute this element.<br>
     * A empty array means that this element is compatible with every system.
     */
    public SystemType[] allowedSystemType();

    /**
     * @return the category of this element.
     */
    public K getCategory();

    /**
     * @return sort value for this element relative to the other element in the same subcategory.<br>
     * This doesn't have to be unique, it's used just as a comparator between other element.<br>
     * So if both element have the same, they could be displayed in a random order.
     */
    public int order();

    /**
     * @return true if the element could have some parameters to set.<br>
     * For example, an action "Write text" have the text to write as parameter, while a command "Next page" doesn't have any parameter.<br>
     * This will be use to know if the element is directly added to its parent, or if a configuration screen is displayed before.
     */
    public boolean isParameterizableElement();
    //========================================================================
}
