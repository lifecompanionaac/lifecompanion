package org.lifecompanion.plugin.caaai.model;

import org.lifecompanion.framework.commons.translation.Translation;

public enum ConversationMessageAuthor {
    ME("caa.ai.plugin.todo.author.me"),
    INTERLOCUTOR("caa.ai.plugin.todo.author.interlocutor");

    private final String textId;

    ConversationMessageAuthor(String textId) {
        this.textId = textId;
    }

    public String getText() {
        return Translation.getText(this.textId);
    }
}
