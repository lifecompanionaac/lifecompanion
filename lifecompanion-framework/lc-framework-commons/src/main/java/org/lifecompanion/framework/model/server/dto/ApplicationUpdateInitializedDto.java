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

import java.util.Collection;

public class ApplicationUpdateInitializedDto {
	private String applicationUpdateId;
	private Collection<ApplicationUpdateFileDto> filesToUpload;

	public ApplicationUpdateInitializedDto(String applicationUpdateId, Collection<ApplicationUpdateFileDto> filesToUpload) {
		super();
		this.applicationUpdateId = applicationUpdateId;
		this.filesToUpload = filesToUpload;
	}

	public String getApplicationUpdateId() {
		return applicationUpdateId;
	}

	public void setApplicationUpdateId(String applicationUpdateId) {
		this.applicationUpdateId = applicationUpdateId;
	}

	public Collection<ApplicationUpdateFileDto> getFilesToUpload() {
		return filesToUpload;
	}

	public void setFilesToUpload(Collection<ApplicationUpdateFileDto> filesToUpload) {
		this.filesToUpload = filesToUpload;
	}
}
