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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.model.server.update.ApplicationUpdate;
import org.lifecompanion.framework.model.server.update.ApplicationUpdateFile;
import org.lifecompanion.framework.model.server.update.UpdateVisibility;
import org.sql2o.Connection;
import org.sql2o.Query;

public enum ApplicationUpdateDao {
    INSTANCE;

    public void insertApplicationUpdate(Connection connection, ApplicationUpdate applicationUpdate) {
        connection.createQuery(
                "INSERT INTO application_update (id,version,version_major,version_minor,version_patch,update_date,visibility,application_id,description)"//
                        + " VALUES "//
                        + "(:id,:version,:versionMajor,:versionMinor,:versionPatch,:updateDate,:visibility,:applicationId,:description)")//
                .bind(applicationUpdate)//
                .executeUpdate();
    }

    public ApplicationUpdate getLastestUpdateFor(Connection connection, final String applicationId, boolean preview) {
        return connection.createQuery("SELECT * FROM application_update WHERE "//
                + "application_id = :applicationId "//
                + "AND (visibility = 'PUBLISHED' OR (:preview AND visibility = 'PREVIEW')) "//
                + "ORDER BY version_major DESC, version_minor DESC, version_patch DESC "//
                + "LIMIT 1")//
                .addParameter("applicationId", applicationId)//
                .addParameter("preview", preview)//
                .executeAndFetchFirst(ApplicationUpdate.class);
    }

    public List<ApplicationUpdate> getAllUpdateAboveOrderByVersion(final String applicationId, final int versionMajor,
                                                                   int versionMinor, int versionPatch, boolean preview) {
        try (Connection connection = DataSource.INSTANCE.getSql2o().open()) {
            return connection.createQuery("SELECT * FROM application_update "//
                    + "WHERE application_id = :applicationId "//
                    + "AND (visibility = 'PUBLISHED' OR (:preview AND visibility = 'PREVIEW')) "//
                    + "AND (version_major > :versionMajor OR (version_major = :versionMajor AND (version_minor > :versionMinor OR (version_minor = :versionMinor AND version_patch > :versionPatch)))) "//
                    + "ORDER BY version_major ASC, version_minor ASC, version_patch ASC")//
                    .addParameter("applicationId", applicationId)//
                    .addParameter("preview", preview)//
                    .addParameter("versionMajor", versionMajor)//
                    .addParameter("versionMinor", versionMinor)//
                    .addParameter("versionPatch", versionPatch)//
                    .executeAndFetch(ApplicationUpdate.class);
        }
    }

    public Long countByApplicationAndVersionAndSystem(Connection connection, final String applicationId, final String version, SystemType system) {
        return connection.createQuery("SELECT COUNT(*) FROM application_update_file LEFT JOIN application_update on application_update_file.application_update_id = application_update.id "
                + "WHERE application_id = :applicationId " //
                + "AND system = :system "//
                + "AND version = :version "
                + "LIMIT 1")//
                .addParameter("applicationId", applicationId)//
                .addParameter("system", system)//
                .addParameter("version", version)//
                .executeScalar(Long.class);
    }

    public ApplicationUpdate getUpdateByApplicationAndVersion(Connection connection, final String applicationId, final String version) {
        return connection.createQuery("SELECT * FROM application_update "
                + "WHERE application_id = :applicationId " //
                + "AND version = :version "
                + "LIMIT 1")//
                .addParameter("applicationId", applicationId)//
                .addParameter("version", version)//
                .executeAndFetchFirst(ApplicationUpdate.class);
    }

    public List<ApplicationUpdateFile> getFilesForUpdate(final String updateId, final SystemType system) {
        try (Connection connection = DataSource.INSTANCE.getSql2o().open()) {
            return connection.createQuery("SELECT * FROM application_update_file WHERE "
                    + "(system::varchar IS NULL OR system = :system) "//
                    + "AND application_update_id = :updateId")//
                    .addParameter("system", system)//
                    .addParameter("updateId", updateId)//
                    .executeAndFetch(ApplicationUpdateFile.class);
        }
    }

    public List<ApplicationUpdateFile> getLastModifiedUpdateFile(Connection connection, final String applicationId, SystemType system, final int versionMajor,
                                                                 int versionMinor, int versionPatch, Collection<String> targetPaths) {
        return connection.createQuery("SELECT application_update_file.* FROM application_update_file LEFT JOIN application_update on application_update_file.application_update_id = application_update.id "
                + "WHERE application_id = :applicationId " //
                + "AND target_path IN (:targetPaths) " //
                + "AND (system::varchar IS NULL OR system = :system) "//
                + "AND (version_major < :versionMajor OR ((version_major = :versionMajor AND version_minor < :versionMinor) OR (version_minor = :versionMinor AND version_patch <= :versionPatch)))"//
                + "ORDER BY version_major DESC, version_minor DESC, version_patch DESC")//
                .addParameter("applicationId", applicationId)//
                .addParameter("system", system)//
                .addParameter("versionMajor", versionMajor)//
                .addParameter("versionMinor", versionMinor)//
                .addParameter("versionPatch", versionPatch)//
                .addParameter("targetPaths", targetPaths)//
                .executeAndFetch(ApplicationUpdateFile.class);
    }


    public List<ApplicationUpdateFile> getAllFileForTargetOrderByVersionDesc(Connection connection, final String applicationId, SystemType system, String targetPath) {
        return connection.createQuery("SELECT application_update_file.* FROM application_update_file LEFT JOIN application_update on application_update_file.application_update_id = application_update.id "
                + "WHERE application_id = :applicationId " //
                + "AND target_path = :targetPath " //
                + "AND (system::varchar IS NULL OR system = :system) "//
                + "ORDER BY version_major DESC, version_minor DESC, version_patch DESC")//
                .addParameter("applicationId", applicationId)//
                .addParameter("system", system)//
                .addParameter("targetPath", targetPath)//
                .executeAndFetch(ApplicationUpdateFile.class);
    }

    public List<String> getAllUniqueUpdateFilePathBellow(Connection connection, final String applicationId, SystemType system, final int versionMajor,
                                                         int versionMinor, int versionPatch) {
        return connection.createQuery("SELECT DISTINCT target_path, application_id,system,version_major,version_minor,version_patch FROM application_update_file LEFT JOIN application_update on application_update_file.application_update_id = application_update.id "
                + "WHERE application_id = :applicationId " //
                + "AND (system::varchar IS NULL OR system = :system) "//
                + "AND (version_major < :versionMajor OR ((version_major = :versionMajor AND version_minor < :versionMinor) OR (version_minor = :versionMinor AND version_patch < :versionPatch)))"//
                + "ORDER BY version_major DESC, version_minor DESC, version_patch DESC")//
                .addParameter("applicationId", applicationId)//
                .addParameter("system", system)//
                .addParameter("versionMajor", versionMajor)//
                .addParameter("versionMinor", versionMinor)//
                .addParameter("versionPatch", versionPatch)//
                .executeScalarList(String.class);
    }

    public List<String> getAllUniqueUpdateFilePath(Connection connection, final String applicationId, SystemType system) {
        return connection.createQuery("SELECT DISTINCT target_path, application_id,system,version_major,version_minor,version_patch FROM application_update_file LEFT JOIN application_update on application_update_file.application_update_id = application_update.id "
                + "WHERE application_id = :applicationId " //
                + "AND (system::varchar IS NULL OR system = :system) "//
                + "ORDER BY version_major DESC, version_minor DESC, version_patch DESC")//
                .addParameter("applicationId", applicationId)//
                .addParameter("system", system)//
                .executeScalarList(String.class);
    }

    public void insertApplicationUpdateFiles(Connection connection, List<ApplicationUpdateFile> newUpdateFiles) {
        final String insertSql = "INSERT INTO application_update_file (id,target_path,file_size,file_storage_id,file_hash,file_state,target_type,system,system_modifier,to_unzip,application_update_id)"//
                + " VALUES "//
                + "(:id,:targetPath,:fileSize,:fileStorageId,:fileHash,:fileState,:targetType,:system, :systemModifier,:toUnzip, :applicationUpdateId)";
        Query query = connection.createQuery(insertSql);
        for (ApplicationUpdateFile file : newUpdateFiles) {
            query.bind(file).addToBatch();
        }
        query.executeBatch();
    }

    public void updateApplicationUpdateFileFileStorageId(Connection connection, String id, String fileStorageId) {
        Connection connection1 = connection.createQuery("UPDATE application_update_file SET file_storage_id = :fileStorageId WHERE id = :id")//
                .addParameter("id", id)//
                .addParameter("fileStorageId", fileStorageId)//
                .executeUpdate();
    }

    public void updateApplicationUpdateVisibilityAndDate(Connection connection, String id, UpdateVisibility visibility, Date updateDate) {
        connection.createQuery("UPDATE application_update SET visibility = :visibility, update_date = :updateDate WHERE id = :id")//
                .addParameter("id", id)//
                .addParameter("visibility", visibility)//
                .addParameter("updateDate", updateDate)//
                .executeUpdate();
    }

    public ApplicationUpdateFile getFileById(final String id) {
        try (Connection connection = DataSource.INSTANCE.getSql2o().open()) {
            return connection.createQuery("SELECT * FROM application_update_file WHERE id = :id")//
                    .addParameter("id", id)//
                    .executeAndFetchFirst(ApplicationUpdateFile.class);
        }
    }
}
