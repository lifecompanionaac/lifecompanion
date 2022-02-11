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

package org.lifecompanion.model.api.categorizedelement;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

/**
 * Useful class to always have unique color on categories.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CategorizedElementColorProvider {
	private static final Color[] COLORS = { //
			Color.web("#8BC34A"), //
			Color.web("#00BCD4"), //
			Color.web("#FF5722"), //
			Color.web("#3F51B5"), //
			Color.web("#F44336"), //
			Color.web("#009688") //
	};

	private static final Map<Object, Integer> indexes = new HashMap<>();

	public static Color nextColor(final Object key) {
		int index = 0;
		if (CategorizedElementColorProvider.indexes.containsKey(key)) {
			index = CategorizedElementColorProvider.indexes.get(key);
			if (index == CategorizedElementColorProvider.COLORS.length - 1) {
				index = 0;
			} else {
				index++;
			}
		}
		CategorizedElementColorProvider.indexes.put(key, index);
		return CategorizedElementColorProvider.COLORS[index];
	}
}
