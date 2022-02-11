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

import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContentDisplay;

/**
 * Represent the leaf component of a grid.<br>
 * This component can contains image, text, and use actions on it.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface GridPartKeyComponentI extends GridPartComponentI, ImageUseComponentI, UseActionTriggerComponentI {

	/**
	 * @return a property that contains this key text content
	 */
	StringProperty textContentProperty();

	/**
	 * @return a property that should indicate how the text is displayed in this key.
	 */
	ObjectProperty<ContentDisplay> textPositionProperty();

	/**
	 * @return the key option for this key.<br>
	 * The option should never be null for this key, but a normal option can be used to don't request any particular behavior for this key.
	 */
	ReadOnlyObjectProperty<KeyOptionI> keyOptionProperty();

	/**
	 * This is the method to call if you want to update the key option (because the {@link #keyOptionProperty()} is read only)
	 * @param newKeyOption the new option to set to the key
	 * @param fireKeyNewlyAttached should be on true if we want {@link KeyOptionI#keyNewlyAttached()} to be called once the key option changed.</br>
	 * Should be true most of the time.
	 * @throws IllegalArgumentException if the key option is null
	 */
	void changeKeyOption(KeyOptionI newKeyOption, boolean fireKeyNewlyAttached) throws IllegalArgumentException;

	/**
	 * @return should return true if this key text content is written by one or more of this key actions.<br>
	 * This is used to know if the text content should be updated when the written content changes.
	 */
	boolean isTextContentWritten();
}
