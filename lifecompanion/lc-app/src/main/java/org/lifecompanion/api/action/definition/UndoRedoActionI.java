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

package org.lifecompanion.api.action.definition;

import org.lifecompanion.api.exception.LCException;

/**
 * Base action interface that define an action that can be undone or redone.
 */
public interface UndoRedoActionI extends BaseConfigActionI {

	/**
	 * Must do the inverse of this action.
	 * @throws LCException should be thrown only if the problem must be shown to user, if not silently log the problem
	 */
	public void undoAction() throws LCException;

	/**
	 * Must redo the action.<br>
	 * This method is called only if the undoAction have already be called.<br>
	 * Note that this method is different than {@link #doAction()} because all the redone action must have always the same behavior.<br>
	 * E.g. : every modification that could be done to dependent object of the modification should keep working
	 * @throws LCException should be thrown only if the problem must be shown to user, if not silently log the problem
	 */
	public void redoAction() throws LCException;
}
