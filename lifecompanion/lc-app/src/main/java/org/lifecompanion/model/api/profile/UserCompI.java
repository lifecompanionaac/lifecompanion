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

package org.lifecompanion.model.api.profile;

import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;

/**
 * Represent the thing that really hold the user component.<br>
 * This should be loaded only when needed to limit the memory footprint.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface UserCompI extends XMLSerializable<IOContextI> {
	/**
	 * @return true if the displayable component is loaded for this user component
	 */
	boolean isLoaded();

	/**
	 * This method should be called only if {@link #isLoaded()} return true
	 * @return should create a new instance of the saved component
	 */
	<T extends DisplayableComponentI> T createNewComponent();

	/**
	 * Remove the current loaded component ({@link #createNewComponent()} ()} will fail afterwards).</br>
	 * No-op component was not loaded yet.
	 */
	void unloadComponent();
}
