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
package org.lifecompanion.model.impl.profile;

import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jdom2.Element;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.ChangelogEntryI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LcTechInfoI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * Implementation of {@link LCConfigurationDescriptionI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCConfigurationDescription implements LCConfigurationDescriptionI {
    private final static Logger LOGGER = LoggerFactory.getLogger(LCConfigurationDescription.class);
    /**
     * The ID of the configuration represented by this description
     */
    private String configurationID;

    /**
     * Image to save for this description - if null, no image will be saved
     */
    private Image configurationImageToSave;

    /**
     * Date when configuration was the last saved/open/created, must never be null
     */
    @XMLGenericProperty(Date.class)
    private final ObjectProperty<Date> lastDate;

    /**
     * The name of the configuration
     */
    private final StringProperty configurationName;

    /**
     * Configuration author
     */
    private final StringProperty configurationAuthor;

    /**
     * Configuration description
     */
    private final StringProperty configurationDescription;

    /**
     * The configuration loaded related to this configuration description
     */
    private final ObjectProperty<LCConfigurationI> loadedConfiguration;

    /**
     * Launch the configuration in use mode if selected
     */
    private final BooleanProperty launchInUseMode;

    /**
     * Tech info
     */
    private final LcTechInfoI techInfo;

    /**
     * Path to configuration image
     */
    private transient String imagePath;

    /**
     * Change log entries
     */
    private final List<ChangelogEntryI> changelogEntries;

    /**
     * Create the description for a given configuration
     */
    public LCConfigurationDescription() {
        this.configurationName = new SimpleStringProperty();
        this.configurationDescription = new SimpleStringProperty();
        this.configurationAuthor = new SimpleStringProperty();
        this.loadedConfiguration = new SimpleObjectProperty<>();
        this.lastDate = new SimpleObjectProperty<>(new Date());
        this.launchInUseMode = new SimpleBooleanProperty(false);
        this.techInfo = new LcTechInfo();
        this.changelogEntries = new ArrayList<>(50);
        //Change ID with configuration change
        this.loadedConfiguration.addListener((obs) -> {
            LCConfigurationI config = this.loadedConfiguration.get();
            if (config != null) {
                this.configurationID = config.getID();
            }
        });
    }

    @Override
    public void setConfigurationImageToSave(Image configurationImageToSave) {
        this.configurationImageToSave = configurationImageToSave;
    }

    @Override
    public Image getConfigurationImageToSave() {
        return configurationImageToSave;
    }

    @Override
    public void requestImageLoad(final Consumer<Image> callback) {
        if (this.imagePath != null) {
            File imageFile = new File(this.imagePath);
            if (imageFile.exists()) {
                AsyncExecutorController.INSTANCE.addAndExecute(false, true, () -> {
                    try (FileInputStream imageIS = new FileInputStream(imageFile)) {
                        final Image value = new Image(imageIS);
                        FXThreadUtils.runOnFXThread(() -> callback.accept(value));
                    } catch (IOException e) {
                        LCConfigurationDescription.LOGGER.warn("Couldn't load the configuration preview image", e);
                    }
                });
            } else {
                LCConfigurationDescription.LOGGER.warn("There is no image for the configuration description {}", this.configurationID);
                callback.accept(null);
            }
        }
    }

    @Override
    public StringProperty configurationNameProperty() {
        return this.configurationName;
    }

    @Override
    public StringProperty configurationAuthorProperty() {
        return this.configurationAuthor;
    }

    @Override
    public StringProperty configurationDescriptionProperty() {
        return this.configurationDescription;
    }

    @Override
    public String getConfigurationId() {
        return this.configurationID;
    }

    @Override
    public ObjectProperty<LCConfigurationI> loadedConfigurationProperty() {
        return this.loadedConfiguration;
    }

    @Override
    public ObjectProperty<Date> configurationLastDateProperty() {
        return this.lastDate;
    }

    @Override
    public BooleanProperty launchInUseModeProperty() {
        return this.launchInUseMode;
    }

    @Override
    public LcTechInfoI getTechInfo() {
        return this.techInfo;
    }

    public void setConfigurationId(String configurationID) {
        this.configurationID = configurationID;
    }

    @Override
    public List<ChangelogEntryI> getChangelogEntries() {
        return changelogEntries;
    }

    // Class part : "XML"
    //========================================================================
    private static final String NODE_CONFIG_DESCRIPTION = "ConfigurationDescription", NODE_CHANGELOG_ENTRIES = "ChangelogEntries";

    @Override
    public Element serialize(final File contextP) {
        Element element = new Element(LCConfigurationDescription.NODE_CONFIG_DESCRIPTION);
        XMLObjectSerializer.serializeInto(LCConfigurationDescription.class, this, element);

        //Save the tech info
        element.addContent(this.techInfo.serialize(contextP));

        //Save image when exist
        if (this.configurationImageToSave != null) {
            File imageFile = new File(contextP + File.separator + LCConstant.CONFIGURATION_SCREENSHOT_NAME);
            BufferedImage buffImage = SwingFXUtils.fromFXImage(configurationImageToSave, null);
            try {
                ImageIO.write(buffImage, "png", imageFile);
                configurationImageToSave = null;
                this.imagePath = imageFile.getAbsolutePath();
                LCConfigurationDescription.LOGGER.info("Configuration description preview saved to {}", imageFile);
            } catch (IOException e) {
                LCConfigurationDescription.LOGGER.warn("Couldn't save the configuration preview image", e);
            }
        }

        // Changelog entries
        Element changelogEntriesElement = new Element(LCConfigurationDescription.NODE_CHANGELOG_ENTRIES);
        element.addContent(changelogEntriesElement);
        for (ChangelogEntryI changelogEntry : changelogEntries) {
            changelogEntriesElement.addContent(changelogEntry.serialize(contextP));
        }

        return element;
    }

    @Override
    public void deserialize(final Element nodeP, final File contextP) throws LCException {
        XMLObjectSerializer.deserializeInto(LCConfigurationDescription.class, this, nodeP);
        //Image
        this.imagePath = new File(contextP + File.separator + LCConstant.CONFIGURATION_SCREENSHOT_NAME).getAbsolutePath();
        //Tech info
        Element techInfoElem = nodeP.getChild(LcTechInfo.NODE_TECH_INFO);
        if (techInfoElem != null) {
            this.techInfo.deserialize(techInfoElem, contextP);
        }

        // Changelog entries
        final Element changelogEntriesElement = nodeP.getChild(LCConfigurationDescription.NODE_CHANGELOG_ENTRIES);
        if (changelogEntriesElement != null) {
            final List<Element> changelogEntriesElementChildren = changelogEntriesElement.getChildren();
            for (Element changelogEntryElement : changelogEntriesElementChildren) {
                ChangelogEntry changelogEntry = new ChangelogEntry();
                changelogEntry.deserialize(changelogEntryElement, contextP);
                changelogEntries.add(changelogEntry);
            }
        }
    }
    //========================================================================

}
