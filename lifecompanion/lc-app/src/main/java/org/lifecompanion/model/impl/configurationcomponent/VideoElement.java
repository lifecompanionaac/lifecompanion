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
package org.lifecompanion.model.impl.configurationcomponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;

import java.io.File;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VideoElement implements VideoElementI {
    private final String id;
    private final File path;
    private final ObjectProperty<File> thumbnailPath;

    public VideoElement(String id, File path) {
        this.id = id;
        this.path = path;
        this.thumbnailPath = new SimpleObjectProperty<>();
    }

    public void updateThumbnailPath(File thumbnailPath) {
        this.thumbnailPath.set(thumbnailPath);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public File getPath() {
        return path;
    }

    @Override
    public ReadOnlyObjectProperty<File> thumbnailPathProperty() {
        return thumbnailPath;
    }
}
