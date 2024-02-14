/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.controller.hub;

import java.time.ZonedDateTime;

public class HubDeviceLocalData {
    private String configurationId;
    private ZonedDateTime loadedAt;

    public HubDeviceLocalData(String configurationId, ZonedDateTime loadedAt) {
        this.configurationId = configurationId;
        this.loadedAt = loadedAt;
    }

    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }

    public void setLoadedAt(ZonedDateTime loadedAt) {
        this.loadedAt = loadedAt;
    }

    public String getConfigurationId() {
        return configurationId;
    }

    public ZonedDateTime getLoadedAt() {
        return loadedAt;
    }
}
