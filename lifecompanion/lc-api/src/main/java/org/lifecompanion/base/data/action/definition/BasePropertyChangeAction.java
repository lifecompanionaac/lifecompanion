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

package org.lifecompanion.base.data.action.definition;

import javafx.beans.value.WritableValue;
import org.lifecompanion.api.action.definition.UndoRedoActionI;
import org.lifecompanion.api.exception.LCException;

/**
 * Base action for all action that change a value of a property.<br>
 * Avoid useless code duplication because some action are just registering a property value change.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class BasePropertyChangeAction<T> implements UndoRedoActionI {
	protected WritableValue<T> property;
	protected T previousValue, wantedValue;

	public BasePropertyChangeAction(final WritableValue<T> propertyP, final T wantedValueP) {
		this.property = propertyP;
		this.wantedValue = wantedValueP;
	}

	public BasePropertyChangeAction(final WritableValue<T> propertyP, final T previousValueP, final T wantedValueP) {
		this(propertyP, wantedValueP);
		this.previousValue = previousValueP;
	}

	@Override
	public void doAction() throws LCException {
		this.previousValue = this.property.getValue();
		this.property.setValue(this.wantedValue);
	}

	@Override
	public void undoAction() throws LCException {
		this.property.setValue(this.previousValue);
	}

	@Override
	public void redoAction() throws LCException {
		this.doAction();
	}

}
