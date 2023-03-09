package org.lifecompanion.plugin.spellgame.model;

import org.lifecompanion.plugin.spellgame.SpellGamePluginProperties;

public interface GameStepV2 {
    String getName();

    String getInstruction(String word);

    String getGeneralInstruction();

    boolean checkWord(SpellGamePluginProperties spellGamePluginProperties, String word, String input);

    String getExpectedResult(String word);

    GameStepV2 getStepOnFail();

    GameStepV2 getStepOnSuccess();

    StepDisplayMode getDisplayMode();
}
