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

import java.util.List;

import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * Represent a way to provide element for component configuration.<br>
 * This is the way to describe contextuel content on component in configuration view.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface ConfigOptionComponentI {
	/**
	 * @return the orientation of the component.<br>
	 * To place the component on first line, use horizontal.
	 */
	Orientation getOrientation();

	/**
	 * @return the list of component of this option
	 */
	List<Node> getOptions();

	/**
	 * @return true if the component must be hidden when the configured component is not selected
	 */
	boolean hideOnUnselect();
}
