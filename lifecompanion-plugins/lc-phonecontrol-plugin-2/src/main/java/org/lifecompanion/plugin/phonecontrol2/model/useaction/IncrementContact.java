package org.lifecompanion.plugin.phonecontrol2.model.useaction;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.phonecontrol2.controller.PhoneControlController;

import java.util.Map;

public class IncrementContact extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public IncrementContact() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.CONTACT;
        this.parameterizableAction = false;
        this.nameID = "phonecontrol2.plugin.action.contact.suivant.name";
        this.staticDescriptionID = "phonecontrol2.plugin.action.contact.suivant.description";
        this.configIconPath = "current/contact.png";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        PhoneControlController.INSTANCE.incrementContact();
    }
}
