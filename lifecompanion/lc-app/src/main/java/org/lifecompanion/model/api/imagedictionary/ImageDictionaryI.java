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

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.io.File;

public interface ImageDictionaryI {

    // TODO : search methods ?

    // INFO
    //========================================================================
    String getId();

    /**
     * @return the name of this dictionary
     */
    String getName();

    /**
     * @return the description of this dictionary
     */
    String getDescription();

    /**
     * @return the author of this dictionary
     */
    String getAuthor();

    /**
     * @return an url to this dictionary source
     */
    String getUrl();

    /**
     * @return true if this dictionary is a user added dictionary : its images should be saved within configurations files
     */
    boolean isCustomDictionary();

    /**
     * @return true if this dictionary is an optional dictionary : its images should be saved within configurations files
     */
    boolean isOptionalDictionary();
    //========================================================================

    // CONTENT
    //========================================================================

    /**
     * @return the {@link ObservableList} list that contains every element inside this dictionary.
     */
    ObservableList<ImageElementI> getImages();
    //========================================================================

    // IMAGE LOADING
    //========================================================================
    void loadImage(String imageId, final ObjectProperty<Image> target, final File path, final double width, final double height, final boolean keepRatio,
                   final boolean smooth, final Runnable callback);

    void cancelLoadImage(String imageId);

    File getImagePath(ImageElementI imageElement);
    //========================================================================

    /*
     * Idea to have a dictionary able to search in external resource.
     * Search method that would return ImageElementSearchResult containing only the name, description, thumbnail...
     * Each SearchResult could then return ImageElement if the image is selected.
     * This would allow dictionary search with API or local folder without having to firstly cache results.
     */
}
