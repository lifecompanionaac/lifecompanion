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

package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.keylist;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.lifecompanion.controller.configurationcomponent.dynamickey.KeyListController;
import org.lifecompanion.controller.editaction.KeyListActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListLeaf;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListLinkLeaf;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListNode;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.ui.common.control.generic.FlowPaneListView;
import org.lifecompanion.ui.common.pane.specific.cell.KeyListCellHandler;
import org.lifecompanion.ui.common.pane.specific.cell.KeyListFlowPaneCell;
import org.lifecompanion.ui.common.pane.specific.cell.KeyListNodeTreeCell;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.binding.ListBindingWithMapper;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class KeyListContentConfigView extends VBox implements LCViewInitHelper {
    private static final double TREE_VIEW_HEIGHT = 200.0;
    private final ObjectProperty<KeyListNodeI> rootKeyListNode;
    private final ObjectProperty<List<KeyListNodeI>> searchResult;
    private final IntegerProperty foundIndex;
    private String lastSearch;
    private boolean dirty;
    private final ObjectProperty<KeyListNodeI> cutOrCopiedNode;
    private final Map<KeyListNodeI, KeyListNodeTreeItem> keyListTreeItems;

    private Button buttonAddKey, buttonAddCategory, buttonAddLinkKey, buttonDelete, buttonMoveUp, buttonMoveDown, buttonCut, buttonCopy, buttonPaste, buttonExportKeys, buttonImportKeys, buttonShowHideProperties;

    private TextField textFieldSearchNode;
    private Button buttonSearch, buttonPreviousFound, buttonNextFound, buttonClearSearch;
    private Label labelFoundNodeInfo;

    private HBox selectionPathContainer;
    private KeyListNodePropertiesEditionView keyListNodePropertiesEditionView;

    private TreeView<KeyListNodeI> keyListTreeView;
    private FlowPaneListView<KeyListNodeI> currentListContentView;
    private ScrollPane scrollPanePaneCurrentListContentView;

    private final BooleanProperty propertiesShowing;

    // Mandatory to avoid garbage collection
    private ObjectBinding<KeyListNodeI> currentSelectedNodeAndNotLeaf;

    public KeyListContentConfigView() {
        this.rootKeyListNode = new SimpleObjectProperty<>();
        this.cutOrCopiedNode = new SimpleObjectProperty<>();
        this.keyListTreeItems = new HashMap<>();
        this.propertiesShowing = new SimpleBooleanProperty(true);
        searchResult = new SimpleObjectProperty<>();
        foundIndex = new SimpleIntegerProperty();
        initAll();
    }

    public ObjectProperty<KeyListNodeI> rootKeyListNodeProperty() {
        return rootKeyListNode;
    }

    /**
     * Implementation note : will true only if structural changes are made (add, remove, move) and false if only node properties are changed (for easier dev)
     *
     * @return true if the edited key list nodes were modified.<br>
     */
    public boolean isDirty() {
        return dirty;
    }

    // UI
    //========================================================================
    @Override
    public void initUI() {
        selectionPathContainer = new HBox(5.0);
        selectionPathContainer.setAlignment(Pos.CENTER_LEFT);
        selectionPathContainer.setMinHeight(23.0);

        // TOP : list + add button
        this.buttonAddCategory = createActionButton(FontAwesome.Glyph.FOLDER, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.add.category");
        this.buttonAddKey = createActionButton(new ImageView(IconHelper.get("keylist/icon_add_leaf.png")), "tooltip.keylist.button.add.key");
        this.buttonAddLinkKey = createActionButton(FontAwesome.Glyph.LINK, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.add.link");
        buttonMoveUp = createActionButton(FontAwesome.Glyph.CHEVRON_UP, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.move.up");
        buttonMoveDown = createActionButton(FontAwesome.Glyph.CHEVRON_DOWN, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.move.down");
        buttonCopy = createActionButton(FontAwesome.Glyph.COPY, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.copy");
        buttonCut = createActionButton(FontAwesome.Glyph.CUT, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.cut");
        buttonPaste = createActionButton(FontAwesome.Glyph.PASTE, LCGraphicStyle.MAIN_DARK, "tooltip.keylist.button.paste");
        buttonDelete = createActionButton(FontAwesome.Glyph.TRASH, LCGraphicStyle.SECOND_DARK, "tooltip.keylist.button.delete");

        // Command buttons
        GridPane gridButtons = new GridPane();
        gridButtons.setHgap(2.0);
        gridButtons.setVgap(2.0);
        gridButtons.setAlignment(Pos.CENTER);
        int rowIndex = 0;
        GridPane.setMargin(buttonAddKey, new Insets(3.5, 0, 0, 0));
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
        keyListTreeView.setCellFactory(tv -> new KeyListNodeTreeCell(this::selectAndScrollToId));
        keyListTreeView.setShowRoot(false);
        keyListTreeView.setMaxHeight(Double.MAX_VALUE);
        keyListTreeView.setFixedCellSize(KeyListCellHandler.CELL_HEIGHT + 5);
        HBox.setHgrow(keyListTreeView, Priority.SOMETIMES);

        currentListContentView = new FlowPaneListView<>(KeyListFlowPaneCell::new);
        currentListContentView.setHgap(10.0);
        currentListContentView.setVgap(10.0);
        currentListContentView.setAlignment(Pos.CENTER);
        scrollPanePaneCurrentListContentView = new ScrollPane(currentListContentView);
        scrollPanePaneCurrentListContentView.setFitToWidth(true);
        scrollPanePaneCurrentListContentView.setMaxHeight(Double.MAX_VALUE);
        // TODO : style for background and border
        scrollPanePaneCurrentListContentView.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        scrollPanePaneCurrentListContentView.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, new Insets(0))));

        currentListContentView.prefWrapLengthProperty().bind(scrollPanePaneCurrentListContentView.widthProperty().subtract(10.0));
        HBox.setHgrow(scrollPanePaneCurrentListContentView, Priority.ALWAYS);

        this.buttonExportKeys = FXControlUtils.createLeftTextButton(Translation.getText("general.configuration.view.key.list.button.export.keys"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.UPLOAD).size(14).color(LCGraphicStyle.MAIN_DARK),
                null);
        this.buttonExportKeys.setMaxWidth(Double.MAX_VALUE);
        this.buttonExportKeys.setAlignment(Pos.CENTER);

        this.buttonImportKeys = FXControlUtils.createLeftTextButton(Translation.getText("general.configuration.view.key.list.button.import.keys"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.DOWNLOAD).size(14).color(LCGraphicStyle.MAIN_DARK),
                null);
        this.buttonImportKeys.setMaxWidth(Double.MAX_VALUE);
        this.buttonImportKeys.setAlignment(Pos.CENTER);

        textFieldSearchNode = new TextField();
        textFieldSearchNode.setPromptText(Translation.getText("generaL.configuration.view.key.list.search.prompt"));
        textFieldSearchNode.setPrefColumnCount(30);
        buttonSearch = createSearchBarButton(FontAwesome.Glyph.SEARCH, LCGraphicStyle.MAIN_DARK, "todo", 16);
        buttonNextFound = createSearchBarButton(FontAwesome.Glyph.CHEVRON_RIGHT, LCGraphicStyle.MAIN_DARK, "todo", 18);
        buttonPreviousFound = createSearchBarButton(FontAwesome.Glyph.CHEVRON_LEFT, LCGraphicStyle.MAIN_DARK, "todo", 18);
        buttonClearSearch = createSearchBarButton(FontAwesome.Glyph.TIMES, LCGraphicStyle.SECOND_DARK, "todo", 18);
        labelFoundNodeInfo = new Label();
        labelFoundNodeInfo.setPrefWidth(40.0);
        labelFoundNodeInfo.getStyleClass().add("text-weight-bold");

        HBox searchBox = new HBox(10.0, textFieldSearchNode, buttonSearch, buttonClearSearch, labelFoundNodeInfo, buttonPreviousFound, buttonNextFound);
        searchBox.setAlignment(Pos.CENTER);
        VBox.setMargin(searchBox, new Insets(10, 0, 0, 0));

        HBox boxExportImportsButtons = new HBox(10.0, buttonImportKeys, buttonExportKeys);
        boxExportImportsButtons.setAlignment(Pos.CENTER);

        HBox boxTreeAndCommands = new HBox(5.0, keyListTreeView, scrollPanePaneCurrentListContentView, gridButtons);
        boxTreeAndCommands.setAlignment(Pos.CENTER);
        VBox.setVgrow(boxTreeAndCommands, Priority.ALWAYS);

        buttonShowHideProperties = FXControlUtils.createLeftTextButton(Translation.getText("keylist.config.hide.key.properties"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.EYE_SLASH).size(18).color(LCGraphicStyle.MAIN_DARK),
                "TODO");
        buttonShowHideProperties.setAlignment(Pos.CENTER_RIGHT);

        keyListNodePropertiesEditionView = new KeyListNodePropertiesEditionView();
        VBox.setMargin(keyListNodePropertiesEditionView, new Insets(2, 0, 0, 0));
        keyListNodePropertiesEditionView.setPrefHeight(350.0);
        keyListNodePropertiesEditionView.setMinHeight(250.0);

        // Total
        this.setSpacing(2.0);
        this.getChildren().addAll(boxExportImportsButtons, searchBox, selectionPathContainer, boxTreeAndCommands, buttonShowHideProperties, keyListNodePropertiesEditionView);
        this.setAlignment(Pos.CENTER);
    }

    private Button createSearchBarButton(FontAwesome.Glyph icon, Color color, String tooltip, int size) {
        final Button button = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(icon).size(size).color(color), tooltip);
        button.getStyleClass().add("padding-0");
        return button;
    }

    private Button createActionButton(FontAwesome.Glyph glyph, Color color, String tooltip) {
        return createActionButton(GlyphFontHelper.FONT_AWESOME.create(glyph).size(22).color(color), tooltip);
    }

    private Button createActionButton(Node graphics, String tooltip) {
        final Button button = FXControlUtils.createGraphicButton(graphics, tooltip);
        button.setAlignment(Pos.CENTER);
        GridPane.setHalignment(button, HPos.CENTER);
        return button;
    }
    //========================================================================


    // LISTENER
    //========================================================================
    @Override
    public void initListener() {
        this.buttonAddKey.setOnAction(createAddNodeListener(KeyListLeaf::new));
        this.buttonAddLinkKey.setOnAction(createAddNodeListener(KeyListLinkLeaf::new));
        this.buttonAddCategory.setOnAction(createAddNodeListener(KeyListNode::new));
        this.buttonMoveUp.setOnAction(createMoveNodeListener(-1));
        this.buttonMoveDown.setOnAction(createMoveNodeListener(+1));
        this.buttonDelete.setOnAction(e -> ifSelectedItemNotNull(selectedNode -> removeNode(selectedNode, "keylist.action.removed.action.notification.title")));
        this.buttonCopy.setOnAction(e -> ifSelectedItemNotNull(selectedNode -> {
            KeyListNodeI duplicated = (KeyListNodeI) selectedNode.duplicate(true);
            cutOrCopiedNode.set(duplicated);
        }));
        this.buttonCut.setOnAction(e -> ifSelectedItemNotNull(selectedNode -> {
            cutOrCopiedNode.set(selectedNode);
            removeNode(selectedNode, "keylist.action.cut.action.notification.title");
        }));
        this.buttonPaste.setOnAction(e -> {
            final KeyListNodeI toPaste = cutOrCopiedNode.get();
            if (toPaste != null) {
                cutOrCopiedNode.set(null);
                addNodeToSelectedDestination(toPaste);
            }
        });
        this.buttonExportKeys.setOnAction(e ->
                ConfigActionController.INSTANCE.executeAction(new KeyListActions.ExportKeyListsAction(buttonExportKeys, this.rootKeyListNode.get())));
        this.buttonImportKeys.setOnAction(e ->
                ConfigActionController.INSTANCE.executeAction(new KeyListActions.ImportKeyListsAction(buttonImportKeys, this::addNodeToSelectedDestination)));

        buttonShowHideProperties.setOnAction(e -> toggleProperties());

        buttonSearch.setOnAction(e -> executeSearch(false));
        textFieldSearchNode.setOnAction(buttonSearch.getOnAction());
        textFieldSearchNode.textProperty().addListener((obs, ov, nv) -> clearSearch(false));
        buttonClearSearch.setOnAction(e -> clearSearch(true));
        buttonNextFound.setOnAction(e -> showNextSearchResult());
        buttonPreviousFound.setOnAction(e -> showPreviousSearchResult());

        List<Pair<KeyCombination, Button>> keyCombination = Arrays.asList(
                Pair.of(new KeyCodeCombination(KeyCode.DELETE), buttonDelete),
                Pair.of(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN), buttonCopy),
                Pair.of(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN), buttonCut),
                Pair.of(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN), buttonPaste),
                Pair.of(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN), buttonAddKey),
                Pair.of(new KeyCodeCombination(KeyCode.UP, KeyCombination.ALT_DOWN), buttonMoveUp),
                Pair.of(new KeyCodeCombination(KeyCode.DOWN, KeyCombination.ALT_DOWN), buttonMoveDown)
        );
        this.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            for (Pair<KeyCombination, Button> handler : keyCombination) {
                Button button = handler.getRight();
                if (!button.isDisabled() && handler.getLeft().match(keyEvent)) {
                    button.getOnAction().handle(null);
                    break;
                }
            }
        });
    }


    private void removeNode(KeyListNodeI selectedNode, String notificationTitle) {
        this.dirty = true;
        final KeyListNodeI parentNode = selectedNode.parentProperty().get();
        int previousIndex = parentNode.getChildren().indexOf(selectedNode);
        parentNode.getChildren().remove(selectedNode);
        keyListTreeView.getSelectionModel().clearSelection();
        executeSearch(true);
        LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText(notificationTitle, selectedNode.getHumanReadableText()),
                true,
                "keylist.action.remove.cancel",
                () -> {
                    if (!parentNode.getChildren().contains(selectedNode)) {
                        if (previousIndex > 0 && previousIndex <= parentNode.getChildren().size()) {
                            parentNode.getChildren().add(previousIndex, selectedNode);
                        } else {
                            parentNode.getChildren().add(0, selectedNode);
                        }
                        selectAndScrollTo(selectedNode);
                    }
                }));
    }

    private EventHandler<ActionEvent> createMoveNodeListener(final int indexMove) {
        return ae -> {
            ifSelectedItemNotNull(selectedNode -> {
                this.dirty = true;
                final ObservableList<KeyListNodeI> children = selectedNode.parentProperty().get().getChildren();
                int index = children.indexOf(selectedNode);
                if (index + indexMove >= 0 && index + indexMove < children.size()) {
                    Collections.swap(children, index, index + indexMove);
                    selectAndScrollTo(selectedNode);
                }
            });
        };
    }

    private EventHandler<ActionEvent> createAddNodeListener(Supplier<KeyListNodeI> supplier) {
        return ev -> addNodeToSelectedDestination(supplier.get());
    }
    //========================================================================

    // PROP PANEL
    //========================================================================
    private void toggleProperties() {
        if (this.propertiesShowing.get()) hideProperties();
        else showProperties();
    }

    private void hideProperties() {
        keyListNodePropertiesEditionView.setVisible(false);
        keyListNodePropertiesEditionView.setManaged(false);
        buttonShowHideProperties.setText(Translation.getText("keylist.config.show.key.properties"));
        this.propertiesShowing.set(false);
    }

    private void showProperties() {
        keyListNodePropertiesEditionView.setVisible(true);
        keyListNodePropertiesEditionView.setManaged(true);
        buttonShowHideProperties.setText(Translation.getText("keylist.config.hide.key.properties"));
        this.propertiesShowing.set(true);
    }
    //========================================================================

    // SEARCH
    //========================================================================
    private void showNextSearchResult() {
        if (foundIndex.get() + 1 < searchResult.get().size()) {
            foundIndex.set(foundIndex.get() + 1);
        } else {
            foundIndex.set(0);
        }
        updateDisplayedResult();
    }

    private void showPreviousSearchResult() {
        if (foundIndex.get() - 1 >= 0) {
            foundIndex.set(foundIndex.get() - 1);
        } else {
            foundIndex.set(searchResult.get().size() - 1);
        }
        updateDisplayedResult();
    }

    private void clearSearch(boolean clearTextField) {
        searchResult.set(null);
        lastSearch = null;
        if (clearTextField) {
            textFieldSearchNode.clear();
        }
    }

    private static final Comparator<Pair<KeyListNodeI, Double>> SCORE_MAP_COMPARATOR = (e1, e2) -> Double.compare(e2.getRight(), e1.getRight());

    private void executeSearch(boolean forceUpdate) {
        final String searchText = this.textFieldSearchNode.getText();
        if (StringUtils.isNotBlank(searchText)) {
            if (forceUpdate || StringUtils.isDifferent(searchText, lastSearch)) {
                lastSearch = searchText;

                foundIndex.set(0);
                // Create a list with all nodes
                List<KeyListNodeI> allNodes = new ArrayList<>(100);
                this.rootKeyListNode.get().traverseTreeToBottom(allNodes::add);

                // Search for similarity
                final List<KeyListNodeI> foundNodes = allNodes
                        .parallelStream()
                        .map(node -> Pair.of(node, getSimilarityScore(node, searchText)))
                        .sorted(SCORE_MAP_COMPARATOR)
                        .filter(e -> e.getRight() > ConfigurationComponentUtils.SIMILARITY_CONTAINS)
                        .map(Pair::getLeft)
                        .collect(Collectors.toList());
                this.searchResult.set(foundNodes);
                updateDisplayedResult();
            } else {
                showNextSearchResult();
            }
            textFieldSearchNode.requestFocus();
        }
    }

    private void updateDisplayedResult() {
        int i = foundIndex.get();
        final List<KeyListNodeI> resultList = searchResult.get();
        if (resultList != null && i >= 0 && i < resultList.size()) {
            final KeyListNodeI toSelect = resultList.get(i);
            selectAndScrollTo(toSelect);
        }
    }

    public double getSimilarityScore(KeyListNodeI node, String searchFull) {
        double score = 0.0;
        score += ConfigurationComponentUtils.getSimilarityScoreFor(searchFull, node,
                n -> Pair.of(n.textProperty().get(), 1.0),
                n -> Pair.of(n.textToWriteProperty().get(), node.enableWriteProperty().get() ? 0.8 : 0),
                n -> Pair.of(n.textToSpeakProperty().get(), node.enableSpeakProperty().get() ? 0.8 : 0)
        );
        return score;
    }
    //========================================================================

    // BINDING
    //========================================================================
    @Override
    public void initBinding() {
        this.buttonSearch.disableProperty().bind(textFieldSearchNode.textProperty().isEmpty());
        this.buttonClearSearch.disableProperty().bind(searchResult.isNull().and(textFieldSearchNode.textProperty().isEmpty()));
        this.buttonNextFound.visibleProperty().bind(searchResult.isNotNull());
        this.buttonNextFound.managedProperty().bind(buttonNextFound.visibleProperty());
        this.buttonPreviousFound.visibleProperty().bind(searchResult.isNotNull());
        this.buttonPreviousFound.managedProperty().bind(buttonNextFound.visibleProperty());
        this.labelFoundNodeInfo.visibleProperty().bind(searchResult.isNotNull());
        this.labelFoundNodeInfo.managedProperty().bind(labelFoundNodeInfo.visibleProperty());

        labelFoundNodeInfo.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    final List<KeyListNodeI> resultList = searchResult.get();
                    return resultList != null && !resultList.isEmpty() ? (foundIndex.get() + 1) + " / " + resultList.size() : Translation.getText("generaL.configuration.view.key.list.search.empty");
                },
                searchResult, foundIndex)
        );
        this.rootKeyListNode.addListener((obs, ov, nv) -> {
            keyListTreeItems.clear();
            clearSearch(true);
            updatePathForSelection(null);
            this.keyListTreeView.getSelectionModel().clearSelection();
            cutOrCopiedNode.set(null);
            this.dirty = false;
            if (nv != null) {
                this.keyListTreeView.setRoot(new KeyListNodeTreeItem(nv));
            } else {
                this.keyListTreeView.setRoot(null);
            }
        });

        // Bind on current selection value (but not root item)
        // this was necessary because it seems impossible to clear selection on tree view (to disabled selection on parent item)
        final MonadicBinding<KeyListNodeI> currentSelectedNode = EasyBind
                .select(keyListTreeView.getSelectionModel().selectedItemProperty())
                .selectObject(TreeItem::valueProperty)
                .orElse((KeyListNodeI) null);
        final ObjectBinding<KeyListNodeI> currentSelectionAndNotRoot = Bindings.createObjectBinding(() ->
                        currentSelectedNode.get() != rootKeyListNode.get() ? currentSelectedNode.get() : null
                , currentSelectedNode, rootKeyListNode);

        currentSelectionAndNotRoot.addListener((obs, ov, nv) -> updatePathForSelection(nv));
        this.keyListNodePropertiesEditionView.selectedNodeProperty().bind(currentSelectionAndNotRoot);

        this.buttonPaste.disableProperty().bind(this.cutOrCopiedNode.isNull());
        this.buttonCopy.disableProperty().bind(currentSelectionAndNotRoot.isNull());
        this.buttonCut.disableProperty().bind(currentSelectionAndNotRoot.isNull());
        this.buttonMoveUp.disableProperty().bind(currentSelectionAndNotRoot.isNull());
        this.buttonMoveDown.disableProperty().bind(currentSelectionAndNotRoot.isNull());
        this.buttonDelete.disableProperty().bind(currentSelectionAndNotRoot.isNull());

        currentSelectedNodeAndNotLeaf = Bindings.createObjectBinding(() -> currentSelectedNode.get() != null && (currentSelectedNode.get().isLeafNode() || currentSelectedNode.get().isLinkNode()) ? null : currentSelectedNode.get(), currentSelectedNode);
        currentSelectedNodeAndNotLeaf.addListener((obs, ov, nv) -> currentListContentView.bindItems(nv != null ? nv.getChildren() : null));
    }
    //========================================================================

    // SELECTION PATH
    //========================================================================
    private void updatePathForSelection(KeyListNodeI selected) {
        selectionPathContainer.getChildren().clear();
        if (selected != null) {
            KeyListNodeI current = selected;
            while (current != null) {
                KeyListNodeI nodeForLink = current;
                final KeyListNodeI currentParent = nodeForLink.parentProperty().get();
                createAndAddLinkForNode(nodeForLink);
                current = currentParent;
                if (current != null && current.parentProperty().get() != null) {
                    selectionPathContainer.getChildren().add(0, GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(10).color(LCGraphicStyle.MAIN_DARK));
                }
            }
        }
    }

    private void createAndAddLinkForNode(KeyListNodeI nodeForLink) {
        if (nodeForLink != rootKeyListNode.get()) {
            final Hyperlink hyperlink = new Hyperlink((nodeForLink.getHumanReadableText()));
            if (nodeForLink != rootKeyListNode.get()) {
                hyperlink.setOnAction(e -> {
                    selectAndScrollTo(nodeForLink);
                });
            }
            selectionPathContainer.getChildren().add(0, hyperlink);
        }
    }
    //========================================================================


    // FUNCTIONAL
    //========================================================================
    private void addNodeToSelectedDestination(KeyListNodeI toAdd) {
        addNodeToSelectedDestination(List.of(toAdd));
    }

    private void addNodeToSelectedDestination(List<KeyListNodeI> toAdd) {
        this.dirty = true;
        if (keyListTreeView.getRoot() != null) {
            final TreeItem<KeyListNodeI> selectedItem = this.keyListTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue() != null && selectedItem.getValue() != rootKeyListNode.get()) {
                final KeyListNodeI selectedNode = selectedItem.getValue();
                if (selectedNode.isLeafNode() || !selectedNode.getChildren().isEmpty()) {
                    final KeyListNodeI parentNode = selectedNode.parentProperty().get();
                    final int i = parentNode.getChildren().indexOf(selectedNode);
                    parentNode.getChildren().addAll(i + 1, toAdd);
                } else {
                    selectedNode.getChildren().addAll(0, toAdd);
                }
            } else {
                keyListTreeView.getRoot().getValue().getChildren().addAll(toAdd);
            }
            if (LangUtils.isNotEmpty(toAdd)) {
                selectAndScrollTo(toAdd.get(0));
                LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("notification.keylist.node.added").withMsDuration(LCGraphicStyle.SHORT_NOTIFICATION_DURATION_MS));
            }
        }
    }

    public void selectAndScrollToId(String itemId) {
        if (itemId != null && this.rootKeyListNode.get() != null) {
            final KeyListNodeI foundNode = KeyListController.findNodeByIdInSubtree(this.rootKeyListNode.get(), itemId);
            if (foundNode != null) {
                selectAndScrollTo(foundNode);
            }
        }
    }

    public void selectAndScrollTo(KeyListNodeI item) {
        final KeyListNodeTreeItem keyListNodeTreeItem = this.keyListTreeItems.get(item);
        if (keyListNodeTreeItem != null) {
            keyListTreeView.getSelectionModel().select(keyListNodeTreeItem);

            // Scroll to selection
            final int selectedIndex = this.keyListTreeView.getSelectionModel().getSelectedIndex();
            int indexToSelect = selectedIndex;
            // try to go back 3 index behind (better for UX)
            while (indexToSelect-- > 0 && selectedIndex - indexToSelect < 2) ;
            this.keyListTreeView.scrollTo(indexToSelect);
        }
    }

    private void ifSelectedItemNotNull(Consumer<KeyListNodeI> action) {
        final TreeItem<KeyListNodeI> selectedItem = this.keyListTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() != null && selectedItem.getValue() != rootKeyListNode.get()) {
            action.accept(selectedItem.getValue());
        }
    }
    //========================================================================

    // TREE ITEM
    //========================================================================
    private class KeyListNodeTreeItem extends TreeItem<KeyListNodeI> {
        public KeyListNodeTreeItem(KeyListNodeI value) {
            super(value);
            keyListTreeItems.put(value, this);
            if (!value.isLeafNode()) {
                ListBindingWithMapper.mapContent(getChildren(), value.getChildren(), KeyListNodeTreeItem::new);
            }
            this.expandedProperty().addListener((obs, ov, nv) -> selectAndScrollTo(value));
        }
    }
    //========================================================================

}
