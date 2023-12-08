package org.lifecompanion.plugin.ppp.actions;

import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
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

public class UserProfileSelectAction extends SimpleUseActionImpl<GridPartKeyComponentI> {
    public UserProfileSelectAction() {
        super(GridPartKeyComponentI.class);
        this.nameID = "todo";
        this.staticDescriptionID = "todo";
        this.category = PPPActionSubCategories.VARIOUS;
        this.order = 40;
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "actions/icon_action_select.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            if (parentKey.keyOptionProperty().get() instanceof UserProfileCellKeyOption userProfileCellKeyOption) {
                UserProfile user = userProfileCellKeyOption.userProperty().get();
                if (user != null) {
                    UserDatabaseService.INSTANCE.selectUser(user);
                } else {
                    variables.put(UseActionController.FLAG_INTERRUPT_EXECUTION, new FlagUseVariable(new UseVariableDefinition(UseActionController.FLAG_INTERRUPT_EXECUTION)));
                }
            }
        }
    }
}
