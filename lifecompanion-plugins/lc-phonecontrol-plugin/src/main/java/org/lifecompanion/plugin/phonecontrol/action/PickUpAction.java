package org.lifecompanion.plugin.phonecontrol.action;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.phonecontrol.action.categories.PhoneControlActionSubCategories;
import org.lifecompanion.plugin.phonecontrol.controller.ConnexionController;

import java.util.Map;

public class PickUpAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    public PickUpAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.CALL;
        this.parameterizableAction = false;
        this.nameID = "phonecontrol.plugin.action.callpickup.name";
        this.staticDescriptionID = "phonecontrol.plugin.action.callpickup.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/pickup.png";
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        ConnexionController.INSTANCE.pickUp();
    }
}
