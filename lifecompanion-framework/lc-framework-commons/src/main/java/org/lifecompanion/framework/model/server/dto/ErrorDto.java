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

package org.lifecompanion.framework.model.server.dto;

import org.lifecompanion.framework.model.server.error.BusinessLogicError;

public class ErrorDto {
	private final long timestamp;
	private final String errorCode;
	private final String errorMessage;

	private ErrorDto(long timestamp, String errorCode, String errorMessage) {
		super();
		this.timestamp = timestamp;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public ErrorDto() {
		this(-1, null, null);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public static ErrorDto create(Throwable t) {
		return new ErrorDto(System.currentTimeMillis(), t.getClass().getSimpleName(), t.getMessage());
	}

	public static ErrorDto create(String code, String message) {
		return new ErrorDto(System.currentTimeMillis(), code, message);
	}

	public static ErrorDto create(BusinessLogicError error) {
		return create(error.name(), error.getMessage());
	}
}
