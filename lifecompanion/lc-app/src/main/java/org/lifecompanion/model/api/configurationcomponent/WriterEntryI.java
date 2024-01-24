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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.io.XMLSerializable;

/**
 * A entry that all the writer should display/write.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface WriterEntryI extends XMLSerializable<Void> {

    /**
     * @return this entry text content
     */
    StringProperty entryTextProperty();

    /**
     * @return this entry associated image
     */
    ObjectProperty<ImageElementI> imageProperty();

    /**
     * @return if the insert is disabled for this entry (you don't want the text to be inserted into this entry text property)
     */
    BooleanProperty disableInsertProperty();

    /**
     * @return the entry font color to use.</br>
     * If null, will use the default font color.
     */
    ObjectProperty<Color> fontColorProperty();

    /**
     * Capitalize this entry {@link #entryTextProperty()}
     */
    void capitalize();

    /**
     * Set this entry {@link #entryTextProperty()} to upper case
     */
    void toUpperCase();

    boolean isValid();
}
