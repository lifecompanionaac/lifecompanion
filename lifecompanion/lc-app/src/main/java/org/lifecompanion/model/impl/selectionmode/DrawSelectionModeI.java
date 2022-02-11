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

package org.lifecompanion.model.impl.selectionmode;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.paint.Color;
import org.lifecompanion.model.api.selectionmode.SelectionModeParameterI;

/**
 * Define common method for selection mode that can be draw with the same UI component.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface DrawSelectionModeI {
	ReadOnlyBooleanProperty playingProperty();

	BooleanBinding currentPartNotNullProperty();

	ObjectProperty<Color> strokeFillProperty();

	ObjectProperty<Color> progressFillProperty();

	ReadOnlyBooleanProperty drawProgressProperty();

	ReadOnlyBooleanProperty backgroundReductionEnabledProperty();

	ReadOnlyDoubleProperty backgroundReductionLevelProperty();

	SelectionModeParameterI getParameters();
}
