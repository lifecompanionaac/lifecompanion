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

package org.lifecompanion.model.api.imagedictionary;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.image.Image;

import java.io.File;

public interface ImageElementI {

    // INFOS
    //========================================================================

    /**
     * @return the dictionary where this image is "stored"
     */
    ImageDictionaryI getDictionary();

    /**
     * @param imageDictionary the parent dictionary
     */
    void setDictionary(ImageDictionaryI imageDictionary);

    /**
     * @return the name of this element
     */
    String getName();

    /**
     * @return this image description (can be null)
     */
    String getDescription();

    /**
     * @return the keyword for this element
     */
    String[] getKeywords();

    /**
     * @param keyWords the keywords associated to this image
     */
    void setKeywords(String languageCode, String[] keyWords);

    /**
     * @return a unique ID for this component, typically : image's hash
     */
    String getId();

    /**
     * @return the extension of this image file
     */
    String getExtension();

    /**
     * @return the path to the image file (generated or static, depending of the dictionary)
     */
    File getRealFilePath();

    /**
     * @return true if this image file still exists (because it can be removed after add).
     */
    boolean isImageFileExist();

    /**
     * @return
     */
    File getRawPath();

    void updateNameAndKeywords(String name, String languageCode, String[] keywords);
    //========================================================================

    // JAVAFX
    //========================================================================

    /**
     * Note : this property is always changed on JavaFX thread.
     *
     * @return the loaded image property.<br>
     * The property will be null until a load request is done.
     */
    ReadOnlyObjectProperty<Image> loadedImageProperty();
    //========================================================================

    // CACHE/LOADING
    //========================================================================
//    /**
//     * Test if the image should be loaded (if there is at least one request for image to load).<br>
//     * If the image shouldn't be loaded, the loaded image is deleted (free memory).
//     *
//     * @return true if the image should be loaded
//     */
//    boolean checkIfExistingLoadRequestOrCleanIfNot();

    /**
     * Indicate that the component with the given ID needs the image loaded.
     *
     * @param componentId the component ID.
     */
    void requestImageLoad(String componentId, final double width, final double height, final boolean keepRatio, final boolean smooth);

    /**
     * Indicate that the component with the given ID needs the image unloaded (to free memory).<br>
     *
     * @param componentId the component ID.
     */
    void requestImageUnload(String componentId);

    boolean shouldSaveImage();

    boolean shouldSaveImageId();

//    /**
//     * Must try to load the image from the internal file of this image.<br>
//     * Could chose to reload it if the wanted size is greater than previous loaded size
//     *
//     * @param width     the wanted width
//     * @param height    the wanted height
//     * @param keepRatio if we want to keep the image ratio
//     * @param smooth    if we want a smooth resize
//     */
    //void requestLoadImage(final double width, final double height, final boolean keepRatio, final boolean smooth);

//    void unloadImage();


    //========================================================================
}
