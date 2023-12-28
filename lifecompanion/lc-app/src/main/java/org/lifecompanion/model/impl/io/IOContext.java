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
package org.lifecompanion.model.impl.io;

import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.IOResourceI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * The context of a loading or a save for configuration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class IOContext implements IOContextI {

    private final List<ImageElementI> imagesV2;

    private final Set<String> pluginIdDependencies;

    /**
     * Resources associated to this configuration
     */
    private final Map<String, IOResourceI> resources;

    private final Map<String, VideoElementI> videos;

    private final Map<String, String> backwardImageCompatibilityIdsMap;

    /**
     * Directory
     */
    private final File directory;

    private boolean fallbackOnDefaultInstanceOnFail = true;


    public IOContext(final File directoryP) {
        imagesV2 = new ArrayList<>();
        this.resources = new HashMap<>();
        backwardImageCompatibilityIdsMap = new HashMap<>();
        this.pluginIdDependencies = new HashSet<>();
        this.videos = new HashMap<>();
        this.directory = directoryP;
    }

    // Class part : "Interface implementation"
    //========================================================================
    @Override
    public List<ImageElementI> getImagesToSaveV2() {
        return imagesV2;
    }

    @Override
    public Map<String, VideoElementI> getVideos() {
        return videos;
    }

    @Override
    public Map<String, IOResourceI> getIOResource() {
        return this.resources;
    }

    @Override
    public Map<String, String> getBackwardImageCompatibilityIdsMap() {
        return backwardImageCompatibilityIdsMap;
    }

    @Override
    public Set<String> getAutomaticPluginDependencyIds() {
        return pluginIdDependencies;
    }

    @Override
    public boolean isFallbackOnDefaultInstanceOnFail() {
        return fallbackOnDefaultInstanceOnFail;
    }

    @Override
    public void setFallbackOnDefaultInstanceOnFail(boolean fallbackOnDefaultInstanceOnFail) {
        this.fallbackOnDefaultInstanceOnFail = fallbackOnDefaultInstanceOnFail;
    }

    @Override
    public File getDirectory() {
        return this.directory;
    }

    //========================================================================

    @Override
    public String addResourceToSave(final String id, final String name, final File resourcePath) throws IOException {
        //Check stream
        if (resourcePath == null) {
            throw new NullPointerException("Resource path shouldn't be null");
        }
        if (!resourcePath.exists()) {
            throw new FileNotFoundException("Can't find resource file");
        }
        IOResource ioResource = new IOResource(id, name, resourcePath);
        this.resources.put(ioResource.getId(), ioResource);
        return ioResource.getId();
    }

}
