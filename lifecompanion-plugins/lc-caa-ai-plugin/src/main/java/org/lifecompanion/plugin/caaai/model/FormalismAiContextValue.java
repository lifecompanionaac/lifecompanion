package org.lifecompanion.plugin.caaai.model;

import org.lifecompanion.framework.commons.translation.Translation;

public enum FormalismAiContextValue implements AiContextValue {
    FORMAL("caa.ai.plugin.contexts.formalism.formal"),
    NEUTRAL("caa.ai.plugin.contexts.formalism.neutral"),
    FAMILIAR("caa.ai.plugin.contexts.formalism.familiar");

    private final String textId;

    FormalismAiContextValue(String textId) {
        this.textId = textId;
    }

    @Override
    public String getTextValue() {
        return Translation.getText(this.textId);
    }
}
