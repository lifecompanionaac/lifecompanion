package org.lifecompanion.plugin.caaai.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConversationMessage {
    private final ConversationMessageAuthor author;
    private final StringProperty message;

    public ConversationMessage(ConversationMessageAuthor author, String message) {
        this.author = author;
        this.message = new SimpleStringProperty(message);
    }

    public ConversationMessageAuthor author() {
        return this.author;
    }

    public StringProperty message() {
        return this.message;
    }
}
