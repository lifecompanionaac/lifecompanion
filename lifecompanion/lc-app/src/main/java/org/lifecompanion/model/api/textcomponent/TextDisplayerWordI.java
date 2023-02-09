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

import java.util.List;

import org.lifecompanion.model.api.configurationcomponent.WriterEntryI;
import org.lifecompanion.model.api.style.TextCompStyleI;

public interface TextDisplayerWordI {
	boolean isPreviousLineSplittedOnThisWord();

	void setPreviousLineSplittedOnThisWord(boolean previousLineSplitOnThisWord);

	Character getWordSeparatorChar();

	void setWordSeparatorChar(Character c);
	//

	List<TextDisplayerWordPartI> getParts();

	int getCaretStart();

	void setCaretStart(int caretStart);

	int getCaretEnd();

	double getWordSeparatorCharWidth();

	void setCaretEnd(int caretEnd);

	/**
	 * Compute the width of this word using the given bounds provider.</br>
	 * Use the {@link WriterEntryI} of each part as style information, or the default style if needed.</br>
	 * The given width includes the end stop char when present.
	 * @param provider bounds provider to compute text size
	 * @param defaultTextStyle text style to use if entry has not its own style
	 * @return the width of this word, including end stop char
	 */
	double getWidth(TextBoundsProviderI provider, TextCompStyleI defaultTextStyle);

	double getWidth();

	double getHeight();

	void computeSize(TextBoundsProviderI provider, TextCompStyleI defaultTextStyle);
}
