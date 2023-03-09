package org.lifecompanion.plugin.spellgame.model;

public record SpellGameStepResult(GameStepV2 step, SpellGameStepResultStatusEnum status, String word, String input, long timeSpent) {
}
