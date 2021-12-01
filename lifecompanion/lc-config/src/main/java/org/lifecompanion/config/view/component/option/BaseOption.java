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

package org.lifecompanion.config.view.component.option;

/**
 * Represent a base option that can be applied on a model component
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 * @param <T> the component where option is applied
 */
public abstract class BaseOption<T> {
	/**
	 * The model that can be modified by this option
	 */
	protected T model;

	/**
	 * Create the option for the given model
	 * @param modelP the option model
	 */
	public BaseOption(final T modelP) {
		this.model = modelP;
	}
}
