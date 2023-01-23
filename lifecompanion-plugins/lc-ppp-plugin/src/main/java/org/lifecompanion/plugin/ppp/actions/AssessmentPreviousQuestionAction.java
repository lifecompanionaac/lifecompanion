package org.lifecompanion.plugin.ppp.actions;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.plugin.ppp.actions.categories.PPPActionSubCategories;
import org.lifecompanion.plugin.ppp.services.AssessmentService;

import java.util.Map;

public class AssessmentPreviousQuestionAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    public AssessmentPreviousQuestionAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "ppp.plugin.actions.assessment.previous_question.name";
        this.staticDescriptionID = "ppp.plugin.actions.assessment.previous_question.description";
        this.category = PPPActionSubCategories.ASSESSMENT;
        this.order = 65;
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "actions/icon_assessment_previous_question.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        AssessmentService.INSTANCE.previousQuestion();
    }
}
