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
import org.lifecompanion.framework.commons.fx.io.XMLCustomProperty;
import org.lifecompanion.framework.commons.fx.io.XMLIgnoreNullValue;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.style.GridCompStyleI;
import org.lifecompanion.model.api.style.IntegerStylePropertyI;
import org.lifecompanion.model.api.style.StyleChangeUndo;
import org.lifecompanion.model.impl.exception.LCException;

public class GridCompStyle extends AbstractShapeCompStyle<GridCompStyleI> implements GridCompStyleI {

    @XMLCustomProperty(value = Integer.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final IntegerStylePropertyI vGap;

    @XMLCustomProperty(value = Integer.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final IntegerStylePropertyI hGap;

    public GridCompStyle() {
        this.vGap = new IntegerStyleProperty();
        this.hGap = new IntegerStyleProperty();
    }

    @Override
    protected String getNodeName() {
        return NODE_SHAPE_STYLE_GRID;
    }

    @Override
    public IntegerStylePropertyI vGapProperty() {
        return vGap;
    }

    @Override
    public IntegerStylePropertyI hGapProperty() {
        return hGap;
    }

    @Override
    protected void bindStyle(final GridCompStyleI style) {
        super.bindStyle(style);
        this.bindP(GridCompStyleI::vGapProperty, style);
        this.bindP(GridCompStyleI::hGapProperty, style);
    }

    @Override
    protected void unbindStyle() {
        super.unbindStyle();
        this.unbindP(GridCompStyleI::vGapProperty, null);
        this.unbindP(GridCompStyleI::hGapProperty, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StyleChangeUndo copyChanges(final GridCompStyleI other, final boolean copyIfNull) {
        StyleChangeUndoImpl<GridCompStyleI> undo = (AbstractStyle.StyleChangeUndoImpl<GridCompStyleI>) super.copyChanges(other, copyIfNull);
        this.copyChange(undo, GridCompStyleI::vGapProperty, other, copyIfNull);
        this.copyChange(undo, GridCompStyleI::hGapProperty, other, copyIfNull);
        return undo;
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(GridCompStyle.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(GridCompStyle.class, this, node);
    }
}
