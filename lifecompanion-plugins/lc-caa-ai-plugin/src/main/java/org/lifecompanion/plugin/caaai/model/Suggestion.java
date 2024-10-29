package org.lifecompanion.plugin.caaai.model;

public class Suggestion {
    private final String content;

    public Suggestion(String content) {
        this.content = content;
    }

    public String content() {
        return this.content;
    }
}
