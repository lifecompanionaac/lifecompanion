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
package org.lifecompanion.model.api.profile;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.api.ui.editmode.AddTypeEnum;

import java.io.File;

/**
 * Represent a component saved by user.<br>
 * A component saved by a user can be any {@link DisplayableComponentI}, it can be use to save a component as a model and reuse it later in different configuration.<br>
 * The component is saved with its own information but also with the global information (like styles, plugin used, etc...)
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface UserCompDescriptionI extends XMLSerializable<File> {
    /**
     * @return the image for this component
     */
    ObjectProperty<Image> componentImageProperty();

    /**
     * To request the {@link #componentImageProperty()} to be loaded.<br>
     * May take a while before the image is loaded.
     */
    void requestImageLoad();

    /**
     * @return the name for this component (chosen by user)
     */
    StringProperty nameProperty();

    /**
     * @return the author for this component (defined by current profile name)
     */
    StringProperty authorProperty();

    /**
     * @return the target for this saved component (for example if the saved component is a key, the target is a grid part, because key can only be added on the grid part)
     */
    AddTypeEnum getTargetType();

    /**
     * @return should return the id of the component associated to this component
     */
    String getSavedComponentId();

    /**
     * @return the real component type. Will always be provided even if {@link #getUserComponent()} is not loaded.
     */
    Class<? extends DisplayableComponentI> getComponentType();

    /**
     * @return the user component holder.<br>
     * Is never null, be the component inside can be null if it's not already loaded.
     */
    UserCompI getUserComponent();

}
