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
package org.lifecompanion.api.plugins;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.useevent.UseVariableDefinitionI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.mode.ModeListenerI;
import org.lifecompanion.base.data.control.UseVariableController;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface PluginI extends ModeListenerI {

    // GENERAL
    //========================================================================

    /**
     * This should return language files path
     *
     * @param languageCode the language code, to know the file you should return.<br>
     *                     Code is from ISO 639-1 ("fr" for French)
     * @return the path array <i>(can return null if you don't have any file)</i>
     */
    String[] getLanguageFiles(String languageCode);

    String[] getJavaFXStylesheets();
    //========================================================================


    // PLUGIN START/STOP
    //========================================================================

    /**
     * Called by LifeCompanion on plugin initialize (when plugin is added or LifeCompanion starts).<br>
     * This is called once.
     *
     * @param dataFolder a data folder for this plugin.<br>
     *                   If this plugin have to write files, it should use this folder.
     */
    void start(File dataFolder);

    /**
     * Called by LifeCompanion on plugin stop (when plugin is removed or LifeCompanion stops)<br>
     * This is called once.
     *
     * @param dataFolder a data folder for this plugin.<br>
     *                   If this plugin have to write files, it should use this folder.
     */
    void stop(File dataFolder);
    //========================================================================

    // VARIABLE
    //========================================================================

    /**
     * This should always return the same {@link UseVariableDefinitionI} instances.
     *
     * @return the list of all use variables defined by this plugin.<br>
     * <i>Can return null.</i>
     */
    List<UseVariableDefinitionI> getDefinedVariables();

    /**
     * This will be called every second by LifeCompanion to get the value for each variable defined by plugin.<br>
     * Note that this will not be called only if {@link #getDefinedVariables()} return variables.<br>
     * This can also be called if a manual variable update is needed (by calling {@link UseVariableController#requestVariablesUpdate()} ()} )
     *
     * @param variablesToGenerate the map that contains all plugin variables ( id -> variable definition)
     * @return the map with each variable value by id (you should use definition in given map for your use variable)
     */
    Map<String, UseVariableI<?>> generateVariables(Map<String, UseVariableDefinitionI> variablesToGenerate);
    //========================================================================

    // IO
    //========================================================================
    PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration);
    //========================================================================
}
