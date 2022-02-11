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

import org.jdom2.Element;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.style.KeyCompStyleI;
import org.lifecompanion.model.api.style.StyleChangeUndo;
import org.lifecompanion.model.api.style.StylePropertyI;
import org.lifecompanion.framework.commons.fx.io.XMLCustomProperty;
import org.lifecompanion.framework.commons.fx.io.XMLIgnoreNullValue;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;


public class KeyCompStyle extends AbstractShapeCompStyle<KeyCompStyleI> implements KeyCompStyleI {

    @XMLCustomProperty(value = Boolean.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final StylePropertyI<Boolean> autoFontSize;

    public KeyCompStyle() {
        this.autoFontSize = new StyleProperty<>();
    }

    @Override
    public StylePropertyI<Boolean> autoFontSizeProperty() {
        return this.autoFontSize;
    }

    @Override
    protected void bindStyle(final KeyCompStyleI style) {
        super.bindStyle(style);
        this.bindP(KeyCompStyleI::autoFontSizeProperty, style);
    }

    @Override
    protected void unbindStyle() {
        super.unbindStyle();
        this.unbindP(KeyCompStyleI::autoFontSizeProperty, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StyleChangeUndo copyChanges(final KeyCompStyleI other, final boolean copyIfNull) {
        StyleChangeUndoImpl<KeyCompStyleI> undo = (AbstractStyle.StyleChangeUndoImpl<KeyCompStyleI>) super.copyChanges(
                other, copyIfNull);
        this.copyChange(undo, KeyCompStyleI::autoFontSizeProperty, other, copyIfNull);
        return undo;
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(KeyCompStyle.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(KeyCompStyle.class, this, node);
    }

    @Override
    protected String getNodeName() {
        return NODE_KEY_STYLE;
    }
}
