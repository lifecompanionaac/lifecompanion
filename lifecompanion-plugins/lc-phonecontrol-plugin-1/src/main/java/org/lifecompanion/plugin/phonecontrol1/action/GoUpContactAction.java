package org.lifecompanion.plugin.phonecontrol1.action;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.phonecontrol1.PhoneControlController;
import org.lifecompanion.plugin.phonecontrol1.action.categories.PhoneControlActionSubCategories;

import java.util.Map;

/**
 * @author Etudiants IUT Vannes : HASCOËT Anthony, GUERNY Baptiste,
 *         Le CHANU Simon, PAVOINE Oscar
 */
public class GoUpContactAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public GoUpContactAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.SMS;
        this.parameterizableAction = false;
        this.nameID = "phonecontrol1.plugin.action.contact.up.list.name";
        this.staticDescriptionID = "phonecontrol1.plugin.action.contact.up.list.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/list_up.png";
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        PhoneControlController.INSTANCE.upConvIndex();
    }
}
