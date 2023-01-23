package org.lifecompanion.plugin.ppp.actions;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.ppp.actions.categories.PPPActionSubCategories;
import org.lifecompanion.plugin.ppp.model.Action;
import org.lifecompanion.plugin.ppp.services.ActionService;

import java.util.Map;

public class ActionSelectAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    @XMLGenericProperty(Action.class)
    private final ObjectProperty<Action> action;

    public ActionSelectAction() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "ppp.plugin.actions.action.select.name";
        this.staticDescriptionID = "ppp.plugin.actions.action.select.description";
        this.category = PPPActionSubCategories.ACTION;
        this.order = 40;
        this.parameterizableAction = false;
        this.action = new SimpleObjectProperty<>();
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    public ObjectProperty<Action> actionProperty() {
        return this.action;
    }

    @Override
    public String getConfigIconPath() {
        return "actions/icon_action_select.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        ActionService.INSTANCE.selectAction(this.action.get());
    }

    // Class part : "IO"
    //========================================================================

    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(AssessmentSelectChoicePPPAction.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(AssessmentSelectChoicePPPAction.class, this, nodeP);
    }

    //========================================================================
}
