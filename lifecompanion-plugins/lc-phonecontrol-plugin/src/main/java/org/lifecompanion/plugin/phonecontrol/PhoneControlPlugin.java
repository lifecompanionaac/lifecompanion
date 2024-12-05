package org.lifecompanion.plugin.phonecontrol;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.usevariable.IntegerUseVariable;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class PhoneControlPlugin implements PluginI {
    public static final String PLUGIN_ID = "lc-phonecontrol-plugin";

    public PhoneControlPlugin() { }

    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[] { "/text/" + languageCode + "_phonecontrol_plugin.xml" };
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return null;
    }

    @Override
    public String[] getDefaultConfigurations(String languageCode) {
        return new String[] { "/configurations/" + languageCode + "_phonecontrol-example.lcc" };
    }

    @Override
    public void start(File dataDirectory) {
        PhoneControlController.INSTANCE.start(dataDirectory);
    }

    @Override
    public void stop(File dataDirectory) {
        PhoneControlController.INSTANCE.stop();
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        PhoneControlController.INSTANCE.modeStart(configuration);
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        PhoneControlController.INSTANCE.modeStop(configuration);
    }

    @Override
    public List<UseVariableDefinitionI> getDefinedVariables() {
        return null;
    }

    @Override
    public Function<UseVariableDefinitionI, UseVariableI<?>> getSupplierForUseVariable(String id) {
        return null;
    }

    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new PhoneControlPluginProperties(parentConfiguration);
    }
}
