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

package org.lifecompanion.model.api.style;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.paint.Color;

/**
 * Represent a style applied to component with shape (keys, grids...).<br>
 * Allows to keep common properties on a same style type.
 * @param <T> the style type
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface AbstractShapeCompStyleI<T extends AbstractShapeCompStyleI<?>> extends StyleI<T> {
	/**
	 * @return stroke color for the shape
	 */
	StylePropertyI<Color> strokeColorProperty();

	/**
	 * @return background color for the shape
	 */
	StylePropertyI<Color> backgroundColorProperty();

	/**
	 * @return radius for the shape (pixel) - used by both background and stroke
	 */
	IntegerStylePropertyI shapeRadiusProperty();

	/**
	 * @return stroke size for the shape (pixel)
	 */
	IntegerStylePropertyI strokeSizeProperty();

	// Class part : "Computed"
	//========================================================================
	/**
	 * @return the Java FX css style that change on value changes
	 */
	ReadOnlyStringProperty cssStyleProperty();
	//========================================================================

}
