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

package org.lifecompanion.model.api.textprediction;

import java.util.HashSet;
import java.util.List;

/**
 * Represent char predictor that predict next character for a given text.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface CharPredictorI extends BasePredictorI {
	/**
	 * Should predict the next character for a given text.<br>
	 * Note that the text could be null or empty.<br>
	 * @param text the current typed text
	 * @param limit the max number of character we want (can return less than this limit)
	 * @return the list of predicted character. The first character is the list should be the most probable char for the given character.
	 */
	public List<Character> predict(String text, int limit);

	/**
	 * As the {@link #predict(String, int)} method do, should predict the next character for a given text.<br>
	 * This method is different because the returned character should be in the accepted characters, this mean that the predictor should exclude some results.
	 * @param text the current typed text
	 * @param limit the max number of character we want (can return less than this limit)
	 * @param acceptedCharacters the only allowed characters, characters that are not in this set shouldn't be returned
	 * @return the list of predicted character. The first character is the list should be the most probable char for the given character. The list should contains only accepted characters.
	 * @throws UnsupportedOperationException if this predictor doesn't support this prediction method.
	 */
	public List<Character> predict(String text, int limit, HashSet<Character> acceptedCharacters) throws UnsupportedOperationException;
}
