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

package org.lifecompanion.base.data.control.stats;

import java.util.Date;
import java.util.List;

public class SessionPart {
    private String sessionType;
    private Date startedAt;
    private Date endedAt;
    private String configId;
    private String configName;
    private String profileId;
    private String profileName;
    private List<SessionEvent> events;

    public SessionPart(String sessionType, Date startedAt, String configId, String configName, String profileId, String profileName) {
        this.sessionType = sessionType;
        this.startedAt = startedAt;
        this.configId = configId;
        this.configName = configName;
        this.profileId = profileId;
        this.profileName = profileName;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public List<SessionEvent> getEvents() {
        return events;
    }

    public void setEvents(List<SessionEvent> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "SessionPart{" +
                "sessionType='" + sessionType + '\'' +
                ", startedAt=" + startedAt +
                ", configId='" + configId + '\'' +
                ", configName='" + configName + '\'' +
                ", profileId='" + profileId + '\'' +
                ", profileName='" + profileName + '\'' +
                '}';
    }
}
