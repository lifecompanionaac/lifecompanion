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
package org.lifecompanion.model.api.profile;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.model.api.configurationcomponent.IdentifiableComponentI;
import org.lifecompanion.model.api.configurationcomponent.NamedComponentI;
import org.lifecompanion.model.api.io.ProfileIOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.api.ui.editmode.ConfigurationProfileLevelEnum;

/**
 * Profile in LifeCompanion.<br>
 * A profile contains setting, default parameter, and list of configuration.<br>
 * The file that is given to profile serialization in the directory where profile need to be saved.
 * TODO : add language , last usage, parameters ?
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface LCProfileI extends XMLSerializable<ProfileIOContextI>, IdentifiableComponentI, NamedComponentI {

    /**
     * @return the list of all current configuration in the profile
     */
    ObservableList<LCConfigurationDescriptionI> getConfiguration();

    /**
     * @return a property that count the number of configurations for this profiles
     */
    ReadOnlyIntegerProperty configurationCountProperty();

    /**
     * @return the configuration count (to be update from external source if profile was never fully loaded)
     */
    IntegerProperty cachedConfigurationCountProperty();

    /**
     * @return the property that define the base color use by the profile
     */
    ObjectProperty<Color> colorProperty();

    /**
     * @return the profile level (to hide/show element in configuration)
     */
    ObjectProperty<ConfigurationProfileLevelEnum> levelProperty();

    /**
     * @param configurationID the ID of the wanted configuration description
     * @return the description, or null when there is no configuration for the given ID
     */
    LCConfigurationDescriptionI getConfigurationById(String configurationID);

    /**
     * @return the first {@link LCConfigurationDescriptionI} that have it's {@link LCConfigurationDescriptionI#launchInUseModeProperty()} to true.<br>
     * Should be unique for this profile.
     */
    LCConfigurationDescriptionI getCurrentDefaultConfiguration();

    void setID(String id);
}
