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

package org.lifecompanion.framework.server.controller.handler;

import static org.lifecompanion.framework.server.service.JsonService.toJson;

import org.lifecompanion.framework.model.server.dto.ErrorDto;
import org.lifecompanion.framework.model.server.error.BusinessLogicError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

import spark.ExceptionHandler;
import spark.Route;

public class ErrorHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

	public static final ExceptionHandler<Exception> baseExceptionHandler = (exception, request, response) -> {
		if (exception instanceof BusinessLogicException) {
			BusinessLogicException ble = (BusinessLogicException) exception;
			response.status(ble.getHttpErrorCode() > 0 ? ble.getHttpErrorCode() : 500);
			response.body(toJson(ErrorDto.create(ble.getError())));
		} else if (exception instanceof JsonSyntaxException) {
			LOGGER.warn("Incorrect json", exception);
			response.status(400);
			response.body(toJson(ErrorDto.create(BusinessLogicError.INCORRECT_REQUEST_FORMAT)));
		} else {
			LOGGER.error("Exception thrown and not handled", exception);
			response.status(500);
			response.body(toJson(ErrorDto.create(exception)));
		}
	};

	public static final Route internalErrorHandler = (request, response) -> {
		response.status(500);
		LOGGER.error("500 ERROR NOT HANDLED!");
		return "500 error";
	};

	public static final Route notFoundHandler = (request, response) -> {
		response.status(404);
		LOGGER.error("404 ERROR NOT HANDLED!");
		return "404 error";
	};
}
