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
package org.lifecompanion.plugin.ppp;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PediatricPainProfilePlugin implements PluginI {
    public static final String PLUGIN_ID = "lc-ppp-plugin";

    public PediatricPainProfilePlugin() {
    }

    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[]{"/text/" + languageCode + "_ppp_plugin.xml"};
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return null;
    }


    @Override
    public String[] getDefaultConfigurations(String languageCode) {
        return new String[]{"/configurations/" + languageCode + "_example-ppp1.lcc"};
    }

    // START/STOP
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        PediatricPainProfilePluginService.INSTANCE.modeStart(configuration);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        PediatricPainProfilePluginService.INSTANCE.modeStop(configuration);
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
        return Arrays.asList(
                new UseVariableDefinition(PediatricPainProfilePluginService.VAR_CURRENT_QUESTION_TEXT,
                        "ppp.plugin.variables.current_question_text.name",
                        "ppp.plugin.variables.current_question_text.description",
                        "ppp.plugin.variables.current_question_text.example"),
                new UseVariableDefinition(PediatricPainProfilePluginService.VAR_CURRENT_QUESTION_INDEX,
                        "ppp.plugin.variables.current_question_index.name",
                        "ppp.plugin.variables.current_question_index.description",
                        "ppp.plugin.variables.current_question_index.example"),
                new UseVariableDefinition(PediatricPainProfilePluginService.VAR_LATEST_PPP_SCORE,
                        "ppp.plugin.variables.latest_ppp_score.name",
                        "ppp.plugin.variables.latest_ppp_score.description",
                        "ppp.plugin.variables.latest_ppp_score.example"),
                new UseVariableDefinition(PediatricPainProfilePluginService.VAR_PROFILE_BASE_SCORE,
                        "ppp.plugin.variables.profile_base_score.name",
                        "ppp.plugin.variables.profile_base_score.description",
                        "ppp.plugin.variables.profile_base_score.example"),
                new UseVariableDefinition(PediatricPainProfilePluginService.VAR_PROFILE_BASE_SCORE_AT,
                        "ppp.plugin.variables.profile_base_score_at.name",
                        "ppp.plugin.variables.profile_base_score_at.description",
                        "ppp.plugin.variables.profile_base_score_at.example"),
                new UseVariableDefinition(PediatricPainProfilePluginService.VAR_PROFILE_NAME,
                        "ppp.plugin.variables.profile_name.name",
                        "ppp.plugin.variables.profile_name.description",
                        "ppp.plugin.variables.profile_name.example"),
                new UseVariableDefinition(PediatricPainProfilePluginService.VAR_CURRENT_KEYBOARD_INPUT,
                        "ppp.plugin.variables.current_keyboard_input.name",
                        "ppp.plugin.variables.current_keyboard_input.description",
                        "ppp.plugin.variables.current_keyboard_input.example")
        );
    }

    @Override
    public Map<String, UseVariableI<?>> generateVariables(final Map<String, UseVariableDefinitionI> variablesToGenerate) {
        return PediatricPainProfilePluginService.INSTANCE.generateVariables(variablesToGenerate);
    }

    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new PediatricPainProfileProperties(parentConfiguration);
    }

    //========================================================================

}
