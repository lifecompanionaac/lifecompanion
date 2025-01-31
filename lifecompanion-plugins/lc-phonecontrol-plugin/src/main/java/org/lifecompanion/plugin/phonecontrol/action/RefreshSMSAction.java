package org.lifecompanion.plugin.phonecontrol.action;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.phonecontrol.action.categories.PhoneControlActionSubCategories;
import org.lifecompanion.plugin.phonecontrol.controller.ConnexionController;

import java.util.Map;

public class RefreshSMSAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    public RefreshSMSAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.REFRESH;
        this.parameterizableAction = false;
        this.nameID = "phonecontrol.plugin.action.refreshsmslist.name";
        this.staticDescriptionID = "phonecontrol.plugin.action.refreshsmslist.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/refresh.png";
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        ConnexionController.INSTANCE.refreshSMSList();
    }
}
