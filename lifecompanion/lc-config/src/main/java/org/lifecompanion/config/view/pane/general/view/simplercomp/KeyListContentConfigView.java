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

package org.lifecompanion.config.view.pane.general.view.simplercomp;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.lifecompanion.api.component.definition.simplercomp.KeyListNodeI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.component.simplercomp.KeyListLeaf;
import org.lifecompanion.base.data.component.simplercomp.KeyListLinkLeaf;
import org.lifecompanion.base.data.component.simplercomp.KeyListNode;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.config.data.action.impl.KeyListActions;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.notif.LCNotification;
import org.lifecompanion.config.view.pane.main.notification2.LCNotificationController;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class KeyListContentConfigView extends BorderPane implements LCViewInitHelper {
    private final SimpleListProperty<KeyListNodeI> nodeChildren;
    private final ObjectProperty<KeyListNodeI> editedNode;

    private Glyph listGlyph, keyGlyph, linkGlyph;
    private ListView<KeyListNodeI> listItemList;

    private TreeView<KeyListNodeI> treeViewItems;

    private Button buttonAddKey, buttonAddCategory, buttonAddLinkKey, buttonConfigurationKeys, buttonExportKeys, buttonImportKeys, buttonDelete;
    private Label labelSelectedItemTitle;

    private HBox parentNodeContainer;

    private CommonListViewActionContainer<KeyListNodeI> commonListViewActionContainer;

    /**
     * To edit node properties
     */
    private KeyListNodePropertiesEditionView keyListNodePropertiesEditionView;

    public KeyListContentConfigView() {
        this.nodeChildren = new SimpleListProperty<>();
        this.editedNode = new SimpleObjectProperty<>();
        initAll();
    }

    public ObjectProperty<KeyListNodeI> editNodeProperty() {
        return editedNode;
    }

    // UI
    //========================================================================
    @Override
    public void initUI() {
        parentNodeContainer = new HBox(5.0);
        parentNodeContainer.setAlignment(Pos.CENTER_LEFT);

        // TOP : list + add button
        listItemList = new ListView<>();
        listItemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.commonListViewActionContainer = new CommonListViewActionContainer<>(listItemList);
        listItemList.setCellFactory(lv -> new DetailledKeyListContentListCell(this.commonListViewActionContainer));
        listItemList.setOrientation(Orientation.HORIZONTAL);
        listItemList.setMaxHeight(DetailledSimplerKeyContentContainerListCell.CELL_HEIGHT + 20.0);
        listItemList.setPrefHeight(DetailledSimplerKeyContentContainerListCell.CELL_HEIGHT + 20.0);
        HBox.setHgrow(listItemList, Priority.ALWAYS);
        this.buttonAddCategory = UIUtils.createRightTextButton(Translation.getText("general.configuration.view.key.button.add.list"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        this.buttonAddKey = UIUtils.createRightTextButton(Translation.getText("general.configuration.view.key.button.add.key"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        this.buttonAddLinkKey = UIUtils.createRightTextButton(Translation.getText("general.configuration.view.key.button.add.link"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        buttonDelete = new Button("Suppr.");
        VBox boxAddButtons = new VBox(5.0, buttonAddKey, buttonAddCategory, buttonAddLinkKey, buttonDelete);
        boxAddButtons.setAlignment(Pos.CENTER_RIGHT);


        treeViewItems = new TreeView<>();
        treeViewItems.setCellFactory(tv -> new KeyListNodeTreeCell());
        treeViewItems.setShowRoot(false);

        treeViewItems.setOnDragExited(da -> {
            // FIXME : find a way to scroll up/down
            //            if (da.getY() > 0) {
            //treeViewItems.refresh();
            //            }
        });

        HBox boxList = new HBox(10.0, treeViewItems, boxAddButtons);
        boxList.setAlignment(Pos.CENTER);

        // CENTER : configuration grid
        labelSelectedItemTitle = new Label();
        labelSelectedItemTitle.setPadding(new Insets(10, 0, 0, 0));
        labelSelectedItemTitle.getStyleClass().addAll("text-h4", "text-label-center");
        labelSelectedItemTitle.setMaxWidth(Double.MAX_VALUE);

        listGlyph = LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.FOLDER).size(16).color(LCGraphicStyle.LC_GRAY);
        keyGlyph = LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PICTURE_ALT).size(16).color(LCGraphicStyle.LC_GRAY);
        linkGlyph = LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.LINK).size(16).color(LCGraphicStyle.LC_GRAY);
        labelSelectedItemTitle.setContentDisplay(ContentDisplay.LEFT);
        labelSelectedItemTitle.setGraphicTextGap(8.0);

        this.buttonConfigurationKeys = UIUtils.createLeftTextButton(Translation.getText("general.configuration.view.key.list.button.configuration.key"), LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.GEARS).size(14).color(LCGraphicStyle.MAIN_DARK), null);
        this.buttonConfigurationKeys.setMaxWidth(Double.MAX_VALUE);
        this.buttonConfigurationKeys.setAlignment(Pos.CENTER);

        this.buttonExportKeys = UIUtils.createLeftTextButton(Translation.getText("general.configuration.view.key.list.button.export.keys"), LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.UPLOAD).size(14).color(LCGraphicStyle.MAIN_DARK), null);
        this.buttonExportKeys.setMaxWidth(Double.MAX_VALUE);
        this.buttonExportKeys.setAlignment(Pos.CENTER);

        this.buttonImportKeys = UIUtils.createLeftTextButton(Translation.getText("general.configuration.view.key.list.button.import.keys"), LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.DOWNLOAD).size(14).color(LCGraphicStyle.MAIN_DARK), null);
        this.buttonImportKeys.setMaxWidth(Double.MAX_VALUE);
        this.buttonImportKeys.setAlignment(Pos.CENTER);

        VBox boxTop = new VBox(2.0, parentNodeContainer, boxList, labelSelectedItemTitle);
        boxTop.setMaxWidth(Double.MAX_VALUE);
        boxTop.setAlignment(Pos.CENTER);
        boxTop.getChildren().addAll(buttonConfigurationKeys, buttonExportKeys, buttonImportKeys);
        buttonConfigurationKeys.getStyleClass().add("padding-0");
        keyListNodePropertiesEditionView = new KeyListNodePropertiesEditionView();

        // Total
        this.setTop(boxTop);
        this.setCenter(keyListNodePropertiesEditionView);
    }

    private KeyListNodeI dragged;

    private class KeyListNodeTreeCell extends TreeCell<KeyListNodeI> {
        private final ImageView imageView;

        KeyListNodeTreeCell() {
            final String nodeIdForImageLoading = "KeyListNodeTreeCell" + this.hashCode();
            imageView = new ImageView();
            imageView.setFitHeight(30);
            imageView.setFitWidth(30);
            this.itemProperty().addListener((obs, ov, nv) -> {
                if (ov != null) {
                    ov.removeExternalLoadingRequest(nodeIdForImageLoading);
                }
                if (nv != null)
                    nv.addExternalLoadingRequest(nodeIdForImageLoading);
            });
            this.setOnDragDetected(me -> {
                dragged = getItem();
                Dragboard dragboard = this.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putImage(this.snapshot(null, null));
                dragboard.setContent(content);
            });
            this.setOnDragOver(me -> {
                if (dragged != null && dragged != this.getItem()) {
                    me.acceptTransferModes(TransferMode.ANY);
                    // TODO : add timer on this
                    if (!this.getTreeItem().isLeaf() && !this.getTreeItem().isExpanded()) {
                        this.getTreeItem().setExpanded(true);
                    }
                }
            });
            this.setOnDragDropped(me -> {

            });
        }

        @Override
        protected void updateItem(KeyListNodeI item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                LCUtils.unbindAndSetNull(textProperty());
                LCUtils.unbindAndSetNull(graphicProperty());
            } else {
                imageView.imageProperty().bind(item.loadedImageProperty());
                textProperty().bind(item.textProperty());
                graphicProperty().set(imageView);
            }
        }
    }

    private static class KeyListNodeTreeItem extends TreeItem<KeyListNodeI> {
        public KeyListNodeTreeItem(KeyListNodeI value) {
            super(value);
            if (!value.isLeafNode()) {
                BindingUtil.mapContent(getChildren(), value.getChildren(), KeyListNodeTreeItem::new);
            }
        }

    }
    //========================================================================


    // LISTENER
    //========================================================================
    @Override
    public void initListener() {
        this.buttonAddKey.setOnAction(ev -> {
            final TreeItem<KeyListNodeI> selectedItem = this.treeViewItems.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue() != null) {
                final KeyListNodeI selectedNode = selectedItem.getValue();
                final KeyListNodeI parentNode = selectedNode.parentProperty().get();
                final int i = parentNode.getChildren().indexOf(selectedNode);
                parentNode.getChildren().add(i + 1, new KeyListNode());
            }
        });
        this.buttonDelete.setOnAction(e -> {
            final TreeItem<KeyListNodeI> selectedItem = this.treeViewItems.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue() != null) {
                final KeyListNodeI selectedNode = selectedItem.getValue();
                final KeyListNodeI parentNode = selectedNode.parentProperty().get();
                parentNode.getChildren().remove(selectedNode);
            }
        });
        this.buttonAddCategory.setOnAction(this::addButtonOnAction);
        this.buttonAddLinkKey.setOnAction(this::addButtonOnAction);
        this.buttonConfigurationKeys.setOnAction(e -> configureNodeChildren(this.listItemList.getSelectionModel().getSelectedItem()));
        this.buttonExportKeys.setOnAction(e -> ConfigActionController.INSTANCE.executeAction(new KeyListActions.ExportKeyListsAction(buttonExportKeys, new ArrayList<>(this.listItemList.getSelectionModel().getSelectedItems()))));
        this.buttonImportKeys.setOnAction(e -> {
            ConfigActionController.INSTANCE.executeAction(new KeyListActions.ImportKeyListsAction(buttonImportKeys, importedNode -> {
                // FIXME : check if need to duplicate imported node (if already present in the current node tree)
                List<KeyListNodeI> nodesToAdd = importedNode;
                if (!nodesToAdd.isEmpty()) {
                    // Add just after current item, or to the edited node
                    final KeyListNodeI selectedItem = this.listItemList.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        final int addIndex = editedNode.get().getChildren().indexOf(selectedItem);
                        editedNode.get().getChildren().addAll(addIndex + 1, nodesToAdd);
                    } else {
                        editedNode.get().getChildren().addAll(nodesToAdd);
                    }
                    commonListViewActionContainer.selectAndScrollTo(nodesToAdd.get(0));
                }
            }));
        });
        this.keyListNodePropertiesEditionView.setAddRequestListener(() -> commonListViewActionContainer.addAndScrollTo(new KeyListLeaf()));
        this.keyListNodePropertiesEditionView.setRemoveRequestListener(commonListViewActionContainer::deleteItem);

        this.commonListViewActionContainer.setDuplicateFunction(item -> {
            KeyListNodeI duplicated = (KeyListNodeI) item.duplicate(true);
            duplicated.textProperty().set(Translation.getText("general.configuration.view.key.list.copy.label.key.text") + " " + duplicated.textProperty().get());
            return duplicated;
        });
        this.commonListViewActionContainer.setDragEndPriorTester(dragDestination -> !dragDestination.isLeafNode());
        this.commonListViewActionContainer.setDragEndAcceptor((dragDestination, draggedNode) -> {
            // For each node : remove from its parent and moves it to the new parent
            // only if : not the node moved on itself or moved to its current parent
            final KeyListNodeI draggedNodeParent = draggedNode.parentProperty().get();
            if (draggedNodeParent != dragDestination && dragDestination != draggedNode) {
                draggedNodeParent.getChildren().remove(draggedNode);
                dragDestination.getChildren().add(draggedNode);
                return true;
            }
            return false;
        });
        this.commonListViewActionContainer.setDragFinishedConsumer((movedCount, dragDestination) -> LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("general.configuration.view.keylist.moved." +
                (movedCount > 1 ? "plural." : "") + "result", movedCount, getFullNameForNode(dragDestination)))));
        this.commonListViewActionContainer.setDoubleClicConsumer(this::configureNodeChildren);
    }
    //========================================================================

    // BINDING
    //========================================================================
    @Override
    public void initBinding() {
        buttonConfigurationKeys.textProperty().bind(TranslationFX.getTextBinding("general.configuration.view.key.list.button.configuration.key", nodeChildren.sizeProperty()));
        buttonConfigurationKeys.visibleProperty().bind(listItemList.getSelectionModel().selectedItemProperty().isNotNull().and(keyListNodePropertiesEditionView.selectedNodeIsLeafProperty().not()).and(keyListNodePropertiesEditionView.selectedNodeIsLinkProperty().not()));
        buttonExportKeys.visibleProperty().bind(listItemList.getSelectionModel().selectedItemProperty().isNotNull());

        this.editedNode.addListener((obs, ov, nv) -> {
            parentNodeContainer.getChildren().clear();
            if (nv != null) {
                KeyListNodeI current = nv;
                while (current != null) {
                    KeyListNodeI nodeForLink = current;
                    final KeyListNodeI currentParent = nodeForLink.parentProperty().get();
                    final Hyperlink hyperlink = new Hyperlink(getFullNameForNode(nodeForLink));
                    hyperlink.setOnAction(e -> editedNode.set(nodeForLink));
                    hyperlink.setOnDragOver(e -> {
                        if (commonListViewActionContainer.isDraggedNodes()) e.acceptTransferModes(TransferMode.ANY);
                    });
                    hyperlink.setOnDragDropped(e -> commonListViewActionContainer.dragEnd(nodeForLink));
                    parentNodeContainer.getChildren().add(0, hyperlink);
                    current = currentParent;
                    if (current != null) {
                        parentNodeContainer.getChildren().add(0, LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(12).color(LCGraphicStyle.MAIN_DARK));
                    }
                }
                setItemList(nv.getChildren());
            } else {
                setItemList(null);
            }
        });
        this.editedNode.addListener((obs, ov, nv) -> {
            if (nv != null) {
                this.treeViewItems.setRoot(new KeyListNodeTreeItem(nv));
            } else {
                this.treeViewItems.setRoot(null);
            }
        });
        this.keyListNodePropertiesEditionView.selectedNodeProperty().bind(listItemList.getSelectionModel().selectedItemProperty());
        this.keyListNodePropertiesEditionView.selectedNodeProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                labelSelectedItemTitle.textProperty().unbind();
                labelSelectedItemTitle.setGraphic(null);
                nodeChildren.set(null);
            }
            if (nv != null) {
                labelSelectedItemTitle.textProperty().bind(TranslationFX.getTextBinding(
                        nv.isLinkNode() ? "general.configuration.view.key.list.selected.link.title" : nv.isLeafNode() ? "general.configuration.view.key.list.selected.key.title" : "general.configuration.view.key.list.selected.keylist.title",
                        nv.textProperty())
                );
                labelSelectedItemTitle.setGraphic(nv.isLinkNode() ? linkGlyph : nv.isLeafNode() ? keyGlyph : listGlyph);
                nodeChildren.set(nv.isLeafNode() ? null : nv.getChildren());
            } else {
                labelSelectedItemTitle.textProperty().unbind();
                labelSelectedItemTitle.setText(Translation.getText("general.configuration.view.key.list.no.selected"));
            }
        });
    }

    private String getFullNameForNode(KeyListNodeI node) {
        return (node.parentProperty().get() == null ? Translation.getText("general.configuration.view.keylist.root.node.text") :
                StringUtils.isNotBlank(node.textProperty().get()) ? node.textProperty().get() : Translation.getText("general.configuration.view.keylist.empty.node.text"))
                + " " + Translation.getText("general.configuration.view.keylist.node.level", node.levelProperty());
    }
    //========================================================================


    // HELPER
    //========================================================================
    private void setItemList(ObservableList<KeyListNodeI> list) {
        this.listItemList.getSelectionModel().clearSelection();
        if (list != null) {
            this.listItemList.setItems(list);
        } else {
            this.listItemList.setItems(FXCollections.observableArrayList());
        }
    }

    private void addButtonOnAction(ActionEvent actionEvent) {
        KeyListNodeI added = actionEvent.getSource() == buttonAddCategory ? new KeyListNode() : actionEvent.getSource() == buttonAddKey ? new KeyListLeaf() : new KeyListLinkLeaf();
        this.commonListViewActionContainer.addAndScrollTo(added);
    }

    void configureNodeChildren(KeyListNodeI item) {
        if (item != null && !item.isLeafNode()) {
            editedNode.set(item);
        }
    }

    public void selectToBeEditedInTree(KeyListNodeI item) {
        editedNode.set(item.parentProperty().get());
        this.commonListViewActionContainer.selectAndScrollTo(item);
    }
    //========================================================================

}
