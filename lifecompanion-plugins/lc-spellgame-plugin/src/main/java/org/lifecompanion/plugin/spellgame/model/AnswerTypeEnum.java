package org.lifecompanion.plugin.spellgame.model;

import org.lifecompanion.framework.commons.translation.Translation;

import java.util.function.Predicate;

public enum AnswerTypeEnum {
    ALL("spellgame.plugin.answer.type.all", a -> a.status() != SpellGameStepResultStatusEnum.UNDONE),
    GOOD("spellgame.plugin.answer.type.good", a -> a.status() == SpellGameStepResultStatusEnum.SUCCESS),
    BAD("spellgame.plugin.answer.type.bad", a -> a.status() == SpellGameStepResultStatusEnum.FAILED);

    private final String nameId;
    private final Predicate<SpellGameStepResult> filter;

    AnswerTypeEnum(String nameId, Predicate<SpellGameStepResult> filter) {
        this.nameId = nameId;
        this.filter = filter;
    }

    public String getName() {
        return Translation.getText(nameId);
    }

    public boolean filter(SpellGameStepResult answer) {
        return filter.test(answer);
    }
}
