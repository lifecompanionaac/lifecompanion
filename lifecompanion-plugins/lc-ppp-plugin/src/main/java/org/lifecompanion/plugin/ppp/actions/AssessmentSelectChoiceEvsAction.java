package org.lifecompanion.plugin.ppp.actions;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.ppp.actions.categories.PPPActionSubCategories;
import org.lifecompanion.plugin.ppp.model.Choice;
import org.lifecompanion.plugin.ppp.services.AssessmentService;

import java.util.Map;

public class AssessmentSelectChoiceEvsAction extends SimpleUseActionImpl<UseActionTriggerComponentI> implements SelectChoiceActionI {
    @XMLGenericProperty(Choice.class)
    private final ObjectProperty<Choice> choice;

    public AssessmentSelectChoiceEvsAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "ppp.plugin.actions.assessment.select_choice.evs.name";
        this.staticDescriptionID = "ppp.plugin.actions.assessment.select_choice.evs.description";
        this.category = PPPActionSubCategories.ASSESSMENT;
        this.order = 50;
        this.parameterizableAction = true;
        this.choice = new SimpleObjectProperty<>();
        this.variableDescriptionProperty().bind(
                TranslationFX.getTextBinding("ppp.plugin.actions.abstract.select_choice.variable_description",
                        Bindings.createStringBinding(
                                () -> this.choice.get() != null ? this.choice.get().getText() : "", this.choice)));
    }

    public ObjectProperty<Choice> choiceProperty() {
        return this.choice;
    }

    @Override
    public String getConfigIconPath() {
        return "actions/icon_assessment_select.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        AssessmentService.INSTANCE.selectChoice(this.choice.get());
    }

    // Class part : "IO"
    //========================================================================

    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(AssessmentSelectChoiceEvsAction.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(AssessmentSelectChoiceEvsAction.class, this, nodeP);
    }

    //========================================================================
}
