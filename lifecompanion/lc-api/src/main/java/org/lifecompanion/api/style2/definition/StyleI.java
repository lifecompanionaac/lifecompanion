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
package org.lifecompanion.api.style2.definition;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.io.XMLSerializable;
import org.lifecompanion.api.style2.property.definition.StylePropertyI;

/**
 * Represent a style associated to a component.<br>
 * Component can have different kind of style, each style subclass define its own property to style a component.<br>
 * Each {@link StylePropertyI} is based on an inheritance concept : a default value is set, but user can override it.
 *
 * @param <T> represent the style concrete class (type)
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface StyleI<T extends StyleI<?>> extends XMLSerializable<IOContextI> {

    /**
     * @return this component parent component parent style (not the selected value, but the real style).<br>
     * This is useful if this component doesn't override the selected style, to display the style value with {@link #styleProperty()} and to use the parent override values.
     */
    ObjectProperty<T> parentComponentStyleProperty();

    /**
     * To copy the style element from another style to this style.<br>
     * Will copy null only if enabled.<br>
     * The returned {@link StyleChangeUndo} allows to undo the changes done in copy.
     *
     * @param other      the style to copy
     * @param copyIfNull true if we want to also copy the null selected values of the other style
     * @return a change undo, that allow you to undo the style change done in the copy.
     */
    StyleChangeUndo copyChanges(T other, boolean copyIfNull);
}
