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

import org.jdom2.Element;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.XMLHelper;
import org.lifecompanion.controller.media.VideoPlayerController;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequencesI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.impl.configurationcomponent.VideoElement;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.model.LCTask;
import org.lifecompanion.model.impl.profile.LCConfigurationDescription;
import org.lifecompanion.model.impl.configurationcomponent.LCConfigurationComponent;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.model.impl.io.IOContext;
import org.lifecompanion.model.impl.io.IOResource;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * An abstract class just to provide configuration loading commons between every loading task.<br>
 * With this class, each class can extends and use the protected method to use a task without instanciate a new separate task.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractLoadUtilsTask<T> extends LCTask<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractLoadUtilsTask.class);

    protected AbstractLoadUtilsTask(String title) {
        super(title);
    }

    // Class part : "Configuration load"
    //========================================================================

    protected LCConfigurationI loadConfiguration(final File directory, final LCConfigurationDescriptionI configurationDescription) throws Exception {
        return loadConfiguration(directory, configurationDescription, this::updateProgress);
    }

    /**
     * Load a configuration from a directory
     *
     * @param directory                the directory where configuration files are located
     * @param configurationDescription the configuration to load description
     * @return the loaded configuration
     * @throws Exception if loading fail
     */
    public static LCConfigurationI loadConfiguration(final File directory, final LCConfigurationDescriptionI configurationDescription, BiConsumer<Double, Double> progress) throws Exception {
        LCConfigurationI config = new LCConfigurationComponent();
        loadElementIn(config, directory, LCConstant.CONFIGURATION_XML_NAME, progress);

        // Load keylist
        final KeyListNodeI keyListNode = ThreadUtils.executeInCurrentThread(IOHelper.createLoadKeyListTask(directory));
        config.rootKeyListNodeProperty().set(keyListNode);

        // Load sequences
        final UserActionSequencesI sequences = ThreadUtils.executeInCurrentThread(IOHelper.createLoadSequenceTask(directory));
        config.userActionSequencesProperty().set(sequences);

        AbstractLoadUtilsTask.LOGGER.info("Configuration successfully loaded from {}", directory);
        if (configurationDescription != null) {
            configurationDescription.loadedConfigurationProperty().set(config);
        }
        return config;
    }

    protected <K extends XMLSerializable<IOContextI>> void loadElementIn(final K element, final File directory, final String xmlName) throws Exception {
        loadElementIn(element, directory, xmlName, this::updateProgress);
    }

    protected static <K extends XMLSerializable<IOContextI>> void loadElementIn(final K element, final File directory, final String xmlName, BiConsumer<Double, Double> progress)
            throws Exception {
        updateProgress(progress, 0.0, 3.0);
        AbstractLoadUtilsTask.LOGGER.info("A element will be loaded from {}", directory);
        //Load images
        IOContext ioContext = new IOContext(directory);
        File imageDirectory = new File(directory.getPath() + File.separator + LCConstant.CONFIGURATION_IMAGE_DIRECTORY + File.separator);
        File[] imagePaths = imageDirectory.listFiles();
        if (imagePaths != null) {
            for (File imagePath : imagePaths) {
                loadImage(imagePath, ioContext);
            }
        }
        loadVideos(directory, ioContext);
        updateProgress(progress, 1.0, 3.0);
        loadResources(directory, ioContext);
        AbstractLoadUtilsTask.LOGGER.info("Loaded {} resources", ioContext.getIOResource().size());
        updateProgress(progress, 2.0, 3.0);

        //Load xml
        long start = System.currentTimeMillis();
        XMLHelper.loadXMLSerializable(new File(directory.getPath() + File.separator + xmlName), element, ioContext);
        LOGGER.info("Loading took {} ms", (System.currentTimeMillis() - start));
        updateProgress(progress, 3.0, 3.0);
    }

    private static void updateProgress(BiConsumer<Double, Double> progress, double workDone, double max) {
        if (progress != null) {
            progress.accept(workDone, max);
        }
    }

    private static void loadVideos(File directory, IOContext ioContext) {
        File videoDirectory = new File(directory.getPath() + File.separator + LCConstant.CONFIGURATION_VIDEO_DIRECTORY + File.separator);
        File[] videoFiles = videoDirectory.listFiles();
        if (videoFiles != null) {
            for (File videoFile : videoFiles) {
                if (videoFile.isFile()) {
                    String videoId = FileNameUtils.getNameWithoutExtension(videoFile);
                    ioContext.getVideos().put(videoId, new VideoElement(videoId, videoFile));
                }
            }
        }
    }

    /**
     * Load all the configuration resources.
     *
     * @param directory the configuration directory
     * @param context   the io context
     * @throws Exception if the resource loading fails
     */
    private static void loadResources(final File directory, final IOContextI context) throws Exception {
        File resourceDirectory = new File(directory.getPath() + File.separator + LCConstant.CONFIGURATION_RESOURCE_DIRECTORY + File.separator);
        Element rootElement = XMLHelper.readXml(new File(resourceDirectory.getPath() + File.separator + LCConstant.CONFIGURATION_RESOURCE_XML));
        //Load each resource
        List<Element> children = rootElement.getChildren();
        for (Element resourceElement : children) {
            //Load it
            IOResource ioResource = new IOResource();
            ioResource.deserialize(resourceElement, null);
            File resourcePath = new File(resourceDirectory.getPath() + File.separator + ioResource.getId());
            ioResource.setPath(resourcePath);
            context.getIOResource().put(ioResource.getId(), ioResource);
        }
    }

    /**
     * Method to load an image in the image gallery
     *
     * @param image the path to image
     */
    private static void loadImage(final File image, IOContextI ioContext) {
        try {
            ImageElementI addedImage = ImageDictionaries.INSTANCE.getOrAddToConfigurationImageDictionary(image);
            String oldId = FileNameUtils.getNameWithoutExtension(image);
            ioContext.getBackwardImageCompatibilityIdsMap().put(oldId, addedImage.getId());
        } catch (Exception e) {
            AbstractLoadUtilsTask.LOGGER.error("Problem when adding the loaded image {} to the gallery", image, e);
        }
    }
    //========================================================================

    // Configuration description load
    //========================================================================
    protected LCConfigurationDescriptionI loadDescription(final File directory) throws Exception {
        this.updateProgress(0, 1);
        //Try to load XML
        File configDescriptionXMLPath = new File(directory.getPath() + File.separator + LCConstant.CONFIGURATION_DESCRIPTION_XML_NAME);
        if (configDescriptionXMLPath.exists()) {
            LCConfigurationDescriptionI desc = XMLHelper.loadXMLSerializable(configDescriptionXMLPath, new LCConfigurationDescription(), directory);
            this.updateProgress(1, 1);
            return desc;
        } else {
            throw new IllegalArgumentException(
                    "The given configuration description directory doesn't contains any configuration description xml file");
        }
    }
    //========================================================================

}
