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
import org.lifecompanion.api.prediction.BasePredictorI;
import javafx.beans.property.StringProperty;

/**
 * Represent the prediction parameters that are common between each {@link BasePredictorI} (char and word predictor)
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface PredictionParameterI extends XMLSerializable<IOContextI> {

	/**
	 * @return the word predictor engine id selected for this configuration
	 */
	public StringProperty selectedWordPredictorIdProperty();

	/**
	 * @return the char predictor engine id selected for this configuration
	 */
	public StringProperty selectedCharPredictorIdProperty();

	/**
	 * @return the string that represent the space in char prediction (can be "_" but can also be longer that 1 char, e.g. "SPACE")
	 */
	public StringProperty charPredictionSpaceCharProperty();
}
