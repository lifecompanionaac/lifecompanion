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

import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;

/**
 * A class that represent a possible component to be added in configuration.<br>
 * A same add component can instantiate different component, for example, a stack can be in a grid, but also in the configuration.<br>
 * This class is useful to describe the configuration element on the left panel.<br>
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface PossibleAddComponentI<T extends DisplayableComponentI> {
	/**
	 * @return the icon that represent this component
	 */
	String getIconPath();

	/**
	 * @return name of the component
	 */
	String getNameID();

	/**
	 * @return a description of the added component
	 */
	String getDescriptionID();

	/**
	 * To create the component to add.
	 * @param addType the add type, will be in the {@link #getAllowedAddType()}<br>
	 * Can be used to create the correct component.
	 * @param optionalParams the optional params that can be passed to the add.<br>
	 * As it, the component can be configured
	 * @return the new component for the given {@link AddTypeEnum}, if the add type is correct
	 */
	T getNewComponent(AddTypeEnum addType, Object... optionalParams);

	/**
	 * @return all the add types that this component accept
	 */
	AddTypeEnum[] getAllowedAddType();

	/**
	 * @return the category for this possible category
	 */
	PossibleAddComponentCategoryI getCategory();
}
