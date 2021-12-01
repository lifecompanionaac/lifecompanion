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

public class UploadUpdateFileDto {
    private String fileName;
    private String filePath;
    private long fileSize;
    private String applicationUpdateIdInDb;
    private String applicationUpdateFileIdInDb;
    private SystemType system;

    public UploadUpdateFileDto(String fileName, String filePath, long fileSize, String applicationUpdateIdInDb, String applicationUpdateFileIdInDb, SystemType system) {
        super();
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.applicationUpdateIdInDb = applicationUpdateIdInDb;
        this.applicationUpdateFileIdInDb = applicationUpdateFileIdInDb;
        this.system = system;
    }

    public UploadUpdateFileDto() {
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getApplicationUpdateFileIdInDb() {
        return applicationUpdateFileIdInDb;
    }

    public void setApplicationUpdateFileIdInDb(String applicationUpdateFileIdInDb) {
        this.applicationUpdateFileIdInDb = applicationUpdateFileIdInDb;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getApplicationUpdateIdInDb() {
        return applicationUpdateIdInDb;
    }

    public void setApplicationUpdateIdInDb(String applicationUpdateIdInDb) {
        this.applicationUpdateIdInDb = applicationUpdateIdInDb;
    }

    public SystemType getSystem() {
        return system;
    }

    public void setSystem(SystemType system) {
        this.system = system;
    }
}
