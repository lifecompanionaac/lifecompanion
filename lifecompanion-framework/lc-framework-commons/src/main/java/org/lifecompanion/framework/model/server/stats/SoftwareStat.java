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

package org.lifecompanion.framework.model.server.stats;

import org.lifecompanion.framework.commons.SystemType;

import javax.persistence.Column;
import java.util.Date;

public class SoftwareStat {
    private String id;
    private String event;

    @Column(name = "recorded_at")
    private Date recordedAt;

    private String version;

    @Column(name = "system_id")
    private SystemType systemId;

    @Column(name = "installation_id")
    private String installationId;

    @Column(name = "push_status")
    private PushStatus pushStatus;

    @Column(name = "push_error")
    private String pushError;

    @Column(name = "push_tries")
    private int pushTries;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public SystemType getSystemId() {
        return systemId;
    }

    public void setSystemId(SystemType systemId) {
        this.systemId = systemId;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public PushStatus getPushStatus() {
        return pushStatus;
    }

    public void setPushStatus(PushStatus pushStatus) {
        this.pushStatus = pushStatus;
    }

    public String getPushError() {
        return pushError;
    }

    public void setPushError(String pushError) {
        this.pushError = pushError;
    }

    public Date getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Date recordedAt) {
        this.recordedAt = recordedAt;
    }

    public int getPushTries() {
        return pushTries;
    }

    public void setPushTries(int pushTries) {
        this.pushTries = pushTries;
    }

    @Override
    public String toString() {
        return "SoftwareStat{" +
                "id='" + id + '\'' +
                ", event='" + event + '\'' +
                ", recordedAt=" + recordedAt +
                ", version='" + version + '\'' +
                ", systemId=" + systemId +
                ", installationId='" + installationId + '\'' +
                ", pushStatus=" + pushStatus +
                ", pushError='" + pushError + '\'' +
                ", pushTries=" + pushTries +
                '}';
    }
}
