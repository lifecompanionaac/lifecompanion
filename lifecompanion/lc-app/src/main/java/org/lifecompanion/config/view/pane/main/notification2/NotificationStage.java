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

package org.lifecompanion.config.view.pane.main.notification2;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Parent;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.util.StageUtils;
import org.lifecompanion.config.data.control.ErrorHandlingController;
import org.lifecompanion.config.data.notif.LCNotification;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static javafx.concurrent.Worker.State.*;

/**
 * Note that notification doesn't have an owner because we don't want the notification input to be block if the source window is not focused
 */
public class NotificationStage extends Stage {
    private final static AtomicInteger SESSION_NOTIFICATION_COUNT = new AtomicInteger(0);

    private static final double HEIGHT_ADDED = 30.0;

    private final Timeline timeLineStageShowing;
    private final Timeline timelineAutoClose;
    private final NotificationScene scene;

    private final Effect maskBackgroundEffect;
    private final EventHandler<Event> consumeEventFilter;

    NotificationStage(NotificationScene scene) {
        this.scene = scene;
        this.scene.setCloseRequestListener(this::hideWithAnimation);

        Stage sourceWindow = StageUtils.getEditOrUseStageVisible();
        this.setTitle(Translation.getText("notification.stage.win.title", SESSION_NOTIFICATION_COUNT.incrementAndGet()));
        this.initStyle(StageStyle.TRANSPARENT);
        this.setAlwaysOnTop(true);
        this.setScene(scene);

        // Size
        this.setResizable(false);
        this.setWidth(600.0);
        this.setHeight(40.0);
        this.setOpacity(0.0);

        // Place
        this.setX(sourceWindow.getX() + (sourceWindow.getWidth() / 2.0) - (this.getWidth() / 2.0));
        this.setY(sourceWindow.getY() + sourceWindow.getHeight() - this.getHeight() - HEIGHT_ADDED);

        // Show animation
        timeLineStageShowing = new Timeline();
        timeLineStageShowing.getKeyFrames().add(new KeyFrame(Duration.millis(500), new KeyValue(this.opacityProperty(), 0.8)));

        // Auto close
        this.timelineAutoClose = new Timeline(new KeyFrame(Duration.millis(scene.getNotification().getMsDuration()), e -> hideWithAnimation()));

        this.maskBackgroundEffect = getMaskBackgroundEffect();
        consumeEventFilter = Event::consume;
    }

    private void hideWithAnimation() {
        // Notify error controller on hide
        if (this.scene.getNotification().getType() == LCNotification.LCNotificationType.ERROR) {
            ErrorHandlingController.INSTANCE.errorNotificationHidden();
        }
        Timeline timeLineClosing = new Timeline();
        timeLineClosing.getKeyFrames().add(new KeyFrame(Duration.millis(300.0), new KeyValue(this.opacityProperty(), 0.0)));
        timeLineClosing.setOnFinished(e -> this.hide());
        timeLineClosing.play();
    }

    void show(List<LCNotificationController.DisplayedNotificationContainer> currentlyDisplayNotifications, Runnable onShowFinished) {
        // Animation to shift up displayed notification from their own size
        for (LCNotificationController.DisplayedNotificationContainer displayedNotification : currentlyDisplayNotifications) {
            timeLineStageShowing.getKeyFrames().add(new KeyFrame(Duration.millis(300), displayedNotification.getShiftUpKeyValue()));
        }
        timeLineStageShowing.setOnFinished(e -> onShowFinished.run());

        this.show();
        timeLineStageShowing.play();

        // Auto close for the notification
        if (scene.getNotification().isAutomaticClose()) {
            timelineAutoClose.play();
        }
        // Task notification : handling blocking and auto close on finished
        else if (scene.getNotification().getType() == LCNotification.LCNotificationType.TASK) {
            final Task<?> task = scene.getNotification().getTask();
            // If this is task not finished yet (and blocking) hide parent
            if (task.getState() != SUCCEEDED && task.getState() != FAILED && task.getState() != CANCELLED) {
                Set<Runnable> hiddenParents = new HashSet<>();
                // When task will be finished, should restore parent (if needed) and start autoclose
                task.stateProperty().addListener(inv -> {
                    if (task.getState() == SUCCEEDED || task.getState() == FAILED || task.getState() == CANCELLED) {
                        timelineAutoClose.play();
                        hiddenParents.forEach(Runnable::run);
                    }
                });
                // If task last more than 500 ms, should block scene for task
                if (scene.getNotification().isBlocking()) {
                    new Timeline(new KeyFrame(Duration.millis(LCGraphicStyle.BLOCKING_TASK_NOTIFICATION_DURATION_THRESHOLD), e -> {
                        if (task.getState() != SUCCEEDED && task.getState() != FAILED && task.getState() != CANCELLED) {
                            blockSceneForTask(hiddenParents);
                        }
                    })).play();
                }
            }
            // Task is finished, auto close
            else {
                timelineAutoClose.play();
            }
        }
    }

    private void blockSceneForTask(Set<Runnable> restoreRunnables) {
        if (scene.getNotification().isBlocking()) {
            final List<Window> windows = Stage.getWindows();
            for (Window window : windows) {
                if (window.isShowing() && !(window instanceof NotificationStage)) {
                    final Parent root = window.getScene().getRoot();
                    root.setEffect(maskBackgroundEffect);
                    root.addEventFilter(EventType.ROOT, consumeEventFilter);
                    restoreRunnables.add(() -> {
                        root.setEffect(null);
                        root.removeEventFilter(EventType.ROOT, consumeEventFilter);
                    });
                }
            }
        }
    }

    private Effect getMaskBackgroundEffect() {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.25);
        BoxBlur effect = new BoxBlur();
        effect.setInput(colorAdjust);
        effect.setWidth(10.0);
        effect.setHeight(10.0);
        effect.setIterations(3);
        return effect;
    }
}
