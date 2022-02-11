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
package org.lifecompanion.model.impl.editaction;

import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.api.configurationcomponent.ComponentGridI;
import org.lifecompanion.model.impl.configurationcomponent.GridState;
import org.lifecompanion.model.impl.exception.LCException;

/**
 * Base action for all action that change the grid structure.<br>
 * This action save the grid both before do the action, and before undo the action.<br>
 * Subclass must set the {@link #stateBeforeDo}
 */
public abstract class BaseGridChangeAction implements UndoRedoActionI {
    /**
     * The grid that changed
     */
    protected ComponentGridI grid;

    /**
     * The grid state before the action that will be done
     */
    protected GridState stateBeforeDo;

    /**
     * The grid action before the undo that can be done
     */
    protected GridState stateBeforeUndo;

    /**
     * Create a new base grid action for a given grid
     *
     * @param gridP the grid that changed
     */
    public BaseGridChangeAction(final ComponentGridI gridP) {
        this.grid = gridP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void redoAction() throws LCException {
        if (this.stateBeforeUndo != null) {
            this.grid.restoreGrid(this.stateBeforeUndo);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undoAction() throws LCException {
        this.stateBeforeUndo = this.grid.saveGrid();
        this.grid.restoreGrid(this.stateBeforeDo);
    }
}
