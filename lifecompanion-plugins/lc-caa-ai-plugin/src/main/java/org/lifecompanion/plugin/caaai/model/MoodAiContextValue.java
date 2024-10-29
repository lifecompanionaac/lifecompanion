package org.lifecompanion.plugin.caaai.model;

import org.lifecompanion.framework.commons.translation.Translation;

public enum MoodAiContextValue implements AiContextValue {
    HAPPY("caa.ai.plugin.contexts.mood.happy"),
    NEUTRAL("caa.ai.plugin.contexts.mood.neutral"),
    SAD("caa.ai.plugin.contexts.mood.sad");

    private final String textId;

    MoodAiContextValue(String textId) {
        this.textId = textId;
    }

    @Override
    public String getTextValue() {
        return Translation.getText(this.textId);
    }
}
