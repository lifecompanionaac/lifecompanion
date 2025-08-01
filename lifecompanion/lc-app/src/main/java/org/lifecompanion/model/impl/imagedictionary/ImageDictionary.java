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

package org.lifecompanion.model.impl.imagedictionary;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.imagedictionary.PathSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ImageDictionary implements ImageDictionaryI {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageDictionary.class);
    private String id;
    private String name;
    private String description;
    private String author;
    private String imageExtension;
    private String url;
    private boolean customDictionary;
    private boolean optionalDictionary;
    private String idCheck;
    private final ObservableList<ImageElementI> images;
    private final Map<String, String> patched;

    private transient File imageDirectory;

    public ImageDictionary() {
        images = FXCollections.observableArrayList();
        patched = new HashMap<>();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return Translation.getText(description);
    }

    @Override
    public String getAuthor() {
        return Translation.getText(author);
    }

    @Override
    public boolean isCustomDictionary() {
        return customDictionary;
    }

    @Override
    public boolean isOptionalDictionary() {
        return optionalDictionary;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getIdCheck() {
        return idCheck;
    }

    @Override
    public boolean isEncodedDictionary() {
        return StringUtils.isNotBlank(idCheck);
    }

    @Override
    public ObservableList<ImageElementI> getImages() {
        return images;
    }

    // SETTER (not public API)
    //========================================================================
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCustomDictionary(boolean customDictionary) {
        this.customDictionary = customDictionary;
    }
    //========================================================================


    // IMAGE LOADING
    //========================================================================
    public File getImageDirectory() {
        return imageDirectory;
    }

    public void setImageDirectory(File imageDirectory) {
        this.imageDirectory = imageDirectory;
    }

    public File getImagePath(ImageElementI imageElement) {
        if (this.customDictionary) {
            return imageElement.getRawPath();
        } else {
            return new File(this.imageDirectory.getPath() + File.separator + imageElement.getId() + "." + this.imageExtension);
        }
    }

    @Override
    public void cancelLoadImage(String imageId) {
        ImageDictionaries.INSTANCE.cancelLoadImage(imageId);
    }

    @Override
    public void loadImage(String imageId, ObjectProperty<Image> target, PathSupplier pathSupplier, double width, double height, boolean keepRatio, boolean smooth, Runnable callback) {
        ImageDictionaries.INSTANCE.requestImageLoading(new ImageLoadingTask(imageId, target, pathSupplier, width, height, keepRatio, smooth, callback));
    }

    public void loaded(Map<String, ImageElementI> allImageMap) {
        // TODO : clean > delete files that doesn't exist...
        this.images.forEach(e -> {
            e.setDictionary(this);
            allImageMap.put(e.getId(), e);
        });
        patched.forEach((previousId, newId) -> {
            ImageElementI updatedImage = allImageMap.get(newId);
            if (updatedImage != null) {
                allImageMap.put(previousId, updatedImage);
            }
        });
    }
    //========================================================================
}
