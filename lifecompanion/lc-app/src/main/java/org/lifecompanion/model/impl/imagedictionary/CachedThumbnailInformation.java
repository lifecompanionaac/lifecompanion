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

package org.lifecompanion.model.impl.imagedictionary;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class CachedThumbnailInformation {
    private final Image loadedImage;
    private final Rectangle2D viewport;

    public CachedThumbnailInformation(Image loadedImage, Rectangle2D viewport) {
        this.loadedImage = loadedImage;
        this.viewport = viewport;
    }

    public Image getLoadedImage() {
        return loadedImage;
    }

    public Rectangle2D getViewport() {
        return viewport;
    }
}
