package org.lifecompanion.plugin.homeassistant.view.listcell;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.homeassistant.model.HAEntity;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;

import java.util.HashMap;
import java.util.Map;

public class HAEntityListCell extends ListCell<HAEntity> {
    private final Label labelName;
    private final Label labelDescription;
    private final BorderPane iconPane;

    public HAEntityListCell() {
        super();
        initGraphicsMap();

        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        // Labels
        this.labelName = new Label();
        this.labelDescription = new Label();
        this.labelDescription.setWrapText(true);
        this.labelDescription.setFont(Font.font(10));

        iconPane = new BorderPane();
        BorderPane.setMargin(iconPane, new Insets(5));

        // Add all
        VBox boxLabels = new VBox(2);
        VBox.setMargin(this.labelDescription, new Insets(1.0, 0.0, 0.0, 0.0));
        BorderPane paneGraphics = new BorderPane();
        BorderPane.setAlignment(iconPane, Pos.CENTER);
        boxLabels.getChildren().addAll(this.labelName, this.labelDescription);
        paneGraphics.setCenter(boxLabels);
        paneGraphics.setLeft(iconPane);

        // Configuration
        this.setPrefWidth(250);
        this.setPrefHeight(40);
        this.setGraphic(paneGraphics);
    }

    @Override
    protected void updateItem(final HAEntity item, final boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            this.labelDescription.setText("ID : " + item.getId());
            this.labelName.setText(item.getFriendlyName());
            this.iconPane.setCenter(getIconFor(item));
        }
    }

    private Node getIconFor(HAEntity entity) {
        Node g = graphicMap.get(entity.getDomainId());
        return g != null ? g : graphicMap.get("all");
    }

    private final Map<String, Node> graphicMap = new HashMap<>();

    private void initGraphicsMap() {
        final int size = 14;
        graphicMap.put("light", GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.LIGHTBULB_ALT).size(size).color(LCGraphicStyle.MAIN_DARK));
        graphicMap.put("sensor", GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.COGS).size(size).color(LCGraphicStyle.MAIN_DARK));
        graphicMap.put("cover", GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.SQUARE_ALT).size(size).color(LCGraphicStyle.MAIN_DARK));
        graphicMap.put("all", GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.QUESTION).size(size).color(LCGraphicStyle.MAIN_DARK));
    }
}
