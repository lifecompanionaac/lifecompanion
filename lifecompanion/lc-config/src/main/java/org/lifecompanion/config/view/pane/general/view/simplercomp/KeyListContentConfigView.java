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
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
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
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

public class KeyListContentConfigView extends BorderPane implements LCViewInitHelper {
    private final ObjectProperty<KeyListNodeI> rootKeyListNode;

    private Glyph listGlyph, keyGlyph, linkGlyph;

    private TreeView<KeyListNodeI> keyListTreeView;

    private Button buttonAddKey, buttonAddCategory, buttonAddLinkKey, buttonDelete, buttonMoveUp, buttonMoveDown, buttonCut, buttonCopy, buttonPaste, buttonExportKeys, buttonImportKeys;

    private HBox selectionPathContainer;

    /**
     * To edit node properties
     */
    private KeyListNodePropertiesEditionView keyListNodePropertiesEditionView;

    public KeyListContentConfigView() {
        this.rootKeyListNode = new SimpleObjectProperty<>();
        initAll();
    }

    public ObjectProperty<KeyListNodeI> rootKeyListNodeProperty() {
        return rootKeyListNode;
    }

    // UI
    //========================================================================
    @Override
    public void initUI() {
        selectionPathContainer = new HBox(5.0);
        selectionPathContainer.setAlignment(Pos.CENTER_LEFT);

        // TOP : list + add button
        this.buttonAddCategory = createButton(FontAwesome.Glyph.FOLDER, LCGraphicStyle.MAIN_PRIMARY, "todo");
        this.buttonAddKey = createButton(FontAwesome.Glyph.PICTURE_ALT, LCGraphicStyle.MAIN_PRIMARY, "todo");
        this.buttonAddLinkKey = createButton(FontAwesome.Glyph.LINK, LCGraphicStyle.MAIN_PRIMARY, "todo");
        buttonMoveUp = createButton(FontAwesome.Glyph.CHEVRON_UP, LCGraphicStyle.MAIN_PRIMARY, "todo");
        buttonMoveDown = createButton(FontAwesome.Glyph.CHEVRON_DOWN, LCGraphicStyle.MAIN_PRIMARY, "todo");
        buttonCopy = createButton(FontAwesome.Glyph.COPY, LCGraphicStyle.MAIN_DARK, "todo");
        buttonCut = createButton(FontAwesome.Glyph.CUT, LCGraphicStyle.MAIN_DARK, "todo");
        buttonPaste = createButton(FontAwesome.Glyph.PASTE, LCGraphicStyle.MAIN_DARK, "todo");
        buttonDelete = createButton(FontAwesome.Glyph.TRASH, LCGraphicStyle.SECOND_DARK, "todo");

        // Command buttons
        GridPane gridButtons = new GridPane();
        gridButtons.setHgap(2.0);
        gridButtons.setVgap(2.0);
        gridButtons.setAlignment(Pos.CENTER);
        int rowIndex = 0;
        gridButtons.add(buttonAddKey, 0, rowIndex);
        gridButtons.add(buttonAddCategory, 1, rowIndex++);
        gridButtons.add(buttonDelete, 0, rowIndex);
        gridButtons.add(buttonAddLinkKey, 1, rowIndex++);
        gridButtons.add(new Separator(Orientation.HORIZONTAL), 0, rowIndex++, 2, 1);
        gridButtons.add(buttonCopy, 0, rowIndex);
        gridButtons.add(buttonCut, 1, rowIndex++);
        gridButtons.add(buttonPaste, 0, rowIndex++, 2, 1);
        gridButtons.add(new Separator(Orientation.HORIZONTAL), 0, rowIndex++, 2, 1);
        gridButtons.add(buttonMoveUp, 0, rowIndex);
        gridButtons.add(buttonMoveDown, 1, rowIndex++);

        keyListTreeView = new TreeView<>();
        keyListTreeView.setCellFactory(tv -> new KeyListNodeTreeCell());
        keyListTreeView.setShowRoot(false);
        keyListTreeView.setMaxHeight(200.0);
        HBox.setHgrow(keyListTreeView, Priority.ALWAYS);

        keyListTreeView.setOnDragExited(da -> {
            // FIXME : find a way to scroll up/down
            //            if (da.getY() > 0) {
            //treeViewItems.refresh();
            //            }
        });


        this.buttonExportKeys = UIUtils.createLeftTextButton(Translation.getText("general.configuration.view.key.list.button.export.keys"), LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.UPLOAD).size(14).color(LCGraphicStyle.MAIN_DARK), null);
        this.buttonExportKeys.setMaxWidth(Double.MAX_VALUE);
        this.buttonExportKeys.setAlignment(Pos.CENTER);

        this.buttonImportKeys = UIUtils.createLeftTextButton(Translation.getText("general.configuration.view.key.list.button.import.keys"), LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.DOWNLOAD).size(14).color(LCGraphicStyle.MAIN_DARK), null);
        this.buttonImportKeys.setMaxWidth(Double.MAX_VALUE);
        this.buttonImportKeys.setAlignment(Pos.CENTER);

        HBox boxExportImportsButtons = new HBox(10.0, buttonImportKeys, buttonExportKeys);
        boxExportImportsButtons.setAlignment(Pos.CENTER);

        HBox boxTreeAndCommands = new HBox(5.0, keyListTreeView, gridButtons);
        boxTreeAndCommands.setAlignment(Pos.CENTER);

        VBox boxTop = new VBox(2.0, boxExportImportsButtons, selectionPathContainer, boxTreeAndCommands);
        boxTop.setMaxWidth(Double.MAX_VALUE);
        boxTop.setAlignment(Pos.CENTER);
        keyListNodePropertiesEditionView = new KeyListNodePropertiesEditionView();

        // Total
        this.setTop(boxTop);
        this.setCenter(keyListNodePropertiesEditionView);
    }

    private Button createButton(FontAwesome.Glyph trash, Color color, String tooltip) {
        final Button button = UIUtils.createGraphicButton(LCGlyphFont.FONT_AWESOME.create(trash)
                .size(18).color(color), tooltip);
        //button.getStyleClass().addAll("padding-0");
        return button;
    }

    private KeyListNodeI dragged;

    private class KeyListNodeTreeCell extends TreeCell<KeyListNodeI> {
        private final ImageView imageView;

        KeyListNodeTreeCell() {
            final String nodeIdForImageLoading = "KeyListNodeTreeCell" + this.hashCode();
            imageView = new ImageView();
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            imageView.setSmooth(true);
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
            final TreeItem<KeyListNodeI> selectedItem = this.keyListTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue() != null) {
                final KeyListNodeI selectedNode = selectedItem.getValue();
                final KeyListNodeI parentNode = selectedNode.parentProperty().get();
                final int i = parentNode.getChildren().indexOf(selectedNode);
                parentNode.getChildren().add(i + 1, new KeyListNode());
            }
        });
        this.buttonDelete.setOnAction(e -> {
            final TreeItem<KeyListNodeI> selectedItem = this.keyListTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue() != null) {
                final KeyListNodeI selectedNode = selectedItem.getValue();
                final KeyListNodeI parentNode = selectedNode.parentProperty().get();
                parentNode.getChildren().remove(selectedNode);
            }
        });
        this.buttonAddCategory.setOnAction(this::addButtonOnAction);
        this.buttonAddLinkKey.setOnAction(this::addButtonOnAction);
        //        this.buttonExportKeys.setOnAction(e ->
        //                ConfigActionController.INSTANCE.executeAction(new KeyListActions.ExportKeyListsAction(buttonExportKeys, new ArrayList<>(this.listItemList.getSelectionModel().getSelectedItems()))));
        this.buttonImportKeys.setOnAction(e -> {
            ConfigActionController.INSTANCE.executeAction(new KeyListActions.ImportKeyListsAction(buttonImportKeys, importedNode -> {
                //                // FIXME : check if need to duplicate imported node (if already present in the current node tree)
                //                List<KeyListNodeI> nodesToAdd = importedNode;
                //                if (!nodesToAdd.isEmpty()) {
                //                    // Add just after current item, or to the edited node
                //                    final KeyListNodeI selectedItem = this.listItemList.getSelectionModel().getSelectedItem();
                //                    if (selectedItem != null) {
                //                        final int addIndex = editedNode.get().getChildren().indexOf(selectedItem);
                //                        editedNode.get().getChildren().addAll(addIndex + 1, nodesToAdd);
                //                    } else {
                //                        editedNode.get().getChildren().addAll(nodesToAdd);
                //                    }
                //                    commonListViewActionContainer.selectAndScrollTo(nodesToAdd.get(0));
                //                }
            }));
        });
        //        this.keyListNodePropertiesEditionView.setAddRequestListener(() -> commonListViewActionContainer.addAndScrollTo(new KeyListLeaf()));
        //        this.keyListNodePropertiesEditionView.setRemoveRequestListener(commonListViewActionContainer::deleteItem);

        //        this.commonListViewActionContainer.setDuplicateFunction(item -> {
        //            KeyListNodeI duplicated = (KeyListNodeI) item.duplicate(true);
        //            duplicated.textProperty().set(Translation.getText("general.configuration.view.key.list.copy.label.key.text") + " " + duplicated.textProperty().get());
        //            return duplicated;
        //        });
        //        this.commonListViewActionContainer.setDragEndPriorTester(dragDestination -> !dragDestination.isLeafNode());
        //        this.commonListViewActionContainer.setDragEndAcceptor((dragDestination, draggedNode) -> {
        //            // For each node : remove from its parent and moves it to the new parent
        //            // only if : not the node moved on itself or moved to its current parent
        //            final KeyListNodeI draggedNodeParent = draggedNode.parentProperty().get();
        //            if (draggedNodeParent != dragDestination && dragDestination != draggedNode) {
        //                draggedNodeParent.getChildren().remove(draggedNode);
        //                dragDestination.getChildren().add(draggedNode);
        //                return true;
        //            }
        //            return false;
        //        });
        //        this.commonListViewActionContainer.setDragFinishedConsumer((movedCount, dragDestination) -> LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("general.configuration.view.keylist.moved." +
        //                (movedCount > 1 ? "plural." : "") + "result", movedCount, getFullNameForNode(dragDestination)))));
        //        this.commonListViewActionContainer.setDoubleClicConsumer(this::configureNodeChildren);
    }
    //========================================================================

    // BINDING
    //========================================================================
    @Override
    public void initBinding() {
        this.rootKeyListNode.addListener((obs, ov, nv) -> {
            if (nv != null) {
                this.keyListTreeView.setRoot(new KeyListNodeTreeItem(nv));
            } else {
                this.keyListTreeView.setRoot(null);
            }
        });
        final MonadicBinding<KeyListNodeI> keyListNodeIMonadicBinding = EasyBind.select(keyListTreeView.getSelectionModel().selectedItemProperty()).selectObject(TreeItem::valueProperty).orElse((KeyListNodeI) null);
        keyListNodeIMonadicBinding.addListener((obs, ov, nv) -> {
            selectionPathContainer.getChildren().clear();
            if (nv != null) {
                KeyListNodeI current = nv;
                while (current != null) {
                    KeyListNodeI nodeForLink = current;
                    final KeyListNodeI currentParent = nodeForLink.parentProperty().get();
                    final Hyperlink hyperlink = new Hyperlink(getFullNameForNode(nodeForLink));
                    hyperlink.setOnAction(e -> {
                        // TODO : fix node in tree
                        //rootKeyListNode.set(nodeForLink)
                        //keyListTreeView.getSelectionModel().select();
                    });
                    selectionPathContainer.getChildren().add(0, hyperlink);
                    current = currentParent;
                    if (current != null) {
                        selectionPathContainer.getChildren().add(0, LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(12).color(LCGraphicStyle.MAIN_DARK));
                    }
                }
            }
        });

        // FIXME : check this for memory
        this.keyListNodePropertiesEditionView.selectedNodeProperty().bind(keyListNodeIMonadicBinding);
    }

    private String getFullNameForNode(KeyListNodeI node) {
        return (node.parentProperty().get() == null ? Translation.getText("general.configuration.view.keylist.root.node.text") :
                StringUtils.isNotBlank(node.textProperty().get()) ? node.textProperty().get() : Translation.getText("general.configuration.view.keylist.empty.node.text"))
                + " " + Translation.getText("general.configuration.view.keylist.node.level", node.levelProperty());
    }
    //========================================================================


    // HELPER
    //========================================================================
    private void addButtonOnAction(ActionEvent actionEvent) {
        KeyListNodeI added = actionEvent.getSource() == buttonAddCategory ? new KeyListNode() : actionEvent.getSource() == buttonAddKey ? new KeyListLeaf() : new KeyListLinkLeaf();
        //this.commonListViewActionContainer.addAndScrollTo(added);
    }

    void configureNodeChildren(KeyListNodeI item) {
        if (item != null && !item.isLeafNode()) {
            rootKeyListNode.set(item);
        }
    }

    public void selectToBeEditedInTree(KeyListNodeI item) {
        rootKeyListNode.set(item.parentProperty().get());
        //this.commonListViewActionContainer.selectAndScrollTo(item);
    }
    //========================================================================

}
