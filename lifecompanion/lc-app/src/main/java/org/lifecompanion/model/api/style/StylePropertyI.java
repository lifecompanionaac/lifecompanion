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

package org.lifecompanion.model.api.style;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

/**
 * Represent a property for a style.<br>
 * Each property on styles use inheritance to compute a value : a {@link #parent()} value is computed from the parent component {@link #value()}, and a {@link #selected()} value can be set by user.<br>
 * The {@link #value()} is computed : it uses the selected value if it's not null, or the parent value.<br>
 * This allows user to set values on each computed with its specific choice.
 *
 * @param <T> the property type
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface StylePropertyI<T> {


    /**
     * @return the value selected by system : most of the time null to use the {@link #selected()} or {@link #parent()}
     */
    Property<T> forced();

    /**
     * @return the value selected by user : most of the time null to use the parent value
     */
    Property<T> selected();


    /**
     * @return true if the selected value is not null
     */
    BooleanBinding isSelectedNotNull();

    /**
     * @return parent value, used if the selected value is null.<br>
     * Parent value is based on parent value if the style is the same than parent, or on style value, if the style is different.
     */
    Property<T> parent();

    /**
     * @return "real" value for this property : computed with {@link #forced()}, {@link #selected()} and {@link #parent()} values.
     */
    ReadOnlyProperty<T> value();
}
