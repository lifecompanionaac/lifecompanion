package org.lifecompanion.plugin.homeassistant.view.listcell;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.lifecompanion.plugin.homeassistant.model.HAEntity;

public class HAEntitySimpleListCell extends ListCell<HAEntity> {
    @Override
    protected void updateItem(final HAEntity item, final boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            this.setText(item.getFriendlyName());
        } else {
            setText(null);
        }
    }
}
