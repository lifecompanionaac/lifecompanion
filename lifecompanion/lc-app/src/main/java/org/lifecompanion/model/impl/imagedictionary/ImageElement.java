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

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class ImageElement implements ImageElementI {

    /**
     * A name for this image
     */
    @SerializedName("nm")
    protected String name;

    /**
     * Identifier for this element (to keep unique directories)
     */
    protected String id;

    /**
     * Keywords for this element
     */
    @SerializedName("kws")
    protected Map<String, String[]> keywords;

    /**
     * Path to the image (can be null if the image is from a default dictionary)
     */
    @SerializedName("rp")
    protected File rawPath;

    protected String description;

    /**
     * Dictionary
     */
    protected transient ImageDictionaryI dictionary;

    /**
     * The image object when image is loaded
     */
    protected transient ObjectProperty<Image> loadedImage;

    /**
     * Keep all loading requests per component
     */
    private final transient Map<String, Boolean> loadingRequest;

    public ImageElement(String id, String name, Map<String, String[]> keywords, File rawPath) {
        this();
        this.id = id;
        this.name = name;
        this.keywords = keywords;
        this.rawPath = rawPath;
    }

    /**
     * To create a image, call this constructor only when load from file.
     */
    public ImageElement() {
        super();
        this.loadedImage = new SimpleObjectProperty<>(this, "loadedImage");
        this.loadingRequest = new HashMap<>(4, 0.95f);
    }

    @Override
    public ImageDictionaryI getDictionary() {
        return dictionary;
    }

    @Override
    public void setDictionary(ImageDictionaryI dictionary) {
        this.dictionary = dictionary;
    }


    @Override
    public String getDescription() {
        return StringUtils.isNotBlank(description) ? description : StringUtils.stripToEmpty(getName()) + "\n" + Arrays.stream(getKeywords())
                .filter(e -> !e.equals(getName()))
                .map(String::toLowerCase)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getExtension() {
        return FileNameUtils.getExtension(getRealFilePath());
    }

    @Override
    public File getRawPath() {
        return rawPath;
    }

    @Override
    public ReadOnlyObjectProperty<Image> loadedImageProperty() {
        return loadedImage;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getKeywords() {
        return keywords != null ? this.keywords.getOrDefault(UserConfigurationController.INSTANCE.userLanguageProperty().get(), this.keywords.entrySet().iterator().next().getValue()) : new String[0];
    }

    @Override
    public void setKeywords(String languageCode, String[] keyWords) {
        this.keywords.put(languageCode, keyWords);
    }

    @Override
    public File getRealFilePath() {
        return this.dictionary.getImagePath(this);
    }

    @Override
    public File getOrGenerateDecodedFilePath() throws Exception {
        File realFilePath = getRealFilePath();
        if (dictionary.isEncodedDictionary()) {
            File decodedFile = new File(IOUtils.getTempDir("decoded-image") + File.separator + realFilePath.getName());
            org.lifecompanion.framework.commons.utils.io.IOUtils.createParentDirectoryIfNeeded(decodedFile);
            if (!decodedFile.exists()) {
                DESKeySpec keySpec = new DESKeySpec(dictionary.getIdCheck().getBytes(StandardCharsets.UTF_8));
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                SecretKey key = keyFactory.generateSecret(keySpec);
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.DECRYPT_MODE, key);
                try (CipherInputStream cipherInputStream = new CipherInputStream(new BufferedInputStream(new FileInputStream(realFilePath)), cipher)) {
                    try (BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(decodedFile))) {
                        org.lifecompanion.framework.commons.utils.io.IOUtils.copyStream(cipherInputStream, fos);
                    }
                }
            }
            return decodedFile;
        }
        return realFilePath;
    }

    @Override
    public boolean isImageFileExist() {
        return this.getRealFilePath().exists();
    }

    @Override
    public void updateNameAndKeywords(String name, String languageCode, String[] keywords) {
        this.name = name;
        this.keywords.put(languageCode, keywords);
    }

    // LOADING
    //========================================================================
    private transient double lastRequestedWidth, lastRequestedHeight;
    private transient boolean cachedKeepRatio, cachedSmooth;

    @Override
    public void requestImageLoad(String componentId, double width, double height, boolean keepRatio, boolean smooth) {
        this.cachedKeepRatio = keepRatio;
        this.cachedSmooth = smooth;
        this.loadingRequest.put(componentId, true);
        loadOrUnloadImage(width, height);
    }

    Map<String, Boolean> getLoadingRequest() {
        return loadingRequest;
    }

    @Override
    public void requestImageUnload(final String componentId) {
        this.loadingRequest.remove(componentId);
        this.loadOrUnloadImage(-1, -1);
    }


    private void loadOrUnloadImage(double width, double height) {
        if (this.loadingRequest.containsValue(true)) {
            if (this.loadedImage.get() == null || (lastRequestedWidth < width || lastRequestedHeight < height)) {
                lastRequestedWidth = Math.max(width, lastRequestedWidth);
                lastRequestedHeight = Math.max(height, lastRequestedHeight);
                this.dictionary.loadImage(id, loadedImage, this::getOrGenerateDecodedFilePath, lastRequestedWidth, lastRequestedHeight, cachedKeepRatio, cachedSmooth, null);
            }
        } else {
            lastRequestedWidth = 0.0;
            lastRequestedHeight = 0.0;
            this.dictionary.cancelLoadImage(id);
            FXThreadUtils.runOnFXThread(() -> this.loadedImage.set(null));
        }
    }

    @Override
    public boolean shouldSaveImage() {
        return this.dictionary.isCustomDictionary() || this.dictionary.isOptionalDictionary();
    }

    @Override
    public boolean shouldSaveImageId() {
        return true;
    }
    //========================================================================
}
