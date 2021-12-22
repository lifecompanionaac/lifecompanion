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

package org.lifecompanion.framework.server.controller.filters;

import org.lifecompanion.framework.model.server.error.BusinessLogicError;
import org.lifecompanion.framework.model.server.user.AuthenticatedUserDetail;
import org.lifecompanion.framework.model.server.user.UserRole;
import org.lifecompanion.framework.server.controller.handler.BusinessLogicException;
import org.lifecompanion.framework.server.service.AuthenticationService;
import spark.Filter;

public class Filters {
    public static final Filter addCurrentUser = (request, response) -> {
        AuthenticationService.INSTANCE.addUserInRequest(request);
    };

    public static final Filter checkBaseUser = (request, response) -> {
        AuthenticatedUserDetail userDetail = request.attribute("user");
        if (userDetail == null || userDetail.getRole() == null) {
            throw new BusinessLogicException(BusinessLogicError.AUTHENTICATION_NEEDED, 401);
        }
    };

    public static final Filter checkAdminUser = (request, response) -> {
        AuthenticatedUserDetail userDetail = request.attribute("user");
        if (userDetail != null && userDetail.getRole() != null) {
            if (userDetail.getRole() != UserRole.ADMIN) {
                throw new BusinessLogicException(BusinessLogicError.RIGHTS_NEEDED, 403);
            }
        } else {
            throw new BusinessLogicException(BusinessLogicError.AUTHENTICATION_NEEDED, 401);
        }
    };

    public static final Filter typeJson = (request, response) -> {
        response.type("application/json");
    };

    public static final Filter robotsTag = (request, response) -> {
        response.header("X-Robots-Tag", "none");
    };
}
