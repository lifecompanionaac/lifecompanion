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
package org.lifecompanion.model.impl.style;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.style.StyleChangeUndo;
import org.lifecompanion.model.api.style.StyleI;
import org.lifecompanion.model.api.style.StylePropertyI;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractStyle<T extends StyleI<?>> implements StyleI<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractStyle.class);

    private final ObjectProperty<T> parentComponentStyle;

    public AbstractStyle() {
        this.parentComponentStyle = new SimpleObjectProperty<>();
        this.parentComponentStyle.addListener((obs, ov, nv) -> {
            if (nv != null) {
                bindStyle(nv);
            } else {
                unbindStyle();
            }
        });
    }

    @Override
    public ObjectProperty<T> parentComponentStyleProperty() {
        return this.parentComponentStyle;
    }

    // Class part : "Abstract"
    //========================================================================
    protected abstract void bindStyle(T style);

    protected abstract void unbindStyle();
    //========================================================================

    @SuppressWarnings({"unchecked"})
    protected <K> void bindP(final Function<T, StylePropertyI<K>> getter, final T style) {
        getter.apply((T) this).parent().bind(getter.apply(style).value());
    }

    @SuppressWarnings("unchecked")
    protected <K> void unbindP(final Function<T, StylePropertyI<K>> getter, final T style) {
        getter.apply((T) this).parent().unbind();
    }

    // Class part : "Style changes"
    //========================================================================
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public StyleChangeUndo copyChanges(final T other, final boolean copyIfNull) {
        return new StyleChangeUndoImpl(this);
    }

    @SuppressWarnings("unchecked")
    protected <K> void copyChange(final StyleChangeUndoImpl<T> undo, final Function<T, StylePropertyI<K>> getter, final T other,
                                  final boolean copyIfNull) {
        K selectedValue = other != null ? getter.apply(other).selected().getValue() : null;
        if (selectedValue != null || copyIfNull) {
            undo.changes.add(new StyleChange<>(getter, getter.apply((T) this).selected().getValue()));
            getter.apply((T) this).selected().setValue(selectedValue);
        }
    }

    protected static class StyleChangeUndoImpl<T> implements StyleChangeUndo {
        private final T style;
        private final List<StyleChange<T, ?>> changes = new ArrayList<>();

        public StyleChangeUndoImpl(final T style) {
            super();
            this.style = style;
        }

        @Override
        public void undo() {
            for (StyleChange<T, ?> styleChange : this.changes) {
                styleChange.undo(this.style);
            }
        }
    }

    protected static class StyleChange<T, K> {
        Function<T, StylePropertyI<K>> getter;
        K previousValue;

        public StyleChange(final Function<T, StylePropertyI<K>> getter, final K previousValue) {
            this.getter = getter;
            this.previousValue = previousValue;
        }

        public void undo(final T style) {
            this.getter.apply(style).selected().setValue(this.previousValue);

        }

    }
    //========================================================================

    // Class part : "IO"
    //========================================================================
    @Override
    public Element serialize(final IOContextI context) {
        return XMLObjectSerializer.serializeInto(AbstractStyle.class, this, new Element(getNodeName()));
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(AbstractStyle.class, this, node);
    }

    protected abstract String getNodeName();
    //========================================================================

}
