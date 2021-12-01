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

package org.lifecompanion.framework.server.service;

import java.util.Date;

import org.lifecompanion.framework.model.server.LifeCompanionFrameworkServerConstant;
import org.lifecompanion.framework.model.server.error.BusinessLogicError;
import org.lifecompanion.framework.model.server.user.AuthenticatedUserDetail;
import org.lifecompanion.framework.model.server.user.User;
import org.lifecompanion.framework.model.server.user.UserRole;
import org.lifecompanion.framework.server.LifeCompanionFrameworkServer;
import org.lifecompanion.framework.server.controller.handler.BusinessLogicException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import spark.Request;
import spark.utils.StringUtils;

public enum AuthenticationService {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    private static final long EXPIRE_DELAY = 1000 * 3600 * 1;// 1 hours auth token

    private Algorithm jwtAlgorithm;

    AuthenticationService() {
        String JWT_SECRET;
        if (LifeCompanionFrameworkServer.ONLINE) {
            JWT_SECRET = System.getenv("JWT_SECRET");
            LOGGER.info("Auth service initialized with prod JWT_SECRET from env");
        } else {
            JWT_SECRET = "devjwtsecret";
            LOGGER.info("Auth service initialized with dev");
        }
        try {
            jwtAlgorithm = Algorithm.HMAC256(JWT_SECRET);
        } catch (Exception e) {
            LOGGER.error("Couldn't initialize JWT Algo", e);
            throw new RuntimeException(e);
        }
    }

    public String getJWTTokenFor(User user) {
        Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_DELAY);
        LOGGER.info("User {} logged in, expire date : {}", user.getLogin(), expiresAt);
        return JWT.create().withExpiresAt(expiresAt).withClaim("login", user.getLogin()).withClaim("role", user.getRole().name()).sign(jwtAlgorithm);
    }

    /**
     * Fill the request attribute "user" with the authenticated user.</br>
     * This method is non blocking : if there is no "Authorization: Bearer" in the request, will not throw any Exception.</br>
     * Will only throw exception if there is a token but the token is not correct.</br>
     * This is a simple check : this will not increase the validity duration for the given token and return a new token.
     *
     * @param request server request
     * @throws if there is a incorrect token
     */
    public void addUserInRequest(Request request) throws BusinessLogicException {
        String authHeader = request.headers(LifeCompanionFrameworkServerConstant.AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(authHeader)) {
            String[] headerParts = authHeader.split(" ");
            if (headerParts.length == 2 && LifeCompanionFrameworkServerConstant.AUTHORIZATION_HEADER_VALUE_PREFIX.equals(headerParts[0])) {
                String token = headerParts[1];
                try {
                    DecodedJWT checkedToken = JWT.require(jwtAlgorithm).build().verify(token);
                    request.attribute("user", new AuthenticatedUserDetail(checkedToken.getClaim("login").asString(),
                            UserRole.valueOf(checkedToken.getClaim("role").asString())));
                } catch (TokenExpiredException tokenExpired) {
                    LOGGER.warn("Request token expired", tokenExpired);
                    throw new BusinessLogicException(BusinessLogicError.TOKEN_EXPIRED);
                } catch (Exception e) {
                    LOGGER.warn("Token verification failed", e);
                    throw new BusinessLogicException(BusinessLogicError.TOKEN_ERROR);
                }
            } else {
                LOGGER.info("Authorization header present but not in the correct format : {}", authHeader);
            }
        }
    }
}
