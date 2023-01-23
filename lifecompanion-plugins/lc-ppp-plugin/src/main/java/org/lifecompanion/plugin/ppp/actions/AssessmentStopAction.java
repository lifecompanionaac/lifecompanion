package org.lifecompanion.plugin.ppp.actions;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.ppp.actions.categories.PPPActionSubCategories;
import org.lifecompanion.plugin.ppp.services.AssessmentService;

import java.util.Map;

public class AssessmentStopAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    public AssessmentStopAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "ppp.plugin.actions.assessment.stop.name";
        this.staticDescriptionID = "ppp.plugin.actions.assessment.stop.description";
        this.category = PPPActionSubCategories.ASSESSMENT;
        this.order = 30;
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "actions/icon_assessment_stop.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        AssessmentService.INSTANCE.stopAssessment();
    }
}
