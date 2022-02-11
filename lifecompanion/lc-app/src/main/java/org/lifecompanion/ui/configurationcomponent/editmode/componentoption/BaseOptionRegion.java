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

package org.lifecompanion.ui.configurationcomponent.editmode.componentoption;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * Represent a base option that can be applied on a model component
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 * @param <T> the component where option is applied
 */
public abstract class BaseOptionRegion<T> extends Pane {
	/**
	 * The model that can be modified by this option
	 */
	protected T model;

	/**
	 * Create the option for the given model
	 * @param modelP the option model
	 */
	public BaseOptionRegion(final T modelP) {
		this.model = modelP;
		this.setPickOnBounds(false);
	}

	/**
	 * Bind this option width and height to the given region size
	 * @param r the region to bind
	 */
	public void bindSize(final Region r) {
		this.prefWidthProperty().bind(r.widthProperty());
		this.prefHeightProperty().bind(r.heightProperty());
	}

	/**
	 * Bind this option x and y to the given region location
	 * @param r the region to bind
	 */
	public void bindPosition(final Region r) {
		this.layoutXProperty().bind(r.layoutXProperty());
		this.layoutYProperty().bind(r.layoutYProperty());
	}
}
