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

package org.lifecompanion.base.data.control.stats;

public enum SessionEventType {
    PART_START("part.start"),
    PART_STOP("part.stop"),
    CONFIG_UPDATED("config.updated"),
    USER_INTERACTION_COUNT_LAST_MINUTE("user.interaction.count.in.last.minute"),
    FEATURE_USED_KEYLIST_SELECTED("feature.used.keylist.selected");

    private final String id;

    SessionEventType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
