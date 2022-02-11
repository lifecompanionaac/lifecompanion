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

package org.lifecompanion.model.api.selectionmode;

import java.util.function.Supplier;

import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;

/**
 * Selection mode where mouse position on keys is important.<br>
 * User can clic on key, or the selection can be done with a timer.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface DirectSelectionModeI extends SelectionModeI {

	// Class part : "To fire action when needed"
	//========================================================================
	/**
	 * Event called when the mouse enter over a specific key
	 * @param key the key where the mouse is over
	 */
	void selectionEnter(GridPartKeyComponentI key);

	/**
	 * Event called when the mouse exit a specific key
	 * @param key the exited key
	 */
	void selectionExit(GridPartKeyComponentI key);

	/**
	 * Event called when the user starts press on a key
	 * @param key the key where user starts press
	 */
	void selectionPress(GridPartKeyComponentI key);

	/**
	 * Event called when the user stop press on a key
	 * @param key the key where user stop press
	 * @param skipActionFire if this event should fire any action
	 */
	void selectionRelease(GridPartKeyComponentI key, boolean skipActionFire);

	/**
	 * Event called when the user move over a key
	 * @param key the key where the user move on
	 */
	void selectionMovedOver(GridPartKeyComponentI key);

	/**
	 * Set a listener
	 * @param nextSelectionListener
	 */
	void setNextSelectionListener(Supplier<Boolean> nextSelectionListener);
	//========================================================================
}
