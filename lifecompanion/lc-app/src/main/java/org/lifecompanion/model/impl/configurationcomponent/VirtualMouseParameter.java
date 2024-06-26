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
package org.lifecompanion.model.impl.configurationcomponent;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.DirectionalMouseDrawing;
import org.lifecompanion.model.api.configurationcomponent.VirtualMouseType;
import org.lifecompanion.model.api.configurationcomponent.VirtualMouseParameterI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

/**
 * Represent the configuration virtual mouse parameter implementation.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VirtualMouseParameter implements VirtualMouseParameterI {

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> mouseColor;

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> mouseStrokeColor;

    @XMLGenericProperty(VirtualMouseType.class)
    private final ObjectProperty<VirtualMouseType> virtualMouseType;

    @XMLGenericProperty(DirectionalMouseDrawing.class)
    private final ObjectProperty<DirectionalMouseDrawing> directionalMouseDrawing;

    @XMLGenericProperty(Boolean.class)
    private final ObjectProperty<Boolean> mouseAccuracy;

    private final IntegerProperty mouseSize;
    private final IntegerProperty mouseSpeed;
    private final IntegerProperty mouseMaxLoop;

    public VirtualMouseParameter() {
        this.mouseColor = new SimpleObjectProperty<>(Color.CADETBLUE);
        this.mouseStrokeColor = new SimpleObjectProperty<>(LCGraphicStyle.LC_BLACK);
        this.virtualMouseType = new SimpleObjectProperty<>(VirtualMouseType.DIRECTIONAL);
        this.directionalMouseDrawing = new SimpleObjectProperty<>(DirectionalMouseDrawing.SIMPLE_CIRCLE);
        this.mouseAccuracy = new SimpleObjectProperty<>(false);
        this.mouseSize = new SimpleIntegerProperty(10);
        this.mouseSpeed = new SimpleIntegerProperty(5);
        this.mouseMaxLoop = new SimpleIntegerProperty(3);
    }

    // Class part : "Properties"
    //========================================================================
    @Override
    public ObjectProperty<Color> mouseColorProperty() {
        return this.mouseColor;
    }

    @Override
    public ObjectProperty<Color> mouseStrokeColorProperty() {
        return mouseStrokeColor;
    }

    @Override
    public ObjectProperty<VirtualMouseType> virtualMouseTypeProperty() {
        return this.virtualMouseType;
    }

    @Override
    public ObjectProperty<DirectionalMouseDrawing> directionalMouseDrawingProperty() {
        return this.directionalMouseDrawing;
    }

    @Override
    public IntegerProperty mouseSizeProperty() {
        return this.mouseSize;
    }

    @Override
    public IntegerProperty mouseSpeedProperty() {
        return this.mouseSpeed;
    }
    @Override
    public ObjectProperty<Boolean> mouseAccuracyProperty() {
        return this.mouseAccuracy;
    }

    @Override
    public IntegerProperty mouseMaxLoopProperty() {
        return this.mouseMaxLoop;
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
