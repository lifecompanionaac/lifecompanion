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
package org.lifecompanion.base.data.component.simple;

import org.jdom2.Element;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import org.lifecompanion.api.component.definition.VirtualMouseDrawing;
import org.lifecompanion.api.component.definition.VirtualMouseParameterI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

/**
 * Represent the configuration virtual mouse parameter implementation.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VirtualMouseParameter implements VirtualMouseParameterI {

    @XMLGenericProperty(Color.class)
    private ObjectProperty<Color> mouseColor;

    @XMLGenericProperty(VirtualMouseDrawing.class)
    private ObjectProperty<VirtualMouseDrawing> mouseDrawing;

    private IntegerProperty mouseSize;
    private IntegerProperty mouseSpeed;

    public VirtualMouseParameter() {
        this.mouseColor = new SimpleObjectProperty<>(this, "mouseColor", Color.CADETBLUE);
        this.mouseDrawing = new SimpleObjectProperty<>(this, "mouseDrawing", VirtualMouseDrawing.SIMPLE_CIRCLE);
        this.mouseSize = new SimpleIntegerProperty(this, "mouseSize", 10);//4 -> 20 : 10 = ratio 1.0
        this.mouseSpeed = new SimpleIntegerProperty(this, "mouseSpeed", 5);// 1 -> 10 : 5 = average speed
    }

    // Class part : "Properties"
    //========================================================================
    @Override
    public ObjectProperty<Color> mouseColorProperty() {
        return this.mouseColor;
    }

    @Override
    public ObjectProperty<VirtualMouseDrawing> mouseDrawingProperty() {
        return this.mouseDrawing;
    }

    @Override
    public IntegerProperty mouseSizeProperty() {
        return this.mouseSize;
    }

    @Override
    public IntegerProperty mouseSpeedProperty() {
        return this.mouseSpeed;
    }
    //========================================================================

    // Class part : "IO"
    //========================================================================
    public static final String NODE_VIRTUAL_MOUSE_PARAMETER = "VirtualMouseParameter";

    @Override
    public Element serialize(final IOContextI context) {
        Element element = new Element(VirtualMouseParameter.NODE_VIRTUAL_MOUSE_PARAMETER);
        XMLObjectSerializer.serializeInto(VirtualMouseParameter.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(VirtualMouseParameter.class, this, node);
    }
    //========================================================================

}
