package org.lifecompanion.plugin.translate;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;

import java.io.File;
import java.util.List;
import java.util.function.Function;

public class TranslatePlugin implements PluginI {
    public static final String ID = "lc-translate-plugin";

    // RES
    //========================================================================
    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[]{"/text/" + languageCode + "_translate_plugin.xml"};
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return null;
    }

    @Override
    public String[] getDefaultConfigurations(String languageCode) {
        return null;
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

    }

    @Override
    public void modeStop(LCConfigurationI configuration) {

    }
    //========================================================================


    // VARIABLES
    //========================================================================
    @Override
    public List<UseVariableDefinitionI> getDefinedVariables() {
        // TODO : current language
        return null;
//        return Arrays.asList(//
//                new UseVariableDefinition(SpellGameController.VAR_ID_USER_SCORE,
//                        "spellgame.plugin.use.variable.user.score.name",
//                        "spellgame.plugin.use.variable.user.score.description",
//                        "spellgame.plugin.use.variable.user.score.example",
//                        1000));
    }

    @Override
    public Function<UseVariableDefinitionI, UseVariableI<?>> getSupplierForUseVariable(String id) {
        return null;
    }
    //========================================================================

    // PLUGIN PROPERTIES
    //========================================================================
    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new TranslatePluginProperties(parentConfiguration);
    }
    //========================================================================
}
