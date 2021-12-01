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

package org.lifecompanion.api.component.definition.usercomp;

import org.lifecompanion.api.component.definition.DisplayableComponentI;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.io.XMLSerializable;

/**
 * Represent the thing that really hold the user component.<br>
 * This should be loaded only when needed to limit the memory footprint.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface UserCompI extends XMLSerializable<IOContextI> {
	/**
	 * @return the loaded component for this user component.<br>
	 * If null, it means that the component is not loaded yet.
	 */
	public DisplayableComponentI getLoadedComponent();

	/**
	 * @return true if the displayable component is loaded for this user component
	 */
	public boolean isLoaded();

	/**
	 * This method should be called only if {@link #isLoaded()} return true
	 * @return should create a new instance of the saved component
	 */
	public <T extends DisplayableComponentI> T createNewComponent();

	/**
	 * Remove the current loaded component ({@link #getLoadedComponent()} will now return null).</br>
	 * No-op component was not loaded yet.
	 */
	public void unloadComponent();
}
