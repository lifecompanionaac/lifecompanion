package org.lifecompanion.plugin.spellgame.ui.cell;

import javafx.scene.control.ListCell;
import org.lifecompanion.plugin.spellgame.model.SpellGameWordList;
import org.lifecompanion.util.binding.BindingUtils;

public class SpellGameWordListListCell extends ListCell<SpellGameWordList> {
    @Override
    protected void updateItem(final SpellGameWordList item, final boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            BindingUtils.unbindAndSetNull(textProperty());
        } else {
            this.textProperty().bind(item.nameProperty());
        }
    }
}

