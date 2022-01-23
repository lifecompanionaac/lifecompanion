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
package org.lifecompanion.base.data.component.baseimpl;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.*;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;

/**
 * Base class for sub-class of {@link DisplayableComponentI}.<br>
 * A component that extends this class is equals to another only if there two ids are the same.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class DisplayableComponentBaseImpl extends CoreDisplayableComponentBaseImpl
        implements SelectableComponentI, OverableComponentI, IdentifiableComponentI, TreeDisplayableComponentI, ConfigurationChildComponentI {

    /**
     * If the component is selected or not
     */
    protected transient BooleanProperty selected;

    /**
     * If we show the selection or not
     */
    protected transient BooleanProperty showSelected;

    /**
     * If the component will be selected by next clic
     */
    protected transient BooleanProperty possibleSelected;

    /**
     * If the component can be over
     */
    protected transient BooleanProperty over;

    /**
     * Create the base for a displayable component.<br>
     * Initialize its ID and its properties.
     */
    public DisplayableComponentBaseImpl() {
        this.selected = new SimpleBooleanProperty(this, "selected", false);
        this.showSelected = new SimpleBooleanProperty(this, "showSelected", false);
        this.possibleSelected = new SimpleBooleanProperty(this, "possibleSelected", false);
        this.over = new SimpleBooleanProperty(this, "over", false);
    }

    // Class part : "Base method"
    //========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty overProperty() {
        return this.over;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty selectedProperty() {
        return this.selected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty showSelectedProperty() {
        return this.showSelected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty showPossibleSelectedProperty() {
        return this.possibleSelected;
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================

    @Override
    public Element serialize(final IOContextI contextP) {
        return super.serialize(contextP);
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
    }
    //========================================================================
}
