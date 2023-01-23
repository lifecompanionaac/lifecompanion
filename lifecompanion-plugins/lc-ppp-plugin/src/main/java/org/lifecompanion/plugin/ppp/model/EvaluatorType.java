package org.lifecompanion.plugin.ppp.model;

import org.lifecompanion.framework.commons.translation.Translation;

public enum EvaluatorType {
    PROFESSIONAL("ppp.plugin.model.evaluators.types.text.professional"),
    FAMILY("ppp.plugin.model.evaluators.types.text.family"),
    OTHER("ppp.plugin.model.evaluators.types.text.other");

    private final String textId;

    EvaluatorType(String textId) {
        this.textId = textId;
    }

    public String getText() {
        return Translation.getText(this.textId);
    }
}
