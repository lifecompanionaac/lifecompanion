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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.List;

/**
 * Class that hold all the edit menu actions.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UndoRedoActions {
    public static final EventHandler<ActionEvent> HANDLER_UNDO = (ea) -> {
        if (!ConfigActionController.INSTANCE.undoDisabledProperty().get()) {
            ConfigActionController.INSTANCE.executeAction(new UndoAction());
        }
    };
    public static final EventHandler<ActionEvent> HANDLER_REDO = (ea) -> {
        if (!ConfigActionController.INSTANCE.redoDisabledProperty().get()) {
            ConfigActionController.INSTANCE.executeAction(new RedoAction());
        }
    };

    public static final KeyCombination KEY_COMBINATION_UNDO = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCombination KEY_COMBINATION_REDO = new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCombination KEY_COMBINATION_REMOVE = new KeyCodeCombination(KeyCode.DELETE);

    /**
     * Undo last action
     */
    public static class UndoAction implements BaseEditActionI {
        @Override
        public void doAction() throws LCException {
            ConfigActionController.INSTANCE.undo();
            final LCNotification notif = LCNotification.createInfo(Translation.getText("notification.undo.done.title"));
            notif.setMsDuration(LCGraphicStyle.BRIEF_NOTIFICATION_DURATION_MS);
            LCNotificationController.INSTANCE.showNotification(notif);
        }

        @Override
        public String getNameID() {
            return "action.undo.name";
        }
    }

    /**
     * Redo last action
     */
    public static class RedoAction implements BaseEditActionI {
        @Override
        public void doAction() throws LCException {
            ConfigActionController.INSTANCE.redo();
            final LCNotification notif = LCNotification.createInfo(Translation.getText("notification.redo.done.title"));
            notif.setMsDuration(LCGraphicStyle.BRIEF_NOTIFICATION_DURATION_MS);
            LCNotificationController.INSTANCE.showNotification(notif);
        }

        @Override
        public String getNameID() {
            return "action.redo.name";
        }
    }

    public static class MultiActionWrapperAction implements UndoRedoActionI {
        private final String nameId;
        private final List<UndoRedoActionI> actions;

        public MultiActionWrapperAction(String nameId, List<UndoRedoActionI> actions) {
            this.nameId = nameId;
            this.actions = actions;
        }

        @Override
        public void doAction() throws LCException {
            for (UndoRedoActionI action : actions) {
                action.doAction();
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
            for (UndoRedoActionI action : actions) {
                action.redoAction();
            }
        }

        @Override
        public String getNameID() {
            return nameId;
        }
    }
}
