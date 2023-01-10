package org.lifecompanion.plugin.spellgame.model;

public record SpellGameStepResult(GameStep step, String word, String input, String expected, long timeSpent) {

    @Override
    public String toString() {
        return "SpellGameStepResult{" +
                "step=" + step +
                ", word='" + word + '\'' +
                ", input='" + input + '\'' +
                ", expected='" + expected + '\'' +
                ", timeSpent=" + timeSpent +
                '}';
    }
}
