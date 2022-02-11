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

package org.lifecompanion.model.api.textcomponent;

import org.lifecompanion.model.api.configurationcomponent.WriterEntryI;
import org.lifecompanion.model.api.style.TextCompStyleI;

public interface TextDisplayerWordPartI {
	public WriterEntryI getEntry();

	public String getPart();

	public double getWidth();

	public double getHeight();

	public int getCaretStart();

	public void setCaretStart(int caretStart);

	public int getCaretEnd();

	public void setCaretEnd(int caretEnd);

	public boolean isImageStart();

	public void setImageStart(boolean imageStart);

	public double getImageWidth();

	public void setImageWidth(double imageWidth);

	public double getCaretPosition(int caretPosition, TextBoundsProviderI provider, TextCompStyleI defaultTextStyle);

	public int getCaretPositionFromX(double startX, double caretXPosition, TextBoundsProviderI provider, TextCompStyleI defaultTextStyle);

	/**
	 * Split the text returned by {@link #getPart()}, modifying the value of this {@link WordPart} with the split left part and returning the right part.</br>
	 * @param index split index (char at the given index will be in the right part)
	 * @return the right part of the split
	 */
	public String splitOnIndex(int index);

	void computeSize(TextBoundsProviderI provider, TextCompStyleI defaultTextStyle);
}
