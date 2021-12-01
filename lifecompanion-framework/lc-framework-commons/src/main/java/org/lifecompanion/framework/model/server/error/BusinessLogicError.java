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

package org.lifecompanion.framework.model.server.error;

public enum BusinessLogicError {
    INCORRECT_LOGIN_PASSWORD("Incorrect login or password"), //
    INCORRECT_REQUEST_FORMAT("Incorrect request format, please use correct json input"), //
    TOKEN_EXPIRED("Token has expired, please log again"), //
    TOKEN_ERROR("Incorrect authentication token"), //
    AUTHENTICATION_NEEDED("Authentication is required for this service"), //
    RIGHTS_NEEDED("Higher role is required for this service"), //
    FILE_SAVE_FAILED("File save failed, check Amazon S3 connection"), //
    EXISTING_UPDATE_SAME_VERSION_SYSTEM("Existing update with the same version, system and modifiers"), //
    EXISTING_PLUGIN_UPDATE_SAME_VERSION("Existing plugin update with the same version"), //
    ;

    private final String message;

    public String getMessage() {
        return message;
    }

    private BusinessLogicError(String message) {
        this.message = message;
    }
}
