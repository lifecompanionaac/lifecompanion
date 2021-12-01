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

package org.lifecompanion.framework.commons.fx.translation;

import java.util.ArrayList;
import java.util.List;

import org.lifecompanion.framework.commons.translation.Translation;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;

public enum TranslationFX {
	INSTANCE;

	private TranslationFX() {
		Translation.INSTANCE.setSuppStringConverter(arg -> arg instanceof ObservableValue, arg -> ((ObservableValue<?>) arg).getValue());
	}

	/**
	 * Get a new string binding that will change if the given observable arguments change.<br>
	 * This act exactly the same as {@link #getIText(String, Object...)} but the difference is that the property content change with argument changes.
	 * @param key the key
	 * @param args the values to put in argument
	 * @return the string property
	 */
	public StringBinding getITextBinding(final String key, final Object... args) {
		List<Observable> obsList = new ArrayList<>();
		//Get all the observable values
		for (Object arg : args) {
			if (arg instanceof Observable) {
				obsList.add((Observable) arg);
			}
		}
		//Create the binding that changes with observable value change
		return Bindings.createStringBinding(() -> {
			return Translation.INSTANCE.getIText(key, args);
		}, obsList.toArray(new Observable[0]));
	}

	/**
	 * This method is equivalent to {@link #getITextBinding(String, String...)}.<br>
	 *
	 * <pre>
	 * //Equivalent
	 * INSTANCE.getITextBinding(...)
	 * </pre>
	 */
	public static StringBinding getTextBinding(final String key, final Object... args) {
		return TranslationFX.INSTANCE.getITextBinding(key, args);
	}
}
