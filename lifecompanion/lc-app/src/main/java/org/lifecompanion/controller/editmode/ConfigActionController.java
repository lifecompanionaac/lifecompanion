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
package org.lifecompanion.controller.editmode;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

/**
 * Class that execute and dispatch all the configuration action into software.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum ConfigActionController {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(ConfigActionController.class);
    private static final int HISTORY_SIZE = 50, UNDO_REDO_SIZE = 20;

    /**
     * List of all the undoable actions done
     */
    private ListProperty<UndoRedoActionI> undoActions;

    /**
     * List of all the redo actions done
     */
    private ListProperty<UndoRedoActionI> redoActions;

    /**
     * Mapping between history list and action name
     */
    private ObservableList<String> actionStringList;

    /**
     * If the undo/redo command are currently allowed
     */
    private BooleanProperty undoRedoEnabled;

    ConfigActionController() {
        this.actionStringList = FXCollections.observableList(new ArrayList<>(ConfigActionController.HISTORY_SIZE));
        this.redoActions = new SimpleListProperty<>(
                new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>(ConfigActionController.UNDO_REDO_SIZE))));
        this.undoActions = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>(ConfigActionController.UNDO_REDO_SIZE)));
        this.undoRedoEnabled = new SimpleBooleanProperty(true);
        this.initBinding();
        this.LOGGER.info("Singleton {} initialized", this.getClass().getSimpleName());
    }

    private void initBinding() {
        AppModeController.INSTANCE.getEditModeContext().configurationProperty().addListener((obs, ov, nv) -> this.clearUndoRedo());
    }

    private void addActionToHistory(final BaseEditActionI action) {
        this.actionStringList.add(this.actionToString(new Date(), action));
    }

    private String actionToString(final Date date, final BaseEditActionI action) {
        return StringUtils.dateToStringDateWithOnlyHoursMinuteSecond(date) + " - " + Translation.getText(action.getNameID());
    }

    public void addAction(final BaseEditActionI action) {
        this.addAction(action, false);
    }

    /**
     * Add the action to history without executing it.<br>
     * Also add to the undo/redo history when possible.
     *
     * @param action the action
     */
    public void addAction(final BaseEditActionI action, final boolean forceUnsavedAction) {
        // this.history.add(new Pair<>(new Date(), action));
        this.addActionToHistory(action);
        // Unsaved changes on configuration
        if (action instanceof UndoRedoActionI || forceUnsavedAction) {
            AppModeController.INSTANCE.getEditModeContext().increaseUnsavedActionOnCurrentConfiguration();
        }
        // Redo/undo
        if (action instanceof UndoRedoActionI) {
            this.LOGGER.info("Add undo redo action \"{}\"", Translation.getText(action.getNameID()));
            this.undoActions.add((UndoRedoActionI) action);
            this.redoActions.clear();
        } else {
            this.LOGGER.info("Add standard action to history : \"{}\"", Translation.getText(action.getNameID()));
        }
    }

    public BooleanBinding undoDisabledProperty() {
        return this.undoRedoEnabled.not().or(this.undoActions.emptyProperty());
    }

    public BooleanBinding redoDisabledProperty() {
        return this.undoRedoEnabled.not().or(this.redoActions.emptyProperty());
    }

    public BooleanProperty undoRedoEnabled() {
        return this.undoRedoEnabled;
    }

    public ObservableList<String> getActionStringList() {
        return this.actionStringList;
    }

    /**
     * Clear the history of undo/redo command.
     */
    public void clearUndoRedo() {
        this.LOGGER.info("Clear undo/redo actions history");
        this.undoActions.clear();
        this.redoActions.clear();
    }

    /**
     * Undo the previously done undoable action.
     */
    public void undo() {
        if (!this.undoActions.isEmpty() && this.undoRedoEnabled.get()) {
            UndoRedoActionI action = this.undoActions.remove(this.undoActions.size() - 1);
            try {
                this.LOGGER.info("Action will be undone : \"{}\"", Translation.getText(action.getNameID()));
                action.undoAction();
                this.redoActions.add(action);
                AppModeController.INSTANCE.getEditModeContext().decreaseUnsavedActionOnCurrentConfiguration();
            } catch (LCException e) {
                reportErrorOnConfigActionDoRedoUndo(action, e, "undoAction()", "error.config.action.while.undo");
            }
        }
    }

    /**
     * Redo the previously undo action.
     */
    public void redo() {
        if (!this.redoActions.isEmpty() && this.undoRedoEnabled.get()) {
            UndoRedoActionI action = this.redoActions.remove(this.redoActions.size() - 1);
            try {
                this.LOGGER.info("Action will be redone : \"{}\"", Translation.getText(action.getNameID()));
                action.redoAction();
                this.undoActions.add(action);
                AppModeController.INSTANCE.getEditModeContext().increaseUnsavedActionOnCurrentConfiguration();
            } catch (LCException e) {
                reportErrorOnConfigActionDoRedoUndo(action, e, "redoAction()", "error.config.action.while.redo");
            }
        }
    }

    /**
     * Execute the action and add it with {@link #addAction(BaseEditActionI)} on the calling Thread.
     *
     * @param action the action to add (null is allowed and NOP)
     */
    public void executeAction(final BaseEditActionI action) {
        if (action != null) {
            try {
                action.doAction();
                this.addAction(action);
            } catch (LCException e) {
                reportErrorOnConfigActionDoRedoUndo(action, e, "doAction()", "error.config.action.while.do");
            }
        }
    }

    public void reportErrorOnConfigActionDoRedoUndo(BaseEditActionI action, LCException exception, String methodName, String errorNotificationMessageId) {
        LOGGER.error("Problem on action \"{}\" when calling " + methodName, action.getNameID(), exception);
        ErrorHandlingController.INSTANCE.showErrorNotificationWithExceptionDetails(Translation.getText(errorNotificationMessageId, Translation.getText(action.getNameID())), exception);
    }
}
