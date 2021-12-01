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
import org.lifecompanion.framework.model.server.SystemTypeModifier;

import java.util.Collection;
import java.util.List;

public class InitializeApplicationUpdateDto {
    private String applicationId;
    private SystemType system;
    private SystemTypeModifier systemTypeModifer;
    private String description;
    private String version;
    private Collection<ApplicationUpdateFileDto> files;

    public InitializeApplicationUpdateDto(String applicationId, SystemType system, SystemTypeModifier systemTypeModifer, String description,
                                          String version, Collection<ApplicationUpdateFileDto> files) {
        this();
        this.applicationId = applicationId;
        this.system = system;
        this.systemTypeModifer = systemTypeModifer;
        this.description = description;
        this.version = version;
        this.files = files;
    }

    public InitializeApplicationUpdateDto() {
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public SystemType getSystem() {
        return system;
    }

    public void setSystem(SystemType system) {
        this.system = system;
    }

    public SystemTypeModifier getSystemTypeModifer() {
        return systemTypeModifer;
    }

    public void setSystemTypeModifer(SystemTypeModifier systemTypeModifer) {
        this.systemTypeModifer = systemTypeModifer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Collection<ApplicationUpdateFileDto> getFiles() {
        return files;
    }

    public void setFiles(List<ApplicationUpdateFileDto> files) {
        this.files = files;
    }
}
