package org.lifecompanion.plugin.homeassistant.view.listcell;

import javafx.scene.control.ListCell;
import org.lifecompanion.plugin.homeassistant.model.HAEntity;
import org.lifecompanion.plugin.homeassistant.model.HAService;

public class HAServiceSimpleListCell extends ListCell<HAService> {
    @Override
    protected void updateItem(final HAService item, final boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            this.setText(item.getName());
        } else {
            setText(null);
        }
    }
}
