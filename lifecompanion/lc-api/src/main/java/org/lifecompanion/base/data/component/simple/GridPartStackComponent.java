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
import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.TreeDisplayableType;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.component.baseimpl.GridPartComponentBaseImpl;
import org.lifecompanion.base.data.component.baseimpl.StackComponentBaseImpl;
import org.lifecompanion.base.data.component.baseimpl.wrapper.StackComponentPropertyWrapper;

import java.util.function.Consumer;

/**
 * A component inside a grid that can display different component function of the current component.<br>
 * Could be use to create multiple page item without recreating the next/previous key.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridPartStackComponent extends GridPartComponentBaseImpl implements StackComponentBaseImpl {

    /**
     * Property wrapper for stack component
     */
    protected StackComponentPropertyWrapper stackComponentPropertyWrapper;

    public GridPartStackComponent() {
        this.stackComponentPropertyWrapper = new StackComponentPropertyWrapper(this);
        this.initStackComponent();
    }

    // Class part : "Base properties"
    //========================================================================

    /**
     * /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty bindableDisplayedWidthProperty() {
        return this.layoutWidthProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleProperty bindableDisplayedHeightProperty() {
        return this.layoutHeightProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeDisplayableType getNodeType() {
        return TreeDisplayableType.STACK;
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
    public void forEachKeys(final Consumer<GridPartKeyComponentI> action) {
        ObservableList<GridComponentI> componentList = this.getComponentList();
        for (GridPartComponentI child : componentList) {
            child.forEachKeys(action);
        }
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public Element serialize(final IOContextI contextP) {
        Element element = super.serialize(contextP);
        StackComponentBaseImpl.serialize(this, element, contextP);
        return element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        StackComponentBaseImpl.deserialize(this, nodeP, contextP);
    }
    //========================================================================

}
