package org.lifecompanion.plugin.ppp.actions;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.ppp.actions.categories.PPPActionSubCategories;
import org.lifecompanion.plugin.ppp.services.ActionService;

import java.util.Map;

public class ActionNextPageAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    public ActionNextPageAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "ppp.plugin.actions.action.page.next.name";
        this.staticDescriptionID = "ppp.plugin.actions.action.page.next.description";
        this.category = PPPActionSubCategories.ACTION;
        this.order = 60;
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "actions/icon_action_next.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        ActionService.INSTANCE.nextActionsPage();
    }
}
