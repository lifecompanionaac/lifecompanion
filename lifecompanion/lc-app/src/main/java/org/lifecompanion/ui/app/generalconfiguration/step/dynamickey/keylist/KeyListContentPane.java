package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.keylist;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.lifecompanion.controller.configurationcomponent.dynamickey.KeyListController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListLeaf;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListLinkLeaf;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListNode;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.util.binding.ListBindingWithMapper;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class KeyListContentPane extends StackPane implements LCViewInitHelper {

    private boolean tempDisableScrollTo;

    private final KeyListContentConfigView keyListContentConfigView;
    private Runnable previousContentViewUnbind;
    private Button buttonParentNode, buttonShowAddChoices;
    private Button buttonAddKey, buttonAddCategory, buttonAddLinkKey;
    private VBox boxAddButtons;
    private TilePane tilePane;
    private ScrollPane scrollPane;

    public KeyListContentPane(KeyListContentConfigView keyListContentConfigView) {
        this.keyListContentConfigView = keyListContentConfigView;
        initAll();
    }

    public Button getButtonAddKey() {
        return buttonAddKey;
    }

    @Override
    public void initUI() {
        tilePane = new TilePane(Orientation.HORIZONTAL, 10, 10);
        tilePane.setAlignment(Pos.TOP_LEFT);

        scrollPane = new ScrollPane(tilePane);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        buttonParentNode = createFloatingButton("background-third", null, GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.REPLY).size(14).color(Color.WHITE));
        StackPane.setAlignment(buttonParentNode, Pos.TOP_LEFT);
        StackPane.setMargin(buttonParentNode, new Insets(3.0));

        buttonShowAddChoices = createFloatingButton("background-primary-dark",
                "keylist.content.pane.button.add",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS).size(16).color(Color.WHITE));
        StackPane.setAlignment(buttonShowAddChoices, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(buttonShowAddChoices, new Insets(0.0, 18.0, 4.0, 0.0));

        this.buttonAddCategory = createFloatingButton("background-primary-dark-light",
                "keylist.content.pane.button.add.list",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.FOLDER).size(16).color(Color.GRAY));
        this.buttonAddKey = createFloatingButton("background-primary-dark-light", "keylist.content.pane.button.add.key", new ImageView(IconHelper.get("keylist/icon_type_leaf.png")));
        this.buttonAddLinkKey = createFloatingButton("background-primary-dark-light",
                "keylist.content.pane.button.add.link",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.LINK).size(16).color(Color.GRAY));
        boxAddButtons = new VBox(4.0);
        List.of(buttonAddLinkKey, buttonAddCategory, buttonAddKey).forEach(button -> {
            button.getStyleClass().remove("text-fill-white");
            button.getStyleClass().add("text-fill-dimgrey");
            boxAddButtons.getChildren().add(button);
        });
        boxAddButtons.setAlignment(Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(boxAddButtons, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(boxAddButtons, new Insets(0.0, 18.0, 40, 0.0));
        boxAddButtons.setVisible(false);

        this.getChildren().addAll(scrollPane, buttonParentNode, buttonShowAddChoices, boxAddButtons);

        scrollPane.getStyleClass().addAll("scrollpane-white-viewport", "background-white");
    }

    private Button createFloatingButton(String background, String text, Node graphics) {
        Button button = FXControlUtils.createLeftTextButton(text != null ? Translation.getText(text) : null, graphics, null);
        button.getStyleClass().remove("background-none");
        button.getStyleClass().addAll(background, "text-fill-white", "drop-shadow-1", "background-radius-10", "border-transparent");
        return button;
    }

    @Override
    public void initListener() {
        buttonParentNode.setOnAction(e -> keyListContentConfigView.goToParent());
        buttonShowAddChoices.setOnAction(e -> toggleAddChoices());
        this.buttonAddKey.setOnAction(createAddNodeListener(KeyListLeaf::new));
        this.buttonAddLinkKey.setOnAction(createAddNodeListener(KeyListLinkLeaf::new));
        this.buttonAddCategory.setOnAction(createAddNodeListener(KeyListNode::new));
        this.setOnMouseClicked(e -> hideAddChoices());

        buttonParentNode.setOnDragOver(ea -> {
            if (keyListContentConfigView.draggedProperty().get() != null) {
                ea.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        });
        buttonParentNode.setOnDragDropped(ea -> keyListContentConfigView.dragDroppedOn(keyListContentConfigView.currentListProperty().get().parentProperty().get()));
    }


    @Override
    public void initBinding() {
        buttonParentNode.visibleProperty()
                .bind(Bindings.createBooleanBinding(() -> keyListContentConfigView.currentListProperty().get() != null && keyListContentConfigView.currentListProperty()
                                .get()
                                .parentProperty()
                                .get() != null,
                        keyListContentConfigView.currentListProperty()));
        tilePane.getChildren().addListener(BindingUtils.createListChangeListenerV2(added -> {
        }, removed -> {
            if (removed instanceof KeyListContentPaneCell) {
                ((KeyListContentPaneCell) removed).itemProperty().set(null);
            }
        }));
        keyListContentConfigView.selectedProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                getChildrenPaneCellStream()
                        .filter(cell -> cell.itemProperty().get() == ov)
                        .forEach(cell -> cell.selectedProperty().set(false));
            }
            if (nv != null) {
                getChildrenPaneCellStream()
                        .filter(cell -> cell.itemProperty().get() == nv)
                        .peek(cell -> cell.selectedProperty().set(true))
                        .findAny()
                        .ifPresent(cell -> {
                            if (!tempDisableScrollTo) {
                                double h = scrollPane.getContent().getBoundsInLocal().getHeight();
                                double y = (cell.getBoundsInParent().getMaxY() +
                                        cell.getBoundsInParent().getMinY()) / 2.0;
                                double v = scrollPane.getViewportBounds().getHeight();
                                scrollPane.setVvalue(scrollPane.getVmax() * ((y - 0.5 * v) / (h - v)));
                            }
                        });
            }
        });
        keyListContentConfigView.currentListProperty().addListener((obs, ov, nv) -> {
            // Unbind previous
            if (previousContentViewUnbind != null) {
                previousContentViewUnbind.run();
                previousContentViewUnbind = null;
            }
            tilePane.getChildren().clear();
            // Bind new content
            if (nv != null) {
                previousContentViewUnbind = ListBindingWithMapper.mapContent(tilePane.getChildren(), nv.getChildren(), item -> {
                    KeyListContentPaneCell cell = new KeyListContentPaneCell(this, this.keyListContentConfigView);
                    cell.itemProperty().set(item);
                    if (item == keyListContentConfigView.selectedProperty().get())
                        cell.selectedProperty().set(true);
                    return cell;
                });
            }
        });
        tilePane.prefColumnsProperty().bind(KeyListController.INSTANCE.columnCountEstimationProperty());
    }

    private Stream<KeyListContentPaneCell> getChildrenPaneCellStream() {
        return tilePane.getChildren()
                .stream()
                .filter(node -> node instanceof KeyListContentPaneCell)
                .map(node -> (KeyListContentPaneCell) node);
    }

    void setTempDisableScrollTo(boolean tempDisableScrollTo) {
        this.tempDisableScrollTo = tempDisableScrollTo;
    }

    // ADD NODES
    //========================================================================
    private EventHandler<ActionEvent> createAddNodeListener(Supplier<KeyListNodeI> supplier) {
        return ev -> {
            hideAddChoices();
            addNode(supplier.get());
        };
    }

    void addNode(KeyListNodeI toAdd) {
        addNode(List.of(toAdd));
    }

    void addNode(List<KeyListNodeI> toAdd) {
        if (LangUtils.isNotEmpty(toAdd)) {
            this.keyListContentConfigView.markDirty();
            KeyListNodeI currentList = this.keyListContentConfigView.currentListProperty().get();
            KeyListNodeI selected = this.keyListContentConfigView.selectedProperty().get();
            if (currentList != null && !currentList.isLeafNode()) {
                final int selectedIndex = selected != null ? currentList.getChildren().indexOf(selected) : -1;
                final int addIndex = selectedIndex >= 0 ? selectedIndex + 1 : currentList.getChildren().size();
                currentList.getChildren().addAll(addIndex, toAdd);
                LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("notification.keylist.node.added").withMsDuration(LCGraphicStyle.SHORT_NOTIFICATION_DURATION_MS));
                keyListContentConfigView.select(toAdd.get(0));
            }
        }
    }

    private void toggleAddChoices() {
        if (boxAddButtons.isVisible()) hideAddChoices();
        else showAddChoices();
    }

    private void hideAddChoices() {
        boxAddButtons.setVisible(false);
    }

    private void showAddChoices() {
        boxAddButtons.setVisible(true);
    }
    //========================================================================
}
