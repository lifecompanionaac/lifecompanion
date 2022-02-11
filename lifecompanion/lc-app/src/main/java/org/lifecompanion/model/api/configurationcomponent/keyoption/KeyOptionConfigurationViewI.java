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

package org.lifecompanion.model.api.configurationcomponent.keyoption;

import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Region;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * Represent the view that will help a key option to be configuration.<br>
 * The initAll() method shouldn't be called directly by subclass, for optimization reason, this method will be called just before usage.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface KeyOptionConfigurationViewI<T extends KeyOptionI> extends LCViewInitHelper {

	/**
	 * @return the JavaFX node use to configure key option
	 */
	public Region getConfigurationView();

	/**
	 * @return the type of the configured key option
	 */
	public Class<T> getConfiguredKeyOptionType();

	/**
	 * @return the option that is configured by this view
	 */
	public ObjectProperty<T> optionProperty();
}
