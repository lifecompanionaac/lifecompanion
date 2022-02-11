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

package org.lifecompanion.model.impl.categorizedelement.useaction;

import java.util.HashMap;
import java.util.Map;

import org.lifecompanion.model.api.categorizedelement.useaction.ActionExecutionResultI;
import org.lifecompanion.model.api.usevariable.UseVariableI;

/**
 * The implementation of {@link ActionExecutionResultI}
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ActionExecutionResult implements ActionExecutionResultI {

	private boolean movingActionExecuted, errorOnExecution;
	private int actionExecutedCount;
	private Map<String, UseVariableI<?>> executedActionVariables;

	public ActionExecutionResult(final boolean errorOnExecution) {
		this.errorOnExecution = errorOnExecution;
		this.executedActionVariables = new HashMap<>();
	}

	public ActionExecutionResult(final boolean movingActionExecuted, final int actionExecutedCount,
			final Map<String, UseVariableI<?>> executedActionVariablesP) {
		super();
		this.movingActionExecuted = movingActionExecuted;
		this.actionExecutedCount = actionExecutedCount;
		this.executedActionVariables = executedActionVariablesP;
	}

	@Override
	public int getActionExecutedCount() {
		return this.actionExecutedCount;
	}

	@Override
	public boolean isMovingActionExecuted() {
		return this.movingActionExecuted;
	}

	@Override
	public boolean isErrorOnExecution() {
		return this.errorOnExecution;
	}

	@Override
	public Map<String, UseVariableI<?>> executedActionVariables() {
		return this.executedActionVariables;
	}

	@Override
	public String toString() {
		return "ActionExecutionResult [movingActionExecuted=" + this.movingActionExecuted + ", errorOnExecution=" + this.errorOnExecution
				+ ", actionExecutedCount=" + this.actionExecutedCount + ", executedActionVariables=" + this.executedActionVariables + "]";
	}

}
