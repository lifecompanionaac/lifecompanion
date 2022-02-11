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

import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.controller.io.IOManager;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;
import org.lifecompanion.framework.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interface that represent a component that keep {@link GridComponentI} as child.<br>
 * It keeps component in a stack, and display one component in the same time : the component on the top.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface StackComponentBaseImpl extends TreeDisplayableComponentI, StackComponentI {

    /**
     * {@inheritDoc}
     */
    @Override
    default void displayNext() {
        if (this.nextPossibleProperty().get()) {
            this.displayedComponentProperty().set(this.getNextComponent());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void displayPrevious() {
        if (this.previousPossibleProperty().get()) {
            this.displayedComponentProperty().set(this.getPreviousComponent());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default GridComponentI getNextComponent() {
        List<GridComponentI> components = this.getComponentList();
        ObjectProperty<GridComponentI> displayed = this.displayedComponentProperty();
        int indexOf = components.indexOf(displayed.get());
        if (indexOf < components.size() - 1) {
            return components.get(indexOf + 1);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default GridComponentI getPreviousComponent() {
        List<GridComponentI> components = this.getComponentList();
        ObjectProperty<GridComponentI> displayed = this.displayedComponentProperty();
        int indexOf = components.indexOf(displayed.get());
        if (indexOf > 0) {
            return components.get(indexOf - 1);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    @Override
    default void shiftUpComponent(final GridComponentI component) {
        List<GridComponentI> components = this.getComponentList();
        int index = components.indexOf(component);
        if (index > 0) {
            //Issue #35 : swap list
            this._disableChangeListenerProperty().set(true);
            Collections.swap(components, index, index - 1);
            this._disableChangeListenerProperty().set(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    @Override
    default void shiftDownComponent(final GridComponentI component) {
        List<GridComponentI> components = this.getComponentList();
        int index = components.indexOf(component);
        if (index < components.size() - 1) {
            //Issue #35 : swap list
            this._disableChangeListenerProperty().set(true);
            Collections.swap(components, index, index + 1);
            this._disableChangeListenerProperty().set(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isDirectStackChild(final GridComponentI componentP) {
        return this.getComponentList().contains(componentP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void replace(final GridComponentI toReplace, final GridComponentI component) {
        if (this.getComponentList().contains(toReplace)) {
            int indexToReplace = this.getComponentList().indexOf(toReplace);
            this.getComponentList().set(indexToReplace, component);
        } else {
            throw new IllegalArgumentException("Can't replace a compnoent that is not in the grid");
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    @Override
    default void initStackComponent() {
        ObservableList<GridComponentI> components = this.getComponentList();
        ObjectProperty<GridComponentI> displayed = this.displayedComponentProperty();
        Runnable updateNextPreviousPossible = () -> {
            if (displayed.get() != null) {
                int index = components.indexOf(displayed.get());
                this.previousPossibleProperty().set(index > 0);
                this.nextPossibleProperty().set(index < components.size() - 1);
            }
        };
        //Add/remove displayed with specific list changes
        components.addListener((ListChangeListener<? super GridComponentI>) changeP -> {
            while (changeP.next()) {
                //Issue #35 : disable change on swap
                if (!this._disableChangeListenerProperty().get()) {
                    if (changeP.wasAdded() || changeP.wasReplaced()) {
                        List<? extends GridComponentI> addedSubList = changeP.getAddedSubList();
                        for (GridComponentI c : addedSubList) {
                            //By default, a stack component is not displayed
                            c.dispatchDisplayedProperty(false);
                            c.dispatchRemovedPropertyValue(false);
                            //Bind size
                            c.layoutWidthProperty().bind(this.bindableDisplayedWidthProperty());
                            c.layoutHeightProperty().bind(this.bindableDisplayedHeightProperty());
                            //Configuration is the same
                            c.configurationParentProperty().bind(StackComponentBaseImpl.this.configurationParentProperty());
                            //Stack parent is this object
                            c.stackParentProperty().set(this);
                            //Root parent is the same, if this component is a child
                            if (StackComponentBaseImpl.this instanceof RootChildComponentI) {
                                c.rootParentProperty().bind(((RootChildComponentI) StackComponentBaseImpl.this).rootParentProperty());
                            } else if (StackComponentBaseImpl.this instanceof RootGraphicComponentI) {
                                c.rootParentProperty().set((RootGraphicComponentI) StackComponentBaseImpl.this);
                            }
                            //Bind style
                            c.getGridShapeStyle().parentComponentStyleProperty().set(this.getGridShapeStyle());
                            c.getKeyStyle().parentComponentStyleProperty().set(this.getKeyStyle());
                            c.getKeyTextStyle().parentComponentStyleProperty().set(this.getKeyTextStyle());
                        }
                        //Display the first added component
                        if (displayed.get() == null) {
                            GridComponentI added = changeP.getAddedSubList().get(0);
                            displayed.set(added);
                        }
                    }
                    if (changeP.wasRemoved() || changeP.wasReplaced()) {
                        List<? extends GridComponentI> removed = changeP.getRemoved();
                        //If the displayed element is removed, try to show the first
                        if (removed.contains(displayed.get())) {
                            //TODO : fix a rare possible bug if the first next component is a removed component (could happen only on multiple remove...)
                            if (!components.isEmpty()) {
                                displayed.set(components.get(0));
                            } else {
                                displayed.set(null);
                            }
                        }
                        //Remove parent
                        for (GridComponentI c : removed) {
                            c.dispatchRemovedPropertyValue(true);
                            //Unbind size
                            c.layoutWidthProperty().unbind();
                            c.layoutHeightProperty().unbind();
                            //Unbind configuration parent
                            c.configurationParentProperty().unbind();
                            c.stackParentProperty().set(null);
                            //Root parent is the same, if this component is a child
                            if (StackComponentBaseImpl.this instanceof RootChildComponentI) {
                                c.rootParentProperty().unbind();
                            } else if (StackComponentBaseImpl.this instanceof RootGraphicComponentI) {
                                c.rootParentProperty().set(null);
                            }
                        }
                    }
                    //After each list change
                    updateNextPreviousPossible.run();
                    //Check if the stack child is the last
                    if (components.size() == 1) {
                        components.get(0).lastStackChildProperty().set(true);
                    } else {
                        for (GridComponentI child : components) {
                            child.lastStackChildProperty().set(false);
                        }
                    }
                }
            }
        });
        //Bind the displayed component size to this component size
        displayed.addListener((observableP, oldValueP, newValueP) -> {
            if (oldValueP != null) {
                oldValueP.dispatchDisplayedProperty(false);
                this.detailNameProperty().unbind();
            }
            //Compute next and previous
            if (newValueP != null) {
                updateNextPreviousPossible.run();
                newValueP.dispatchDisplayedProperty(true);
                this.detailNameProperty().bind(newValueP.nameProperty());
            }
        });
    }

    // Class part : "Tree part"
    //========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    default ObservableList<? extends TreeDisplayableComponentI> getChildrenNode() {
        return this.getComponentList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isNodeLeaf() {
        return false;
    }

    //========================================================================

    // Class part : "XML"
    //========================================================================
    static final String NODE_GRID = "StackGrid", ATB_DISPLAYED = "displayed";

    /**
     * Useful method to save a stack component part without rewriting it twice.
     *
     * @param stackComponent the stack component to save
     * @param node           the parent node of saving
     * @param contextP       the saving context
     * @return the element that represent this stack component
     */
    static void serialize(final StackComponentI stackComponent, final Element node, final IOContextI contextP) {
        ObservableList<GridComponentI> components = stackComponent.getComponentList();
        ObjectProperty<GridComponentI> displayed = stackComponent.displayedComponentProperty();
        //Displayed
        XMLUtils.write("" + (displayed.get() != null ? displayed.get().getID() : "null"), StackComponentBaseImpl.ATB_DISPLAYED, node);
        //Grid list
        Element grids = new Element(StackComponentBaseImpl.NODE_GRID);
        node.addContent(grids);
        for (GridPartComponentI grid : components) {
            grids.addContent(grid.serialize(contextP));
        }
    }

    /**
     * Useful method to load a stack component.
     *
     * @param stackComponent the stack component to load
     * @param nodeP          the parent node, where stack component informations are
     * @param contextP       the loading context
     * @throws LCException if a problem happen when loading
     */
    static void deserialize(final StackComponentI stackComponent, final Element nodeP, final IOContextI contextP) throws LCException {
        ObjectProperty<GridComponentI> displayed = stackComponent.displayedComponentProperty();
        //Displayed
        String displayedID = XMLUtils.readString(StackComponentBaseImpl.ATB_DISPLAYED, nodeP);
        //Grid list
        Element stackElement = nodeP.getChild(StackComponentBaseImpl.NODE_GRID);
        List<Element> grids = stackElement.getChildren();
        List<GridComponentI> loadedComponents = new ArrayList<>(grids.size() + 5);
        GridComponentI toDisplay = null;
        for (Element grid : grids) {
            Pair<Boolean, XMLSerializable<IOContextI>> loadedComponentResult = IOManager.create(grid, contextP, null);
            if (!loadedComponentResult.getLeft()) {
                GridComponentI loadedComponent = (GridComponentI) loadedComponentResult.getRight();
                loadedComponent.deserialize(grid, contextP);
                if (loadedComponent.getID().equals(displayedID)) {
                    toDisplay = loadedComponent;
                }
                loadedComponents.add(loadedComponent);
            }
        }
        // At the end, if loading failed for every grid (e.g. no grid in stack)
        if (loadedComponents.isEmpty()) {
            loadedComponents.add(new GridPartGridComponent());
        }
        stackComponent.getComponentList().addAll(loadedComponents);
        //Set displayed
        displayed.set(toDisplay);
    }

    //========================================================================
}
