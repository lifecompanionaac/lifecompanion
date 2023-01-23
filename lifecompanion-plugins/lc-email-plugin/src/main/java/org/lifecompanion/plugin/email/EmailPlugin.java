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
package org.lifecompanion.plugin.email;

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

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class EmailPlugin implements PluginI {
    public static final String PLUGIN_ID = "lc-email-plugin";

    public EmailPlugin() {
    }

    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[]{"/text/" + languageCode + "_email_plugin.xml"};
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return null;
    }

    @Override
    public String[] getDefaultConfigurations(String languageCode) {
        return new String[]{"/configurations/" + languageCode + "_email-example1.lcc"};
    }

    // START/STOP
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        EmailPluginService.INSTANCE.start(configuration);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        EmailPluginService.INSTANCE.stop(configuration);
    }

    @Override
    public void start(final File dataFolder) {
    }

    @Override
    public void stop(File dataFolder) {
    }
    //========================================================================


    // VARIABLES
    //========================================================================

    @Override
    public List<UseVariableDefinitionI> getDefinedVariables() {
        return Arrays.asList(//
                new UseVariableDefinition(EmailPluginService.VAR_FROM_SELECTED, "email.plugin.use.variable.from.selected.name",
                        "email.plugin.use.variable.from.selected.description", "email.plugin.use.variable.from.selected.example"), //
                new UseVariableDefinition(EmailPluginService.VAR_TO_SELECTED, "email.plugin.use.variable.to.selected.name",
                        "email.plugin.use.variable.to.selected.description", "email.plugin.use.variable.to.selected.example"), //
                new UseVariableDefinition(EmailPluginService.VAR_SUBJECT_SELECTED, "email.plugin.use.variable.subject.selected.name",
                        "email.plugin.use.variable.subject.selected.description", "email.plugin.use.variable.subject.selected.example"), //
                new UseVariableDefinition(EmailPluginService.VAR_DATE_SELECTED, "email.plugin.use.variable.date.selected.name",
                        "email.plugin.use.variable.date.selected.description", "email.plugin.use.variable.date.selected.example"), //
                new UseVariableDefinition(EmailPluginService.VAR_UNREAD_COUNT, "email.plugin.use.variable.unread.count.name",
                        "email.plugin.use.variable.unread.count.description", "email.plugin.use.variable.unread.count.example"), //
                new UseVariableDefinition(EmailPluginService.VAR_DISPLAYED_INDEX, "email.plugin.use.variable.displayed.index.name",
                        "email.plugin.use.variable.displayed.index.description", "email.plugin.use.variable.displayed.index.example"), //
                new UseVariableDefinition(EmailPluginService.VAR_TOTAL_COUNT, "email.plugin.use.variable.total.count.name",
                        "email.plugin.use.variable.total.count.description", "email.plugin.use.variable.total.count.example"), //
                new UseVariableDefinition(EmailPluginService.VAR_WRITE_TO, "email.plugin.use.variable.write.email.to.name",
                        "email.plugin.use.variable.write.email.to.description", "email.plugin.use.variable.write.email.to.example"), //
                new UseVariableDefinition(EmailPluginService.VAR_WRITE_SUBJECT, "email.plugin.use.variable.write.email.subject.name",
                        "email.plugin.use.variable.write.email.subject.description", "email.plugin.use.variable.write.email.subject.example")//
        );
    }

    @Override
    public Map<String, UseVariableI<?>> generateVariables(final Map<String, UseVariableDefinitionI> variablesToGenerate) {
        return EmailPluginService.INSTANCE.generateVariables(variablesToGenerate);
    }


    //========================================================================

    // IO
    //========================================================================


    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new EmailPluginProperties(parentConfiguration);
    }
    //========================================================================

}
