package org.lifecompanion.plugin.phonecontrol.action;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.phonecontrol.PhoneControlController;
import org.lifecompanion.plugin.phonecontrol.action.categories.PhoneControlActionSubCategories;
import java.util.Map;

/**
 * @author Etudiants IUT Vannes : HASCOËT Anthony, GUERNY Baptiste,
 *         Le CHANU Simon, PAVOINE Oscar
 */
public class PickUpAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public PickUpAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.CALL;
        this.parameterizableAction = false;
        this.nameID = "phonecontrol.plugin.action.call.pickup.name";
        this.staticDescriptionID = "phonecontrol.plugin.action.call.pickup.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/pickup.png";
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        PhoneControlController.INSTANCE.pickUp();
    }
}
