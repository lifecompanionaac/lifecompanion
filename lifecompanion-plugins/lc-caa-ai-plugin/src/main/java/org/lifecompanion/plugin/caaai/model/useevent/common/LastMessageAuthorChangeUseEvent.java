package org.lifecompanion.plugin.caaai.model.useevent.common;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.plugin.caaai.controller.CAAAIController;
import org.lifecompanion.plugin.caaai.model.ConversationMessageAuthor;

import java.util.function.Consumer;

public abstract class LastMessageAuthorChangeUseEvent extends BaseUseEventGeneratorImpl {
    private final ConversationMessageAuthor targetAuthor;

    private final Consumer<ConversationMessageAuthor> lastMessageAuthorChange;

    public LastMessageAuthorChangeUseEvent(ConversationMessageAuthor targetAuthor) {
        super();

        this.parameterizableAction = false;
        this.targetAuthor = targetAuthor;

        this.lastMessageAuthorChange = author -> {
            if (author == this.targetAuthor) {
                this.useEventListener.fireEvent(this, null, null);
            }
        };
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        CAAAIController.INSTANCE.addConversationAuthorChangeListener(this.lastMessageAuthorChange);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        CAAAIController.INSTANCE.removeConversationAuthorChangeListener(this.lastMessageAuthorChange);
    }
}

