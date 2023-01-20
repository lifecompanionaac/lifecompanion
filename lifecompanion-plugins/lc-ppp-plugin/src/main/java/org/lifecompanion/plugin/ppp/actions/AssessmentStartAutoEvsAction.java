package org.lifecompanion.plugin.ppp.actions;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.ppp.actions.categories.PPPActionSubCategories;
import org.lifecompanion.plugin.ppp.services.AssessmentService;

import java.util.Map;

public class AssessmentStartAutoEvsAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final BooleanProperty askPainLocalization;

    public AssessmentStartAutoEvsAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "ppp.plugin.actions.assessment.start.auto_evs.name";
        this.staticDescriptionID = "ppp.plugin.actions.assessment.start.auto_evs.description";
        this.category = PPPActionSubCategories.ASSESSMENT;
        this.order = 20;
        this.parameterizableAction = true;
        this.askPainLocalization = new SimpleBooleanProperty();
        this.variableDescriptionProperty().bind(
                TranslationFX.getTextBinding(
                        "ppp.plugin.actions.assessment.start.auto_evs.variable_description",
                        this.getStaticDescription(),
                        Bindings.createStringBinding(() -> Translation.getText(
                                "ppp.plugin.actions.assessment.start.auto_evs.variable_description."
                                        + (this.askPainLocalization.get() ? "ask" : "do_not_ask")),
                                this.askPainLocalization)));
    }

    public BooleanProperty askPainLocalizationProperty() {
        return askPainLocalization;
    }

    @Override
    public String getConfigIconPath() {
        return "actions/icon_assessment_start.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        AssessmentService.INSTANCE.startAutoEvsAssessment(this.askPainLocalization.get());
    }

    // Class part : "IO"
    //========================================================================

    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(AssessmentStartAutoEvsAction.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(AssessmentStartAutoEvsAction.class, this, nodeP);
    }

    //========================================================================
}
