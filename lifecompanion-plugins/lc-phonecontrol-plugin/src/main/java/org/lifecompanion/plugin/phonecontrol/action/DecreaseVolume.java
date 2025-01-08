package org.lifecompanion.plugin.phonecontrol.action;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.phonecontrol.action.categories.PhoneControlActionSubCategories;
import org.lifecompanion.plugin.phonecontrol.controller.SystemController;

import java.util.Map;

public class DecreaseVolume extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    public DecreaseVolume() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.SYSTEM;
        this.parameterizableAction = false;
        this.nameID = "phonecontrol.plugin.action.decreasevolume.name";
        this.staticDescriptionID = "phonecontrol.plugin.action.decreasevolume.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/volume_minus.png";
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        SystemController.INSTANCE.adjustVolume("decrease");
    }
}
