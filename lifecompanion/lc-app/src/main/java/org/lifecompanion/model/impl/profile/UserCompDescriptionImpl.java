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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.controller.io.XMLHelper;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.api.profile.UserCompI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.ui.editmode.AddTypeEnum;
import org.lifecompanion.model.impl.io.IOContext;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Implementation for {@link UserCompDescriptionI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserCompDescriptionImpl implements UserCompDescriptionI {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCompDescriptionImpl.class);

    private final ObjectProperty<Image> componentImage;
    private final StringProperty name;
    private final StringProperty author;

    private Class<? extends DisplayableComponentI> componentType;

    private String savedComponentId;
    private final UserCompImpl userComponent;

    /**
     * Path to configuration image
     */
    private transient String imagePath;

    /**
     * To know if we are already loading the image
     */
    private transient boolean loadingImage;

    public UserCompDescriptionImpl() {
        this.componentImage = new SimpleObjectProperty<>();
        this.name = new SimpleStringProperty();
        this.author = new SimpleStringProperty();
        this.userComponent = new UserCompImpl();
    }

    public UserCompDescriptionImpl(final DisplayableComponentI comp) {
        this();
        this.userComponent.setLoadedComponent(comp);
        this.componentType = comp.getClass();
        this.savedComponentId = comp.getID();
    }

    // Class part : "Properties"
    //========================================================================
    @Override
    public ObjectProperty<Image> componentImageProperty() {
        return this.componentImage;
    }

    @Override
    public synchronized void requestImageLoad() {
        if (this.imagePath != null && !this.loadingImage && this.componentImage.get() == null) {
            //Read image when exist
            this.loadingImage = true;
            File imageFile = new File(this.imagePath);
            if (imageFile.exists()) {
                AsyncExecutorController.INSTANCE.addAndExecute(false, true, () -> {
                    try (FileInputStream imageIS = new FileInputStream(imageFile)) {
                        final Image value = new Image(imageIS);
                        UserCompDescriptionImpl.LOGGER.info("User component preview was loaded from {}", imageFile);
                        FXThreadUtils.runOnFXThread(() -> this.componentImage.set(value));
                    } catch (IOException e) {
                        UserCompDescriptionImpl.LOGGER.warn("Couldn't load the configuration preview image", e);
                    }
                });
            } else {
                UserCompDescriptionImpl.LOGGER.warn("There is no image for the user component {}", this.savedComponentId);
            }
        }
    }

    @Override
    public StringProperty nameProperty() {
        return this.name;
    }

    @Override
    public StringProperty authorProperty() {
        return this.author;
    }

    @Override
    public AddTypeEnum getTargetType() {
        return AddTypeEnum.ROOT;
    }

    @Override
    public String getSavedComponentId() {
        return this.savedComponentId;
    }

    @Override
    public Class<? extends DisplayableComponentI> getComponentType() {
        return componentType;
    }

    @Override
    public UserCompI getUserComponent() {
        return this.userComponent;
    }
    //========================================================================

    // Class part : "IO"
    //========================================================================
    private static final String NODE_USER_COMP_DESCRIPTION = "UserCompDescription";

    @Override
    public Element serialize(final File context) {
        Element element = new Element(UserCompDescriptionImpl.NODE_USER_COMP_DESCRIPTION);
        XMLObjectSerializer.serializeInto(UserCompDescriptionImpl.class, this, element);
        //Save image when exist
        if (this.componentImage.get() != null) {
            File imageFile = new File(context + File.separator + LCConstant.CONFIGURATION_SCREENSHOT_NAME);
            BufferedImage buffImage = SwingFXUtils.fromFXImage(this.componentImage.get(), null);
            try {
                ImageIO.write(buffImage, "png", imageFile);
                UserCompDescriptionImpl.LOGGER.info("User component preview saved to {}", imageFile);
            } catch (IOException e) {
                UserCompDescriptionImpl.LOGGER.warn("Couldn't save the user component preview image", e);
            }
        }
        return element;
    }

    @Override
    public void deserialize(final Element node, final File context) throws LCException {
        XMLObjectSerializer.deserializeInto(UserCompDescriptionImpl.class, this, node);
        this.imagePath = new File(context + File.separator + LCConstant.USERCOMP_SCREENSHOT_NAME).getAbsolutePath();

        try {
            IOContextI ioContext = new IOContext(context, false);
            ioContext.setFallbackOnDefaultInstanceOnFail(false);
            Element displayableCompElement = UserCompImpl.getElementFromRootXml(XMLHelper.readXml(new File(context + File.separator + LCConstant.USER_COMP_XML_NAME)));
            DisplayableComponentI componentToLoad = (DisplayableComponentI) ConfigurationComponentIOHelper.create(displayableCompElement, ioContext, null).getRight();
            this.componentType = componentToLoad.getClass();
        } catch (IOException | JDOMException e) {
            throw LCException.newException().withCause(e).build();
        }

    }
    //========================================================================
}
