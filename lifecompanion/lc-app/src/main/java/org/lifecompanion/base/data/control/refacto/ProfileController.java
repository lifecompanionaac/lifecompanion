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

package org.lifecompanion.base.data.control.refacto;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ProfileController {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);

    /**
     * List of all existing profile in LC
     */
    private final ObservableList<LCProfileI> profiles;

    /**
     * The current profile in use, can be null
     */
    private final ObjectProperty<LCProfileI> currentProfile;

    /**
     * Private singleton constructor
     */
    ProfileController() {
        currentProfile = new SimpleObjectProperty<>();
        this.profiles = FXCollections.observableArrayList();
        // On profile change, current edited configuration should be reset
        currentProfile.addListener((obs, ov, nv) -> AppModeController.INSTANCE.switchEditModeConfiguration(null, null));
    }

    /**
     * @return list of all found profiles in LifeCompanion
     */
    public ObservableList<LCProfileI> getProfiles() {
        return this.profiles;
    }

    /**
     * Search for a profile by its ID
     *
     * @param id the ID of the wanted profile
     * @return the profile with the wanted ID, or null if there is no profile with the ID
     */
    public LCProfileI getByID(final String id) {
        for (LCProfileI profile : this.profiles) {
            if (profile.getID().equals(id)) {
                return profile;
            }
        }
        return null;
    }

    /**
     * @return the currently selected profile
     */
    public ReadOnlyObjectProperty<LCProfileI> currentProfileProperty() {
        return this.currentProfile;
    }

    public void selectProfile(LCProfileI profile) {
        currentProfile.set(profile);
    }

    public void clearSelectedProfile() {
        currentProfile.set(null);
    }
}
