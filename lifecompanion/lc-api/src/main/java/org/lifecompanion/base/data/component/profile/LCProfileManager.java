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
package org.lifecompanion.base.data.component.profile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to manage existing profile in LifeCompanion.<br>
 * This class keep all existing profile and load them when needed.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum LCProfileManager {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(LCProfileManager.class);

    /**
     * List of all existing profile in LC
     */
    private final ObservableList<LCProfileI> profiles;

    /**
     * Private singleton constructor
     */
    LCProfileManager() {
        this.profiles = FXCollections.observableArrayList();
    }

    // Class part : "Public API"
    //========================================================================

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
    //========================================================================
}
