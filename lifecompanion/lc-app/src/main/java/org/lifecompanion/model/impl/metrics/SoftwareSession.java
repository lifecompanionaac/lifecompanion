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

package org.lifecompanion.model.impl.metrics;

import java.util.Date;
import java.util.List;

public class SoftwareSession {
    private final String id;
    private final Date startedAt;
    private final Date endedAt;
    private final List<SessionPart> data;
    private String installationId;

    public SoftwareSession(String id, String installationId, Date startedAt, Date endedAt, List<SessionPart> data) {
        this.id = id;
        this.installationId = installationId;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.data = data;
    }

    public String getInstallationId() {
        return installationId;
    }

    public String getId() {
        return id;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public List<SessionPart> getData() {
        return data;
    }
}
