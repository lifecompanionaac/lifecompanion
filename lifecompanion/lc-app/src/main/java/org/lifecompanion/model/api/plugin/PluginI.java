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
package org.lifecompanion.model.api.plugin;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.controller.usevariable.UseVariableController;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface PluginI extends ModeListenerI {

    // GENERAL
    //========================================================================

    /**
     * Get the language resource path (included in plugin classpath)
     *
     * @param languageCode the language code, to know the file you should return.<br>
     *                     Code is from ISO 639-1 ("fr" for French)
     * @return the path array <i>(can return null if you don't have any file)</i>
     */
    default String[] getLanguageFiles(String languageCode) {
        return null;
    }

    /**
     * Get the JavaFX stylesheets for this plugin.<br>
     * The given style sheets path will be injected to {@link Scene#getStylesheets()} on edit mode scene.
     *
     * @return the path array <i>(can return null if you don't have any style)</i>
     */
    default String[] getJavaFXStylesheets() {
        return null;
    }

    /**
     * Get the default configuration (lcc files) path (included in plugin classpath)
     *
     * @param languageCode the language code, to know the file you should return.<br>
     *                     Code is from ISO 639-1 ("fr" for French)
     * @return the path array <i>(can return null if you don't have any file)</i>
     */
    default String[] getDefaultConfigurations(String languageCode) {
        return null;
    }
    //========================================================================


    // PLUGIN START/STOP
    //========================================================================

    /**
     * Called by LifeCompanion on starting.<br>
     * This is called out of the FX thread while the application is loading, so you can have sync loading in this method.
     * This is called once.
     *
     * @param dataFolder a data folder for this plugin.<br>
     *                   If this plugin have to store its own files, it should use this folder.<br>
     *                   This folder is shared between all profile and configurations, so it's better to use it for static data.<br>
     *                   This folder is the same on each start/stop (folder is unique for a plugin ID)
     */
    void start(File dataFolder);

    /**
     * Called by LifeCompanion on stopping.<br>
     * The method should return as fast as possible, it is called on FX thread.
     * This is called once.
     *
     * @param dataFolder see {@link #start(File)} for details
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
    default List<UseVariableDefinitionI> getDefinedVariables() {
        return null;
    }

    /**
     * This will be called every second by LifeCompanion to get the value for each variable defined by plugin.<br>
     * Note that this will not be called only if {@link #getDefinedVariables()} return variables.<br>
     * This can also be called if a manual variable update is needed (by calling {@link UseVariableController#requestVariablesUpdate()} ()} )
     *
     * @param variablesToGenerate the map that contains all plugin variables ( id -> variable definition)
     * @return the map with each variable value by id (you should use definition in given map for your use variable)
     */
    default Map<String, UseVariableI<?>> generateVariables(Map<String, UseVariableDefinitionI> variablesToGenerate){
        return null;
    }
    //========================================================================

    // IO
    //========================================================================

    /**
     * Should initialize this plugin own {@link PluginConfigPropertiesI} implementation.<br>
     * The implementation will be then store with the {@link LCConfigurationI} if plugin use it detected.
     * Implementers should extend {@link org.lifecompanion.model.impl.plugin.AbstractPluginConfigProperties} to create their own implementation.
     *
     * @param parentConfiguration property containing the configuration associated to the newly created property
     * @return plugin own properties
     */
    PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration);
    //========================================================================
}
