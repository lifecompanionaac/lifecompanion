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

import org.lifecompanion.framework.commons.utils.app.VersionUtils;
import org.lifecompanion.framework.model.server.dto.CreateApplicationPluginUpdate;
import org.lifecompanion.framework.model.server.error.BusinessLogicError;
import org.lifecompanion.framework.model.server.update.ApplicationPluginUpdate;
import org.lifecompanion.framework.model.server.update.UpdateVisibility;
import org.lifecompanion.framework.server.controller.handler.BusinessLogicException;
import org.lifecompanion.framework.server.data.dao.ApplicationPluginUpdateDao;
import org.lifecompanion.framework.server.data.dao.DataSource;
import org.lifecompanion.framework.server.service.FileStorageService;
import org.lifecompanion.framework.server.service.SoftwareStatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import spark.Request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public enum ApplicationPluginUpdateService {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationPluginUpdateService.class);

    public ApplicationPluginUpdate createUpdate(CreateApplicationPluginUpdate createApplicationPluginUpdate, InputStream installerFile) throws IOException {
        final ApplicationPluginUpdate applicationPluginUpdate = createApplicationPluginUpdate.getApplicationPluginUpdate();
        try (Connection connection = DataSource.INSTANCE.getSql2o().beginTransaction()) {
            // Check that there is no existing update for the same version
            if (ApplicationPluginUpdateDao.INSTANCE.countApplicationPluginUpdateByVersion(connection, createApplicationPluginUpdate.getApplicationPlugin().getId(), applicationPluginUpdate.getVersion()) > 0) {
                throw new BusinessLogicException(BusinessLogicError.EXISTING_UPDATE_SAME_VERSION_SYSTEM);
            }

            // Create or update the application plugin
            if (ApplicationPluginUpdateDao.INSTANCE.countApplicationPlugin(connection, createApplicationPluginUpdate.getApplicationPlugin().getId()) > 0) {
                ApplicationPluginUpdateDao.INSTANCE.updateApplicationPlugin(connection, createApplicationPluginUpdate.getApplicationPlugin());
            } else {
                ApplicationPluginUpdateDao.INSTANCE.insertApplicationPlugin(connection, createApplicationPluginUpdate.getApplicationPlugin());
            }

            UpdateVisibility targetVisibility = applicationPluginUpdate.getVisibility();

            // Initialize : insert and initialize information
            applicationPluginUpdate.setId(UUID.randomUUID().toString());
            applicationPluginUpdate.setUpdateDate(new Date());
            applicationPluginUpdate.setVisibility(UpdateVisibility.UPLOADING);
            VersionUtils.VersionInfo versionInfo = VersionUtils.VersionInfo.parse(applicationPluginUpdate.getVersion());
            applicationPluginUpdate.setVersionMajor(versionInfo.getMajor());
            applicationPluginUpdate.setVersionMinor(versionInfo.getMinor());
            applicationPluginUpdate.setVersionPatch(versionInfo.getPatch());
            ApplicationPluginUpdateDao.INSTANCE.insertApplicationPluginUpdate(connection, applicationPluginUpdate);

            // Upload file to storage
            String fileId = FileStorageService.INSTANCE.saveFile(installerFile,
                    "plugins/" + createApplicationPluginUpdate.getApplicationPlugin().getApplicationId() + "/" + applicationPluginUpdate.getApplicationPluginId() + "/" + applicationPluginUpdate.getVersion() + "/" + applicationPluginUpdate.getFileName(),
                    applicationPluginUpdate.getFileSize());

            // Once uploaded, update file in DB
            ApplicationPluginUpdateDao.INSTANCE.updateApplicationPluginUpdate(connection, applicationPluginUpdate.getId(), targetVisibility, fileId);

            connection.commit();
        }
        return applicationPluginUpdate;
    }

    public List<ApplicationPluginUpdate> getLastPluginUpdate(String pluginId, boolean preview) throws IOException {
        return ApplicationPluginUpdateDao.INSTANCE.getLastPluginUpdate(pluginId, preview);
    }

    public List<ApplicationPluginUpdate> getPluginUpdates(String pluginId, boolean preview) throws IOException {
        return ApplicationPluginUpdateDao.INSTANCE.getPluginUpdates(pluginId, preview);
    }

    public String getPluginUpdateDownloadUrl(Request request, String id) throws IOException {
        ApplicationPluginUpdate applicationPluginUpdate = ApplicationPluginUpdateDao.INSTANCE.getApplicationPluginUpdate(id);
        SoftwareStatService.INSTANCE.pushStat(request, SoftwareStatService.StatEvent.PLUGIN_UPDATE_DONE.code + applicationPluginUpdate.getApplicationPluginId(), applicationPluginUpdate.getVersion(), null);
        return FileStorageService.INSTANCE.generateFileUrl(applicationPluginUpdate.getFileStorageId(), applicationPluginUpdate.getFileName());
    }
}
