package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.keylist;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.binding.ListBindingWithMapper;
import org.lifecompanion.util.javafx.FXControlUtils;

public class KeyListContentPane extends StackPane implements LCViewInitHelper {

    private final KeyListContentConfigView keyListContentConfigView;

    private Runnable previousContentViewUnbind;
    private Button buttonParentNode;
    private FlowPane flowPane;
    private ScrollPane scrollPane;

    public KeyListContentPane(KeyListContentConfigView keyListContentConfigView) {
        this.keyListContentConfigView = keyListContentConfigView;
        initAll();
    }

    @Override
    public void initUI() {
        flowPane = new FlowPane(10, 10);
        flowPane.setAlignment(Pos.CENTER);

        scrollPane = new ScrollPane(flowPane);
        scrollPane.setFitToWidth(true);

        flowPane.prefWrapLengthProperty().bind(scrollPane.widthProperty().subtract(10.0));

        // TODO : better button
        buttonParentNode = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.REPLY).size(28).color(LCGraphicStyle.MAIN_DARK), null);
        StackPane.setAlignment(buttonParentNode, Pos.TOP_LEFT);
        StackPane.setMargin(buttonParentNode, new Insets(3.0));

        this.getChildren().addAll(scrollPane, buttonParentNode);

        this.getStyleClass().addAll("border-lightgrey", "background-white");
    }


    @Override
    public void initListener() {
        buttonParentNode.setOnAction(e -> {
//            KeyListNodeI nodeV = node.get();
//            if (nodeV != null && nodeV.parentProperty().get() != null) {
//                node.set(nodeV.parentProperty().get());
//            }
        });
    }

    @Override
    public void initBinding() {
        keyListContentConfigView.selectedProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                flowPane.getChildren()
                        .stream()
                        .filter(node -> node instanceof KeyListContentPaneCell)
                        .map(node -> (KeyListContentPaneCell) node)
                        .filter(cell -> cell.itemProperty().get() == ov)
                        .forEach(cell -> cell.selectedProperty().set(false));
            }
            if (nv != null) {
                flowPane.getChildren()
                        .stream()
                        .filter(node -> node instanceof KeyListContentPaneCell)
                        .map(node -> (KeyListContentPaneCell) node)
                        .filter(cell -> cell.itemProperty().get() == nv)
                        .peek(cell -> cell.selectedProperty().set(true))
                        .findAny()
                        .ifPresent(cell -> {
                            double yPos = cell.getBoundsInParent().getMaxY();
                            scrollPane.setVvalue(yPos / flowPane.getHeight());
                        });
            }
        });
        keyListContentConfigView.currentListProperty().addListener((obs, ov, nv) -> {
            // Unbind previous
            if (previousContentViewUnbind != null) {
                previousContentViewUnbind.run();
                previousContentViewUnbind = null;
            }
            // Bind new content
            if (nv != null) {
                previousContentViewUnbind = ListBindingWithMapper.mapContent(flowPane.getChildren(), nv.getChildren(), item -> {
                    KeyListContentPaneCell cell = new KeyListContentPaneCell(this);
                    cell.itemProperty().set(item);
                    return cell;
                });
            }
        });
    }

    // INTERNAL SELECTION
    //========================================================================
    void select(KeyListNodeI item) {
        keyListContentConfigView.v2Select(item);
    }

    void openList(KeyListNodeI item) {
        if (!item.isLeafNode()) {
            keyListContentConfigView.v2OpenListFromList(item);
        }
    }
    //========================================================================
}
