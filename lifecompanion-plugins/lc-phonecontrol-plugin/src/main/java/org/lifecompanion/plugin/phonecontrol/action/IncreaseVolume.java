package org.lifecompanion.plugin.phonecontrol.action;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.phonecontrol.action.categories.PhoneControlActionSubCategories;
import org.lifecompanion.plugin.phonecontrol.controller.SystemController;

import java.util.Map;

public class IncreaseVolume extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    public IncreaseVolume() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.SYSTEM;
        this.parameterizableAction = false;
        this.nameID = "phonecontrol.plugin.action.increasevolume.name";
        this.staticDescriptionID = "phonecontrol.plugin.action.increasevolume.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/volume_plus.png";
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        SystemController.INSTANCE.adjustVolume("increase");
    }
}
