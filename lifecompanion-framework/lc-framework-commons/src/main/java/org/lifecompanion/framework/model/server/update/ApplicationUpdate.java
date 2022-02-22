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

package org.lifecompanion.framework.model.server.update;

import java.util.Date;

import javax.persistence.Column;

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.model.server.SystemTypeModifier;

public class ApplicationUpdate {
    private String id;

    private String version;

    @Column(name = "version_major")
    private int versionMajor;

    @Column(name = "version_minor")
    private int versionMinor;

    @Column(name = "version_patch")
    private int versionPatch;

    @Column(name = "update_date")
    private Date updateDate;

    private UpdateVisibility visibility;

    @Column(name = "application_id")
    private String applicationId;

    private String description;

    @Column(name = "api_version")
    private Integer apiVersion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionMajor() {
        return versionMajor;
    }

    public void setVersionMajor(int versionMajor) {
        this.versionMajor = versionMajor;
    }

    public int getVersionMinor() {
        return versionMinor;
    }

    public void setVersionMinor(int versionMinor) {
        this.versionMinor = versionMinor;
    }

    public int getVersionPatch() {
        return versionPatch;
    }

    public void setVersionPatch(int versionPatch) {
        this.versionPatch = versionPatch;
    }


    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public UpdateVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(UpdateVisibility visibility) {
        this.visibility = visibility;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setApiVersion(Integer apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Integer getApiVersion() {
        return apiVersion;
    }

    @Override
    public String toString() {
        return "ApplicationUpdate{" +
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", versionMajor=" + versionMajor +
                ", versionMinor=" + versionMinor +
                ", versionPatch=" + versionPatch +
                ", updateDate=" + updateDate +
                ", visibility=" + visibility +
                ", applicationId='" + applicationId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
