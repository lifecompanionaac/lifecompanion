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
import org.lifecompanion.plugin.ppp.services.ActionService;

import java.util.Map;

public class ActionStartAction extends SimpleUseActionImpl<UseActionTriggerComponentI> implements KeepEvaluatorActionI {
    private final BooleanProperty takePrevEvaluator;

    public ActionStartAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "ppp.plugin.actions.action.start.name";
        this.staticDescriptionID = "ppp.plugin.actions.action.start.description";
        this.category = PPPActionSubCategories.ACTION;
        this.order = 10;
        this.parameterizableAction = true;
        this.takePrevEvaluator = new SimpleBooleanProperty();
        this.variableDescriptionProperty().bind(
                TranslationFX.getTextBinding(
                        "ppp.plugin.actions.abstract.keep_evaluator.variable_description", this.getStaticDescription(),
                        Bindings.createStringBinding(() -> Translation.getText(
                                "ppp.plugin.actions.abstract.keep_evaluator.variable_description."
                                        + (this.takePrevEvaluator.get() ? "take_prev_evaluator" : "new_evaluator")),
                                this.takePrevEvaluator)));
    }

    public BooleanProperty takePrevEvaluatorProperty() {
        return this.takePrevEvaluator;
    }

    @Override
    public String getConfigIconPath() {
        return "actions/icon_action_start.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        ActionService.INSTANCE.startAction(this.takePrevEvaluatorProperty().get(), null);
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(ActionStartAction.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(ActionStartAction.class, this, nodeP);
    }
}
