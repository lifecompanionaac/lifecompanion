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

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.model.server.SystemTypeModifier;
import org.lifecompanion.framework.model.server.update.ApplicationInstaller;
import org.lifecompanion.framework.model.server.update.ApplicationLauncherUpdate;
import org.lifecompanion.framework.model.server.update.UpdateVisibility;
import org.sql2o.Connection;

import java.util.List;

public enum ApplicationLauncherUpdateDao {
    INSTANCE;

    // CREATE UPDATES
    //========================================================================
    public void insert(Connection connection, final ApplicationLauncherUpdate installer) {
        connection.createQuery(
                "INSERT INTO application_launcher_update (id,version,version_major,version_minor,version_patch,system,system_modifier,update_date,visibility,file_size,file_storage_id,file_path,file_hash,application_id)"//
                        + "VALUES(:id,:version, :versionMajor, :versionMinor, :versionPatch, :system,:systemModifier,:updateDate,:visibility,:fileSize,:fileStorageId,:filePath,:fileHash,:applicationId)")//
                .bind(installer)//
                .executeUpdate();
    }

    public void update(Connection connection, String id, UpdateVisibility visibility, String fileId) {
        connection.createQuery("UPDATE application_launcher_update SET visibility=:visibility, file_storage_id=:fileStorageId WHERE " //
                + "id = :id")//
                .addParameter("id", id)//
                .addParameter("visibility", visibility)//
                .addParameter("fileStorageId", fileId)//
                .executeUpdate();
    }

    public void updateFileStorage(Connection connection, String id, String fileId) {
        connection.createQuery("UPDATE application_launcher_update SET file_storage_id=:fileStorageId WHERE " //
                + "id = :id")//
                .addParameter("id", id)//
                .addParameter("fileStorageId", fileId)//
                .executeUpdate();
    }

    public long countByVersionSystemSystemModifierApplicationId(Connection connection, String version, SystemType system, SystemTypeModifier systemModifier,
                                                                String applicationId) {
        return connection.createQuery("SELECT COUNT(*) FROM application_launcher_update WHERE "//
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


    public List<ApplicationLauncherUpdate> getLaunchersBellowForApplicationAndSystem(Connection connection, String applicationId, SystemType system, int versionMajor, int versionMinor, int versionPatch) {
        return connection.createQuery("SELECT * FROM application_launcher_update WHERE "//
                + "application_id = :applicationId "//
                + "AND system = :system "//
                + "AND (version_major < :versionMajor OR ((version_major = :versionMajor AND version_minor < :versionMinor) OR (version_minor = :versionMinor AND version_patch < :versionPatch)))"//
                + "ORDER BY version_major DESC, version_minor DESC, version_patch DESC")//
                .addParameter("applicationId", applicationId)//
                .addParameter("system", system)//
                .addParameter("versionMajor", versionMajor)//
                .addParameter("versionMinor", versionMinor)//
                .addParameter("versionPatch", versionPatch)//
                .executeAndFetch(ApplicationLauncherUpdate.class);
    }
    //========================================================================


    // RETRIEVE FOR UPDATES
    //========================================================================
    public ApplicationLauncherUpdate getLastForApplicationAndSystem(String applicationId, SystemType system, boolean preview) {
        try (Connection connection = DataSource.INSTANCE.getSql2o().open()) {
            return connection.createQuery("SELECT * FROM application_launcher_update WHERE "//
                    + "application_id = :applicationId "//
                    + "AND system = :system "//
                    + "AND (visibility = 'PUBLISHED' OR (:preview AND visibility = 'PREVIEW')) "//
                    + "ORDER BY version_major DESC, version_minor DESC, version_patch DESC LIMIT 1")//
                    .addParameter("applicationId", applicationId)//
                    .addParameter("system", system)//
                    .addParameter("preview", preview)//
                    .executeAndFetchFirst(ApplicationLauncherUpdate.class);
        }
    }

    public ApplicationLauncherUpdate getLauncher(String id) {
        try (Connection connection = DataSource.INSTANCE.getSql2o().open()) {
            return connection.createQuery("SELECT * FROM application_launcher_update WHERE "//
                    + "id = :id ")//
                    .addParameter("id", id)//
                    .executeAndFetchFirst(ApplicationLauncherUpdate.class);
        }
    }
    //========================================================================

}
