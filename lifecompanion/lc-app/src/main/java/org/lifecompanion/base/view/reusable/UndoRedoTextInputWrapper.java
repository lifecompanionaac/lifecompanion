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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TextInputControl;

/**
 * Textfield that allow user to register change event only when needed, to create undo/redo action.<br>
 * This wrapper allows TextField to execute is base behavior for undo/redo, and fire change when needed just on focus lost
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UndoRedoTextInputWrapper {
	private final static Logger LOGGER = LoggerFactory.getLogger(UndoRedoTextInputWrapper.class);

	/**
	 * The controller text control
	 */
	private TextInputControl textControl;

	/**
	 * Previous string value
	 */
	private String previousStringValue;

	/**
	 * The property that should be enable/disable on text field focus
	 */
	private BooleanProperty enableDisableProperty;

	/**
	 * Listener on change
	 */
	private TextChangedListener listener;

	/**
	 * Create a undo/redo text input wrapper.<br>
	 * The given boolean property will be set to false when text is focused, to allow text field to execute its undo/redo actions, then will be set to true when focus is lost
	 * @param textControlP the text control to wrap
	 * @param enableDisablePropertyP a property set to false when text control is focus, and false instead.
	 */
	public UndoRedoTextInputWrapper(final TextInputControl textControlP, final BooleanProperty enableDisablePropertyP) {
		this.textControl = textControlP;
		this.enableDisableProperty = enableDisablePropertyP;
		this.textControl.focusedProperty().addListener((obs, ov, nv) -> {
			String newTextControlValue = this.textControl.getText();
			if (nv) {
				this.previousStringValue = newTextControlValue;
				this.enableDisableProperty.set(false);
			} else {
				this.fireChangeEvent();
				this.enableDisableProperty.set(true);
			}
		});
	}

	/**
	 * Method to fire a change event if needed.<br>
	 * Can be useful if we change the binding of the text field, but we the field doesn't lost focus
	 */
	public void fireChangeEvent() {
		String newTextControlValue = this.textControl.getText();
		boolean fireEvent = this.isValidChange(this.previousStringValue, newTextControlValue);
		if (fireEvent && this.listener != null) {
			UndoRedoTextInputWrapper.LOGGER.debug("Change event will be fired on UndoRedoTextInputWrapper for a change from \"{}\" to \"{}\"",
					this.previousStringValue, newTextControlValue);
			this.listener.changed(this.previousStringValue, newTextControlValue);
		}
	}

	/**
	 * Method to manually refresh the previous value of the text control.<br>
	 * This method is useful the component that listen the changes with {@link #getListener()} change
	 */
	public void clearPreviousValue() {
		this.previousStringValue = this.textControl.getText();
	}

	/**
	 * @param ov the first string
	 * @param nv the second string
	 * @return true if two string are different (null and "" are considered as equals)
	 */
	private boolean isValidChange(final String ov, final String nv) {
		if (ov == null && "".equals(nv)) {
			return false;
		}
		if (nv == null && "".equals(ov)) {
			return false;
		}
		return StringUtils.isDifferent(ov, nv);
	}

	// Class part : "Getter/setter"
	//========================================================================
	public TextInputControl getTextControl() {
		return this.textControl;
	}

	public TextChangedListener getListener() {
		return this.listener;
	}

	public void setListener(final TextChangedListener listenerP) {
		this.listener = listenerP;
	}

	//========================================================================

	// Class part : "Change listener"
	//========================================================================
	public static interface TextChangedListener {
		public void changed(String oldText, String newText);
	}
	//========================================================================

}
