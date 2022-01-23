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

package org.lifecompanion.api.mode;

/**
 * To listen the LC state (startup and exit)
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface LCStateListener {
	// Class part : "Application start/stop"
	//========================================================================
	/**
	 * Called by LifeCompanion when the application starts.<br>
	 * Software initialization should be done here (light initialization).<br>
	 * Called once in application life cycle.
	 */
	void lcStart();

	/**
	 * Called by LifeCompanion when the application exits.<br>
	 * Saving and resource closing should be done here.<br>
	 * Called once in application life cycle.
	 */
	void lcExit();
	//========================================================================
}
