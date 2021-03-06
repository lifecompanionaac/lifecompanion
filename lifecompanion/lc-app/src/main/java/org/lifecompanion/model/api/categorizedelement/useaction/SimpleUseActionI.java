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

package org.lifecompanion.model.api.categorizedelement.useaction;

import java.util.Map;

import org.lifecompanion.model.api.usevariable.UseVariableI;

/**
 * The simple use action provide a way to faster execute action when the action is simple.<br>
 * If the action don't need to detect press start and end, the action should use this action method {@link #execute(UseActionEvent)} instead
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface SimpleUseActionI<T extends UseActionTriggerComponentI> extends BaseUseActionI<T> {

	/**
	 * This method is called to execute the use action.<br>
	 * If this method is called, the action should not override the {@link #eventStarts(UseActionEvent)} and {@link #eventEnds(UseActionEvent)} methods.
	 * This method is a way to faster implements basic actions.
	 * @param variables represent the variables given to the actions, the variables can be generated by the software, by the event that fired action, or by an action before itself.<br>
	 * Variables are not guarantee to be the same depending on the source that generate this action.
	 */
	public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables);
}
