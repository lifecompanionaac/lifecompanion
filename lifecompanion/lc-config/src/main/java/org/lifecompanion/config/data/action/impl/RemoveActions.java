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

package org.lifecompanion.config.data.action.impl;

import org.lifecompanion.api.action.definition.UndoRedoActionI;
import org.lifecompanion.api.component.definition.*;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.base.data.action.definition.BaseGridChangeAction;
import org.lifecompanion.config.data.notif.LCNotification;
import org.lifecompanion.config.view.pane.main.notification2.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to keep every remove action.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RemoveActions {
    public static class RemoveRootComponentAction implements UndoRedoActionI {
        private LCConfigurationI configuration;
        private RootGraphicComponentI toRemove;

        public RemoveRootComponentAction(final RootGraphicComponentI toRemoveP) {
            this.toRemove = toRemoveP;
        }

        @Override
        public void doAction() throws LCException {
            this.configuration = this.toRemove.configurationParentProperty().get();
            if (this.configuration != null) {
                this.configuration.getChildren().remove(this.toRemove);
            }
        }

        @Override
        public String getNameID() {
            return "remove.action.root.component.name";
        }

        @Override
        public void undoAction() throws LCException {
            if (this.configuration != null) {
                this.toRemove.dispatchRemovedPropertyValue(false);
                this.configuration.getChildren().add(this.toRemove);
            }
        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }
    }

    public static class RemoveGridPartAction extends BaseGridChangeAction {
        private GridPartComponentI gridPart;

        public RemoveGridPartAction(final GridPartComponentI gridPartP) {
            super(gridPartP.gridParentProperty().get().getGrid());
            this.gridPart = gridPartP;
        }

        @Override
        public void doAction() throws LCException {
            this.stateBeforeDo = this.grid.saveGrid();
            this.grid.removeComponent(this.gridPart.rowProperty().get(), this.gridPart.columnProperty().get());
        }

        @Override
        public String getNameID() {
            return "remove.action.grid.part.name";
        }

        @Override
        public void undoAction() throws LCException {
            this.gridPart.dispatchRemovedPropertyValue(false);
            super.undoAction();
        }
    }

    public static class RemoveMultipleKeyAction implements UndoRedoActionI {
        private List<GridPartKeyComponentI> keys;
        private List<RemoveGridPartAction> actions;

        public RemoveMultipleKeyAction(final List<GridPartKeyComponentI> keys) {
            this.keys = keys;
            actions = new ArrayList<>(keys.size());
        }

        @Override
        public void doAction() throws LCException {
            for (GridPartKeyComponentI key : keys) {
                RemoveGridPartAction removeAction = new RemoveGridPartAction(key);
                removeAction.doAction();
                actions.add(removeAction);
            }
        }

        @Override
        public void undoAction() throws LCException {
            for (int i = actions.size() - 1; i >= 0; i--) {
                actions.get(i).undoAction();
            }
        }

        @Override
        public void redoAction() throws LCException {
            for (RemoveGridPartAction removeGridPartAction : actions) {
                removeGridPartAction.redoAction();
            }
        }

        @Override
        public String getNameID() {
            return "remove.action.multiple.keys.name";
        }

    }
}
