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

package org.lifecompanion.framework.client.http;

import org.lifecompanion.framework.model.server.dto.ErrorDto;

public class ServerBusinessException extends ApiException {
	private static final long serialVersionUID = 1L;
	private final long timestamp;
	private final String errorCode;
	private final String errorMessage;

	public ServerBusinessException(long timestamp, String errorCode, String errorMessage) {
		super("[" + timestamp + "] " + errorCode + " : " + errorMessage);
		this.timestamp = timestamp;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public ServerBusinessException(ErrorDto errorDto) {
		this(errorDto.getTimestamp(), errorDto.getErrorCode(), errorDto.getErrorMessage());
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
}
