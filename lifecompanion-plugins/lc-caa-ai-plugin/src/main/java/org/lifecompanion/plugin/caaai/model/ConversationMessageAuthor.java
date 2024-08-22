package org.lifecompanion.plugin.caaai.model;

import org.lifecompanion.framework.commons.translation.Translation;

public enum ConversationMessageAuthor {
    ME("caa.ai.plugin.variables.last_conversation_message_author.values.me"),
    INTERLOCUTOR("caa.ai.plugin.variables.last_conversation_message_author.values.interlocutor");

    private final String nameId;

    ConversationMessageAuthor(String nameId) {
        this.nameId = nameId;
    }

    public String getName() {
        return Translation.getText(this.nameId);
    }
}
