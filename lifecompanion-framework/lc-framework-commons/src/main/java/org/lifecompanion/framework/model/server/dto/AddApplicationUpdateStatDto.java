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

import java.util.Date;

public class AddApplicationUpdateStatDto {
    private String version;
    private Date recordedDate;
    private SystemType systemType;

    public AddApplicationUpdateStatDto() {
    }

    public AddApplicationUpdateStatDto(String version, Date recordedDate, SystemType systemType) {
        this();
        this.version = version;
        this.recordedDate = recordedDate;
        this.systemType = systemType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(Date recordedDate) {
        this.recordedDate = recordedDate;
    }

    public SystemType getSystemType() {
        return systemType;
    }

    public void setSystemType(SystemType systemType) {
        this.systemType = systemType;
    }
}
