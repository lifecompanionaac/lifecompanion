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
package org.lifecompanion.plugin.calendar;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.plugin.calendar.controller.CalendarController;
import org.lifecompanion.plugin.calendar.controller.SoundAlarmController;
import org.lifecompanion.plugin.calendar.model.CalendarEvent;

import java.io.File;
import java.util.List;
import java.util.Map;

public class CalendarPlugin implements PluginI {
    public static final String PLUGIN_ID = "lc-calendar-plugin";

    public CalendarPlugin() {
    }

    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[]{"/text/" + languageCode + "_calendar_plugin.xml"};
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return null;
    }

    @Override
    public String[] getDefaultConfigurations(String languageCode) {
        return new String[]{"/configurations/" + languageCode + "_calendar-example1.lcc"};
    }

    // START/STOP
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        SoundAlarmController.INSTANCE.modeStart(configuration);
        CalendarController.INSTANCE.modeStart(configuration);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        CalendarController.INSTANCE.modeStop(configuration);
        SoundAlarmController.INSTANCE.modeStop(configuration);
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
        return null;
    }

    @Override
    public Map<String, UseVariableI<?>> generateVariables(final Map<String, UseVariableDefinitionI> variablesToGenerate) {
        return null;
    }
    //========================================================================

    // IO
    //========================================================================


    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new CalendarPluginProperties(parentConfiguration);
    }
    //========================================================================

}
