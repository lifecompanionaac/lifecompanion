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

import java.util.List;
import java.util.Map;

public class HubData {
    static class HubConfigurationIds {
        String configurationId,configurationHubId ;

        public HubConfigurationIds(String configurationId, String configurationHubId) {
            this.configurationId = configurationId;
            this.configurationHubId = configurationHubId;
        }
    }

    static class LocalHashes {
        final Map<String, String> data;

        LocalHashes(Map<String, String> data) {
            this.data = data;
        }
    }

    static class UpdateFilesResult {
        UpdateFiles data;
    }

    static class UpdateFiles {
        Map<String, UpdatedFileData> added, modified, removed;

        public int getAddedCount() {
            return added != null ? added.size() : 0;
        }

        public int getModifiedCount() {
            return modified != null ? modified.size() : 0;
        }

        public int getRemovedCount() {
            return removed != null ? removed.size() : 0;
        }
    }

    static class UpdatedFileData {
        String id, hash;
    }

    static class FileEndpoint {
        String endpoint;
    }

    static class FileGetEndpointResult {
        FileEndpoint data;
    }

    static class GetDeviceConfigResult {
        List<LcConfig> included;
    }

    static class LcConfig {
        String id;
        LcConfigAttributes attributes;
    }

    static class LcConfigAttributes {
        String localId;
    }
}
