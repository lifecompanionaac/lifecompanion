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

import org.lifecompanion.framework.model.server.stats.PushStatus;
import org.lifecompanion.framework.model.server.stats.SoftwareStat;
import org.sql2o.Connection;

public enum SoftwareStatDao {
    INSTANCE;

    public void insertSoftwareStat(Connection connection, final SoftwareStat softwareStat) {
        connection.createQuery(
                "INSERT INTO software_stat (id, event, recorded_at, version, system_id, installation_id, push_status, push_error, push_tries)"//
                        + "VALUES(:id, :event, :recordedAt, :version, :systemId, :installationId, :pushStatus, :pushError, :pushTries)")//
                .bind(softwareStat)//
                .executeUpdate();
    }

    public SoftwareStat getFirstStatToPush() {
        try (Connection connection = DataSource.INSTANCE.getSql2o().open()) {
            return connection.createQuery("SELECT * FROM software_stat "//
                    + "WHERE push_status != 'DONE' AND push_tries is not null AND push_tries < 5 "//
                    + "ORDER BY recorded_at ASC LIMIT 1")
                    .executeAndFetchFirst(SoftwareStat.class);
        }
    }

    public void updatePushStatusAndTries(Connection connection, String id, PushStatus pushStatus, int pushTries, String pushError) {
        connection.createQuery("UPDATE software_stat SET push_status = :pushStatus, push_error = :pushError, push_tries = :pushTries WHERE id = :id")//
                .addParameter("id", id)//
                .addParameter("pushStatus", pushStatus)//
                .addParameter("pushTries", pushTries)//
                .addParameter("pushError", pushError)//
                .executeUpdate();
    }
}
