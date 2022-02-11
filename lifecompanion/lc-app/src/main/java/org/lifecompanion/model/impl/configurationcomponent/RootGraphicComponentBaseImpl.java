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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.RootGraphicComponentI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

/**
 * Base implementation for {@link RootGraphicComponentI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class RootGraphicComponentBaseImpl extends DisplayableComponentBaseImpl implements RootGraphicComponentI {
    /**
     * Base properties
     */
    protected DoubleProperty x, y, width, height;

    /**
     * Base bool properties
     */
    protected transient BooleanProperty resizing, moving;

    /**
     * Initialize the base properties
     */
    protected RootGraphicComponentBaseImpl() {
        super();
        this.x = new SimpleDoubleProperty(this, "x");
        this.y = new SimpleDoubleProperty(this, "y");
        this.width = new SimpleDoubleProperty(this, "width");
        this.height = new SimpleDoubleProperty(this, "height");
        this.resizing = new SimpleBooleanProperty(this, "resizing");
        this.moving = new SimpleBooleanProperty(this, "moving");
    }

    // Class part : "Base getter"
    //========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty xProperty() {
        return this.x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty yProperty() {
        return this.y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty widthProperty() {
        return this.width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty heightProperty() {
        return this.height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty movingProperty() {
        return this.moving;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty resizingProperty() {
        return this.resizing;
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element content = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(RootGraphicComponentBaseImpl.class, this, content);
        return content;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(RootGraphicComponentBaseImpl.class, this, nodeP);
    }
    //========================================================================
}
