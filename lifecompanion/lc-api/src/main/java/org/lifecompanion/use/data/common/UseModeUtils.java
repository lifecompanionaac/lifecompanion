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

package org.lifecompanion.use.data.common;

import javafx.scene.control.Button;

/**
 * Use for use mode only UI
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseModeUtils {
	public static Button createTextButton(final String text) {
		Button button = new Button(text);
		button.getStyleClass().add("use-only-button");
		return button;
	}

	public static Button createTextButtonFixedWith(final String text, final double width) {
		Button b = UseModeUtils.createTextButton(text);
		b.setPrefWidth(width);
		return b;
	}
}
