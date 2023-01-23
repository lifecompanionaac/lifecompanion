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
import org.lifecompanion.plugin.ppp.model.EvaluatorType;
import org.lifecompanion.plugin.ppp.services.EvaluatorService;

import java.util.Map;

public class SelectEvaluatorAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    @XMLGenericProperty(EvaluatorType.class)
    private final ObjectProperty<EvaluatorType> evaluatorType;

    public SelectEvaluatorAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "ppp.plugin.actions.select_evaluator.name";
        this.staticDescriptionID = "ppp.plugin.actions.select_evaluator.description";
        this.category = PPPActionSubCategories.EVALUATOR;
        this.order = 10;
        this.parameterizableAction = true;
        this.evaluatorType = new SimpleObjectProperty<>();
        this.variableDescriptionProperty().bind(
                TranslationFX.getTextBinding("ppp.plugin.actions.select_evaluator.variable_description",
                        Bindings.createStringBinding(
                                () -> this.evaluatorType.get() != null ? this.evaluatorType.get().getText() : "",
                                this.evaluatorType)));
    }

    public ObjectProperty<EvaluatorType> evaluatorTypeProperty() {
        return this.evaluatorType;
    }

    @Override
    public String getConfigIconPath() {
        return "actions/icon_evaluator_select.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        EvaluatorService.INSTANCE.selectEvaluatorType(this.evaluatorType.get());
    }

    // Class part : "IO"
    //========================================================================

    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(SelectEvaluatorAction.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(SelectEvaluatorAction.class, this, nodeP);
    }

    //========================================================================
}
