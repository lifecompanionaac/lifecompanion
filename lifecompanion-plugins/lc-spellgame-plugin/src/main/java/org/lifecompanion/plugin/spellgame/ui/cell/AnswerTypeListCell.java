package org.lifecompanion.plugin.spellgame.ui.cell;

import javafx.scene.control.ListCell;
import org.lifecompanion.plugin.spellgame.model.AnswerTypeEnum;
import org.lifecompanion.plugin.spellgame.model.SpellGameWordList;
import org.lifecompanion.util.binding.BindingUtils;

public class AnswerTypeListCell extends ListCell<AnswerTypeEnum> {
    @Override
    protected void updateItem(final AnswerTypeEnum item, final boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            BindingUtils.unbindAndSetNull(textProperty());
        } else {
            this.setText(item.getName());
        }
    }
}

