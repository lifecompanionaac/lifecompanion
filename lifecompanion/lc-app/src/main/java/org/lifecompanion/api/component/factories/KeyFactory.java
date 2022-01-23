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

package org.lifecompanion.api.component.factories;

import org.lifecompanion.api.component.definition.GridPartComponentI;

/**
 * Interface that define how a key can be created.<br>
 * This can be use to create default key into grid.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface KeyFactory {
	/**
	 * Create a key binded with the given parent.
	 * @param parent the parent of the key to create
	 * @param row key y position
	 * @param column key x position
	 * @param spanRow the number of row taken by the key
	 * @param spanColumn the number of column taken by the key
	 * @return the created key for the given parameters
	 */
	public GridPartComponentI createKey(int row, int column, int spanRow, int spanColumn);
}
