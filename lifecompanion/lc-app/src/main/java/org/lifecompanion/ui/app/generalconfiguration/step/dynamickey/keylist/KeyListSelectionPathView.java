package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.keylist;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;

public class KeyListSelectionPathView extends HBox implements LCViewInitHelper {
    private final KeyListContentConfigView keyListContentConfigView;

    public KeyListSelectionPathView(KeyListContentConfigView keyListContentConfigView) {
        this.keyListContentConfigView = keyListContentConfigView;
        initAll();
    }

    @Override
    public void initUI() {
        setSpacing(5.0);
        setAlignment(Pos.CENTER_LEFT);
        setMinHeight(23.0);
    }

    @Override
    public void initListener() {
        LCViewInitHelper.super.initListener();
    }

    @Override
    public void initBinding() {
        keyListContentConfigView.selectedProperty().addListener(inv -> updatePathForSelection());
        keyListContentConfigView.currentListProperty().addListener(inv -> updatePathForSelection());
    }

    private void updatePathForSelection() {
        KeyListNodeI currentNode = keyListContentConfigView.selectedProperty().get() != null ? keyListContentConfigView.selectedProperty().get() : keyListContentConfigView.currentListProperty().get();
        getChildren().clear();
        if (currentNode != null) {
            KeyListNodeI current = currentNode;
            while (current != null) {
                KeyListNodeI nodeForLink = current;
                final KeyListNodeI currentParent = nodeForLink.parentProperty().get();
                createAndAddLinkForNode(nodeForLink);
                current = currentParent;
                if (current != null && current.parentProperty().get() != null) {
                    getChildren().add(0, GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(10).color(LCGraphicStyle.MAIN_DARK));
                }
            }
        }
    }

    private void createAndAddLinkForNode(KeyListNodeI nodeForLink) {
        if (nodeForLink != keyListContentConfigView.rootProperty().get()) {
            final Hyperlink hyperlink = new Hyperlink((nodeForLink.getHumanReadableText()));
            if (nodeForLink != keyListContentConfigView.rootProperty().get()) {
                hyperlink.setOnAction(e -> {
                    keyListContentConfigView.select(nodeForLink);
                });
            }
            getChildren().add(0, hyperlink);
        }
    }
}
