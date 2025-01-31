package org.lifecompanion.plugin.phonecontrol.action;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;

import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.phonecontrol.action.categories.PhoneControlActionSubCategories;
import org.lifecompanion.plugin.phonecontrol.controller.ConnexionController;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;

import java.util.Map;

public class SelectConversationManualAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final StringProperty phoneNumber, contactName;

    public SelectConversationManualAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.MISC;
        this.parameterizableAction = true;
        this.nameID = "phonecontrol.plugin.action.misc.selectconversationmanual.name";
        this.staticDescriptionID = "phonecontrol.plugin.action.misc.selectconversationmanual.description";
        this.phoneNumber = new SimpleStringProperty();
        this.contactName = new SimpleStringProperty();
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("phonecontrol.plugin.action.misc.selectconversationmanual.variable.description", this.contactName));
    }

    public StringProperty phoneNumberProperty() {
        return this.phoneNumber;
    }

    public StringProperty contactNameProperty() {
        return this.contactName;
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/select_conv.png";
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        ConnexionController.INSTANCE.selectConv(phoneNumber.get(), contactName.get(), false);
    }

    @Override
    public Element serialize(IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(SelectConversationManualAction.class, this, node);

        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI contextP) throws LCException {
        super.deserialize(node, contextP);
        XMLObjectSerializer.deserializeInto(SelectConversationManualAction.class, this, node);
    }
}
