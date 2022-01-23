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
package org.lifecompanion.base.data.style2.impl;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.style2.definition.AbstractShapeCompStyleI;
import org.lifecompanion.api.style2.definition.StyleChangeUndo;
import org.lifecompanion.api.style2.property.definition.IntegerStylePropertyI;
import org.lifecompanion.api.style2.property.definition.StylePropertyI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.style2.property.impl.IntegerStyleProperty;
import org.lifecompanion.base.data.style2.property.impl.StyleProperty;
import org.lifecompanion.base.data.style2.property.impl.StylePropertyConverter;
import org.lifecompanion.framework.commons.fx.io.XMLCustomProperty;
import org.lifecompanion.framework.commons.fx.io.XMLIgnoreNullValue;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

public abstract class AbstractShapeCompStyle<T extends AbstractShapeCompStyleI<?>> extends AbstractStyle<T> implements AbstractShapeCompStyleI<T> {
    @XMLCustomProperty(value = Color.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final StylePropertyI<Color> strokeColor;

    @XMLCustomProperty(value = Color.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final StylePropertyI<Color> backgroundColor;

    @XMLCustomProperty(value = Integer.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final IntegerStylePropertyI shapeRadius;

    @XMLCustomProperty(value = Integer.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final IntegerStylePropertyI strokeSize;

    private transient StringProperty cssStyle;

    public AbstractShapeCompStyle() {
        this.strokeColor = new StyleProperty<>();
        this.backgroundColor = new StyleProperty<>();
        this.shapeRadius = new IntegerStyleProperty();
        this.strokeSize = new IntegerStyleProperty();
        this.cssStyle = new SimpleStringProperty();
        this.createBindings();
    }

    private void createBindings() {
        InvalidationListener updateCssStyleListener = (obs) -> {
            this.updateCssStyle();
        };
        this.backgroundColor.value().addListener(updateCssStyleListener);
        this.strokeColor.value().addListener(updateCssStyleListener);
        this.strokeSize.value().addListener(updateCssStyleListener);
        this.shapeRadius.value().addListener(updateCssStyleListener);
    }

    private void updateCssStyle() {
        Integer shapeRadiusV = LCUtils.nullToZeroInt(this.shapeRadius.value().getValue());
        Integer strokeSizeV = LCUtils.nullToZeroInt(this.strokeSize.value().getValue());
        this.cssStyle.set(new StringBuilder(255)//
                .append("-fx-background-color:").append(LCUtils.toCssColor(this.backgroundColor.value().getValue())).append(";") //
                .append("-fx-border-color:").append(LCUtils.toCssColor(this.strokeColor.value().getValue())).append(";")//
                .append("-fx-border-width:").append(strokeSizeV).append("px;")//
                .append("-fx-border-radius:").append(shapeRadiusV).append("px;")//
                .append("-fx-background-radius:").append(Math.max(shapeRadiusV - strokeSizeV, 0)).append("px;")//
                .append("-fx-background-insets: ").append(strokeSizeV - 1.0).append("px;")//
                .append("-fx-border-style: solid inside;")//
                .toString());
    }

    @Override
    public StylePropertyI<Color> strokeColorProperty() {
        return this.strokeColor;
    }

    @Override
    public StylePropertyI<Color> backgroundColorProperty() {
        return this.backgroundColor;
    }

    @Override
    public IntegerStylePropertyI shapeRadiusProperty() {
        return this.shapeRadius;
    }

    @Override
    public IntegerStylePropertyI strokeSizeProperty() {
        return this.strokeSize;
    }

    @Override
    public ReadOnlyStringProperty cssStyleProperty() {
        return this.cssStyle;
    }

    @Override
    protected void bindStyle(final T style) {
        this.bindP(AbstractShapeCompStyleI::strokeColorProperty, style);
        this.bindP(AbstractShapeCompStyleI::backgroundColorProperty, style);
        this.bindP(AbstractShapeCompStyleI::shapeRadiusProperty, style);
        this.bindP(AbstractShapeCompStyleI::strokeSizeProperty, style);
    }

    @Override
    protected void unbindStyle() {
        this.unbindP(AbstractShapeCompStyleI::strokeColorProperty, null);
        this.unbindP(AbstractShapeCompStyleI::backgroundColorProperty, null);
        this.unbindP(AbstractShapeCompStyleI::shapeRadiusProperty, null);
        this.unbindP(AbstractShapeCompStyleI::strokeSizeProperty, null);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public StyleChangeUndo copyChanges(final T other, final boolean copyIfNull) {
        StyleChangeUndoImpl<T> undo = (org.lifecompanion.base.data.style2.impl.AbstractStyle.StyleChangeUndoImpl<T>) super.copyChanges(other,
                copyIfNull);
        this.copyChange(undo, AbstractShapeCompStyleI::strokeColorProperty, other, copyIfNull);
        this.copyChange(undo, AbstractShapeCompStyleI::backgroundColorProperty, other, copyIfNull);
        this.copyChange(undo, AbstractShapeCompStyleI::shapeRadiusProperty, other, copyIfNull);
        this.copyChange(undo, AbstractShapeCompStyleI::strokeSizeProperty, other, copyIfNull);
        return undo;
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(AbstractShapeCompStyle.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(AbstractShapeCompStyle.class, this, node);
    }
}
