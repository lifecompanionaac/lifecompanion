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

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A component that can have a name changed by user with a default name if the user name is empty
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface UserNamedComponentI {

	/**
	 * @return a property that gives the name of this component.<br>
	 * The name is the {@link #defaultNameProperty()} or the {@link #userNameProperty()}.
	 */
	ReadOnlyStringProperty nameProperty();

	/**
	 * @return a property that contains the default name of this component.<br>
	 * The default name should be used if there is no user name.
	 */
	ReadOnlyStringProperty defaultNameProperty();

	/**
	 * @return a property that contains the name of a parent component, or some details about the component.<br>
	 * This property if free to set and bind by subclasses.
	 */
	StringProperty detailNameProperty();

	/**
	 * @return a property that contains the name that user chose
	 */
	StringProperty userNameProperty();
}
