package org.lifecompanion.plugin.phonecontrol.action;

import org.lifecompanion.plugin.phonecontrol.PhoneControlController;
import org.lifecompanion.plugin.phonecontrol.action.categories.PhoneControlActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.controller.textcomponent.WritingStateController;

import java.util.Map;

public class SelectConversationFromTextAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public SelectConversationFromTextAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.MISC;
        this.parameterizableAction = false;
        this.nameID = "phonecontrol1.plugin.action.misc.select.conversation.fromtext.name";
        this.staticDescriptionID = "phonecontrol1.plugin.action.misc.select.conversation.fromtext.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/select_conv.png";
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        String phoneNumber = WritingStateController.INSTANCE.currentTextProperty().get();
        PhoneControlController.INSTANCE.selectConv(phoneNumber, phoneNumber);
        WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);
    }
}
