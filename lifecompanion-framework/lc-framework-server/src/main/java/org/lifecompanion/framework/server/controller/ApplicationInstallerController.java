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
import org.lifecompanion.framework.commons.utils.lang.LangUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.server.update.ApplicationInstaller;
import org.lifecompanion.framework.server.service.FileStorageService;
import org.lifecompanion.framework.server.service.SoftwareStatService;
import org.lifecompanion.framework.server.service.model.ApplicationInstallerService;
import spark.Route;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lifecompanion.framework.server.service.JsonService.fromJson;
import static org.lifecompanion.framework.server.service.JsonService.toJson;

public class ApplicationInstallerController {

    public static final Route create = (request, response) -> {
        MultipartUtils.PartHolder parts = MultipartUtils.getDtoAndFileInputStream(request);
        try (InputStream is = parts.fileIS) {
            return toJson(ApplicationInstallerService.INSTANCE.createUpdate(
                    fromJson(parts.dto, ApplicationInstaller.class), is));
        }
    };

    public static final Route downloadFromWeb = (request, response) -> {
        String system = request.params("system") != null ? request.params("system").trim().toUpperCase() : "";
        final String pluginIdsQueryParam = request.queryParams("plugins");
        List<String> pluginIds = new ArrayList<>();
        if (StringUtils.isNotBlank(pluginIdsQueryParam)) {
            pluginIds.addAll(Arrays.asList(pluginIdsQueryParam.split(",")));
        }
        final SystemType systemType = SystemType.valueOf(StringUtils.toUpperCase(system));
        ApplicationInstaller lastInstallerFor = ApplicationInstallerService.INSTANCE.getLastInstallerFor(request.params("application"), systemType, Boolean.parseBoolean(LangUtils.getOr(request.params("preview"), "false")));
        if (lastInstallerFor != null) {
            SoftwareStatService.INSTANCE.pushStat(request, SoftwareStatService.StatEvent.INSTALLER_DOWNLOAD, lastInstallerFor.getVersion(), systemType);
            response.header("Content-disposition", "attachment; filename=" + ApplicationInstallerService.INSTANCE.getApplicationInstallerName(lastInstallerFor, false, pluginIds).replace(";", "%3B"));
            try (OutputStream os = response.raw().getOutputStream()) {
                FileStorageService.INSTANCE.downloadFileTo(lastInstallerFor.getFileStorageId(), os);
            }
        } else {
            return toJson(null);
        }
        return "ok";
    };
}
