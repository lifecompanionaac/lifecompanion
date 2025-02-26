package org.lifecompanion.plugin.ppp.actions;

import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.usevariable.FlagUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.plugin.ppp.actions.categories.PPPActionSubCategories;
import org.lifecompanion.plugin.ppp.keyoption.UserProfileCellKeyOption;
import org.lifecompanion.plugin.ppp.model.UserProfile;
import org.lifecompanion.plugin.ppp.services.UserDatabaseService;

import java.util.Map;

public class UserProfileNextPageAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    public UserProfileNextPageAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "ppp.plugin.actions.user.profile.next.page.name";
        this.staticDescriptionID = "ppp.plugin.actions.user.profile.next.page.description";
        this.category = PPPActionSubCategories.USER_GROUPS;
        this.order = 40;
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "actions/next_user_page.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        UserDatabaseService.INSTANCE.nextPageInGroup();
    }
}
