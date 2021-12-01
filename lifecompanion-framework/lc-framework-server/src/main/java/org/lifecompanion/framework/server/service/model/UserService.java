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

package org.lifecompanion.framework.server.service.model;

import org.lifecompanion.framework.model.server.dto.UserLoginRequestDto;
import org.lifecompanion.framework.model.server.dto.UserLoginResponseDto;
import org.lifecompanion.framework.model.server.error.BusinessLogicError;
import org.lifecompanion.framework.model.server.user.User;
import org.lifecompanion.framework.server.controller.handler.BusinessLogicException;
import org.lifecompanion.framework.server.data.dao.UserDao;
import org.lifecompanion.framework.server.service.AuthenticationService;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum UserService {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public UserLoginResponseDto login(UserLoginRequestDto loginRequest) {
        LOGGER.info("Got a login request : {}", loginRequest.getLogin());
        User user = UserDao.INSTANCE.getUser(loginRequest.getLogin());
        if (user != null && BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
            return new UserLoginResponseDto(AuthenticationService.INSTANCE.getJWTTokenFor(user));
        } else {
            throw new BusinessLogicException(BusinessLogicError.INCORRECT_LOGIN_PASSWORD);
        }
    }
}
