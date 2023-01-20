package org.lifecompanion.plugin.ppp.actions;

import javafx.beans.property.BooleanProperty;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;

public interface KeepEvaluatorActionI extends BaseUseActionI<UseActionTriggerComponentI> {
    public BooleanProperty takePrevEvaluatorProperty();
}
