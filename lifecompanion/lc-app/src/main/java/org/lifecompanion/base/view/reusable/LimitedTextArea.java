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

package org.lifecompanion.base.view.reusable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextArea;

/**
 * Subclass of {@link TextArea} that allows a limited size of text.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LimitedTextArea extends TextArea {
	private IntegerProperty limit;

	public LimitedTextArea() {
		this.limit = new SimpleIntegerProperty(this, "limit", 0);
		this.limit.addListener((inv) -> {
			this.checkText();
		});
	}

	public IntegerProperty limitProperty() {
		return this.limit;
	}

	@Override
	public void replaceText(final int start, final int end, final String text) {
		super.replaceText(start, end, text);
		this.checkText();
	}

	@Override
	public void replaceSelection(final String replacement) {
		super.replaceSelection(replacement);
		this.checkText();
	}

	/**
	 * Check that the current text respect the limit, and change it if needed.
	 */
	private void checkText() {
		String text = this.getText();
		if (this.limit.get() > 0 && text != null && text.length() > this.limit.get()) {
			this.setText(text.substring(text.length() - this.limit.get(), text.length()));
			this.positionCaret(this.limit.get());
		}
	}

}
