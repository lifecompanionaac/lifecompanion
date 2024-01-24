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

package org.lifecompanion.model.api.configurationcomponent;

import org.lifecompanion.framework.commons.translation.Translation;

public enum VideoDisplayMode {
    IN_KEY("video.display.mode.in.key.title", "video.display.mode.in.key.description"),
    FULLSCREEN("video.display.mode.fullscreen.title", "video.display.mode.fullscreen.description");

    private final String title;
    private final String description;

    VideoDisplayMode(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return Translation.getText(title);
    }

    public String getDescription() {
        return Translation.getText(description);
    }
}
