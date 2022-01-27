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
package org.lifecompanion.config.data.control;

import org.jdom2.Element;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.XMLSerializable;
import org.lifecompanion.api.mode.LCStateListener;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.control.InstallationConfigurationController;
import org.lifecompanion.base.data.control.refacto.ProfileController;
import org.lifecompanion.base.data.io.XMLHelper;
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

    //TODO : already displayed tips

    LCStateController() {
        this.defaultDirectories = new HashMap<>();
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
                Arrays.stream(roots).filter(f -> f.getTotalSpace() > MIN_EXTERNAL_DEVICE_SIZE && f.getTotalSpace() < MAX_EXTERNAL_DEVICE_SIZE).min(Comparator.comparingLong(File::getTotalSpace)).orElse(DEFAULT_FOLDER)
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

    private static final String NODE_LC = "LifeCompanion", ATB_DIRECTORY_PATH = "directoryPath", NODE_DEFAULT_DIRECTORIES = "DefaultDirectories", NODE_DEFAULT_DIRECTORY = "DefaultDirectory", ATB_DIR_TYPE = "type", ATB_DIR_PATH = "path";

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
    }
    //========================================================================

}
