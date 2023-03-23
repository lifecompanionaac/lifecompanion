package org.lifecompanion.plugin.spellgame;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.usevariable.IntegerUseVariable;
import org.lifecompanion.model.impl.usevariable.LongUseVariable;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.plugin.spellgame.controller.SpellGameController;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellGamePlugin implements PluginI {
    public static final String ID = "lc-spellgame-plugin";

    // RES
    //========================================================================
    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[]{"/text/" + languageCode + "_spellgame_plugin.xml"};
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return new String[]{"/style/spellgame_plugin.css"};
    }

    @Override
    public String[] getDefaultConfigurations(String languageCode) {
        return new String[]{"/configurations/" + languageCode + "_spellgame-example1.lcc"};
    }
    //========================================================================


    // PLUGIN START/STOP
    //========================================================================
    @Override
    public void start(File dataDirectory) {
        // Plugin global init here
    }

    @Override
    public void stop(File dataDirectory) {
        // Plugin global stop here
    }
    //========================================================================

    // MODE START/STOP
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {
        SpellGameController.INSTANCE.modeStart(configuration);
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        SpellGameController.INSTANCE.modeStop(configuration);
    }
    //========================================================================


    // VARIABLES
    //========================================================================
    @Override
    public List<UseVariableDefinitionI> getDefinedVariables() {
        return Arrays.asList(//
                new UseVariableDefinition(SpellGameController.VAR_ID_USER_SCORE,
                        "spellgame.plugin.use.variable.user.score.name",
                        "spellgame.plugin.use.variable.user.score.description",
                        "spellgame.plugin.use.variable.user.score.example"),
                new UseVariableDefinition(SpellGameController.VAR_ID_WORD_INDEX,
                        "spellgame.plugin.use.variable.word.index.name",
                        "spellgame.plugin.use.variable.word.index.description",
                        "spellgame.plugin.use.variable.word.index.example"),
                new UseVariableDefinition(SpellGameController.VAR_ID_WORD_COUNT,
                        "spellgame.plugin.use.variable.word.count.name",
                        "spellgame.plugin.use.variable.word.count.description",
                        "spellgame.plugin.use.variable.word.count.example"),
                new UseVariableDefinition(SpellGameController.VAR_ID_CURRENT_STEP_INSTRUCTION_WITH_WORD,
                        "spellgame.plugin.use.variable.current.step.instruction.with.word.name",
                        "spellgame.plugin.use.variable.current.step.instruction.with.word.description",
                        "spellgame.plugin.use.variable.current.step.instruction.with.word.example"),
                new UseVariableDefinition(SpellGameController.VAR_ID_CURRENT_STEP_INSTRUCTION,
                        "spellgame.plugin.use.variable.current.step.instruction.name",
                        "spellgame.plugin.use.variable.current.step.instruction.description",
                        "spellgame.plugin.use.variable.current.step.instruction.example"));
    }

    @Override
    public Map<String, UseVariableI<?>> generateVariables(Map<String, UseVariableDefinitionI> variablesToGenerate) {
        Map<String, UseVariableI<?>> vars = new HashMap<>();
        vars.put(SpellGameController.VAR_ID_USER_SCORE, new IntegerUseVariable(variablesToGenerate.get(SpellGameController.VAR_ID_USER_SCORE), SpellGameController.INSTANCE.getUserScore()));
        vars.put(SpellGameController.VAR_ID_WORD_INDEX, new IntegerUseVariable(variablesToGenerate.get(SpellGameController.VAR_ID_WORD_INDEX), SpellGameController.INSTANCE.getWordIndex()));
        vars.put(SpellGameController.VAR_ID_WORD_COUNT, new IntegerUseVariable(variablesToGenerate.get(SpellGameController.VAR_ID_WORD_COUNT), SpellGameController.INSTANCE.getWordCount()));
        vars.put(SpellGameController.VAR_ID_CURRENT_STEP_INSTRUCTION,
                new StringUseVariable(variablesToGenerate.get(SpellGameController.VAR_ID_CURRENT_STEP_INSTRUCTION), SpellGameController.INSTANCE.getCurrentStepInstruction()));
        vars.put(SpellGameController.VAR_ID_CURRENT_STEP_INSTRUCTION_WITH_WORD,
                new StringUseVariable(variablesToGenerate.get(SpellGameController.VAR_ID_CURRENT_STEP_INSTRUCTION), SpellGameController.INSTANCE.getCurrentStepInstructionWithWord()));
        return vars;
    }
    //========================================================================

    // PLUGIN PROPERTIES
    //========================================================================
    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new SpellGamePluginProperties(parentConfiguration);
    }
    //========================================================================
}
