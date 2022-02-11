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

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.ProfileIOContextI;
import org.lifecompanion.model.api.ui.editmode.ConfigurationProfileLevelEnum;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

/**
 * Implementation of a {@link LCProfileI} that represent a LifeCompanion profile
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCProfile implements LCProfileI {
    /**
     * Name for this profile
     */
    private final StringProperty name;

    /**
     * The list of all configurations managed by this profile
     */
    private final ObservableList<LCConfigurationDescriptionI> configurationList;

    /**
     * The property of list configuration
     */
    private final ListProperty<LCConfigurationDescriptionI> configurationListProperty;

    /**
     * Color of the profile
     */
    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> color;

    /**
     * Profile level
     */
    @XMLGenericProperty(ConfigurationProfileLevelEnum.class)
    private final ObjectProperty<ConfigurationProfileLevelEnum> level;

    /**
     * Configuration count (displayed)
     */

    private transient final IntegerProperty configurationCount, cachedConfigurationCount;

    private transient final BooleanProperty fullyLoaded;

    /**
     * Profile unique ID
     * <strong>WARNING : SHOULD NOT BE FINAL BECAUSE IT WILL BE LOADED ON "DESERIALIZE"</strong>
     */
    private String id;

    /**
     * Create a new empty profile
     */
    public LCProfile() {
        this.id = StringUtils.getNewID();
        this.name = new SimpleStringProperty(this, "name");
        this.configurationList = FXCollections.observableArrayList();
        this.level = new SimpleObjectProperty<>(this, "level", ConfigurationProfileLevelEnum.EXPERT);
        this.color = new SimpleObjectProperty<>(this, "color", LCGraphicStyle.MAIN_PRIMARY);
        this.configurationListProperty = new SimpleListProperty<>(this.configurationList);
        this.configurationCount = new SimpleIntegerProperty();
        this.cachedConfigurationCount = new SimpleIntegerProperty();
        this.fullyLoaded = new SimpleBooleanProperty();
        this.configurationCount.bind(Bindings.createIntegerBinding(
                () -> fullyLoaded.get() ? configurationListProperty.getSize() : cachedConfigurationCount.get(),
                configurationListProperty.sizeProperty(), cachedConfigurationCount, fullyLoaded)
        );
    }

    // Class part : "Base property"
    //========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public StringProperty nameProperty() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<LCConfigurationDescriptionI> getConfiguration() {
        return this.configurationList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectProperty<Color> colorProperty() {
        return this.color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getID() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectProperty<ConfigurationProfileLevelEnum> levelProperty() {
        return this.level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateID() {
        //Never happen on profile
        return this.id;
    }

    @Override
    public void setID(String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyIntegerProperty configurationCountProperty() {
        return configurationCount;
    }

    public IntegerProperty cachedConfigurationCountProperty() {
        return cachedConfigurationCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LCConfigurationDescriptionI getCurrentDefaultConfiguration() {
        for (LCConfigurationDescriptionI config : this.configurationList) {
            if (config.launchInUseModeProperty().get()) {
                return config;
            }
        }
        return null;
    }
    //========================================================================

    // Class part : "Various"
    //========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public LCConfigurationDescriptionI getConfigurationById(final String configurationID) {
        for (LCConfigurationDescriptionI configDescription : this.configurationList) {
            if (configurationID.equals(configDescription.getConfigurationId())) {
                return configDescription;
            }
        }
        return null;
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    private final static String NODE_PROFILE = "Profile";

    /**
     * {@inheritDoc}
     */
    @Override
    public Element serialize(final ProfileIOContextI contextP) {
        //This node
        Element node = new Element(LCProfile.NODE_PROFILE);
        XMLObjectSerializer.serializeInto(LCProfile.class, this, node);
        // Configuration description are not saved as a part of the profile XML because they are saved as separated XML file
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deserialize(final Element nodeP, final ProfileIOContextI contextP) throws LCException {
        // Read attribute
        XMLObjectSerializer.deserializeInto(LCProfile.class, this, nodeP);
        // Configuration description are not saved as a part of the profile XML because they are saved as separated XML file

        fullyLoaded.set(contextP.isFullLoading());

        // Backward compatibility : before deleting level, we manually set it to EXPERT
        level.set(ConfigurationProfileLevelEnum.EXPERT);
    }
    //========================================================================

    @Override
    public String toString() {
        return "LCProfile{" +
                "name=" + name.get() +
                ", configurationList=" + configurationList.size() +
                '}';
    }
}
