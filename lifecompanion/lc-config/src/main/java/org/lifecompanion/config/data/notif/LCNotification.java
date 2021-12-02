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
package org.lifecompanion.config.data.notif;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.framework.commons.translation.Translation;

public class LCNotification {
    private final StringProperty title;
    private final LCNotificationType type;
    private long msDuration;
    private boolean automaticClose;
    private final String actionButtonName;
    private final Runnable action;
    private final Task<?> task;
    private final boolean blocking;

    private LCNotification(Task<?> task, String title, LCNotificationType type, long msDuration, boolean automaticClose, final boolean blocking, String actionButtonName, Runnable action) {
        super();
        this.title = new SimpleStringProperty(title);
        if (task != null) this.title.bind(task.titleProperty());
        this.action = action;
        this.actionButtonName = actionButtonName;
        this.type = type;
        this.msDuration = msDuration;
        this.automaticClose = automaticClose;
        this.blocking = blocking;
        this.task = task;
    }

    private LCNotification(Task<?> task, String title, LCNotificationType type, long msDuration, boolean automaticClose, final boolean blocking) {
        this(task, title, type, msDuration, automaticClose, blocking, null, null);
    }

    public ReadOnlyStringProperty titleProperty() {
        return title;
    }

    public String getTitle() {
        return title.get();
    }

    public String getActionButtonName() {
        return actionButtonName;
    }

    public LCNotificationType getType() {
        return type;
    }

    public void setMsDuration(long msDuration) {
        this.msDuration = msDuration;
    }

    public LCNotification withMsDuration(long msDuration) {
        setMsDuration(msDuration);
        return this;
    }

    public void setAutomaticClose(boolean automaticClose) {
        this.automaticClose = automaticClose;
    }

    public long getMsDuration() {
        return msDuration;
    }

    public Runnable getAction() {
        return action;
    }

    public boolean isAutomaticClose() {
        return automaticClose;
    }

    public Task<?> getTask() {
        return task;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public enum LCNotificationType {
        INFO, ERROR, WARNING, TASK;
    }

    public static LCNotification createTask(Task<?> task, boolean blocking) {
        return new LCNotification(task, null, LCNotificationType.TASK, LCGraphicStyle.SHORT_NOTIFICATION_DURATION_MS, false, blocking);
    }

    public static LCNotification createInfo(String titleId) {
        return new LCNotification(null, Translation.getText(titleId), LCNotificationType.INFO, LCGraphicStyle.MEDIUM_NOTIFICATION_DURATION_MS, true, false);
    }

    public static LCNotification createInfo(String titleId, boolean automaticClose) {
        return new LCNotification(null, Translation.getText(titleId), LCNotificationType.INFO, LCGraphicStyle.MEDIUM_NOTIFICATION_DURATION_MS, automaticClose, false);
    }

    public static LCNotification createInfo(String titleId, boolean automaticClose, String actionNameId, Runnable action) {
        return new LCNotification(null, Translation.getText(titleId), LCNotificationType.INFO, LCGraphicStyle.MEDIUM_NOTIFICATION_DURATION_MS, automaticClose, false, Translation.getText(actionNameId), action);
    }

    public static LCNotification createWarning(String titleId) {
        return new LCNotification(null, Translation.getText(titleId), LCNotificationType.WARNING, LCGraphicStyle.MEDIUM_NOTIFICATION_DURATION_MS, true, false);
    }

    public static LCNotification createWarning(String titleId, String actionNameId, Runnable action) {
        return new LCNotification(null, Translation.getText(titleId), LCNotificationType.WARNING, LCGraphicStyle.MEDIUM_NOTIFICATION_DURATION_MS, true, false, Translation.getText(actionNameId), action);
    }

    public static LCNotification createError(String title, String actionNameId, Runnable action) {
        return new LCNotification(null, title, LCNotificationType.ERROR, LCGraphicStyle.MEDIUM_NOTIFICATION_DURATION_MS, false, false, Translation.getText(actionNameId), action);
    }

    public static LCNotification createError(String title) {
        return new LCNotification(null, title, LCNotificationType.ERROR, LCGraphicStyle.MEDIUM_NOTIFICATION_DURATION_MS, false, false);
    }
}
