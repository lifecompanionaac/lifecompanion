package org.lifecompanion.plugin.homeassistant.view.listcell;

import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.lifecompanion.plugin.homeassistant.model.HAService;

public class HAServiceListCell extends ListCell<HAService> {
    private final Label labelName;
    private final Label labelDescription;

    public HAServiceListCell() {
        super();
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.labelName = new Label();
        this.labelDescription = new Label();
        this.labelDescription.setWrapText(true);
        this.labelDescription.setFont(Font.font(10));

        // Add all
        VBox boxLabels = new VBox(2);
        VBox.setMargin(this.labelDescription, new Insets(1.0, 0.0, 0.0, 0.0));
        BorderPane paneGraphics = new BorderPane();
        boxLabels.getChildren().addAll(this.labelName, this.labelDescription);
        paneGraphics.setCenter(boxLabels);

        this.setPrefWidth(250);
        this.setPrefHeight(40);
        this.setGraphic(paneGraphics);
    }

    @Override
    protected void updateItem(final HAService item, final boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            this.labelName.setText(item.getName() + " (" + item.getServiceId() + ")");
            this.labelDescription.setText(item.getDescription());
        }
    }
}

