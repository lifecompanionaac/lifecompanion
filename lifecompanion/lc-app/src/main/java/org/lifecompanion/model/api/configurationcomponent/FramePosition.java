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

import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Represent the different frame positions.<br>
 * The frame position is always relative to the screen bounds.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum FramePosition {
	TOP("frame.position.top"), //
	TOP_RIGHT("frame.position.top_right"), //
	RIGHT("frame.position.right"), //
	BOTTOM_RIGHT("frame.position.bottom_right"), //
	BOTTOM("frame.position.bottom"), //
	BOTTOM_LEFT("frame.position.bottom_left"), //
	LEFT("frame.position.left"), //
	TOP_LEFT("frame.position.top_left"), //
	CENTER("frame.position.center") //
	;
	private String text;

	FramePosition(final String textP) {
		this.text = textP;
	}

	public String getText() {
		return Translation.getText(this.text);
	}

	public String getImagePath() {
		return "frame-positions/" + this.name().toLowerCase() + ".png";
	}
}
