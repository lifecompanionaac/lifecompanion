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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.TreeDisplayableType;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.style2.definition.KeyCompStyleI;
import org.lifecompanion.api.style2.definition.ShapeCompStyleI;
import org.lifecompanion.api.style2.definition.TextCompStyleI;
import org.lifecompanion.base.data.component.baseimpl.RootGraphicComponentBaseImpl;
import org.lifecompanion.base.data.component.baseimpl.StackComponentBaseImpl;
import org.lifecompanion.base.data.component.baseimpl.wrapper.StackComponentPropertyWrapper;
import org.lifecompanion.base.data.style2.impl.GridShapeCompStyle;
import org.lifecompanion.base.data.style2.impl.KeyCompStyle;
import org.lifecompanion.base.data.style2.impl.KeyTextCompStyle;
import org.lifecompanion.base.data.style2.impl.StyleSerialializer;

/**
 * This class represent a root component that can stack a list of grid part.<br>
 * The stacked grid part are taking all the available space of this component.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class StackComponent extends RootGraphicComponentBaseImpl implements StackComponentBaseImpl {

    /**
     * Property wrapper for stack component
     */
    protected StackComponentPropertyWrapper stackComponentPropertyWrapper;

    private final ShapeCompStyleI gridShapeStyle;
    private final KeyCompStyleI keyStyle;
    private final TextCompStyleI keyTextStyle;

    /**
     * Create a grid stack
     */
    public StackComponent() {
        super();
        this.gridShapeStyle = new GridShapeCompStyle();
        this.keyStyle = new KeyCompStyle();
        this.keyTextStyle = new KeyTextCompStyle();
        this.stackComponentPropertyWrapper = new StackComponentPropertyWrapper(this);
        this.configurationParent.addListener((obs, ov, nv) -> {
            if (nv != null) {
                gridShapeStyle.parentComponentStyleProperty().set(nv.getGridShapeStyle());
                keyStyle.parentComponentStyleProperty().set(nv.getKeyStyle());
                keyTextStyle.parentComponentStyleProperty().set(nv.getKeyTextStyle());
            } else {
                gridShapeStyle.parentComponentStyleProperty().set(null);
                keyStyle.parentComponentStyleProperty().set(null);
                keyTextStyle.parentComponentStyleProperty().set(null);
            }
        });
        this.initStackComponent();
    }

    // Class part : "Properties and base implementation"
    //========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isParentSelected() {
        return false;
    }

    @Override
    public DoubleProperty bindableDisplayedWidthProperty() {
        return this.widthProperty();
    }

    @Override
    public DoubleProperty bindableDisplayedHeightProperty() {
        return this.heightProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeDisplayableType getNodeType() {
        return TreeDisplayableType.STACK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchRemovedPropertyValue(final boolean value) {
        super.dispatchRemovedPropertyValue(value);
        ObservableList<GridComponentI> componentList = this.getComponentList();
        for (GridComponentI child : componentList) {
            child.dispatchRemovedPropertyValue(value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchDisplayedProperty(final boolean displayedP) {
        super.dispatchDisplayedProperty(displayedP);
        final GridComponentI displayedComponent = displayedComponentProperty().get();
        ObservableList<GridComponentI> componentList = this.getComponentList();
        for (GridComponentI child : componentList) {
            if (!displayedP || child == displayedComponent) {
                child.dispatchDisplayedProperty(displayedP);
            }
        }
    }

    @Override
    public ObservableList<GridComponentI> getComponentList() {
        return this.stackComponentPropertyWrapper.getComponentList();
    }

    @Override
    public ObjectProperty<GridComponentI> displayedComponentProperty() {
        return this.stackComponentPropertyWrapper.displayedComponentProperty();
    }

    @Override
    public BooleanProperty nextPossibleProperty() {
        return this.stackComponentPropertyWrapper.nextPossibleProperty();
    }

    @Override
    public BooleanProperty previousPossibleProperty() {
        return this.stackComponentPropertyWrapper.previousPossibleProperty();
    }

    @Override
    public ShapeCompStyleI getGridShapeStyle() {
        return this.gridShapeStyle;
    }

    @Override
    public KeyCompStyleI getKeyStyle() {
        return this.keyStyle;
    }

    @Override
    public TextCompStyleI getKeyTextStyle() {
        return this.keyTextStyle;
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================

    @Override
    public Element serialize(final IOContextI contextP) {
        Element element = super.serialize(contextP);
        StackComponentBaseImpl.serialize(this, element, contextP);
        StyleSerialializer.serializeGridStyle(this, element, contextP);
        StyleSerialializer.serializeKeyStyle(this, element, contextP);
        return element;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        StackComponentBaseImpl.deserialize(this, nodeP, contextP);
        StyleSerialializer.deserializeGridStyle(this, nodeP, contextP);
        StyleSerialializer.deserializeKeyStyle(this, nodeP, contextP);
    }
    //========================================================================

}
