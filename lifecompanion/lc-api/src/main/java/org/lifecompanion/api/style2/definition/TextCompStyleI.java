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

package org.lifecompanion.api.style2.definition;

import org.lifecompanion.api.style2.property.definition.IntegerStylePropertyI;
import org.lifecompanion.api.style2.property.definition.StylePropertyI;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Style for every components that use text.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface TextCompStyleI extends StyleI<TextCompStyleI> {
	String NODE_TEXT_STYLE_TEXT = "TextDisplayerTextStyle";
	String NODE_TEXT_STYLE_KEY = "KeyTextStyle";

	// Class part : "Style"
	//========================================================================
	/**
	 * @return the text style font family (e.g. "Arial")
	 */
	StylePropertyI<String> fontFamilyProperty();

	/**
	 * @return the font size (point)
	 */
	IntegerStylePropertyI fontSizeProperty();

	/**
	 * @return true if the font should be displayed in italic
	 */
	StylePropertyI<Boolean> italicProperty();

	/**
	 * @return true if the font should be displayed in bold
	 */
	StylePropertyI<Boolean> boldProperty();

	/**
	 * @return the text alignement for this font
	 */
	StylePropertyI<TextAlignment> textAlignmentProperty();

	/**
	 * @return the font color
	 */
	StylePropertyI<Color> colorProperty();

	/**
	 * @return true if the font should be displayed underlined
	 */
	StylePropertyI<Boolean> underlineProperty();
	
	/**
	 * @return true if the displayed text should be uppercase (shouldn't modify model text)
	 */
	StylePropertyI<Boolean> upperCaseProperty();
	//========================================================================

	// Class part : "Computed"
	//========================================================================
	/**
	 * @return the font computed from different text style values.<br>
	 * Change on text style changes
	 */
	ReadOnlyObjectProperty<Font> fontProperty();

	/**
	 * To create a new font from this text style font.<br>
	 * This is useful to create exactly the same font but with a scale factor.
	 * @param size the new size
	 * @return the font derived from this style font
	 */
	Font deriveFont(double size);

	/**
	 * To add a invalidation listener that will be notified on each text style change
	 * @param invalidationListener the invalidation listener to add
	 */
	void addInvalidationListener(InvalidationListener invalidationListener);

	/**
	 * To remove a invalidation listener
	 * @param invalidationListener the invalidation listener to remove
	 */
	void removeInvalidationListener(InvalidationListener invalidationListener);
	//========================================================================

}
