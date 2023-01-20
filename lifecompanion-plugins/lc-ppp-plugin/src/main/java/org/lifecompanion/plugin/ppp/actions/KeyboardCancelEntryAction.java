package org.lifecompanion.plugin.ppp.actions;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.ppp.actions.categories.PPPActionSubCategories;
import org.lifecompanion.plugin.ppp.services.KeyboardInputService;

import java.util.Map;

public class KeyboardCancelEntryAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    public KeyboardCancelEntryAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "ppp.plugin.actions.keyboard_cancel_entry.name";
        this.staticDescriptionID = "ppp.plugin.actions.keyboard_cancel_entry.description";
        this.category = PPPActionSubCategories.VARIOUS;
        this.order = 15;
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "actions/icon_keyboard_cancel_entry.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        KeyboardInputService.INSTANCE.cancelInput();
    }
}
