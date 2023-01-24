/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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
package org.lifecompanion.plugin.predict4allevaluation;

import org.lifecompanion.plugin.predict4allevaluation.clinicalstudy.Predict4AllClinicalStudyManager;
import javafx.beans.property.ObjectProperty;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class Predict4AllEvaluationPlugin implements PluginI {
    private static final Logger LOGGER = LoggerFactory.getLogger(Predict4AllEvaluationPlugin.class);

    public static final String PLUGIN_ID = "lc-predict4all-evaluation-plugin";

    public static File DATA_FOLDER;

    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[]{"/text/" + languageCode + "_predict4all_plugin.xml"};
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return new String[0];
    }


    @Override
    public void modeStart(final LCConfigurationI configuration) {
        Predict4AllClinicalStudyManager.INSTANCE.modeStart(configuration);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        Predict4AllClinicalStudyManager.INSTANCE.modeStop(configuration);
    }

    @Override
    public void start(final File dataFolder) {
    }

    @Override
    public void stop(final File dataFolder) {
    }

    @Override
    public List<UseVariableDefinitionI> getDefinedVariables() {
        return Arrays.asList(//
                new UseVariableDefinition(Predict4AllClinicalStudyManager.VAR_STUDY_LOGGING_STATUS, "predict4all.clinical.study.var.status.name", "predict4all.clinical.study.var.status.description",
                        "predict4all.clinical.study.var.status.example") //
        );
    }

    @Override
    public Map<String, UseVariableI<?>> generateVariables(final Map<String, UseVariableDefinitionI> variablesToGenerate) {
        Map<String, UseVariableI<?>> useVariables = new HashMap<>();
        try {
            useVariables.put(Predict4AllClinicalStudyManager.VAR_STUDY_LOGGING_STATUS,
                    new StringUseVariable(variablesToGenerate.get(Predict4AllClinicalStudyManager.VAR_STUDY_LOGGING_STATUS), Translation
                            .getText(Predict4AllClinicalStudyManager.INSTANCE.isLogging() ? "predict4all.clinical.study.var.status.value.logging" : "predict4all.clinical.study.var.status.value.waiting")));
        } catch (Exception e) {
            LOGGER.error("Couldn't generated variables", e);
        }
        return useVariables;
    }

    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> objectProperty) {
        return new Predict4AllEvaluationPluginProperties(objectProperty);
    }
}
