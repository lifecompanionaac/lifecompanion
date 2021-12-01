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

package org.lifecompanion.framework.model.server.update;

public enum FileState {
	SAME(false, false, false), CREATED(true, false, false), CHANGED(false, true, false), REMOVED(false, false, true);

	private final boolean created, changed, removed;

	private FileState(boolean created, boolean changed, boolean removed) {
		this.created = created;
		this.changed = changed;
		this.removed = removed;
	}

	public boolean isCreated() {
		return created;
	}

	public boolean isChanged() {
		return changed;
	}

	public boolean isRemoved() {
		return removed;
	}
}
