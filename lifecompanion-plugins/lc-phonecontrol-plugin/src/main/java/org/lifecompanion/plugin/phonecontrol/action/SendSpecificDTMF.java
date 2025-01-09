package org.lifecompanion.plugin.phonecontrol.action;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.phonecontrol.action.categories.PhoneControlActionSubCategories;
import org.lifecompanion.plugin.phonecontrol.controller.CallController;

import java.util.Map;

public class SendSpecificDTMF extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final StringProperty dtmf;

    public SendSpecificDTMF() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.CALL;
        this.parameterizableAction = true;
        this.nameID = "phonecontrol.plugin.action.sendspecificdtmf.name";
        this.staticDescriptionID = "phonecontrol.plugin.action.sendspecificdtmf.description";
        this.dtmf = new SimpleStringProperty();
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("phonecontrol.plugin.action.sendspecificdtmf.variable.description", this.dtmf));
    }

    public StringProperty dtmfProperty() {
        return this.dtmf;
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/keypad.png";
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        CallController.INSTANCE.sendDtmf(dtmf.get());
    }

    @Override
    public Element serialize(IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(CallSpecificContactAction.class, this, node);

        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI contextP) throws LCException {
        super.deserialize(node, contextP);
        XMLObjectSerializer.deserializeInto(CallSpecificContactAction.class, this, node);
    }
}
