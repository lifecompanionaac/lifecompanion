/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.lifecompanion.plugin.homeassistant;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;

import java.io.File;
import java.util.List;
import java.util.Map;

public class HomeAssistantPlugin implements PluginI {
    public static final String PLUGIN_ID = "lc-homeassistant-plugin";

    public HomeAssistantPlugin() {
    }

    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[]{"/text/" + languageCode + "_homeassistant_plugin.xml"};
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return null;
    }

    // START/STOP
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        HomeAssistantPluginService.INSTANCE.start(configuration);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        HomeAssistantPluginService.INSTANCE.stop(configuration);
    }

    @Override
    public void start(final File dataFolder) {
    }

    @Override
    public void stop(final File dataFolder) {
    }
    //========================================================================


    // VARIABLES
    //========================================================================

    @Override
    public List<UseVariableDefinitionI> getDefinedVariables() {
        return null;
    }

    @Override
    public Map<String, UseVariableI<?>> generateVariables(final Map<String, UseVariableDefinitionI> variablesToGenerate) {
        return null;
    }

    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new HomeAssistantPluginProperties(parentConfiguration);
    }
    //========================================================================

}
