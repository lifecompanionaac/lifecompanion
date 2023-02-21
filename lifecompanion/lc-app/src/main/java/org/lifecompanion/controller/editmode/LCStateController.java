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
package org.lifecompanion.controller.editmode;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.appinstallation.InstallationConfigurationController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.io.XMLHelper;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * A controller mainly used to manage the application state after/before the application start or stop.<br>
 * Typically, the state is use to save application information.<br>
 * For example, it saves the configurations directories that should be deleted on startup.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum LCStateController implements XMLSerializable<Void>, LCStateListener {
    INSTANCE;

    private final Logger LOGGER = LoggerFactory.getLogger(LCStateController.class);

    /**
     * The last selected profile ID, to select a default profile on startup
     */
    private String selectedProfile;

    /**
     * Last selected webcam name
     */
    private String lastSelectedWebcamName;

    /**
     * Default directories for export
     */
    private final Map<FileChooserType, String> defaultDirectories;

    /**
     * Contains all dictionaries ID to put in first in search results
     */
    private final Set<String> favoriteImageDictionaries;

    /**
     * If the training dialog should never be shown again
     */
    private final BooleanProperty hideTrainingDialog;

    private long lastTrainingDialogShow;

    LCStateController() {
        this.defaultDirectories = new HashMap<>();
        favoriteImageDictionaries = new HashSet<>();
        this.hideTrainingDialog = new SimpleBooleanProperty(false);
        this.lastTrainingDialogShow = 0;
        this.initBinding();
    }

    private void initBinding() {
        //On profile change, set the last selected profile
        ProfileController.INSTANCE.currentProfileProperty().addListener((obs) -> {
            LCProfileI current = ProfileController.INSTANCE.currentProfileProperty().get();
            if (current != null) {
                this.selectedProfile = current.getID();
            }
        });
    }

    // Class part : "Getter/setter"
    //========================================================================
    public String getLastSelectedProfileID() {
        return this.selectedProfile;
    }

    public String getLastSelectedWebcamName() {
        return lastSelectedWebcamName;
    }

    public void setLastSelectedWebcamName(String lastSelectedWebcamName) {
        this.lastSelectedWebcamName = lastSelectedWebcamName;
    }

    public Set<String> getFavoriteImageDictionaries() {
        return favoriteImageDictionaries;
    }
    //========================================================================

    // TRAINING DIALOG
    //========================================================================
    public BooleanProperty hideTrainingDialogProperty() {
        return hideTrainingDialog;
    }

    public long getLastTrainingDialogShow() {
        return lastTrainingDialogShow;
    }

    public void setLastTrainingDialogShow(long lastTrainingDialogShow) {
        this.lastTrainingDialogShow = lastTrainingDialogShow;
    }
    //========================================================================

    // Default DIR
    //========================================================================
    private static final long ONE_GB = 1073741824L;
    private static final long MIN_EXTERNAL_DEVICE_SIZE = ONE_GB / 4; // 250 MB
    private static final long MAX_EXTERNAL_DEVICE_SIZE = ONE_GB * 80; // 80 GB
    private static final File DEFAULT_FOLDER = new File(System.getProperty("user.home"));

    public void updateDefaultDirectory(FileChooserType fileChooserType, File directory) {
        if (directory != null) {
            this.defaultDirectories.put(fileChooserType, directory.getPath());
        } else {
            this.defaultDirectories.remove(fileChooserType);
        }
    }

    public File getDefaultDirectoryFor(FileChooserType fileChooserType) {
        // Try to find it in saved default directories
        final String defaultDir = defaultDirectories.get(fileChooserType);
        if (defaultDir != null) {
            File defaultDirFile = new File(defaultDir);
            if (defaultDirFile.exists() && StringUtils.isDifferent(defaultDirFile.getAbsolutePath(), DEFAULT_FOLDER.getAbsolutePath())) {
                return defaultDirFile;
            }
        }
        // Try to find it as external device (if export type) or take the user home
        final File[] roots = File.listRoots();
        return fileChooserType.isUseExternalDevice() && roots != null ?
                Arrays.stream(roots)
                        .filter(f -> f.getTotalSpace() > MIN_EXTERNAL_DEVICE_SIZE && f.getTotalSpace() < MAX_EXTERNAL_DEVICE_SIZE)
                        .min(Comparator.comparingLong(File::getTotalSpace))
                        .orElse(DEFAULT_FOLDER)
                : DEFAULT_FOLDER;
    }
    //========================================================================

    // Class part : "Execute startup/shutdown action"
    //========================================================================

    @Override
    public void lcStart() {
        this.load();
    }

    @Override
    public void lcExit() {
        this.save();
    }
    //========================================================================

    // Class part : "Save/load"
    //========================================================================
    private File getStateFilePath() {
        return new File(InstallationConfigurationController.INSTANCE.getUserDirectory().getPath() + File.separator + LCConstant.EXT_PATH_LC_STATE_FILENAME);
    }

    /**
     * Save the application state, this method can be called multiple times while application is still running.<br>
     * It should be done if the saved informations are sensitive, so if the application crash, the informations are still saved.
     */
    public void save() {
        try {
            XMLHelper.saveXMLSerializable(getStateFilePath(), this, null);
            this.LOGGER.info("LifeCompanion state saved");
        } catch (Exception e) {
            this.LOGGER.error("Error while saving the LifeCompanion state to XML file", e);
        }
    }

    /**
     * Load the previous saved application state.<br>
     * Should be called once.
     */
    private void load() {
        try {
            XMLHelper.loadXMLSerializable(getStateFilePath(), this, null);
            this.LOGGER.info("LifeCompanion state loaded");
        } catch (Exception e) {
            this.LOGGER.error("Error while loading the LifeCompanion state from XML file", e);
        }
    }

    private static final String NODE_LC = "LifeCompanion", ATB_DIRECTORY_PATH = "directoryPath", NODE_DEFAULT_DIRECTORIES = "DefaultDirectories", NODE_DEFAULT_DIRECTORY = "DefaultDirectory", ATB_DIR_TYPE = "type", ATB_DIR_PATH = "path", NODE_IMAGE_DICTIONARIES = "FavoriteImageDictionaries", NODE_IMAGE_DICTIONARY = "FavoriteImageDictionary";

    @Override
    public Element serialize(final Void contextP) {
        Element root = new Element(LCStateController.NODE_LC);
        XMLObjectSerializer.serializeInto(LCStateController.class, this, root);

        // Add default
        Element defaultDirectories = new Element(NODE_DEFAULT_DIRECTORIES);
        root.addContent(defaultDirectories);
        this.defaultDirectories.forEach((fileChooserType, path) -> {
            Element dirNode = new Element(NODE_DEFAULT_DIRECTORY);
            defaultDirectories.addContent(dirNode);
            XMLUtils.write(fileChooserType, ATB_DIR_TYPE, dirNode);
            XMLUtils.write(path, ATB_DIR_PATH, dirNode);
        });

        // Image dictionaries
        Element imageDictionaries = new Element(NODE_IMAGE_DICTIONARIES);
        root.addContent(imageDictionaries);
        for (String favoriteImageDictionary : this.favoriteImageDictionaries) {
            Element imageDictionaryNode = new Element(NODE_IMAGE_DICTIONARY);
            XMLUtils.write(favoriteImageDictionary, "id", imageDictionaryNode);
            imageDictionaries.addContent(imageDictionaryNode);
        }

        return root;
    }

    @Override
    public void deserialize(final Element root, final Void contextP) throws LCException {
        XMLObjectSerializer.deserializeInto(LCStateController.class, this, root);
        final Element defaultDirectoriesNode = root.getChild(LCStateController.NODE_DEFAULT_DIRECTORIES);
        if (defaultDirectoriesNode != null) {
            final List<Element> defaultDirNodes = defaultDirectoriesNode.getChildren();
            for (Element defaultDirNode : defaultDirNodes) {
                this.defaultDirectories.put((FileChooserType) XMLUtils.readEnum(FileChooserType.class, ATB_DIR_TYPE, defaultDirNode), XMLUtils.readString(ATB_DIR_PATH, defaultDirNode));
            }
        }

        final Element imageDictionaries = root.getChild(LCStateController.NODE_IMAGE_DICTIONARIES);
        if (imageDictionaries != null) {
            List<Element> imageDictionariesChildren = imageDictionaries.getChildren();
            for (Element imageDictionaryChild : imageDictionariesChildren) {
                String dicID = XMLUtils.readString("id", imageDictionaryChild);
                if (dicID != null) {
                    this.favoriteImageDictionaries.add(dicID);
                }
            }
        }
    }
    //========================================================================

}
