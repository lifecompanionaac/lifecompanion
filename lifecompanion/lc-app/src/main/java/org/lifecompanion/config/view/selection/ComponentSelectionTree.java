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
package org.lifecompanion.config.view.selection;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.lifecompanion.api.component.definition.*;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.view.reusable.impl.BaseConfigurationViewBorderPane;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A view that display a tree to show every existing component in the current configuration.<br>
 * This allow user to select component that are currently hidden.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ComponentSelectionTree extends BaseConfigurationViewBorderPane<LCConfigurationI> implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(ComponentSelectionTree.class);

    /**
     * Tree view that show every selectable component
     */
    private TreeView<TreeDisplayableComponentI> componentTreeView;

    /**
     * Map that contains all the tree item for each component
     */
    private Map<TreeDisplayableComponentI, TreeItem<TreeDisplayableComponentI>> treeNodes;

    /**
     * Map that contains all the listener for each component
     */
    private Map<TreeDisplayableComponentI, ListChangeListener<TreeDisplayableComponentI>> nodeChildrenListener;

    /**
     * Hack : to disable scroll to if the selection comes from the tree itself
     */
    private boolean disableScrollTo;

    public ComponentSelectionTree() {
        this.treeNodes = new HashMap<>();
        this.nodeChildrenListener = new HashMap<>();
        this.initAll();
        this.setPrefSize(0.0, 0.0);
    }

    @Override
    public void initUI() {
        //Create tree and root
        this.componentTreeView = new TreeView<>();
        this.componentTreeView.setShowRoot(false);
        //Tree cell
        this.componentTreeView.setCellFactory(paramP -> new SelectionItemTreeCell());
        Label labelTop = new Label(Translation.getText("component.select.tree"));
        //Add
        this.setTop(labelTop);
        this.setCenter(this.componentTreeView);
    }

    @Override
    public void initListener() {
        //Selection on tree select the component
        this.componentTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observableP, oldValueP, newValueP) -> {
                    if (newValueP != null && newValueP.getValue() != SelectionController.INSTANCE.selectedComponentBothProperty().get()) {
                        TreeDisplayableComponentI component = newValueP.getValue();
                        this.disableScrollTo = true;
                        if (component instanceof RootGraphicComponentI) {
                            SelectionController.INSTANCE.setSelectedRoot((RootGraphicComponentI) component);
                        } else if (component instanceof GridPartComponentI) {
                            SelectionController.INSTANCE.setSelectedPart((GridPartComponentI) component);
                        } else {
                            ComponentSelectionTree.LOGGER.warn("Didn't find a correct class to select the component {} in the tree",
                                    component.getClass());
                        }
                        this.disableScrollTo = false;
                    }
                });
        //Selection with mouse select the tree
        ChangeListener<? super DisplayableComponentI> selectedChangeListener = (obs, ov, nv) -> {
            if (nv != null) {
                TreeItem<TreeDisplayableComponentI> treeItem = this.treeNodes.get(nv);
                if (treeItem != null) {
                    this.selectTreeItem(treeItem);
                } else {
                    ComponentSelectionTree.LOGGER.warn("Didn't find a tree for the selected component {}", nv);
                }
            }
        };
        SelectionController.INSTANCE.selectedComponentBothProperty().addListener(selectedChangeListener);
    }

    private void selectTreeItem(final TreeItem<TreeDisplayableComponentI> treeItem) {
        if (this.componentTreeView.getSelectionModel().getSelectedItem() != treeItem) {
            this.componentTreeView.getSelectionModel().select(treeItem);
            if (!this.disableScrollTo) {
                this.componentTreeView.scrollTo(this.componentTreeView.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @Override
    public void initBinding() {
        this.model.bind(AppController.INSTANCE.currentConfigConfigurationProperty());
    }

    // Class part : "Override"
    //========================================================================
    @Override
    protected double computeMinWidth(final double arg0P) {
        return 0.0;
    }

    @Override
    protected double computeMinHeight(final double widthP) {
        return 0.0;
    }

    //========================================================================

    // Class part : "Binding"
    //========================================================================

    @Override
    public void bind(final LCConfigurationI component) {
        TreeItem<TreeDisplayableComponentI> root = new TreeItem<>();
        this.treeNodes.put(component, root);
        this.componentTreeView.setRoot(root);
        //Base
        ObservableList<? extends TreeDisplayableComponentI> childrenNode = component.getChildrenNode();
        for (TreeDisplayableComponentI treeNode : childrenNode) {
            this.nodeAdded(component, treeNode);
        }
        childrenNode.addListener(this.createCL(component));
    }

    @Override
    public void unbind(final LCConfigurationI component) {
        this.nodeRemoved(component, component);
        this.treeNodes.clear();
        this.nodeChildrenListener.clear();
        this.componentTreeView.getSelectionModel().clearSelection();
        this.componentTreeView.setRoot(null);
    }

    /**
     * Add the node from view.<br>
     * Also add the node children and needed listener on them.
     *
     * @param parent the parent node of the node to add
     * @param item   the added node
     */
    private void nodeAdded(final TreeDisplayableComponentI parent, final TreeDisplayableComponentI item) {
        //Create the item
        TreeItem<TreeDisplayableComponentI> treeItemView = new TreeItem<>(item);
        this.treeNodes.put(item, treeItemView);
        //Get the parent and add
        TreeItem<TreeDisplayableComponentI> parentView = this.treeNodes.get(parent);
        parentView.getChildren().add(treeItemView);
        //Listener
        if (!item.isNodeLeaf()) {
            ObservableList<TreeDisplayableComponentI> children = item.getChildrenNode();
            children.addListener(this.createCL(item));
            for (TreeDisplayableComponentI child : children) {
                this.nodeAdded(item, child);
            }
        }
    }

    /**
     * Remove the node from view.<br>
     * Also remove the node children and listener.
     *
     * @param parent the parent node of the node to remove
     * @param item   the removed node
     */
    private void nodeRemoved(final TreeDisplayableComponentI parent, final TreeDisplayableComponentI item) {
        //If item is selected
        TreeItem<TreeDisplayableComponentI> selectedItem = this.componentTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && item == selectedItem.getValue()) {
            this.componentTreeView.getSelectionModel().clearSelection();
        }
        //Get the item and its view
        TreeItem<TreeDisplayableComponentI> itemView = this.treeNodes.get(item);
        TreeItem<TreeDisplayableComponentI> parentView = this.treeNodes.get(parent);
        //Remove from parent and listener
        parentView.getChildren().remove(itemView);
        if (!item.isNodeLeaf()) {
            ObservableList<TreeDisplayableComponentI> children = item.getChildrenNode();
            children.removeListener(this.nodeChildrenListener.get(item));
            for (TreeDisplayableComponentI child : children) {
                this.nodeRemoved(item, child);
            }
        }

    }

    /**
     * Create the tree children change listener for a given node.
     *
     * @param parent the node that will provide children list to listen
     * @return the list change listener for this parent children
     */
    @SuppressWarnings("deprecation")
    private ListChangeListener<TreeDisplayableComponentI> createCL(final TreeDisplayableComponentI parent) {
        ListChangeListener<TreeDisplayableComponentI> cl = (change) -> {
            TreeItem<TreeDisplayableComponentI> selectedItem = this.componentTreeView.getSelectionModel().getSelectedItem();
            while (change.next()) {
                if (!parent._disableChangeListenerProperty().get()) {
                    if (change.wasAdded()) {
                        List<? extends TreeDisplayableComponentI> addeds = change.getAddedSubList();
                        for (TreeDisplayableComponentI add : addeds) {
                            ComponentSelectionTree.this.nodeAdded(parent, add);
                        }
                    }
                    if (change.wasRemoved()) {
                        List<? extends TreeDisplayableComponentI> removed = change.getRemoved();
                        for (TreeDisplayableComponentI remove : removed) {
                            //Should never select back
                            if (selectedItem != null && remove == selectedItem.getValue()) {
                                selectedItem = null;
                            }
                            ComponentSelectionTree.this.nodeRemoved(parent, remove);

                        }
                    }
                }
            }
            //WORKAROUND : sometimes selected item change on item add/remove even if the item is not removed from the tree, this restore the previous selected item if it exists
            if (selectedItem != null) {
                this.selectTreeItem(selectedItem);
            }
        };
        this.nodeChildrenListener.put(parent, cl);
        return cl;
    }

    //========================================================================
}
