package org.lifecompanion.plugin.phonecontrol.action;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.phonecontrol.PhoneControlController;
import org.lifecompanion.plugin.phonecontrol.action.categories.PhoneControlActionSubCategories;
import org.lifecompanion.plugin.phonecontrol.keyoption.ConversationListKeyOption;

import java.util.Map;

/**
 * @author Etudiants IUT Vannes : HASCOËT Anthony, GUERNY Baptiste,
 *         Le CHANU Simon, PAVOINE Oscar
 */
public class UnselectConversationAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public UnselectConversationAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.MISC;
        this.parameterizableAction = false;
        this.nameID = "phonecontrol.plugin.action.misc.unselect.conversation.name";
        this.staticDescriptionID = "phonecontrol.plugin.action.misc.unselect.conversation.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/unselect_conv.png";
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        PhoneControlController.INSTANCE.unselectConv();
    }
}
