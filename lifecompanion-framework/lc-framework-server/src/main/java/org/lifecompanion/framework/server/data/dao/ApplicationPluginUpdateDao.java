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

import org.lifecompanion.framework.model.server.update.ApplicationPlugin;
import org.lifecompanion.framework.model.server.update.ApplicationPluginUpdate;
import org.lifecompanion.framework.model.server.update.UpdateVisibility;
import org.sql2o.Connection;

import java.util.List;

public enum ApplicationPluginUpdateDao {
    INSTANCE;

    // APPLICATION PLUGIN UPDATE
    //========================================================================
    public List<ApplicationPluginUpdate> getPluginUpdatesOrderByVersionForPluginIdAndPreview(String pluginId, boolean preview) {
        try (Connection connection = DataSource.INSTANCE.getSql2o().open()) {
            return connection.createQuery("SELECT * FROM application_plugin_update WHERE "//
                    + "application_plugin_id = :pluginId "//
                    + "AND (visibility = 'PUBLISHED' OR (:preview AND visibility = 'PREVIEW')) "//
                    + "ORDER BY version_major DESC, version_minor DESC, version_patch DESC LIMIT 1")//
                    .addParameter("pluginId", pluginId)//
                    .addParameter("preview", preview)//
                    .executeAndFetch(ApplicationPluginUpdate.class);
        }
    }

    public ApplicationPluginUpdate getApplicationPluginUpdate(String id) {
        try (Connection connection = DataSource.INSTANCE.getSql2o().open()) {
            return connection.createQuery("SELECT * FROM application_plugin_update WHERE "//
                    + "id = :id ")//
                    .addParameter("id", id)//
                    .executeAndFetchFirst(ApplicationPluginUpdate.class);
        }
    }

    public long countApplicationPluginUpdateByVersion(Connection connection, String applicationPluginId, String version) {
        return connection.createQuery("SELECT COUNT(*) FROM application_plugin_update WHERE "//
                + "version = :version " //
                + "AND application_plugin_id = :applicationPluginId ")//
                .addParameter("version", version)//
                .addParameter("applicationPluginId", applicationPluginId)//
                .executeScalar(Long.class);
    }

    public void insertApplicationPluginUpdate(Connection connection, ApplicationPluginUpdate applicationPluginUpdate) {
        connection.createQuery(
                "INSERT INTO application_plugin_update (id,version,version_major,version_minor,version_patch,update_date,visibility,file_size,file_storage_id,file_name,file_hash,application_plugin_id,min_app_version)"//
                        + " VALUES(:id,:version,:versionMajor,:versionMinor,:versionPatch,:updateDate,:visibility,:fileSize,:fileStorageId,:fileName,:fileHash,:applicationPluginId,:minAppVersion)")//
                .bind(applicationPluginUpdate)//
                .executeUpdate();
    }

    public void updateApplicationPluginUpdate(Connection connection, String id, UpdateVisibility visibility, String fileStorageId) {
        connection.createQuery("UPDATE application_plugin_update SET visibility=:visibility, file_storage_id=:fileStorageId WHERE " //
                + "id = :id")//
                .addParameter("id", id)//
                .addParameter("visibility", visibility)//
                .addParameter("fileStorageId", fileStorageId)//
                .executeUpdate();
    }

    public void updateApplicationPluginUpdateFileStorageId(Connection connection, String id, String fileStorageId) {
        connection.createQuery("UPDATE application_plugin_update SET file_storage_id=:fileStorageId WHERE " //
                + "id = :id")//
                .addParameter("id", id)//
                .addParameter("fileStorageId", fileStorageId)//
                .executeUpdate();
    }

    public List<ApplicationPluginUpdate> getApplicationPluginUpdateBellowForApplicationPlugin(Connection connection, ApplicationPluginUpdate applicationPluginUpdate) {
        return connection.createQuery("SELECT * FROM application_plugin_update WHERE "//
                + "application_plugin_id = :applicationPluginId "//
                + "AND (version_major < :versionMajor OR ((version_major = :versionMajor AND version_minor < :versionMinor) OR (version_minor = :versionMinor AND version_patch < :versionPatch)))"//
                + "ORDER BY version_major DESC, version_minor DESC, version_patch DESC")//
                .addParameter("applicationPluginId", applicationPluginUpdate.getApplicationPluginId())//
                .addParameter("versionMajor", applicationPluginUpdate.getVersionMajor())//
                .addParameter("versionMinor", applicationPluginUpdate.getVersionMinor())//
                .addParameter("versionPatch", applicationPluginUpdate.getVersionPatch())//
                .executeAndFetch(ApplicationPluginUpdate.class);
    }
    //========================================================================

    // APPLICATION PLUGIN
    //========================================================================
    public void insertApplicationPlugin(Connection connection, final ApplicationPlugin applicationPlugin) {
        connection.createQuery(
                "INSERT INTO application_plugin (id, author, name, description, application_id)"//
                        + "VALUES(:id, :author, :name, :description, :applicationId)")//
                .bind(applicationPlugin)//
                .executeUpdate();
    }

    public void updateApplicationPlugin(Connection connection, ApplicationPlugin applicationPlugin) {
        connection.createQuery("UPDATE application_plugin SET author=:author, name=:name, description=:description WHERE " //
                + "id = :id")//
                .bind(applicationPlugin)
                .executeUpdate();
    }

    public long countApplicationPlugin(Connection connection, String applicationPluginId) {
        return connection.createQuery("SELECT COUNT(*) FROM application_plugin WHERE "//
                + "id = :applicationPluginId ")//
                .addParameter("applicationPluginId", applicationPluginId)//
                .executeScalar(Long.class);
    }
    //========================================================================

}
