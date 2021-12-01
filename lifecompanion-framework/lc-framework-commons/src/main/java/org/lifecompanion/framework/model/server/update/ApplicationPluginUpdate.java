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

import javax.persistence.Column;
import java.util.Date;

public class ApplicationPluginUpdate {
    private String id;
    private String version;

    @Column(name = "update_date")
    private Date updateDate;

    private UpdateVisibility visibility;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "file_storage_id")
    private String fileStorageId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_hash")
    private String fileHash;

    @Column(name = "application_plugin_id")
    private String applicationPluginId;

    @Column(name = "version_major")
    private int versionMajor;

    @Column(name = "version_minor")
    private int versionMinor;

    @Column(name = "version_patch")
    private int versionPatch;

    @Column(name = "min_app_version")
    private String minAppVersion;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getApplicationPluginId() {
        return applicationPluginId;
    }

    public void setApplicationPluginId(String applicationPluginId) {
        this.applicationPluginId = applicationPluginId;
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

    public String getMinAppVersion() {
        return minAppVersion;
    }

    public void setMinAppVersion(String minAppVersion) {
        this.minAppVersion = minAppVersion;
    }

    @Override
    public String toString() {
        return "ApplicationPluginUpdate{" +
                "version='" + version + '\'' +
                ", updateDate=" + updateDate +
                ", visibility=" + visibility +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                ", minAppVersion='" + minAppVersion + '\'' +
                '}';
    }
}
