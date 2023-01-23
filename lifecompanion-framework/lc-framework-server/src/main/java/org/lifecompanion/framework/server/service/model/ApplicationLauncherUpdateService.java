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

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.app.VersionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.server.error.BusinessLogicError;
import org.lifecompanion.framework.model.server.update.ApplicationLauncherUpdate;
import org.lifecompanion.framework.model.server.update.UpdateVisibility;
import org.lifecompanion.framework.server.LifeCompanionFrameworkServer;
import org.lifecompanion.framework.server.controller.handler.BusinessLogicException;
import org.lifecompanion.framework.server.data.dao.ApplicationLauncherUpdateDao;
import org.lifecompanion.framework.server.data.dao.DataSource;
import org.lifecompanion.framework.server.service.FileStorageService;
import org.lifecompanion.framework.server.service.SoftwareStatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import spark.Request;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public enum ApplicationLauncherUpdateService {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLauncherUpdateService.class);

    public ApplicationLauncherUpdate createUpdate(ApplicationLauncherUpdate launcherUpdate, InputStream installerFile) throws IOException {
        try (Connection connection = DataSource.INSTANCE.getSql2o().beginTransaction()) {
            // Check that there is no existing update for the same version/system/systemModifier
            if (ApplicationLauncherUpdateDao.INSTANCE.countByVersionSystemSystemModifierApplicationId(connection, launcherUpdate.getVersion(), launcherUpdate.getSystem(),
                    launcherUpdate.getSystemModifier(), launcherUpdate.getApplicationId()) > 0) {
                throw new BusinessLogicException(BusinessLogicError.EXISTING_UPDATE_SAME_VERSION_SYSTEM);
            }

            UpdateVisibility targetVisibility = launcherUpdate.getVisibility();

            // Initialize : insert and initialize information
            launcherUpdate.setId(UUID.randomUUID().toString());
            launcherUpdate.setUpdateDate(new Date());
            launcherUpdate.setVisibility(UpdateVisibility.UPLOADING);
            VersionUtils.VersionInfo version = VersionUtils.VersionInfo.parse(launcherUpdate.getVersion());
            launcherUpdate.setVersionMajor(version.getMajor());
            launcherUpdate.setVersionMinor(version.getMinor());
            launcherUpdate.setVersionPatch(version.getPatch());
            ApplicationLauncherUpdateDao.INSTANCE.insert(connection, launcherUpdate);

            // Upload file to storage
            String fileId = FileStorageService.INSTANCE.saveFile(installerFile, getApplicationLauncherName(launcherUpdate), launcherUpdate.getFileSize());

            // Once uploaded, update file in DB
            ApplicationLauncherUpdateDao.INSTANCE.update(connection, launcherUpdate.getId(), targetVisibility, fileId);

            // Remove previous installer in storage (if this one is published)
            if (LifeCompanionFrameworkServer.DELETE_PREVIOUS_FILE_ON_UPDATE && targetVisibility == UpdateVisibility.PUBLISHED) {
                List<ApplicationLauncherUpdate> installersBeforeForSameSystem = ApplicationLauncherUpdateDao.INSTANCE.getLaunchersBellowForApplicationAndSystem(connection, launcherUpdate.getApplicationId(), launcherUpdate.getSystem(), launcherUpdate.getVersionMajor(), launcherUpdate.getVersionMinor(), launcherUpdate.getVersionPatch());
                for (ApplicationLauncherUpdate previousLauncher : installersBeforeForSameSystem) {
                    final String fileStorageId = previousLauncher.getFileStorageId();
                    if (StringUtils.isNotBlank(fileStorageId)) {
                        try {
                            FileStorageService.INSTANCE.removeFile(fileStorageId);
                            ApplicationLauncherUpdateDao.INSTANCE.updateFileStorage(connection, previousLauncher.getId(), null);
                            LOGGER.info("Removed {} with storage ID {} from storage to only keep the last version", previousLauncher.getFilePath(), fileStorageId);
                        } catch (IOException e) {
                            LOGGER.error("Couldn't remove file {} with storage ID {} from storage", previousLauncher, fileStorageId, e);
                        }
                    }
                }
            }
            connection.commit();
        }
        return launcherUpdate;

    }

    public ApplicationLauncherUpdate getLastLauncherUpdate(String applicationId, SystemType system, boolean preview) throws IOException {
        return ApplicationLauncherUpdateDao.INSTANCE.getLastForApplicationAndSystem(applicationId, system, preview);
    }

    public String getLauncherFileDownloadUrl(Request request, String launcherId) throws IOException {
        ApplicationLauncherUpdate launcher = ApplicationLauncherUpdateDao.INSTANCE.getLauncher(launcherId);
        return FileStorageService.INSTANCE.generateFileUrl(launcher.getFileStorageId());
    }


    public String getApplicationLauncherName(ApplicationLauncherUpdate launcherUpdate) {
        return "launchers/" + launcherUpdate.getApplicationId() + "/" + new File(launcherUpdate.getFilePath()).getName() + "_" + launcherUpdate.getSystem() + "_" + launcherUpdate.getVersion();
    }
}
