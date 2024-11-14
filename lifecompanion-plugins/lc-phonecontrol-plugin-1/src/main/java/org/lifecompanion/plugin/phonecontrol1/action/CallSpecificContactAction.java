package org.lifecompanion.plugin.phonecontrol1.action;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.phonecontrol1.PhoneControlController;
import org.lifecompanion.plugin.phonecontrol1.action.categories.PhoneControlActionSubCategories;

import java.util.Map;

/**
 * @author Etudiants IUT Vannes : HASCOËT Anthony, GUERNY Baptiste,
 *         Le CHANU Simon, PAVOINE Oscar
 */
public class CallSpecificContactAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private final StringProperty phoneNumber, contactName;

    public CallSpecificContactAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = PhoneControlActionSubCategories.CALL;
        this.parameterizableAction = true;
        this.nameID = "phonecontrol1.plugin.action.call.specific.contact.name";
        this.staticDescriptionID = "phonecontrol1.plugin.action.call.specific.contact.description";
        this.phoneNumber = new SimpleStringProperty();
        this.contactName = new SimpleStringProperty();
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    public StringProperty phoneNumberProperty() {
        return this.phoneNumber;
    }

    public StringProperty contactNameProperty() {
        return this.contactName;
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/contact_call.png";
    }

    @Override
    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
        PhoneControlController.INSTANCE.selectConv(phoneNumber.get(), contactName.get());
        PhoneControlController.INSTANCE.callContact();
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
