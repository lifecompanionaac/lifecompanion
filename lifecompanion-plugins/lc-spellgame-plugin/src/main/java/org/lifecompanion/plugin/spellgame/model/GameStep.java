package org.lifecompanion.plugin.spellgame.model;

import org.lifecompanion.plugin.spellgame.SpellGamePluginProperties;

public interface GameStep {
    String getName();

    String getInstruction(String word);

    String getGeneralInstruction();

    boolean checkWord(SpellGamePluginProperties spellGamePluginProperties, String word, String input);

    String getExpectedResult(String word);

    GameStep getStepOnFail();

    GameStep getStepOnSuccess();

    StepDisplayMode getDisplayMode();
}
