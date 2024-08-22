package org.lifecompanion.plugin.caaai.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConversationMessage {
    private final ConversationMessageAuthor author;
    private final StringProperty content;

    public ConversationMessage(ConversationMessageAuthor author, String content) {
        this.author = author;
        this.content = new SimpleStringProperty(content);
    }

    public ConversationMessageAuthor author() {
        return this.author;
    }

    public StringProperty content() {
        return this.content;
    }
}
