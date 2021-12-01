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

package org.lifecompanion.framework.model.server.dto;

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.model.server.update.TargetType;

import java.io.File;

public class ApplicationUpdateFileDto {
    private String targetPath;
    private String fileHash;
    private TargetType targetType;
    private long fileSize;
    private SystemType system;
    private String applicationUpdateFileIdInDb;
    private boolean toUnzip;
    private String presetFileStorageId;
    private transient File sourceFile;


    //    public ApplicationUpdateFileDto(String targetPath, String fileHash, TargetType targetType, long fileSize, File sourceFile, SystemType system) {
    //        super();
    //        this.targetPath = targetPath;
    //        this.fileHash = fileHash;
    //        this.targetType = targetType;
    //        this.fileSize = fileSize;
    //        this.sourceFile = sourceFile;
    //        this.system = system;
    //    }

    public ApplicationUpdateFileDto(String targetPath, String fileHash, TargetType targetType, long fileSize, SystemType system, boolean toUnzip, String presetFileStorageId, File sourceFile) {
        this();
        this.targetPath = targetPath;
        this.fileHash = fileHash;
        this.targetType = targetType;
        this.fileSize = fileSize;
        this.system = system;
        this.toUnzip = toUnzip;
        this.presetFileStorageId = presetFileStorageId;
        this.sourceFile = sourceFile;
    }

    public ApplicationUpdateFileDto() {
    }

    public String getTargetPath() {
        return targetPath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public String getApplicationUpdateFileIdInDb() {
        return applicationUpdateFileIdInDb;
    }

    public void setApplicationUpdateFileIdInDb(String applicationUpdateFileIdInDb) {
        this.applicationUpdateFileIdInDb = applicationUpdateFileIdInDb;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public SystemType getSystem() {
        return system;
    }

    public void setSystem(SystemType system) {
        this.system = system;
    }

    public boolean isToUnzip() {
        return toUnzip;
    }

    public void setToUnzip(boolean toUnzip) {
        this.toUnzip = toUnzip;
    }

    public String getPresetFileStorageId() {
        return presetFileStorageId;
    }

    public void setPresetFileStorageId(String presetFileStorageId) {
        this.presetFileStorageId = presetFileStorageId;
    }
}
