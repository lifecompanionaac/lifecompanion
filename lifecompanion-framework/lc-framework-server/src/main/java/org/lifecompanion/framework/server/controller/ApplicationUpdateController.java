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

package org.lifecompanion.framework.server.controller;

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.model.server.dto.AddApplicationUpdateStatDto;
import org.lifecompanion.framework.model.server.dto.FinishApplicationUpdateDto;
import org.lifecompanion.framework.model.server.dto.InitializeApplicationUpdateDto;
import org.lifecompanion.framework.model.server.dto.UploadUpdateFileDto;
import org.lifecompanion.framework.server.service.FileStorageService;
import org.lifecompanion.framework.server.service.model.ApplicationUpdateService;
import spark.Route;

import java.io.InputStream;
import java.io.OutputStream;

import static org.lifecompanion.framework.server.service.JsonService.fromJson;
import static org.lifecompanion.framework.server.service.JsonService.toJson;

public class ApplicationUpdateController {
    public static final Route initialize = (request, response) -> toJson(ApplicationUpdateService.INSTANCE.initializeApplicationUpdate(fromJson(request.body(), InitializeApplicationUpdateDto.class)));

    public static final Route uploadFile = (request, response) -> {
        MultipartUtils.PartHolder parts = MultipartUtils.getDtoAndFileInputStream(request);
        try (InputStream is = parts.fileIS) {
            return ApplicationUpdateService.INSTANCE.uploadApplicationUpdateFile(
                    fromJson(parts.dto, UploadUpdateFileDto.class), is);
        }
    };

    public static final Route finish = (request, response) ->
            ApplicationUpdateService.INSTANCE.finishApplicationUpdate(fromJson(request.body(), FinishApplicationUpdateDto.class));

    public static final Route getLastApplicationUpdateDiffOld = (request, response) -> toJson(ApplicationUpdateService.INSTANCE.getLastApplicationUpdateDiffOld(request.params("application"),
            SystemType.valueOf(request.params("system")), request.params("fromVersion"), Boolean.parseBoolean(request.params("preview"))));

    public static final Route getLastApplicationUpdateDiff = (request, response) -> toJson(ApplicationUpdateService.INSTANCE.getLastApplicationUpdateDiff(request.params("application"),
            SystemType.valueOf(request.params("system")), request.params("fromVersion"), Boolean.parseBoolean(request.params("preview"))));

    public static final Route getApplicationUpdateDiff = (request, response) -> toJson(ApplicationUpdateService.INSTANCE.getApplicationUpdateDiff(request.params("application"),
            SystemType.valueOf(request.params("system")), request.params("fromVersion"), null, request.params("maxVersion"), Boolean.parseBoolean(request.params("preview"))));


    public static final Route getApplicationFileDownloadUrl = (request, response) -> ApplicationUpdateService.INSTANCE.getApplicationFileDownloadUrl(request.params("id"));

    public static final Route getLastApplicationUpdateOld = (request, response) -> toJson(ApplicationUpdateService.INSTANCE.getLastApplicationUpdateOld(request.params("application"), Boolean.parseBoolean(request.params("preview"))));

    public static final Route getLastApplicationUpdate = (request, response) -> toJson(ApplicationUpdateService.INSTANCE.getLastApplicationUpdate(request.params("application"), Boolean.parseBoolean(request.params("preview"))));

    public static final Route cleanPreviousUpdates = (request, response) -> toJson(ApplicationUpdateService.INSTANCE.cleanPreviousUpdates(request.params("application")));

    public static Route downloadFile = (request, response) -> {
        try (OutputStream os = response.raw().getOutputStream()) {
            return FileStorageService.INSTANCE.downloadFileTo(request.splat()[0], os);
        }
    };

    public static Route addUpdateDoneStat = (request, response) -> {
        ApplicationUpdateService.INSTANCE.addUpdateDoneStat(request, fromJson(request.body(), AddApplicationUpdateStatDto.class));
        return toJson(null);
    };


    public static final Route deleteUpdate = (request, response) -> ApplicationUpdateService.INSTANCE.deleteUpdate(request.params("id"));
}
