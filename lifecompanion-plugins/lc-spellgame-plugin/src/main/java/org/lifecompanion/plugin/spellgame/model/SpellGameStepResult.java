package org.lifecompanion.plugin.spellgame.model;

public record SpellGameStepResult(GameStep step, SpellGameStepResultStatusEnum status, String word, String input, long timeSpent) {
}
