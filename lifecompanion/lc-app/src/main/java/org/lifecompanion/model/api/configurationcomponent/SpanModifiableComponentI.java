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

package org.lifecompanion.model.api.configurationcomponent;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;

/**
 * Represent a component that can take multiple row and column in a grid.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface SpanModifiableComponentI {

	// Class part : "Expand"
	//========================================================================
	/**
	 * To expand this component column space by right side
	 */
	public void expandRight();

	/**
	 * To collapse this component column space by right side
	 */
	public void collapseRight();

	/**
	 * To expand this component column space by left side
	 */
	public void expandLeft();

	/**
	 * To collapse this component column space by left side
	 */
	public void collapseLeft();

	/**
	 * To expand this component row space by top side
	 */
	public void expandTop();

	/**
	 * To collapse this component row space by top side
	 */
	public void collapseTop();

	/**
	 * To expand this component row space by bottom side
	 */
	public void expandBottom();

	/**
	 * To collapse this component row space by bottom side
	 */
	public void collapseBottom();
	//========================================================================

	// Class part : "Span"
	//========================================================================
	/**
	 * <strong>The property must be modified only by the layout component, use expand* and collapse* method to change this property.</strong>
	 * @return the number of column space taken by this component
	 */
	public IntegerProperty columnSpanProperty();

	/**
	 * <strong>The property must be modified only by the layout component, use expand* and collapse* method to change this property.</strong>
	 * @return the number of row space taken by this component
	 */
	public IntegerProperty rowSpanProperty();
	//========================================================================

	// Class part : "Enable/disable expand/collapse"
	//========================================================================
	/**
	 * @return a property true if we can't call {@link #expandRight()}
	 */
	public BooleanProperty expandRightDisabledProperty();

	/**
	 * @return a property true if we can't call {@link #expandLeft()}
	 */
	public BooleanProperty expandLeftDisabledProperty();

	/**
	 * @return a property true if we can't call {@link #expandTop()}
	 */
	public BooleanProperty expandTopDisabledProperty();

	/**
	 * @return a property true if we can't call {@link #expandBottom()}
	 */
	public BooleanProperty expandBottomDisabledProperty();

	/**
	 * @return a property true if we can't call {@link #collapseRight()}
	 */
	public BooleanProperty collapseRightDisabledProperty();

	/**
	 * @return a property true if we can't call {@link #collapseLeft()}
	 */
	public BooleanProperty collapseLeftDisabledProperty();

	/**
	 * @return a property true if we can't call {@link #collapseTop()}
	 */
	public BooleanProperty collapseTopDisabledProperty();

	/**
	 * @return a property true if we can't call {@link #collapseBottom()}
	 */
	public BooleanProperty collapseBottomDisabledProperty();
	//========================================================================

}
