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

package org.lifecompanion.api.component.definition;

import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.io.XMLSerializable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;

/**
 * Represent the parameters for the virtual mouse in a configuration.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface VirtualMouseParameterI extends XMLSerializable<IOContextI> {
	/**
	 * @return the mouse drawing color
	 */
	ObjectProperty<Color> mouseColorProperty();

	/**
	 * @return the mouse stroke drawing color
	 */
	ObjectProperty<Color> mouseStrokeColorProperty();

	/**
	 * @return the type of drawing we want for the mouse
	 */
	ObjectProperty<VirtualMouseDrawing> mouseDrawingProperty();

	/**
	 * @return the size of virtual mouse drawing
	 */
	IntegerProperty mouseSizeProperty();

	/**
	 * @return the mouse speed on move
	 */
	IntegerProperty mouseSpeedProperty();
}
