package org.lifecompanion.plugin.aac4all.wp2.ui;

import javafx.scene.control.ListCell;
import org.lifecompanion.plugin.aac4all.wp2.model.logs.RandomType;



public class RandomTypeListCell extends ListCell<RandomType> {
    @Override
    protected void updateItem(final RandomType item, final boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            this.setText(null);
        } else {
            this.setText(item.getName());
        }
    }
}

