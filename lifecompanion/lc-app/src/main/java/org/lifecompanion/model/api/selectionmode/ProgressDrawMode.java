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

package org.lifecompanion.model.api.selectionmode;

import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Represent how the progress on scanning mode is drawing.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum ProgressDrawMode {
	PROGRESS_BAR("scanning.mode.draw.progress.bar"), //
	FILL_PART("scanning.mode.draw.fill.part")//
	;

	/**
	 * Name of this draw mode
	 */
	private String labelId;

	ProgressDrawMode(final String labelIdP) {
		this.labelId = labelIdP;
	}

	public String getLabel() {
		return Translation.getText(this.labelId);
	}
}
