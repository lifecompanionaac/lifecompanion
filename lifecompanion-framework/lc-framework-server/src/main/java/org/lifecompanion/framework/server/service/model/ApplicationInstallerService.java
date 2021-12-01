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
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.model.server.error.BusinessLogicError;
import org.lifecompanion.framework.model.server.update.ApplicationInstaller;
import org.lifecompanion.framework.model.server.update.UpdateVisibility;
import org.lifecompanion.framework.server.LifeCompanionFrameworkServer;
import org.lifecompanion.framework.server.controller.handler.BusinessLogicException;
import org.lifecompanion.framework.server.data.dao.ApplicationInstallerDao;
import org.lifecompanion.framework.server.data.dao.DataSource;
import org.lifecompanion.framework.server.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public enum ApplicationInstallerService {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationInstallerService.class);

    public ApplicationInstaller createUpdate(ApplicationInstaller installer, InputStream installerFile) throws IOException {
        try (Connection connection = DataSource.INSTANCE.getSql2o().beginTransaction()) {
            // Check that there is no existing update for the same version/system/systemModifier
            if (ApplicationInstallerDao.INSTANCE.countInstallerByVersionSystemSystemModifierApplicationId(connection, installer.getVersion(), installer.getSystem(),
                    installer.getSystemModifier(), installer.getApplicationId()) > 0) {
                throw new BusinessLogicException(BusinessLogicError.EXISTING_UPDATE_SAME_VERSION_SYSTEM);
            }

            UpdateVisibility targetVisibility = installer.getVisibility();

            // Initialize : insert and initialize information
            installer.setId(UUID.randomUUID().toString());
            installer.setUpdateDate(new Date());
            installer.setVisibility(UpdateVisibility.UPLOADING);
            VersionUtils.VersionInfo versionInfo = VersionUtils.VersionInfo.parse(installer.getVersion());
            installer.setVersionMajor(versionInfo.getMajor());
            installer.setVersionMinor(versionInfo.getMinor());
            installer.setVersionPatch(versionInfo.getPatch());
            ApplicationInstallerDao.INSTANCE.insertInstaller(connection, installer);

            // Upload file to storage
            String fileId = FileStorageService.INSTANCE.saveFile(installerFile,
                    "installers/" + installer.getApplicationId() + "/" + getApplicationInstallerName(installer, true, null),
                    installer.getFileSize());

            // Once uploaded, update file in DB
            ApplicationInstallerDao.INSTANCE.updateInstaller(connection, installer.getId(), targetVisibility, fileId);

            // Remove previous installer in storage (if this one is published)
            if (LifeCompanionFrameworkServer.DELETE_PREVIOUS_FILE_ON_UPDATE && targetVisibility == UpdateVisibility.PUBLISHED) {
                List<ApplicationInstaller> installersBeforeForSameSystem = ApplicationInstallerDao.INSTANCE.getInstallersBellowForApplicationAndSystem(connection, installer.getApplicationId(), installer.getSystem(), installer.getVersionMajor(), installer.getVersionMinor(), installer.getVersionPatch());
                for (ApplicationInstaller previousInstaller : installersBeforeForSameSystem) {
                    final String fileStorageId = previousInstaller.getFileStorageId();
                    if (StringUtils.isNotBlank(fileStorageId)) {
                        try {
                            FileStorageService.INSTANCE.removeFile(fileStorageId);
                            ApplicationInstallerDao.INSTANCE.updateInstallerFileStorageId(connection, previousInstaller.getId(), null);
                            LOGGER.info("Removed {} with storage ID {} from storage to only keep the last version", previousInstaller.getFileNameRoot(), fileStorageId);
                        } catch (IOException e) {
                            LOGGER.error("Couldn't remove file {} with storage ID {} from storage", previousInstaller, fileStorageId, e);
                        }
                    }
                }
            }
            connection.commit();
        }
        return installer;
    }

    public ApplicationInstaller getLastInstallerFor(String applicationId, SystemType system, boolean preview) throws IOException {
        return ApplicationInstallerDao.INSTANCE.getLastInstallerForApplicationAndSystem(applicationId, system, preview);
    }

    public String getApplicationInstallerName(ApplicationInstaller lastInstallerFor, boolean includeVersion, List<String> pluginIds) {
        return lastInstallerFor.getFileNameRoot() + "_" + lastInstallerFor.getSystem() + (includeVersion ? "_" + lastInstallerFor.getVersion() : "") + (CollectionUtils.isEmpty(pluginIds) ? "" : ";" + String.join(";", pluginIds)) + "." + lastInstallerFor.getFileNameExtension();
    }
}
