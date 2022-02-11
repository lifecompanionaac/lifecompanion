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
package org.lifecompanion.controller.io;

import org.jdom2.Element;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.IOResourceI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.util.LCTask;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.model.impl.io.IOContext;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

    protected AbstractSavingUtilsTask(String title) {
        super(title);
    }

    protected void saveXmlSerializable(final XMLSerializable<IOContextI> element, final File directory, final String xmlName) throws Exception {
        //Prestart
        File imageDirectory = new File(directory.getPath() + File.separator + LCConstant.CONFIGURATION_IMAGE_DIRECTORY + File.separator);
        if (!imageDirectory.exists()) {
            imageDirectory.mkdirs();
        }
        //Save the XML
        IOContext context = new IOContext(directory);
        LOGGER.info("XML tree created from the element.");
        this.updateProgress(1, 5);
        File file = new File(directory.getPath() + File.separator + xmlName);
        XMLHelper.saveXMLSerializable(file, element, context);
        this.updateProgress(2, 5);
        LOGGER.info("XML saved to {}", file);
        //Images saving
        List<ImageElementI> images = context.getImagesToSaveV2();
        LOGGER.info("Will save {} images for the element", images.size());
        long start = System.currentTimeMillis();
        HashSet<String> imageIds = new HashSet<>();
        for (ImageElementI image : images) {
            if (image.shouldSaveImage()) {
                this.saveImage(imageDirectory, context, image);
                imageIds.add(image.getId());
            }
        }
        //Clean images
        int deletedImages = this.cleanImages(imageDirectory, imageIds);
        LOGGER.info("{} images saved and {} images deleted in {} ms", images.size(), deletedImages,
                System.currentTimeMillis() - start);
        this.updateProgress(3, 5);
        //Resources
        Map<String, IOResourceI> resources = context.getIOResource();
        LOGGER.info("Will save {} resources for the element", resources.size());
        start = System.currentTimeMillis();
        this.saveResources(directory, resources);
        LOGGER.info("{} resources saved in {} ms", resources.size(), System.currentTimeMillis() - start);
        this.updateProgress(4, 5);
    }

    /**
     * To clean the images in the configuration directory that are not used by the configuration.<br>
     * This also remove images from gallery if they were loaded from the configuration resources.
     *
     * @param imageDirectory the directory that contains configuration images
     * @param imageIds       all the configuration image ids
     * @return the delete image count
     */
    private int cleanImages(final File imageDirectory, final HashSet<String> imageIds) {
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
    private void saveImage(final File imageDirectory, final IOContextI context, final ImageElementI image) {
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
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("The given image {} was not save!", image.getId(), e);
            }
        }

    }

    /**
     * Save all the resource associated to the configuration
     *
     * @throws IOException if the resource XML can't be saved
     */
    private void saveResources(final File directory, final Map<String, IOResourceI> resources) throws IOException {
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
    }

}
