package org.lifecompanion.plugin.ppp.actions;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.plugin.ppp.model.Choice;

public interface SelectChoiceActionI extends BaseUseActionI<UseActionTriggerComponentI> {
    public ObjectProperty<Choice> choiceProperty();
}
