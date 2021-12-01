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

package org.lifecompanion.base.data.dev;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.scene.paint.Color;

public class LogEntry {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
	private final long timestamp;
	private final String threadName;
	private final String loggerName;
	private final String message;
	private final LogLevel level;
	private String cachedFormattedMessage;

	public LogEntry(final long timestamp, final String threadName, final String loggerName, final String message, final LogLevel level) {
		super();
		this.timestamp = timestamp;
		this.threadName = threadName;
		this.loggerName = loggerName;
		this.message = message;
		this.level = level;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public String getThreadName() {
		return this.threadName;
	}

	public String getLoggerName() {
		return this.loggerName;
	}

	public String getMessage() {
		return this.message;
	}

	public LogLevel getLevel() {
		return this.level;
	}

	public static enum LogLevel {
		TRACE(Color.GRAY), DEBUG(Color.DARKGRAY), INFO(Color.BLACK), WARN(Color.ORANGE), ERROR(Color.RED);
		private Color color;

		private LogLevel(final Color color) {
			this.color = color;
		}

		public Color getColor() {
			return this.color;
		}
	}

	public String getFormattedMessage() {
		if (this.cachedFormattedMessage == null) {
			this.cachedFormattedMessage = "[" + LogEntry.DATE_FORMAT.format(new Date(this.getTimestamp())) + "][" + this.getThreadName() + "]["
					+ this.getLoggerName() + "] " + this.getMessage();
		}
		return this.cachedFormattedMessage;
	}
}
