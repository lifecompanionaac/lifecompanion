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

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.ui.common.control.generic.dialog.ExceptionAlert;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.util.javafx.StageUtils;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.ui.notification.LCNotificationController;
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

    private final Runnable DECREASE_ERROR_DIALOG_COUNT_HANDLER = () -> displayedErrorDialogCount--;

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

    /**
     * Display an error notification that will contain a button allowing the user to see the error detail
     *
     * @param title the notification title
     * @param cause the error cause that will be used for details
     */
    public void showErrorNotificationWithExceptionDetails(final String title, final Throwable cause) {
        LOGGER.info("Error is reported with a notification (title {})", title, cause);
        if (this.displayedErrorNotificationCount < MAX_ERROR_DISPLAY_COUNT) {
            this.displayedErrorNotificationCount++;
            if (cause instanceof LCException && ((LCException) cause).containsOnCatchAction()) {
                FXThreadUtils.runOnFXThread(((LCException) cause).getOnCatchCallback());
            } else {
                LCNotificationController.INSTANCE
                        .showNotification(LCNotification.createError(title, "notification.error.details.action", () -> this.showExceptionDialog(cause)));
            }
        }
    }

    /**
     * Create and show a exception dialog for the given action
     *
     * @param cause the fail cause
     */
    public void showExceptionDialog(final Throwable cause) {
        LOGGER.warn("A error is reported with a exception dialog ", cause);
        FXThreadUtils.runOnFXThread(() -> {
            if (this.displayedErrorDialogCount < MAX_ERROR_DISPLAY_COUNT) {
                this.displayedErrorDialogCount++;
                if (cause instanceof LCException) {
                    LCException lcE = (LCException) cause;
                    DialogUtils
                            .alertWithSourceAndType(StageUtils.getEditOrUseStageVisible(), Alert.AlertType.ERROR)
                            .withContentText(lcE.getUserMessage())
                            .withHeaderText(lcE.getUserHeader() != null ? lcE.getUserHeader() : Translation.getText("exception.dialog.generic.error.header"))
                            .withOnHidden(DECREASE_ERROR_DIALOG_COUNT_HANDLER)
                            .show();
                } else {
                    // TODO : call apply configuration from DialogUtils (and create custom Exception Dialog)
                    ExceptionAlert dialog = new ExceptionAlert(cause);
                    dialog.initOwner(StageUtils.getEditOrUseStageVisible());
                    dialog.setTitle(Translation.getText("unknown.error.on.fx.thread.title"));
                    dialog.setHeaderText(Translation.getText("unknown.error.on.fx.thread.message"));
                    dialog.setOnHidden(e -> DECREASE_ERROR_DIALOG_COUNT_HANDLER.run());
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
