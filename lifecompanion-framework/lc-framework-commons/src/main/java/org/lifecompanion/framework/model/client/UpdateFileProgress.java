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

package org.lifecompanion.framework.model.client;

import org.lifecompanion.framework.model.server.update.TargetType;

public class UpdateFileProgress {
    private String fileId;
    private String targetPath;
    private UpdateFileProgressType status;
    private TargetType targetType;
    private String fileHash;
    private long fileSize;
    private boolean toUnzip;

    public UpdateFileProgress(String fileId, String targetPath, UpdateFileProgressType status, TargetType targetType, String fileHash, long fileSize, boolean toUnzip) {
        this.fileId = fileId;
        this.targetPath = targetPath;
        this.status = status;
        this.targetType = targetType;
        this.fileHash = fileHash;
        this.fileSize = fileSize;
        this.toUnzip = toUnzip;
    }

    public UpdateFileProgress() {
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public UpdateFileProgressType getStatus() {
        return status;
    }

    public void setStatus(UpdateFileProgressType status) {
        this.status = status;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isToUnzip() {
        return toUnzip;
    }

    public void setToUnzip(boolean toUnzip) {
        this.toUnzip = toUnzip;
    }

    @Override
    public String toString() {
        return "UpdateFileProgress{" +
                "fileId='" + fileId + '\'' +
                ", targetPath='" + targetPath + '\'' +
                ", status=" + status +
                ", targetType=" + targetType +
                ", fileHash='" + fileHash + '\'' +
                ", fileSize=" + fileSize +
                ", toUnzip=" + toUnzip +
                '}';
    }
}
