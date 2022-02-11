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

package org.lifecompanion.model.impl.textcomponent;

import javafx.geometry.Bounds;
import org.lifecompanion.model.api.configurationcomponent.WriterEntryI;
import org.lifecompanion.model.api.textcomponent.TextBoundsProviderI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerWordPartI;
import org.lifecompanion.model.api.style.TextCompStyleI;

/**
 * Represent a part of a word.</br>
 * This is useful to keep the {@link WriterEntryI} related to this part.</br>
 * Most of the time, a {@link TextDisplayerWordPart} will contains the whole word.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class TextDisplayerWordPart implements TextDisplayerWordPartI {
	private final WriterEntryI entry;
	private String part;
	private int caretStart, caretEnd;
	private double width, height;
	private boolean imageStart;
	private double imageWidth;

	public TextDisplayerWordPart(WriterEntryI entry, String part) {
		super();
		this.entry = entry;
		this.part = part;
		if (entry == null)
			throw new NullPointerException("Entry can't be null");
	}

	@Override
	public WriterEntryI getEntry() {
		return entry;
	}

	@Override
	public String getPart() {
		return part;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public int getCaretStart() {
		return caretStart;
	}

	@Override
	public void setCaretStart(int caretStart) {
		this.caretStart = caretStart;
	}

	@Override
	public int getCaretEnd() {
		return caretEnd;
	}

	@Override
	public void setCaretEnd(int caretEnd) {
		this.caretEnd = caretEnd;
	}

	@Override
	public boolean isImageStart() {
		return imageStart;
	}

	@Override
	public void setImageStart(boolean imageStart) {
		this.imageStart = imageStart;
	}

	@Override
	public double getImageWidth() {
		return imageWidth;
	}

	@Override
	public void setImageWidth(double imageWidth) {
		this.imageWidth = imageWidth;
	}

	@Override
	public String toString() {
		return "(" + part + ", " + caretStart + ", " + caretEnd + ")";
	}

	@Override
	public double getCaretPosition(int caretPosition, TextBoundsProviderI provider, TextCompStyleI defaultTextStyle) {
		double x = 0.0;
		for (int c = 0; c < part.length(); c++) {
			if (this.caretStart + c < caretPosition) {
				x += provider.getBounds(String.valueOf(part.charAt(c)), defaultTextStyle).getWidth();
			} else {
				return x;
			}
		}
		return x;
	}

	@Override
	public int getCaretPositionFromX(double startX, double caretXPosition, TextBoundsProviderI provider, TextCompStyleI defaultTextStyle) {
		double x = startX;
		for (int c = 0; c < part.length(); c++) {
			x += provider.getBounds(String.valueOf(part.charAt(c)), defaultTextStyle).getWidth();
			if (x >= caretXPosition) {
				return caretStart + c;
			}
		}
		return caretEnd;
	}

	/**
	 * Split the text returned by {@link #getPart()}, modifying the value of this {@link TextDisplayerWordPart} with the split left part and returning the right part.</br>
	 * @param index split index (char at the given index will be in the right part)
	 * @return the right part of the split
	 */
	@Override
	public String splitOnIndex(int index) {
		String total = this.part;
		this.part = total.substring(0, index);
		return total.substring(index, total.length());
	}

	@Override
	public void computeSize(TextBoundsProviderI provider, TextCompStyleI defaultTextStyle) {
		Bounds bounds = provider.getBounds(part, defaultTextStyle);
		this.width = bounds.getWidth();
		this.height = bounds.getHeight();
	}
}
