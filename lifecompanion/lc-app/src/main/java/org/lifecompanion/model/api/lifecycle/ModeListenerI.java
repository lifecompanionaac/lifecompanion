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

package org.lifecompanion.model.api.lifecycle;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;

/**
 * To listen for mode starts/stop.<br>
 * The mode can any of the {@link AppMode}.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface ModeListenerI {
	/**
	 * Called when the mode starts (use mode or configuration mode).<br>
	 * This can be called more than once (but {@link #modeStop(LCConfigurationI)} is called between each call)
	 * @param configuration the configuration where the mode starts
	 */
	void modeStart(LCConfigurationI configuration);

	/**
	 * Called when the mode stops.<br>
	 * This can be called more than once (but {@link #modeStart(LCConfigurationI)} is always call before)
	 * @param configuration the configuration where the mode stops (the same configuration that the {@link #modeStart(LCConfigurationI)} configuration)
	 */
	void modeStop(LCConfigurationI configuration);
}
