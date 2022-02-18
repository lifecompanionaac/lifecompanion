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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.XMLSerializable;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Represent the description of a {@link LCConfigurationI}, can be use to display a configuration without loading it.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface LCConfigurationDescriptionI extends XMLSerializable<File> {
    /**
     * @return a image property that contains a configuration image.<br>
     * This image can be null until the configuration is saved, or if {@link #requestImageLoad()} was not called yet.
     */
    ObjectProperty<Image> configurationImageProperty();

    /**
     * To request the {@link #configurationImageProperty()} to be loaded.<br>
     * May take a while before the image is loaded (async)
     */
    void requestImageLoad();

    void unloadImage();

    /**
     * @return the name of the associated configuration
     */
    StringProperty configurationNameProperty();

    /**
     * @return the name of configuration author (default name will be set to the profile name)
     */
    StringProperty configurationAuthorProperty();

    /**
     * @return the configuration description
     */
    StringProperty configurationDescriptionProperty();

    /**
     * @return the date when the configuration was loaded/saved for the last time
     */
    ObjectProperty<Date> configurationLastDateProperty();

    /**
     * @return the id of the configuration associated to the configuration
     */
    String getConfigurationId();

    /**
     * @return the associated configuration when loaded, can be null if the description is just loaded without configuration
     */
    ObjectProperty<LCConfigurationI> loadedConfigurationProperty();

    /**
     * @return if the configuration associated should be launched in use mode by default.
     */
    BooleanProperty launchInUseModeProperty();

    /**
     * @return the tech version updated with the last read informations<br>
     * Can never return null, but can return a tech info with out dated informations.
     */
    LcTechInfoI getTechInfo();

    /**
     * @return the list of changes made (updated automatically on save)
     */
    List<ChangelogEntryI> getChangelogEntries();
}
