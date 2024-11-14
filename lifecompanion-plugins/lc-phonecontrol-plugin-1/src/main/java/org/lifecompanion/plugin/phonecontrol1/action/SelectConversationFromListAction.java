package org.lifecompanion.plugin.phonecontrol1.action;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.phonecontrol1.PhoneControlController;
import org.lifecompanion.plugin.phonecontrol1.action.categories.PhoneControlActionSubCategories;
import org.lifecompanion.plugin.phonecontrol1.keyoption.ConversationListKeyOption;

import java.util.Map;

/**
 * @author Etudiants IUT Vannes : HASCOËT Anthony, GUERNY Baptiste,
 *         Le CHANU Simon, PAVOINE Oscar
 */
public class SelectConversationFromListAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

    public SelectConversationFromListAction() {
        super(GridPartKeyComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.MISC;
        this.parameterizableAction = false;
        this.nameID = "phonecontrol1.plugin.action.misc.select.conversation.fromlist.name";
        this.staticDescriptionID = "phonecontrol1.plugin.action.misc.select.conversation.fromlist.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/select_conv.png";
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            if (parentKey.keyOptionProperty().get() instanceof ConversationListKeyOption) {
                ConversationListKeyOption conversationListKeyOption = (ConversationListKeyOption) parentKey
                        .keyOptionProperty().get();
                String phoneNumber = conversationListKeyOption.convProperty().get().getPhoneNumber();
                String phoneNumberOrContactName = conversationListKeyOption.convProperty().get().getContactName();
                PhoneControlController.INSTANCE.selectConv(phoneNumber, phoneNumberOrContactName);
            }
        }
    }
}
