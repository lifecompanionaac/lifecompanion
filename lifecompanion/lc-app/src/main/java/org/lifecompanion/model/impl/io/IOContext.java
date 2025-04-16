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

import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
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

    private final Map<ImageElementI, List<ImageUseComponentI>> imagesV3;

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

    private final boolean mobileVersion;

    public IOContext(final File directoryP, boolean mobileVersion) {
        this.imagesV2 = new ArrayList<>();
        this.resources = new HashMap<>();
        this.backwardImageCompatibilityIdsMap = new HashMap<>();
        this.pluginIdDependencies = new HashSet<>();
        this.videos = new HashMap<>();
        this.imagesV3 = new HashMap<>();
        this.directory = directoryP;
        this.mobileVersion = mobileVersion;
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
    public Map<ImageElementI, List<ImageUseComponentI>> getImages() {
        return imagesV3;
    }

    @Override
    public void addImage(ImageElementI imageElement, ImageUseComponentI imageUseComponent) {
        List<ImageUseComponentI> imageUseComponents = imagesV3.computeIfAbsent(imageElement, k -> new ArrayList<>());
        if (imageUseComponent != null) {
            imageUseComponents.add(imageUseComponent);
        }
    }

    @Override
    public Map<String, String> getBackwardImageCompatibilityIdsMap() {
        return backwardImageCompatibilityIdsMap;
    }

    @Override
    public boolean isMobileVersion() {
        return mobileVersion;
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
