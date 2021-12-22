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

package org.lifecompanion.framework.server;

import org.lifecompanion.framework.server.controller.*;
import org.lifecompanion.framework.server.controller.filters.Filters;
import org.lifecompanion.framework.server.controller.handler.ErrorHandler;
import org.lifecompanion.framework.server.data.dao.DataSource;
import org.lifecompanion.framework.server.service.SoftwareStatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.io.InputStream;
import java.util.Properties;

import static spark.Spark.*;

/**
 * LifeCompanion framework server : useful to manage application update, installer, etc.<br>
 *
 * @author Mathieu THEBAUD
 */
public class LifeCompanionFrameworkServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(LifeCompanionFrameworkServer.class);
    public static boolean ONLINE = false;

    public static boolean DELETE_PREVIOUS_FILE_ON_UPDATE = false;

    public static void main(String[] args) {
        String serverVersion = "unknown";
        try (InputStream is = LifeCompanionFrameworkServer.class.getResourceAsStream("/server.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            serverVersion = properties.getProperty("serverVersion");
        } catch (Exception e) {
            LOGGER.warn("Can't read server properties", e);
        }
        final String serverVersionF = serverVersion;

        // SERVER CONFIG
        if (args.length > 0) {
            try {
                port(Integer.parseInt(args[0]));
                LOGGER.info("Running server in production mode on port {}", args[0]);
                ONLINE = true;
            } catch (NumberFormatException nfe) {
                LOGGER.warn("Couldn't parse port arg", nfe);
            }
        }
        if (!ONLINE) {
            int port = 1234;
            String portFromProp = System.getProperty("org.lifecompanion.framework.server.dev.port");
            try {
                port = Integer.parseInt(portFromProp);
            } catch (NumberFormatException ignored) {
            }
            LOGGER.info("Running server in dev mode on port " + port);
            port(port);
        }

        // ERROR HANDLER
        exception(Exception.class, ErrorHandler.baseExceptionHandler);
        notFound(ErrorHandler.notFoundHandler);
        internalServerError(ErrorHandler.internalErrorHandler);

        // GLOBAL
        before(Filters.addCurrentUser);

        // ROBOTS.TXT
        get("/robots.txt", ServerController.robots);

        // PUBLIC
        path("/public", () -> {
            get("/wakeup", ServerController.wakeup);
            get("/blank-image.png", ServerController.blankImage);
            get("/version", (req, res) -> serverVersionF);
            post("/login", PublicUserController.login);

            get("/installer/:application/:system", ApplicationInstallerController.downloadFromWeb);
            get("/installer/:application/:system/:preview", ApplicationInstallerController.downloadFromWeb);

            get("/get-last-application-update/:application/:preview", ApplicationUpdateController.getLastApplicationUpdate);
            get("/get-last-update-diff/:application/:system/:fromVersion/:preview", ApplicationUpdateController.getLastApplicationUpdateDiff);
            get("/get-application-file-url/:id", ApplicationUpdateController.getApplicationFileDownloadUrl);
            get("/download-file/*", ApplicationUpdateController.downloadFile); // this URL is only useful with a local file storage service
            post("/add-update-stat", ApplicationUpdateController.addUpdateDoneStat);

            get("/get-last-launcher-update/:application/:system/:preview", ApplicationLauncherUpdateController.getLastLauncherUpdate);
            get("/get-launcher-file-url/:id", ApplicationLauncherUpdateController.getLauncherFileDownloadUrl);

            get("/get-plugin-updates-order-by-version/:pluginId/:preview", ApplicationPluginUpdateController.getPluginUpdatesOrderByVersion);
            get("/get-plugin-update-file-url/:id", ApplicationPluginUpdateController.getPluginUpdateDownloadUrl);
        });

        // ADMIN API
        path("/api/admin", () -> {
            before("/*", Filters.checkAdminUser);

            path("/installer", () -> {
                post("/create-update", ApplicationInstallerController.create);
            });

            path("/launcher", () -> {
                post("/create-update", ApplicationLauncherUpdateController.create);
            });

            path("/application-update", () -> {
                post("/initialize-update", ApplicationUpdateController.initialize);
                post("/upload-file", ApplicationUpdateController.uploadFile);
                post("/finish-update", ApplicationUpdateController.finish);
                post("/delete-update/:id", ApplicationUpdateController.deleteUpdate);
                post("/clean-previous-update/:application", ApplicationUpdateController.cleanPreviousUpdates);
            });

            path("/application-plugin", () -> {
                post("/create-update", ApplicationPluginUpdateController.create);
            });
        });

        // AFTER
        after(Filters.typeJson);
        after(Filters.robotsTag);

        // DATABASE
        DataSource.INSTANCE.checkDatabaseMigrations();

        // STATS
        SoftwareStatService.INSTANCE.startPushStatsBackgroundThread();

        LOGGER.info("LifeCompanion framework started on port {}, version = {}, online = {}", Spark.port(), serverVersion, ONLINE);

    }

}
