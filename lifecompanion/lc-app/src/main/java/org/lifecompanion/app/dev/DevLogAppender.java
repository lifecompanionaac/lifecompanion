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
package org.lifecompanion.app.dev;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.lifecompanion.base.data.control.refacto.DevModeController;
import org.lifecompanion.base.data.dev.LogEntry;

import java.util.HashMap;
import java.util.Map;


public class DevLogAppender extends AppenderBase<ILoggingEvent> {
    private static final Map<Level, LogEntry.LogLevel> LEVELS = new HashMap<>();

    static {
        LEVELS.put(Level.TRACE, LogEntry.LogLevel.TRACE);
        LEVELS.put(Level.DEBUG, LogEntry.LogLevel.DEBUG);
        LEVELS.put(Level.INFO, LogEntry.LogLevel.INFO);
        LEVELS.put(Level.WARN, LogEntry.LogLevel.WARN);
        LEVELS.put(Level.ERROR, LogEntry.LogLevel.ERROR);
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!DevModeController.INSTANCE.isDisableLog()) {
            DevModeController.INSTANCE.appendLog(new LogEntry(eventObject.getTimeStamp(), eventObject.getThreadName(),
                    convertLoggerName(eventObject.getLoggerName()), eventObject.getFormattedMessage(), convertLevel(eventObject.getLevel())));
        }
    }

    private String convertLoggerName(String loggerName) {
        if (loggerName != null && loggerName.contains(".")) {
            String[] split = loggerName.split(".");
            return split[split.length - 1];
        } else {
            return loggerName;
        }
    }

    private LogEntry.LogLevel convertLevel(Level level) {
        return LEVELS.get(level);
    }
}
