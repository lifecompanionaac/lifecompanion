package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.keylist;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.ui.common.pane.specific.cell.KeyListCellHandler;
import org.lifecompanion.ui.common.pane.specific.cell.KeyListNodeTreeCell;
import org.lifecompanion.util.binding.ListBindingWithMapper;

import java.util.HashMap;
import java.util.Map;

public class KeyListTreeView extends TreeView<KeyListNodeI> implements LCViewInitHelper {
    private final KeyListContentConfigView keyListContentConfigView;

    private final Map<KeyListNodeI, KeyListNodeTreeItem> treeItems;

    KeyListTreeView(KeyListContentConfigView keyListContentConfigView) {
        this.keyListContentConfigView = keyListContentConfigView;
        this.treeItems = new HashMap<>();
        initAll();
    }

    @Override
    public void initUI() {
        this.setCellFactory(tv -> new KeyListNodeTreeCell(this::selectAndScrollToId));
        this.setShowRoot(false);
        this.setMaxHeight(Double.MAX_VALUE);
        this.setFixedCellSize(KeyListCellHandler.CELL_HEIGHT + 5);
    }

    @Override
    public void initListener() {
        this.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> keyListContentConfigView.v2Select(nv != null ? nv.getValue() : null));
    }

    @Override
    public void initBinding() {
        keyListContentConfigView.rootProperty().addListener((obs, ov, nv) -> {
            treeItems.clear();
            if (nv != null) {
                this.setRoot(new KeyListNodeTreeItem(nv));
            } else {
                this.setRoot(null);
            }
        });
        keyListContentConfigView.selectedProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                System.out.println("Selected changed callback " + nv);
                final KeyListNodeTreeItem keyListNodeTreeItem = this.treeItems.get(nv);
                if (keyListNodeTreeItem != null && this.getSelectionModel().getSelectedItem() != keyListNodeTreeItem) {
                    this.getSelectionModel().select(keyListNodeTreeItem);
                    this.scrollTo(this.getSelectionModel().getSelectedIndex());
                }

                // Scroll to selection
//            final int selectedIndex = this.getSelectionModel().getSelectedIndex();
//            int indexToSelect = selectedIndex;
//
//            // TODO : enable/disable depending of selection source
//            // try to go back 3 index behind (better for UX)
//            while (indexToSelect-- > 0 && selectedIndex - indexToSelect < 2) ;
//            this.scrollTo(indexToSelect);
            } else {
                this.getSelectionModel().clearSelection();
            }
        });
    }

    // SELECTION
    //========================================================================
    public void selectAndScrollToId(String itemId) {
//        if (itemId != null && this.node.get() != null) {
//            final KeyListNodeI foundNode = KeyListController.findNodeByIdInSubtree(this.node.get(), itemId);
//            if (foundNode != null) {
//                selectAndScrollTo(foundNode);
//            }
//        }
    }

    public void selectAndScrollTo(KeyListNodeI item) {
//        final KeyListNodeTreeItem keyListNodeTreeItem = this.treeItems.get(item);
//        if (keyListNodeTreeItem != null) {
//            this.getSelectionModel().select(keyListNodeTreeItem);
//
//            // Scroll to selection
//            final int selectedIndex = this.getSelectionModel().getSelectedIndex();
//            int indexToSelect = selectedIndex;
//
//            // TODO : enable/disable depending of selection source
//            // try to go back 3 index behind (better for UX)
//            while (indexToSelect-- > 0 && selectedIndex - indexToSelect < 2) ;
//            this.scrollTo(indexToSelect);
//        }
    }
//========================================================================

    // TREE ITEM
//========================================================================
    private class KeyListNodeTreeItem extends TreeItem<KeyListNodeI> {
        public KeyListNodeTreeItem(KeyListNodeI value) {
            super(value);
            treeItems.put(value, this);
            if (!value.isLeafNode()) {
                ListBindingWithMapper.mapContent(getChildren(), value.getChildren(), KeyListNodeTreeItem::new);
            }
            this.expandedProperty().addListener((obs, ov, nv) -> selectAndScrollTo(value));
        }
    }
//========================================================================
}
