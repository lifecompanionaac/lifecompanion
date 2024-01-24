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

import javafx.scene.media.MediaPlayer;
import org.lifecompanion.framework.commons.translation.Translation;

public enum VideoPlayMode {
    CONTINUOUS(true, MediaPlayer.INDEFINITE, "video.play.mode.continuous.title", "video.play.mode.continuous.description"),
    ON_ACTIVATION(false, 1, "video.play.mode.on.activation.title", "video.play.mode.on.activation.description"),
    WHILE_OVER(false, MediaPlayer.INDEFINITE, "video.play.mode.while.over.title", "video.play.mode.while.over.description");

    private final boolean autoplay;
    private final int cycleCount;
    private final String title;
    private final String description;

    VideoPlayMode(boolean autoplay, int cycleCount, String title, String description) {
        this.autoplay = autoplay;
        this.cycleCount = cycleCount;
        this.title = title;
        this.description = description;
    }

    public boolean isAutoplay() {
        return autoplay;
    }

    public int getCycleCount() {
        return cycleCount;
    }

    public String getTitle() {
        return Translation.getText(title);
    }

    public String getDescription() {
        return Translation.getText(description);
    }
}
