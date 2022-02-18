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

/**
 * This model has been replaced by {@link ApplicationUpdateFile} with {@link TargetType} LAUNCHER.<br>
 * We keep this model to ensure backward compatibility with previous installations.
 */
public class ApplicationLauncherUpdate {
    private String id;
    private String version;
    private SystemType system;

    @Column(name = "system_modifier")
    private SystemTypeModifier systemModifier;

    @Column(name = "update_date")
    private Date updateDate;

    private UpdateVisibility visibility;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "file_storage_id")
    private String fileStorageId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_hash")
    private String fileHash;

    @Column(name = "application_id")
    private String applicationId;

    @Column(name = "version_major")
    private int versionMajor;

    @Column(name = "version_minor")
    private int versionMinor;

    @Column(name = "version_patch")
    private int versionPatch;

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

    public SystemType getSystem() {
        return system;
    }

    public void setSystem(SystemType system) {
        this.system = system;
    }

    public SystemTypeModifier getSystemModifier() {
        return systemModifier;
    }

    public void setSystemModifier(SystemTypeModifier systemModifier) {
        this.systemModifier = systemModifier;
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileStorageId() {
        return fileStorageId;
    }

    public void setFileStorageId(String fileStorageId) {
        this.fileStorageId = fileStorageId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
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

    @Override
    public String toString() {
        return "ApplicationLauncherUpdate{" +
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", system=" + system +
                ", systemModifier=" + systemModifier +
                ", updateDate=" + updateDate +
                ", visibility=" + visibility +
                ", fileSize=" + fileSize +
                ", fileStorageId='" + fileStorageId + '\'' +
                ", fileName='" + filePath + '\'' +
                ", fileHash='" + fileHash + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", versionMajor=" + versionMajor +
                ", versionMinor=" + versionMinor +
                ", versionPatch=" + versionPatch +
                '}';
    }
}
