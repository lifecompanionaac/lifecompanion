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
 * The possible application mode.<br>
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum AppMode {
	CONFIG(true, true), USE(false, false);

	private boolean copyNeeded = false;
	private boolean restoreConfigurationNeeded = false;

	AppMode(final boolean copyNeededP, final boolean restoreConfigurationNeededP) {
		this.copyNeeded = copyNeededP;
		this.restoreConfigurationNeeded = restoreConfigurationNeededP;
	}

	/**
	 * @return if a copy of the configuration should be done before using it in the next mode
	 */
	public boolean isCopyNeeded() {
		return this.copyNeeded;
	}

	/**
	 * @return if the previous should be restored on this mode start
	 */
	public boolean isRestoreConfigurationNeeded() {
		return this.restoreConfigurationNeeded;
	}
}
