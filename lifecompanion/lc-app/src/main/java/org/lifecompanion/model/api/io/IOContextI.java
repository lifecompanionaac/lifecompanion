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
package org.lifecompanion.model.api.io;

import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.slf4j.Marker;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The context to load or save configuration element.<br>
 * This is use to store and retrieve some informations on loading/saving.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface IOContextI {

    String NODE_USE_INFOS = "UseInformations";
    String NODE_USE_INFO = "UseInformation";
    String ATB_ID = "id";

    // PLUGIN
    //========================================================================
    Set<String> getAutomaticPluginDependencyIds();

    boolean isFallbackOnDefaultInstanceOnFail();

    void setFallbackOnDefaultInstanceOnFail(boolean fallbackOnDefaultInstanceOnFail);
    //========================================================================

    // Class part : "Styles"
    //========================================================================
    /**
     * Directory where associated XML file is saved/loaded.<br>
     * <strong>This shouldn't be used to load/save images or resources</strong> : use {@link #getImagesToSave()} or {@link #addResourceToSave(String, String, File)} instead
     *
     * @return the directory where the xml file will be saved/loaded
     */
    File getDirectory();
    //========================================================================

    // Class part : "Resources"
    //========================================================================
    List<ImageElementI> getImagesToSaveV2();

    Map<String,VideoElementI> getVideos();

    /**
     * To add a resource that should be saved by LifeCompanion<br>
     * Note that doesn't immediately save the resource.
     *
     * @param id           the original resource ID, if the resource was already used.<br>
     *                     If null, the resource ID will be generated
     * @param name         a name for this resource, the name will be returned on loading
     * @param resourcePath the resource path
     * @return the resource ID, this ID will be used on loading by execute a get on the map returned by {@link #getIOResource()} (equals to the given id if the given id is not null)
     * @throws IOException if given resourcePath is incorrect (file doesn't exist, etc...)
     */
    String addResourceToSave(String id, String name, File resourcePath) throws IOException;

    /**
     * @return the map of all current IO resources
     */
    Map<String, IOResourceI> getIOResource();
    //========================================================================

    // RETRO COMPATIBILITY
    //========================================================================
    Map<String, String> getBackwardImageCompatibilityIdsMap();
    //========================================================================
}
