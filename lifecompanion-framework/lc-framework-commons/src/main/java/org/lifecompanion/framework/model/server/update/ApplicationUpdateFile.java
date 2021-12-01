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

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.model.server.SystemTypeModifier;

import javax.persistence.Column;

public class ApplicationUpdateFile {
    private String id;

    @Column(name = "file_state")
    private FileState fileState;

    @Column(name = "target_path")
    private String targetPath;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "file_storage_id")
    private String fileStorageId;

    @Column(name = "file_hash")
    private String fileHash;

    @Column(name = "target_type")
    private TargetType targetType;

    private SystemType system;

    @Column(name = "system_modifier")
    private SystemTypeModifier systemModifier;

    @Column(name = "to_unzip")
    private boolean toUnzip;

    @Column(name = "application_update_id")
    private String applicationUpdateId;

    public ApplicationUpdateFile copy() {
        ApplicationUpdateFile copy = new ApplicationUpdateFile();
        copy.targetPath = this.targetPath;
        copy.fileSize = this.fileSize;
        copy.fileStorageId = this.fileStorageId;
        copy.fileHash = this.fileHash;
        copy.targetType = this.targetType;
        copy.fileState = this.fileState;
        copy.system = this.system;
        copy.systemModifier = this.systemModifier;
        copy.toUnzip = this.toUnzip;
        return copy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
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

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public FileState getFileState() {
        return fileState;
    }

    public void setFileState(FileState fileState) {
        this.fileState = fileState;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public String getApplicationUpdateId() {
        return applicationUpdateId;
    }

    public void setApplicationUpdateId(String applicationUpdateId) {
        this.applicationUpdateId = applicationUpdateId;
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

    public boolean isToUnzip() {
        return toUnzip;
    }

    public void setToUnzip(boolean toUnzip) {
        this.toUnzip = toUnzip;
    }
}
