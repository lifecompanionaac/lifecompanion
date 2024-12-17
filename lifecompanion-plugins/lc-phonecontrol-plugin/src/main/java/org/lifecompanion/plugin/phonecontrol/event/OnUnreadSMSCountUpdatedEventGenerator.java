package org.lifecompanion.plugin.phonecontrol.event;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.phonecontrol.controller.ConnexionController;
import org.lifecompanion.plugin.phonecontrol.controller.SMSController;
import org.lifecompanion.plugin.phonecontrol.event.categories.PhoneControlEventSubCategories;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.function.Consumer;

public class OnUnreadSMSCountUpdatedEventGenerator extends BaseUseEventGeneratorImpl {
    @XMLGenericProperty(UnreadEventGenerateCondition.class)
    private ObjectProperty<UnreadEventGenerateCondition> condition;

    private final Consumer<Integer> unreadCountUpdatedCallback;

    public OnUnreadSMSCountUpdatedEventGenerator() {
        super();
        this.parameterizableAction = true;
        this.order = 20;
        this.category = PhoneControlEventSubCategories.MISC;
        this.nameID = "phonecontrol.plugin.event.misc.sms.unread.count.name";
        this.staticDescriptionID = "phonecontrol.plugin.event.misc.sms.unread.count.description";
        this.condition = new SimpleObjectProperty<>(UnreadEventGenerateCondition.ALWAYS);
        this.variableDescriptionProperty()
            .bind(TranslationFX.getTextBinding("phonecontrol.plugin.event.misc.sms.unread.count.updated.variable.description", this.condition));
        unreadCountUpdatedCallback = (unreadCount) -> {
            final UnreadEventGenerateCondition cond = this.condition.get();

            if (cond == UnreadEventGenerateCondition.ALWAYS
                || (unreadCount > 0 && cond == UnreadEventGenerateCondition.UNREAD)
                || (unreadCount == 0 && cond == UnreadEventGenerateCondition.NONE)
            ) {
                this.useEventListener.fireEvent(this, null, null);
            }
        };
    }

    public ObjectProperty<UnreadEventGenerateCondition> conditionProperty() {
        return condition;
    }

    @Override
    public String getConfigIconPath() {
        return "use-events/phonecontrol.png";
    }

    // Class part : "Mode start/stop"
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        SMSController.INSTANCE.addUnreadCountUpdateCallback(unreadCountUpdatedCallback);
        // On start, fire first event
        int unreadCount = ConnexionController.INSTANCE.getSmsUnread();
        unreadCountUpdatedCallback.accept(unreadCount);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        SMSController.INSTANCE.removeUnreadCountUpdateCallback(unreadCountUpdatedCallback);
    }

    // Class part : "Generation condition"
    public static enum UnreadEventGenerateCondition {
        ALWAYS("phonecontrol.plugin.unread.count.condition.always"),
        UNREAD("phonecontrol.plugin.unread.count.condition.unread.positive"),
        NONE("phonecontrol.plugin.unread.count.condition.unread.none");

        private final String textId;

        private UnreadEventGenerateCondition(String textId) {
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
        XMLObjectSerializer.serializeInto(OnUnreadSMSCountUpdatedEventGenerator.class, this, element);

        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(OnUnreadSMSCountUpdatedEventGenerator.class, this, node);
    }
}
