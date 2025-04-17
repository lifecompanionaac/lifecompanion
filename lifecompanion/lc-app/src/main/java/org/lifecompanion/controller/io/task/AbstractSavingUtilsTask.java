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
package org.lifecompanion.controller.io.task;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jdom2.Element;
import org.lifecompanion.controller.io.XMLHelper;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.IOResourceI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.impl.imagedictionary.StaticImageElement;
import org.lifecompanion.util.javafx.ImageUtils;
import org.lifecompanion.util.model.LCTask;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.io.IOContext;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * An abstract class to provide saving methods.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractSavingUtilsTask<T> extends LCTask<T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractSavingUtilsTask.class);

    protected final boolean mobileVersion;

    protected AbstractSavingUtilsTask(String title, boolean mobileVersion) {
        super(title);
        this.mobileVersion = mobileVersion;
    }

    protected void saveXmlSerializable(final XMLSerializable<IOContextI> element, final File directory, final String xmlName) throws Exception {
        File file = new File(directory.getPath() + File.separator + xmlName);
        IOUtils.createParentDirectoryIfNeeded(file);
        //Save the XML
        IOContext context = new IOContext(directory, mobileVersion);
        LOGGER.info("XML tree created from the element.");
        this.updateProgress(1, 6);
        XMLHelper.saveXMLSerializable(file, element, context);
        this.updateProgress(2, 6);
        LOGGER.info("XML saved to {}", file);

        this.saveImages(directory, context);
        this.saveVideos(directory, context);

        //Resources
        Map<String, IOResourceI> resources = context.getIOResource();
        this.saveResources(directory, resources);
        this.updateProgress(5, 6);
    }

    private void saveImages(final File directory, IOContext context) {
        //Images saving
        File imageDirectory = new File(directory.getPath() + File.separator + LCConstant.CONFIGURATION_IMAGE_DIRECTORY + File.separator);
        if (!imageDirectory.exists()) {
            imageDirectory.mkdirs();
        }
        Map<ImageElementI, List<ImageUseComponentI>> images = context.getImages();
        LOGGER.info("Will save {} images for the element", images.size());
        long start = System.currentTimeMillis();
        HashSet<String> imageIds = new HashSet<>();
        for (Map.Entry<ImageElementI, List<ImageUseComponentI>> imageEntry : images.entrySet()) {
            ImageElementI imageElement = imageEntry.getKey();
            if (imageElement.shouldSaveImage() || (context.isMobileVersion() && !(imageElement instanceof StaticImageElement))) {
                List<ImageUseComponentI> imageUseComponents = imageEntry.getValue();
                if (CollectionUtils.isEmpty(imageUseComponents)) {
                    String imageId = this.saveImage(imageDirectory, context, imageElement, null);
                    if (imageId != null) {
                        imageIds.add(imageId);
                    }
                } else {
                    for (ImageUseComponentI imageUseComponent : imageUseComponents) {
                        String imageId = this.saveImage(imageDirectory, context, imageElement, imageUseComponent);
                        if (imageId != null) {
                            imageIds.add(imageId);
                        }
                    }
                }
            }
        }
        //Clean images
        int deletedImages = this.cleanUnknownFiles(imageDirectory, imageIds);
        LOGGER.info("{} images saved and {} images deleted in {} ms", images.size(), deletedImages,
                System.currentTimeMillis() - start);
        this.updateProgress(3, 6);
    }


    private int cleanUnknownFiles(final File imageDirectory, final Set<String> imageIds) {
        int count = 0;
        File[] images = imageDirectory.listFiles();
        if (images != null) {
            for (File image : images) {
                String imageId = FileNameUtils.getNameWithoutExtension(image);
                if (!imageIds.contains(imageId)) {
                    image.delete();
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Save the image if necessary
     *
     * @param imageDirectory directory where image are saved
     * @param context        the saving context
     * @param image          the image to save
     */
    private String saveImage(final File imageDirectory, final IOContextI context, final ImageElementI image, ImageUseComponentI imageUseComponent) {
        // Image should be exported with modification
        if (imageUseComponent != null && (imageUseComponent.enableRemoveBackgroundProperty().get() ||
                imageUseComponent.enableReplaceColorProperty().get() ||
                imageUseComponent.useViewPortProperty().get())) {
            // Load
            try (FileInputStream fisOriginalImage = new FileInputStream(image.getRealFilePath())) {
                Image imageFx = new Image(fisOriginalImage);

                // Modification
                if (imageUseComponent.enableRemoveBackgroundProperty().get()) {
                    imageFx = ImageUtils.removeBackground(imageFx, imageUseComponent.removeBackgroundThresholdProperty().get());
                }
                if (imageUseComponent.enableReplaceColorProperty().get()) {
                    imageFx = ImageUtils.replaceColorInImage(imageFx, imageUseComponent.colorToReplaceProperty().get(), imageUseComponent.replacingColorProperty().get(),
                            imageUseComponent.replaceColorThresholdProperty().get());
                }
                BufferedImage buffImage = SwingFXUtils.fromFXImage(imageFx, null);
                if (imageUseComponent.useViewPortProperty().get()) {
                    buffImage = ImageUtils.cropImage(buffImage,
                            imageUseComponent.viewportXPercentProperty().get(),
                            imageUseComponent.viewportYPercentProperty().get(),
                            imageUseComponent.viewportWidthPercentProperty().get(),
                            imageUseComponent.viewportHeightPercentProperty().get());
                }

                // Save
                String modifiedImageId = image.getId() + "_" + imageUseComponent.getID();
                File imageFile = new File(imageDirectory.getPath() + File.separator + modifiedImageId + ".png");
                LOGGER.info("Image {} will be saved {}", modifiedImageId, imageFile);
                boolean write = ImageIO.write(buffImage, "png", imageFile);
                LOGGER.info("Success : {}", write);

                return modifiedImageId;
            } catch (Exception e) {
                LOGGER.warn("The modified image {} was not save!", image.getId(), e);
            }
        }
        // Image modification failed or is not needed
        File imageFile = new File(imageDirectory.getPath() + File.separator + image.getId() + "." + image.getExtension());
        if (!imageFile.exists()) {
            try {
                /*
                 * Avoid saving the image to the loaded dimensions (previous implementation did this)
                 * 1 : it's longer than a file copy
                 * 2 : if the image size change after, the quality will decrease
                 * 3 : the only disadvantage is that configuration are bigger
                 */
                //Copy the image in configuration directory
                try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                    try (FileInputStream fis = new FileInputStream(image.getRealFilePath())) {
                        IOUtils.copyStream(fis, fos);
                        return image.getId();
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("The given image {} was not save!", image.getId(), e);
            }
        }
        return image.getId();
    }

    private void saveVideos(final File directory, IOContext context) {
        File videoDirectory = new File(directory.getPath() + File.separator + LCConstant.CONFIGURATION_VIDEO_DIRECTORY + File.separator);
        if (!videoDirectory.exists()) {
            videoDirectory.mkdirs();
        }
        LOGGER.info("Will save {} videos for the element", context.getVideos().size());
        long start = System.currentTimeMillis();
        for (VideoElementI video : context.getVideos().values()) {
            this.saveVideo(videoDirectory, context, video);
        }
        //Clean images
        int deletedVideos = this.cleanUnknownFiles(videoDirectory, context.getVideos().keySet());
        LOGGER.info("{} video saved and {} videos deleted in {} ms", context.getVideos().size(), deletedVideos,
                System.currentTimeMillis() - start);
        this.updateProgress(4, 6);
    }

    private void saveVideo(File videoDirectory, IOContext context, VideoElementI video) {
        // FIXME : extension
        File imageFile = new File(videoDirectory.getPath() + File.separator + video.getId() + ".mp4");//+ video.getExtension());
        if (!imageFile.exists()) {
            try {
                try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                    try (FileInputStream fis = new FileInputStream(video.getPath())) {
                        IOUtils.copyStream(fis, fos);
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("The given image {} was not save!", video.getId(), e);
            }
        }
    }

    /**
     * Save all the resource associated to the configuration
     *
     * @throws IOException if the resource XML can't be saved
     */
    private void saveResources(final File directory, final Map<String, IOResourceI> resources) throws IOException {
        LOGGER.info("Will save {} resources for the element", resources.size());
        long start = System.currentTimeMillis();
        Element root = new Element(IOResourceI.NODE_RESOURCES);
        File resourceDirectory = new File(directory.getPath() + File.separator + LCConstant.CONFIGURATION_RESOURCE_DIRECTORY + File.separator);
        if (!resourceDirectory.exists()) {
            resourceDirectory.mkdirs();
        }
        Set<String> ids = resources.keySet();
        HashSet<String> validResourceIds = new HashSet<>();
        for (String resourceId : ids) {
            IOResourceI resource = resources.get(resourceId);
            //Try to copy the resource only if the resource is not already present or if the size is not the same
            File resourcePath = new File(resourceDirectory.getPath() + File.separator + resourceId);
            if (!resourcePath.exists() || resource.getFileLength() != resourcePath.length()) {
                try {
                    IOUtils.copyFiles(resource.getPath(), resourcePath);
                    validResourceIds.add(resourceId);
                    root.addContent(resource.serialize(null));
                } catch (Exception e) {
                    LOGGER.warn("Couldn't save the resource with {}", resourceId, e);
                }
            } else {
                //Directly add it to XML
                validResourceIds.add(resourceId);
                root.addContent(resource.serialize(null));
            }
        }

        //Remove all invalid file (to save space)
        File[] resourceFiles = resourceDirectory.listFiles();
        if (resourceFiles != null) {
            for (File resourceFile : resourceFiles) {
                if (!validResourceIds.contains(resourceFile.getName())) {
                    boolean deleted = resourceFile.delete();
                    LOGGER.info("Delete invalid resource file {} (resource ID {}) : {}", resourceFile, resourceFile.getName(),
                            deleted);
                }
            }
        }
        //Save the resource XML
        XMLHelper.writeXml(new File(resourceDirectory.getPath() + File.separator + LCConstant.CONFIGURATION_RESOURCE_XML), root);

        LOGGER.info("{} resources saved in {} ms", resources.size(), System.currentTimeMillis() - start);
    }

}
