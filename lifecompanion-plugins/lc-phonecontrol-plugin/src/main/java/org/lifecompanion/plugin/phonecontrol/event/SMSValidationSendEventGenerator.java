package org.lifecompanion.plugin.phonecontrol.event;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.phonecontrol.controller.ConnexionController;
import org.lifecompanion.plugin.phonecontrol.event.categories.PhoneControlEventSubCategories;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.function.Consumer;

public class SMSValidationSendEventGenerator extends BaseUseEventGeneratorImpl {
    @XMLGenericProperty(ValidationSendCondition.class)
    private ObjectProperty<ValidationSendCondition> condition;

    private final Consumer<Boolean> validationSendSMSCallback;

    public SMSValidationSendEventGenerator() {
        super();
        this.parameterizableAction = true;
        this.order = 20;
        this.category = PhoneControlEventSubCategories.MISC;
        this.nameID = "phonecontrol.plugin.event.misc.smsvalidationsend.name";
        this.staticDescriptionID = "phonecontrol.plugin.event.misc.smsvalidationsend.description";
        this.condition = new SimpleObjectProperty<>(ValidationSendCondition.ALWAYS);
        this.variableDescriptionProperty()
            .bind(TranslationFX.getTextBinding("phonecontrol.plugin.event.misc.smsvalidationsend.variable.description", this.condition));
        validationSendSMSCallback = (validationSend) -> {
            final ValidationSendCondition cond = this.condition.get();

            if (cond == ValidationSendCondition.ALWAYS
                || (validationSend && cond == ValidationSendCondition.SENT)
                || (!validationSend && cond == ValidationSendCondition.NOT_SENT)
            ) {
                this.useEventListener.fireEvent(this, null, null);
            }
        };
    }

    public ObjectProperty<ValidationSendCondition> conditionProperty() {
        return condition;
    }

    @Override
    public String getConfigIconPath() {
        return "use-events/phonecontrol.png";
    }

    // Class part : "Mode start/stop"
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        ConnexionController.INSTANCE.addValidationSendSMSCallback(validationSendSMSCallback);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        ConnexionController.INSTANCE.removeValidationSendSMSCallback(validationSendSMSCallback);
    }

    // Class part : "Generation condition"
    public static enum ValidationSendCondition {
        ALWAYS("phonecontrol.plugin.smsvalidationsendcondition.always"),
        SENT("phonecontrol.plugin.smsvalidationsendcondition.sent"),
        NOT_SENT("phonecontrol.plugin.smsvalidationsendcondition.not.sent");

        private final String textId;

        private ValidationSendCondition(String textId) {
            this.textId = textId;
        }

        public String getText() {
            return Translation.getText(textId);
        }

        @Override
        public String toString() {
            return getText();
        }
    }

    // Class part : "IO"
    @Override
    public Element serialize(final IOContextI context) {
        final Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(SMSValidationSendEventGenerator.class, this, element);

        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(SMSValidationSendEventGenerator.class, this, node);
    }
}
