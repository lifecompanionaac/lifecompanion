package org.lifecompanion.plugin.spellgame.model;

public record SpellGameStepResult(GameStep step, String word, String input, long timeSpent) {

    @Override
    public String toString() {
        return "SpellGameStepResult{" +
                "step=" + step +
                ", word='" + word + '\'' +
                ", input='" + input + '\'' +
                ", timeSpent=" + timeSpent / 1000.0 +
                '}';
    }
}
