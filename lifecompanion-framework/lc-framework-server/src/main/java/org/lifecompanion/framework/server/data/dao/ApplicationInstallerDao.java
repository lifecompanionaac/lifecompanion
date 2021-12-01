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

package org.lifecompanion.framework.server.data.dao;

import java.util.List;

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.model.server.SystemTypeModifier;
import org.lifecompanion.framework.model.server.update.ApplicationInstaller;
import org.lifecompanion.framework.model.server.update.UpdateVisibility;
import org.sql2o.Connection;

public enum ApplicationInstallerDao {
    INSTANCE;

    public void insertInstaller(Connection connection, final ApplicationInstaller installer) {
        connection.createQuery(
                "INSERT INTO application_installer (id,version,version_major,version_minor,version_patch,system,system_modifier,update_date,visibility,file_size,file_storage_id,file_name_root,file_name_extension,file_hash,application_id)"//
                        + "VALUES(:id,:version,:versionMajor,:versionMinor,:versionPatch,:system,:systemModifier,:updateDate,:visibility,:fileSize,:fileStorageId,:fileNameRoot,:fileNameExtension,:fileHash,:applicationId)")//
                .bind(installer)//
                .executeUpdate();
    }

    public void updateInstaller(Connection connection, String id, UpdateVisibility visibility, String fileId) {
        connection.createQuery("UPDATE application_installer SET visibility=:visibility, file_storage_id=:fileStorageId WHERE " //
                + "id = :id")//
                .addParameter("id", id)//
                .addParameter("visibility", visibility)//
                .addParameter("fileStorageId", fileId)//
                .executeUpdate();
    }

    public void updateInstallerFileStorageId(Connection connection, String id, String fileId) {
        connection.createQuery("UPDATE application_installer SET file_storage_id=:fileStorageId WHERE " //
                + "id = :id")//
                .addParameter("id", id)//
                .addParameter("fileStorageId", fileId)//
                .executeUpdate();
    }

    public long countInstallerByVersionSystemSystemModifierApplicationId(Connection connection, String version, SystemType system, SystemTypeModifier systemModifier, String applicationId) {
        return connection.createQuery("SELECT COUNT(*) FROM application_installer WHERE "//
                + "version = :version " //
                + "AND system = :system "//
                + "AND (:systemModifier::varchar IS NULL OR system_modifier = :systemModifier) "//
                + "AND application_id = :applicationId ")//
                .addParameter("version", version)//
                .addParameter("system", system)//
                .addParameter("systemModifier", systemModifier)//
                .addParameter("applicationId", applicationId)//
                .executeScalar(Long.class);
    }

    public ApplicationInstaller getLastInstallerForApplicationAndSystem(String applicationId, SystemType system, boolean preview) {
        try (Connection connection = DataSource.INSTANCE.getSql2o().open()) {
            return connection.createQuery("SELECT * FROM application_installer WHERE "//
                    + "application_id = :applicationId "//
                    + "AND system = :system "//
                    + "AND (visibility = 'PUBLISHED' OR (:preview AND visibility = 'PREVIEW')) "//
                    + "ORDER BY version_major DESC, version_minor DESC, version_patch DESC LIMIT 1")//
                    .addParameter("applicationId", applicationId)//
                    .addParameter("system", system)//
                    .addParameter("preview", preview)//
                    .executeAndFetchFirst(ApplicationInstaller.class);
        }
    }

    public List<ApplicationInstaller> getInstallersBellowForApplicationAndSystem(Connection connection, String applicationId, SystemType system, final int versionMajor, int versionMinor, int versionPatch) {
        return connection.createQuery("SELECT * FROM application_installer WHERE "//
                + "application_id = :applicationId "//
                + "AND system = :system "//
                + "AND (version_major < :versionMajor OR ((version_major = :versionMajor AND version_minor < :versionMinor) OR (version_minor = :versionMinor AND version_patch < :versionPatch)))"//
                + "ORDER BY version_major DESC, version_minor DESC, version_patch DESC")//
                .addParameter("applicationId", applicationId)//
                .addParameter("system", system)//
                .addParameter("versionMajor", versionMajor)//
                .addParameter("versionMinor", versionMinor)//
                .addParameter("versionPatch", versionPatch)//
                .executeAndFetch(ApplicationInstaller.class);
    }
}
