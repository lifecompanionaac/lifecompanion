package org.lifecompanion.plugin.aac4all.wp2;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.usevariable.IntegerUseVariable;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.plugin.aac4all.wp2.controller.AAC4AllWp2Controller;
import org.lifecompanion.plugin.aac4all.wp2.controller.AAC4AllWp2EvaluationController;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class AAC4AllWp2Plugin implements PluginI {
    public static final String ID = "aac4all-wp2-plugin";

    // RES
    //========================================================================
    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[]{"/text/" + languageCode + "_aac4all-wp2-plugin.xml"};
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return new String[]{"/style/aac4all-wp2-plugin.css"};
    }

    @Override
    public String[] getDefaultConfigurations(String languageCode) {
        return new String[]{"/configurations/Configuration RéoLoc Training.lcc",
                "/configurations/Configuration RéoLoc Evaluation.lcc",
                "/configurations/Configuration CurSta Training.lcc",
                "/configurations/Configuration CurSta Evaluation.lcc"};
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
        AAC4AllWp2Controller.INSTANCE.modeStart(configuration);
        AAC4AllWp2EvaluationController.INSTANCE.modeStart(configuration);
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        AAC4AllWp2Controller.INSTANCE.modeStop(configuration);
        AAC4AllWp2EvaluationController.INSTANCE.modeStop(configuration);
    }
    //========================================================================


    // VARIABLES
    //========================================================================
    @Override
    public List<UseVariableDefinitionI> getDefinedVariables() {
        return Arrays.asList(//
                new UseVariableDefinition(
                        VAR_ID_CURRENT_SENTENCE,
                        "aac4all.wp2.plugin.variable.current.sentence.name",
                        "aac4all.wp2.plugin.variable.current.sentence.description",
                        "aac4all.wp2.plugin.variable.current.sentence.example"
                ),
                 new UseVariableDefinition(
                         VAR_ID_FUNCTIONAL_CURRENT_KEYBOARD,
                         "aac4all.wp2.plugin.variable.functional.current.keyboard.name",
                         "aac4all.wp2.plugin.variable.functional.current.keyboard.description",
                         "aac4all.wp2.plugin.variable.functional.current.keyboard.example"
        ),
                new UseVariableDefinition(
                        VAR_ID_INSTRUCTION_CURRENT_KEYBOARD,
                        "aac4all.wp2.plugin.variable.instruction.current.keyboard.name",
                        "aac4all.wp2.plugin.variable.instruction.current.keyboard.description",
                        "aac4all.wp2.plugin.variable.instruction.current.keyboard.example"
                )
        );
    }

    private final String VAR_ID_CURRENT_SENTENCE = "CurrentSentence";
    private final String VAR_ID_FUNCTIONAL_CURRENT_KEYBOARD = "FunctionalCurrentKeyboard";
    private final String VAR_ID_INSTRUCTION_CURRENT_KEYBOARD = "InstructionCurrentKeyboard";

    @Override
    public Function<UseVariableDefinitionI, UseVariableI<?>> getSupplierForUseVariable(String id) {
        return switch (id) {
            case VAR_ID_CURRENT_SENTENCE -> def -> new StringUseVariable(def, AAC4AllWp2EvaluationController.INSTANCE.getCurrentSentence());
            case VAR_ID_FUNCTIONAL_CURRENT_KEYBOARD -> def -> new StringUseVariable(def, AAC4AllWp2EvaluationController.INSTANCE.getFunctionalCurrentKeyboard());
            case VAR_ID_INSTRUCTION_CURRENT_KEYBOARD -> def -> new StringUseVariable(def, AAC4AllWp2EvaluationController.INSTANCE.getInstructionCurrentKeyboard());
            default -> null;
        };
    }
    //========================================================================

    // PLUGIN PROPERTIES
    //========================================================================
    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new AAC4AllWp2PluginProperties(parentConfiguration);
    }
    //========================================================================
}
