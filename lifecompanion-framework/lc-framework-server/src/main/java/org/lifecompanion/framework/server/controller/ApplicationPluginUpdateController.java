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

import org.lifecompanion.framework.model.server.dto.CreateApplicationPluginUpdate;
import org.lifecompanion.framework.server.service.model.ApplicationPluginUpdateService;
import spark.Route;

import java.io.InputStream;

import static org.lifecompanion.framework.server.service.JsonService.fromJson;
import static org.lifecompanion.framework.server.service.JsonService.toJson;

public class ApplicationPluginUpdateController {

    public static final Route create = (request, response) -> {
        MultipartUtils.PartHolder parts = MultipartUtils.getDtoAndFileInputStream(request);
        try (InputStream is = parts.fileIS) {
            return toJson(ApplicationPluginUpdateService.INSTANCE.createUpdate(fromJson(parts.dto, CreateApplicationPluginUpdate.class), is));
        }
    };

    public static final Route getPluginUpdateDownloadUrl = (request, response) -> ApplicationPluginUpdateService.INSTANCE.getPluginUpdateDownloadUrl(request, request.params("id"));

    public static final Route getPluginUpdatesOrderByVersion = (request, response) -> toJson(ApplicationPluginUpdateService.INSTANCE.getLastPluginUpdate(request.params("pluginId"), Boolean.parseBoolean(request.params("preview"))));

    public static final Route getPluginUpdates = (request, response) -> toJson(ApplicationPluginUpdateService.INSTANCE.getPluginUpdates(request.params("pluginId"), Boolean.parseBoolean(request.params("preview"))));
}
