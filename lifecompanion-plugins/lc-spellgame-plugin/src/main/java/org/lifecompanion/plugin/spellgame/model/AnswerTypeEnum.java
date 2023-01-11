package org.lifecompanion.plugin.spellgame.model;

import org.lifecompanion.framework.commons.translation.Translation;

public enum AnswerTypeEnum {
    ALL("spellgame.plugin.answer.type.all"),
    GOOD("spellgame.plugin.answer.type.good"),
    BAD("spellgame.plugin.answer.type.bad");

    private final String nameId;

    AnswerTypeEnum(String nameId) {
        this.nameId = nameId;
    }

    public String getName() {
        return Translation.getText(nameId);
    }
}
