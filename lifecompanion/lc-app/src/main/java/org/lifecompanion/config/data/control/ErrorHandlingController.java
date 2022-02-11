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

package org.lifecompanion.config.data.control;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogEvent;
import org.controlsfx.dialog.ExceptionDialog;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.base.data.control.AsyncExecutorController;
import org.lifecompanion.util.StageUtils;
import org.lifecompanion.config.data.notif.LCNotification;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.pane.main.notification2.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ErrorHandlingController implements LCStateListener {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlingController.class);

    private static final int MAX_ERROR_DISPLAY_COUNT = 5;

    /**
     * Number of currently displayed error dialog count
     */
    private int displayedErrorDialogCount;

    private int displayedErrorNotificationCount;

    private final EventHandler<DialogEvent> DECREASE_ERROR_DIALOG_COUNT_HANDLER = e -> displayedErrorDialogCount--;

    // STATE LISTENER
    //========================================================================
    @Override
    public void lcStart() {
        AsyncExecutorController.INSTANCE.addTaskAddedForExecutionListener((task, blocking, hideFromNotification) -> {
            final EventHandler<WorkerStateEvent> taskOnFailed = task.getOnFailed();
            task.setOnFailed(event -> {
                if (taskOnFailed != null) taskOnFailed.handle(event);
                showErrorNotificationWithExceptionDetails(Translation.getText("error.handling.task.failed", task.getTitle()), task.getException());
            });
        });
    }

    @Override
    public void lcExit() {
    }
    //========================================================================

    // HANDLING ERRORS
    //========================================================================
    public void showErrorNotificationWithExceptionDetails(final String title, final Throwable cause) {
        LOGGER.info("Error is reported with a notification (title {})", title, cause);
        if (this.displayedErrorNotificationCount < MAX_ERROR_DISPLAY_COUNT) {
            this.displayedErrorNotificationCount++;
            LCNotificationController.INSTANCE
                    .showNotification(LCNotification.createError(title, "notification.error.details.action", () -> this.showExceptionDialog(cause)));
        }
    }

    /**
     * Create and show a exception dialog for the given action
     *
     * @param cause the fail cause
     */
    public void showExceptionDialog(final Throwable cause) {
        LOGGER.warn("A error is reported with a exception dialog ", cause);
        LCUtils.runOnFXThread(() -> {
            if (this.displayedErrorDialogCount < MAX_ERROR_DISPLAY_COUNT) {
                this.displayedErrorDialogCount++;
                if (cause instanceof LCException) {
                    LCException lcE = (LCException) cause;
                    Alert dlg = ConfigUIUtils.createAlert(StageUtils.getEditOrUseStageVisible(), Alert.AlertType.ERROR);
                    dlg.getDialogPane().setContentText(lcE.getUserMessage());
                    dlg.getDialogPane().setHeaderText(lcE.getUserHeader() != null ? lcE.getUserHeader() : Translation.getText("exception.dialog.generic.error.header"));
                    dlg.setOnHidden(DECREASE_ERROR_DIALOG_COUNT_HANDLER);
                    dlg.show();
                } else {
                    ExceptionDialog dialog = new ExceptionDialog(cause);
                    dialog.initOwner(StageUtils.getEditOrUseStageVisible());
                    dialog.titleProperty().set(Translation.getText("unknown.error.on.fx.thread.title"));
                    dialog.setHeaderText(Translation.getText("unknown.error.on.fx.thread.message"));
                    dialog.setOnHidden(DECREASE_ERROR_DIALOG_COUNT_HANDLER);
                    dialog.show();
                }
            } else {
                LOGGER.warn("Error wasn't displayed in error dialog because it already has {} error dialog displaying", displayedErrorDialogCount);
            }
        });
    }

    public void errorNotificationHidden() {
        this.displayedErrorNotificationCount--;
    }
    //========================================================================

}
