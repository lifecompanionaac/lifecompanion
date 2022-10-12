package org.lifecompanion.plugin.officialexample;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.plugin.officialexample.spellgame.controller.SpellGameController;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamplePluginOfficial implements PluginI {
    public static final String ID = "lc-example-plugin";

    // RES
    //========================================================================
    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[]{"/text/" + languageCode + "_example_plugin.xml"};
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return new String[]{"/style/example_plugin.css"};
    }
    //========================================================================


    // PLUGIN START/STOP
    //========================================================================
    @Override
    public void start(File dataDirectory) {

    }

    @Override
    public void stop(File dataDirectory) {

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
                new UseVariableDefinition(
                        SpellGameController.VAR_ID_USER_SCORE,
                        "example.plugin.use.variable.user.score.name",
                        "example.plugin.use.variable.user.score.description",
                        "example.plugin.use.variable.user.score.example"
                )
        );
    }

    @Override
    public Map<String, UseVariableI<?>> generateVariables(Map<String, UseVariableDefinitionI> variablesToGenerate) {
        Map<String, UseVariableI<?>> vars = new HashMap<>();
        vars.put(SpellGameController.VAR_ID_USER_SCORE,
                new StringUseVariable(
                        variablesToGenerate.get(SpellGameController.VAR_ID_USER_SCORE),
                        String.valueOf(SpellGameController.INSTANCE.getUserScore())
                ));
        return vars;
    }
    //========================================================================

    // PLUGIN PROPERTIES
    //========================================================================
    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new ExamplePluginProperties(parentConfiguration);
    }
    //========================================================================
}
