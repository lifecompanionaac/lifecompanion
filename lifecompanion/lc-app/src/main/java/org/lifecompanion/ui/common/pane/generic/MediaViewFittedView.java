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

package org.lifecompanion.ui.common.pane.generic;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.media.MediaView;

public class MediaViewFittedView implements FittedViewI {
    private final MediaView mediaView;

    public MediaViewFittedView(MediaView mediaView) {
        this.mediaView = mediaView;
    }

    @Override
    public DoubleProperty rotateProperty() {
        return mediaView.rotateProperty();
    }

    @Override
    public ObjectProperty<Rectangle2D> viewportProperty() {
        return mediaView.viewportProperty();
    }

    @Override
    public void setFitWidth(double value) {
        mediaView.setFitWidth(value);
    }

    @Override
    public void setFitHeight(double value) {
        mediaView.setFitHeight(value);
    }

    @Override
    public Node getNode() {
        return mediaView;
    }
}
