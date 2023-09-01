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
        this.setCellFactory(tv -> new KeyListNodeTreeCell(keyListContentConfigView::openList, keyListContentConfigView::selectById));
        this.setShowRoot(false);
        this.setMaxHeight(Double.MAX_VALUE);
        this.setFixedCellSize(KeyListCellHandler.CELL_HEIGHT + 5);
    }

    @Override
    public void initListener() {
        this.getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, ov, nv) -> {
                // Select (excluding the root)
                keyListContentConfigView.select(nv != null && nv.getValue() != keyListContentConfigView.rootProperty().get() ? nv.getValue() : null);
            });
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
                final KeyListNodeTreeItem keyListNodeTreeItem = this.treeItems.get(nv);
                if (keyListNodeTreeItem != null && this.getSelectionModel().getSelectedItem() != keyListNodeTreeItem) {
                    this.getSelectionModel().select(keyListNodeTreeItem);
                    final int selectedIndex = this.getSelectionModel().getSelectedIndex();
                    int indexToSelect = this.getSelectionModel().getSelectedIndex();
                    while (indexToSelect-- > 0 && selectedIndex - indexToSelect < 2) ;
                    this.scrollTo(indexToSelect);
                }
            } else {
                this.getSelectionModel().clearSelection();
            }
        });
        keyListContentConfigView.currentListProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                final KeyListNodeTreeItem keyListNodeTreeItem = this.treeItems.get(nv);
                if (keyListNodeTreeItem != null) {
                    keyListNodeTreeItem.setExpanded(true);
                }
            }
        });
    }

    // TREE ITEM
    //========================================================================
    private class KeyListNodeTreeItem extends TreeItem<KeyListNodeI> {
        public KeyListNodeTreeItem(KeyListNodeI value) {
            super(value);
            treeItems.put(value, this);
            if (!value.isLeafNode()) {
                ListBindingWithMapper.mapContent(getChildren(), value.getChildren(), KeyListNodeTreeItem::new);
            }
        }
    }
    //========================================================================
}
