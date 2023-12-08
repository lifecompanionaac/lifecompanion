package org.lifecompanion.plugin.ppp.actions;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.WordPredictionKeyOption;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.usevariable.FlagUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.plugin.ppp.actions.categories.PPPActionSubCategories;
import org.lifecompanion.plugin.ppp.keyoption.UserGroupCellKeyOption;
import org.lifecompanion.plugin.ppp.model.Action;
import org.lifecompanion.plugin.ppp.model.UserGroup;
import org.lifecompanion.plugin.ppp.services.ActionService;
import org.lifecompanion.plugin.ppp.services.UserDatabaseService;

import java.util.Map;

public class UserGroupSelectAction extends SimpleUseActionImpl<GridPartKeyComponentI> {
    public UserGroupSelectAction() {
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
            if (parentKey.keyOptionProperty().get() instanceof UserGroupCellKeyOption userGroupCellKeyOption) {
                UserGroup userGroup = userGroupCellKeyOption.groupProperty().get();
                if (userGroup != null) {
                    UserDatabaseService.INSTANCE.selectGroup(userGroup);
                } else {
                    System.out.println("Null user group !");
                    variables.put(UseActionController.FLAG_INTERRUPT_EXECUTION, new FlagUseVariable(new UseVariableDefinition(UseActionController.FLAG_INTERRUPT_EXECUTION)));
                }
            }
        }
    }
}
