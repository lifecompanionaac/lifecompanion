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

package org.lifecompanion.model.api.ui.editmode;

/**
 * Base interface to configure properties on a object.<br>
 * Use to simplify some operation and to keep code clean by always creating the same method.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface BaseConfigurationViewI<T> {

	/**
	 * Called each time that a new model is displayed.<br>
	 * The given model should never be null.
	 * @param model the new model to display/configure (never null)
	 */
	void bind(T model);

	/**
	 * Called each time that the current model is not displayed/configured anymore.
	 * @param model the previous model, to remove (never null)
	 */
	void unbind(T model);
}
