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

package org.lifecompanion.plugin.calendar.controller;

import javafx.animation.Transition;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;

public class AlarmTransition extends Transition {
    private static final Color BACKGROUND_COLOR = Color.web("#822727"), STROKE_COLOR = Color.web("#f08c00");
    private final GridPartKeyComponentI key;
    private final Color sourceColorStroke, sourceColorBackground;

    public AlarmTransition(GridPartKeyComponentI key) {
        this.key = key;
        this.sourceColorStroke = key != null ? key.getKeyStyle().strokeColorProperty().value().getValue() : null;
        this.sourceColorBackground = key != null ? key.getKeyStyle().backgroundColorProperty().value().getValue() : null;
        setCycleCount(2);
        setCycleDuration(Duration.millis(800));
        setAutoReverse(true);
        setOnFinished(e -> this.play());
    }

    @Override
    protected void interpolate(double frac) {
        if (key != null) {
            key.getKeyStyle().strokeColorProperty().forced().setValue(sourceColorStroke.interpolate(STROKE_COLOR, frac));
            key.getKeyStyle().backgroundColorProperty().forced().setValue(sourceColorBackground.interpolate(BACKGROUND_COLOR, frac));
        }
    }

    public void stopAndRestore() {
        setOnFinished(e -> {
            if (key != null) {
                key.getKeyStyle().strokeColorProperty().forced().setValue(null);
                key.getKeyStyle().backgroundColorProperty().forced().setValue(null);
            }
        });
    }

    public AlarmTransition playAndReturnThis() {
        play();
        return this;
    }
}
