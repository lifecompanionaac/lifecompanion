package org.lifecompanion.plugin.ppp.actions;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.ppp.actions.categories.PPPActionSubCategories;
import org.lifecompanion.plugin.ppp.services.AssessmentService;

import java.util.Map;

public class AssessmentSelectPainLocalizationAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final StringProperty localization;

    public AssessmentSelectPainLocalizationAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "ppp.plugin.actions.assessment.select_pain_localization.name";
        this.staticDescriptionID = "ppp.plugin.actions.assessment.select_pain_localization.description";
        this.category = PPPActionSubCategories.ASSESSMENT;
        this.order = 70;
        this.parameterizableAction = true;
        this.localization = new SimpleStringProperty();
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding(
                "ppp.plugin.actions.assessment.select_pain_localization.variable_description",
                Bindings.createStringBinding(this::computeLocalization, this.localization)));
    }

    public StringProperty localizationProperty() {
        return this.localization;
    }

    private String computeLocalization() {
        return StringUtils.isBlank(this.localization.get())
                ? Translation.getText(
                "ppp.plugin.actions.assessment.select_pain_localization.variable_description.not_given")
                : this.localization.get();
    }

    @Override
    public String getConfigIconPath() {
        return "actions/icon_assessment_select_pain_localization.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        AssessmentService.INSTANCE.selectPainLocalization(this.computeLocalization());
    }

    // Class part : "IO"
    //========================================================================

    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(AssessmentSelectPainLocalizationAction.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(AssessmentSelectPainLocalizationAction.class, this, nodeP);
    }

    //========================================================================
}
