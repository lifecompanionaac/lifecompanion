/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

import org.lifecompanion.framework.server.service.ImageResourceAPIService;
import spark.Route;

public class ImageResourceAPIController {

    public static final Route getImageDownloadUrl = (req, res) -> {
        String downloadUrl = ImageResourceAPIService.INSTANCE.getImageDownloadUrl(req.params("imageId").trim());
        if (downloadUrl != null) {
            return downloadUrl;
        } else {
            res.status(404);
            return "";
        }
    };
}
