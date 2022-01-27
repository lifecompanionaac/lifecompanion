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

package org.lifecompanion.base.data.control.refacto;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.base.data.dev.LogEntry;

public enum DevModeController {
    INSTANCE;

    /**
     * List of all log entries (only enabled if dev mode is enabled)
     */
    private final ObservableList<LogEntry> logEntries;

    /**
     * To disable log (disable append to {@link #logEntries} and clear the entry list)
     */
    private boolean disableLog;

    private final BooleanProperty devMode;

    DevModeController() {
        devMode = new SimpleBooleanProperty(false);
        this.logEntries = FXCollections.observableArrayList();
    }

    public BooleanProperty devModeProperty() {
        return this.devMode;
    }

    public boolean isDevMode() {
        return devMode.get();
    }

    public void appendLog(final LogEntry logEntry) {
        if (!this.disableLog) {
            this.logEntries.add(logEntry);
        }
    }

    public ObservableList<LogEntry> getLogEntries() {
        return this.logEntries;
    }

    public boolean isDisableLog() {
        return this.disableLog;
    }

    public void disableLog() {
        this.logEntries.clear();
        this.disableLog = true;
    }
}
