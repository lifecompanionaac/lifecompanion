package org.lifecompanion.plugin.caaai.model;

import org.lifecompanion.framework.commons.translation.Translation;

public enum LengthAiContextValue implements AiContextValue {
    SHORT("caa.ai.plugin.contexts.length.short"),
    NORMAL("caa.ai.plugin.contexts.length.normal"),
    LONG("caa.ai.plugin.contexts.length.long");

    private final String textId;

    LengthAiContextValue(String textId) {
        this.textId = textId;
    }

    @Override
    public String getTextValue() {
        return Translation.getText(this.textId);
    }
}
