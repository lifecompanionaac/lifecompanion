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
package org.lifecompanion.ui.common.control.specific.componenttree;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.TreeDisplayableComponentI;
import org.lifecompanion.ui.common.pane.generic.BaseConfigurationViewBorderPane;
import org.lifecompanion.ui.common.pane.specific.cell.SelectionItemTreeCell;
import org.lifecompanion.util.binding.ListBindingWithMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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
    private final Map<TreeDisplayableComponentI, TreeDisplayableComponentTreeItem> componentTreeNodes;

    private boolean disableScrollTo, disableSelectOnSelectedItemChanged;

    public ComponentSelectionTree() {
        this.componentTreeNodes = new HashMap<>();
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
                    if (newValueP != null) {
                        TreeDisplayableComponentI component = newValueP.getValue();
                        this.disableScrollTo = true;
                        if (component instanceof DisplayableComponentI && !disableSelectOnSelectedItemChanged) {// avoid nested selection loops
                            SelectionController.INSTANCE.selectDisplayableComponent((DisplayableComponentI) component, true);
                        }
                        this.disableScrollTo = false;
                    }
                });
        //External selection with mouse select the tree
        ChangeListener<? super DisplayableComponentI> selectedChangeListener = (obs, ov, nv) -> {
            if (nv != null) {
                TreeItem<TreeDisplayableComponentI> treeItem = this.componentTreeNodes.get(nv);
                if (treeItem != null) {
                    disableSelectOnSelectedItemChanged = true;
                    this.selectTreeItem(treeItem);
                    disableSelectOnSelectedItemChanged = false;
                } else {
                    LOGGER.warn("Didn't find a tree for the selected component {}", nv);
                }
            }
        };
        SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().addListener(selectedChangeListener);
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
        this.model.bind(AppModeController.INSTANCE.getEditModeContext().configurationProperty());
    }

    @Override
    protected double computeMinWidth(final double arg0P) {
        return 0.0;
    }

    @Override
    protected double computeMinHeight(final double widthP) {
        return 0.0;
    }

    @Override
    public void bind(final LCConfigurationI component) {
        this.componentTreeView.setRoot(new TreeDisplayableComponentTreeItem(component));
    }

    @Override
    public void unbind(final LCConfigurationI component) {
        this.componentTreeView.setRoot(null);
        this.componentTreeNodes.clear();
    }

    private class TreeDisplayableComponentTreeItem extends TreeItem<TreeDisplayableComponentI> {
        final FilteredList<TreeDisplayableComponentI> filteredListNoKeys;// Keep here to avoid garbage collection

        public TreeDisplayableComponentTreeItem(TreeDisplayableComponentI value) {
            super(value);
            componentTreeNodes.put(value, this);
            if (!value.isNodeLeaf()) {
                filteredListNoKeys = new FilteredList<>(value.getChildrenNode(), comp -> !(comp instanceof GridPartKeyComponentI));
                ListBindingWithMapper.mapContent(getChildren(), filteredListNoKeys, TreeDisplayableComponentTreeItem::new);
            } else {
                filteredListNoKeys = null;
            }
        }
    }
}
