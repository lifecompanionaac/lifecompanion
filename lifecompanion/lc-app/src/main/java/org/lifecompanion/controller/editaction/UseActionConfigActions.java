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
package org.lifecompanion.controller.editaction;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionManagerI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.ConfigUIUtils;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that keep every config actions relative to use action.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseActionConfigActions {

    public static class MultiAddUseActionAction implements UndoRedoActionI {
        private final Node source;
        private final BaseUseActionI<?> useAction;
        private final List<UseActionTriggerComponentI> elements;
        private final UseActionEvent actionEvent;
        private Map<UseActionTriggerComponentI, BaseUseActionI<?>> generatedActions;

        public MultiAddUseActionAction(Node source, BaseUseActionI<?> useAction, List<UseActionTriggerComponentI> elements, UseActionEvent actionEvent) {
            this.source = source;
            this.useAction = useAction;
            this.elements = elements;
            this.actionEvent = actionEvent;
        }

        @Override
        public void doAction() throws LCException {
            this.generatedActions = new HashMap<>();
            //For each keys, duplicate the actions and add it
            for (UseActionTriggerComponentI element : this.elements) {
                // Issue #126 : should add the action only if not already present on element
                if (element.getActionManager().getFirstActionOfType(this.actionEvent, this.useAction.getClass()) == null) {
                    BaseUseActionI<?> action = (BaseUseActionI<?>) this.useAction.duplicate(true);
                    this.generatedActions.put(element, action);
                    element.getActionManager().componentActions().get(this.actionEvent).add(action);
                }
            }
        }

        @Override
        public void undoAction() throws LCException {
            //Remove generated actions
            for (UseActionTriggerComponentI element : this.elements) {
                BaseUseActionI<?> action = this.generatedActions.get(element);
                if (action != null) {
                    element.getActionManager().componentActions().get(this.actionEvent).remove(action);
                }
            }
        }

        @Override
        public void redoAction() throws LCException {
            //Use generated action
            for (UseActionTriggerComponentI element : this.elements) {
                BaseUseActionI<?> action = this.generatedActions.get(element);
                if (action != null) {
                    element.getActionManager().componentActions().get(this.actionEvent).add(action);
                }
            }
        }

        @Override
        public String getNameID() {
            return "add.actions.to.selected.keys";
        }
    }

    public static class MultiRemoveUseActionAction implements UndoRedoActionI {
        private final Node source;
        private final BaseUseActionI<?> useAction;
        private final List<UseActionTriggerComponentI> elements;
        private final UseActionEvent actionEvent;
        private Map<UseActionTriggerComponentI, BaseUseActionI<?>> removedActions;

        public MultiRemoveUseActionAction(Node source, BaseUseActionI<?> useAction, List<UseActionTriggerComponentI> elements, UseActionEvent actionEvent) {
            this.source = source;
            this.useAction = useAction;
            this.elements = elements;
            this.actionEvent = actionEvent;
        }

        @Override
        public void doAction() throws LCException {
            int notRemovedBecauseOfKeyOptionCount = 0;
            this.removedActions = new HashMap<>();
            for (UseActionTriggerComponentI element : this.elements) {
                BaseUseActionI actionToRemove = element.getActionManager().getFirstActionOfType(this.actionEvent, this.useAction.getClass());
                if (actionToRemove != null) {
                    if (actionToRemove.attachedToKeyOptionProperty().get()) {
                        notRemovedBecauseOfKeyOptionCount++;
                    } else {
                        removedActions.put(element, actionToRemove);
                        element.getActionManager().componentActions().get(this.actionEvent).remove(actionToRemove);
                    }
                }
            }
            if (notRemovedBecauseOfKeyOptionCount > 0) {
                Alert dialog = ConfigUIUtils.createAlert(source, AlertType.WARNING);
                dialog.setHeaderText(Translation.getText("alert.message.disable.remove.action.header"));
                dialog.setContentText(Translation.getText("alert.message.disable.remove.action.message", this.useAction.getName()));
                dialog.show();
            }
        }

        @Override
        public void undoAction() throws LCException {
            for (UseActionTriggerComponentI element : this.elements) {
                BaseUseActionI<?> action = this.removedActions.get(element);
                if (action != null) {
                    element.getActionManager().componentActions().get(this.actionEvent).add(action);
                }
            }
        }

        @Override
        public void redoAction() throws LCException {
            for (UseActionTriggerComponentI element : this.elements) {
                BaseUseActionI<?> action = this.removedActions.get(element);
                if (action != null) {
                    element.getActionManager().componentActions().get(this.actionEvent).remove(action);
                }
            }
        }

        @Override
        public String getNameID() {
            return "remove.actions.from.selected.keys";
        }
    }

    public static class MultiEditUseActionAction implements UndoRedoActionI {
        private final BaseUseActionI<?> useAction;
        private final List<UseActionTriggerComponentI> elements;
        private final UseActionEvent actionEvent;
        private Map<UseActionTriggerComponentI, Pair<BaseUseActionI<?>, BaseUseActionI<?>>> changedAction;

        public MultiEditUseActionAction(BaseUseActionI<?> useAction, List<UseActionTriggerComponentI> elements, UseActionEvent actionEvent) {
            this.useAction = useAction;
            this.elements = elements;
            this.actionEvent = actionEvent;
        }

        @Override
        public void doAction() throws LCException {
            this.changedAction = new HashMap<>();
            for (UseActionTriggerComponentI element : this.elements) {
                BaseUseActionI actionToReplace = element.getActionManager().getFirstActionOfType(this.actionEvent, this.useAction.getClass());
                if (actionToReplace != null) {
                    BaseUseActionI<?> editedAction = (BaseUseActionI<?>) this.useAction.duplicate(true);
                    changedAction.put(element, Pair.of(actionToReplace, editedAction));
                    replaceAction(element, actionToReplace, editedAction);
                }
            }
        }

        @Override
        public void undoAction() throws LCException {
            for (UseActionTriggerComponentI element : this.elements) {
                Pair<BaseUseActionI<?>, BaseUseActionI<?>> replacement = changedAction.get(element);
                if (replacement != null) {
                    replaceAction(element, replacement.getRight(), replacement.getLeft());
                }
            }
        }

        @Override
        public void redoAction() throws LCException {
            for (UseActionTriggerComponentI element : this.elements) {
                Pair<BaseUseActionI<?>, BaseUseActionI<?>> replacement = changedAction.get(element);
                if (replacement != null) {
                    replaceAction(element, replacement.getLeft(), replacement.getRight());
                }
            }
        }

        private void replaceAction(UseActionTriggerComponentI element, BaseUseActionI actionToReplace, BaseUseActionI<?> editedAction) {
            int previousIndex = element.getActionManager().componentActions().get(actionEvent).indexOf(actionToReplace);
            element.getActionManager().componentActions().get(this.actionEvent).set(previousIndex, editedAction);
        }

        @Override
        public String getNameID() {
            return "edit.actions.from.selected.keys";
        }
    }


    /**
     * When a use action is added
     */
    public static class AddUseActionAction implements UndoRedoActionI {
        private final Node source;
        private UseActionManagerI useActionManager;
        private BaseUseActionI<?> useAction;
        private UseActionEvent eventType;

        public AddUseActionAction(final Node source, final UseActionManagerI useActionManagerP, final BaseUseActionI<?> useActionP, final UseActionEvent eventTypeP) {
            this.source = source;
            this.useActionManager = useActionManagerP;
            this.useAction = useActionP;
            this.eventType = eventTypeP;
        }

        @Override
        public void doAction() throws LCException {
            if (this.useAction.allowedParent().isAssignableFrom(this.useActionManager.getActionParent().getClass())) {
                this.useActionManager.componentActions().get(this.eventType).add(this.useAction);
            } else {
                Alert dialog = ConfigUIUtils.createAlert(source, AlertType.WARNING);
                dialog.setHeaderText(Translation.getText("alert.message.invalid.action.parent.header"));
                dialog.setContentText(Translation.getText("alert.message.invalid.action.parent.message", this.useAction.getName()));
                dialog.show();
            }
        }

        @Override
        public String getNameID() {
            return "action.use.action.add";
        }

        @Override
        public void undoAction() throws LCException {
            if (this.useAction.allowedParent().isAssignableFrom(this.useActionManager.getActionParent().getClass())) {
                this.useActionManager.componentActions().get(this.eventType).remove(this.useAction);
            }
        }

        @Override
        public void redoAction() throws LCException {
            if (this.useAction.allowedParent().isAssignableFrom(this.useActionManager.getActionParent().getClass())) {
                this.doAction();
            }
        }

    }

    /**
     * When a use action is removed
     */
    public static class RemoveUseActionAction implements UndoRedoActionI {
        private final Node source;
        private UseActionManagerI useActionManager;
        private BaseUseActionI<?> useAction;
        private UseActionEvent eventType;

        public RemoveUseActionAction(final Node source, final UseActionManagerI useActionManagerP, final BaseUseActionI<?> useActionP, final UseActionEvent eventTypeP) {
            this.source = source;
            this.useActionManager = useActionManagerP;
            this.useAction = useActionP;
            this.eventType = eventTypeP;
        }

        @Override
        public void doAction() throws LCException {
            //Check if action can be modified
            if (this.useAction.attachedToKeyOptionProperty().get()) {
                Alert dialog = ConfigUIUtils.createAlert(source, AlertType.WARNING);
                dialog.setHeaderText(Translation.getText("alert.message.disable.remove.action.header"));
                dialog.setContentText(Translation.getText("alert.message.disable.remove.action.message", this.useAction.getName()));
                dialog.show();
            } else {
                this.useActionManager.componentActions().get(this.eventType).remove(this.useAction);
            }
        }

        @Override
        public String getNameID() {
            return "action.use.action.remove";
        }

        @Override
        public void undoAction() throws LCException {
            if (!this.useAction.attachedToKeyOptionProperty().get()) {
                this.useActionManager.componentActions().get(this.eventType).add(this.useAction);
            }
        }

        @Override
        public void redoAction() throws LCException {
            if (!this.useAction.attachedToKeyOptionProperty().get()) {
                this.doAction();
            }
        }
    }

    /**
     * When a use action is modified
     */
    public static class EditUseActionAction implements BaseEditActionI {

        @Override
        public void doAction() throws LCException {
            //Do nothing, this action is just created to trace
        }

        @Override
        public String getNameID() {
            return "action.use.action.edit";
        }
    }

    /**
     * Move an action in the top in the list
     */
    public static class ShiftActionUpAction implements UndoRedoActionI {
        private BaseUseActionI<?> action;
        private UseActionManagerI actionManager;
        private UseActionEvent actionEvent;

        public ShiftActionUpAction(final BaseUseActionI<?> action, final UseActionManagerI actionManager, final UseActionEvent actionEvent) {
            this.action = action;
            this.actionManager = actionManager;
            this.actionEvent = actionEvent;
        }

        @Override
        public void doAction() throws LCException {
            this.actionManager.shiftActionUp(this.actionEvent, this.action);
        }

        @Override
        public String getNameID() {
            return "action.use.shift.up";
        }

        @Override
        public void undoAction() throws LCException {
            this.actionManager.shiftActionDown(this.actionEvent, this.action);
        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }

    }

    /**
     * Move an action in the bottom in the list
     */
    public static class ShiftActionDownAction implements UndoRedoActionI {
        private BaseUseActionI<?> action;
        private UseActionManagerI actionManager;
        private UseActionEvent actionEvent;

        public ShiftActionDownAction(final BaseUseActionI<?> action, final UseActionManagerI actionManager, final UseActionEvent actionEvent) {
            this.action = action;
            this.actionManager = actionManager;
            this.actionEvent = actionEvent;
        }

        @Override
        public void doAction() throws LCException {
            this.actionManager.shiftActionDown(this.actionEvent, this.action);
        }

        @Override
        public String getNameID() {
            return "action.use.shift.down";
        }

        @Override
        public void undoAction() throws LCException {
            this.actionManager.shiftActionUp(this.actionEvent, this.action);

        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }

    }

}
