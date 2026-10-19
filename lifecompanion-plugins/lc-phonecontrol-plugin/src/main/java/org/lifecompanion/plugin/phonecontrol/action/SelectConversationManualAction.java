package org.lifecompanion.plugin.phonecontrol.action;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;

import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.plugin.phonecontrol.PhoneControlController;
import org.lifecompanion.plugin.phonecontrol.action.categories.PhoneControlActionSubCategories;

import java.util.Map;

/**
 * @author Etudiants IUT Vannes : HASCOËT Anthony, GUERNY Baptiste,
 *         Le CHANU Simon, PAVOINE Oscar
 */
public class SelectConversationManualAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private final StringProperty phoneNumber, contactName;

    public SelectConversationManualAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.MISC;
        this.parameterizableAction = true;
        this.nameID = "phonecontrol.plugin.action.misc.select.conversation.manual.name";
        this.staticDescriptionID = "phonecontrol.plugin.action.misc.select.conversation.manual.description";
        this.phoneNumber = new SimpleStringProperty();
        this.contactName = new SimpleStringProperty();
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("phonecontrol.plugin.action.misc.select.conversation.manual.variable.description", this.contactName));
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
        PhoneControlController.INSTANCE.selectConv(phoneNumber.get(), contactName.get());
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
