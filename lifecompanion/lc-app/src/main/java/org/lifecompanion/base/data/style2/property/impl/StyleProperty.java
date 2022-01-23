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
package org.lifecompanion.base.data.style2.property.impl;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import org.lifecompanion.api.style2.property.definition.StylePropertyI;

public class StyleProperty<T> implements StylePropertyI<T> {

    private final ObjectProperty<T> selected;
    private final ObjectProperty<T> forced;
    private final ObjectProperty<T> parent;
    private final ObjectProperty<T> value;

    public StyleProperty() {
        this.selected = new SimpleObjectProperty<>();
        this.forced = new SimpleObjectProperty<>();
        this.parent = new SimpleObjectProperty<>();
        this.value = new SimpleObjectProperty<>();
        this.value.bind(Bindings.createObjectBinding(() -> {
            T f = this.forced.get();
            T s = this.selected.get();
            T p = this.parent.get();
            return f != null ? f : (s != null ? s : p);
        }, this.selected, this.parent, this.forced));
    }

    @Override
    public Property<T> forced() {
        return forced;
    }

    @Override
    public Property<T> selected() {
        return this.selected;
    }

    @Override
    public Property<T> parent() {
        return this.parent;
    }

    @Override
    public Property<T> value() {
        return this.value;
    }

    @Override
    public BooleanBinding isSelectedNotNull() {
        return this.selected.isNotNull();
    }

    @Override
    public String toString() {
        return "StyleProperty{" +
                "selected=" + selected.get() +
                ", forced=" + forced.get() +
                ", value=" + value.get() +
                '}';
    }
}
