/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.predict4allevaluation.clinicalstudy;

import java.util.Date;

public class ClinicalStudyTestInformationDto {

	private final Date date;
	private final String userId;
	private final ClinicalStudyTestContext context;
	private final String contextOtherDescription;
	private final String lcVersionAndDate;
	private final String p4APluginVersion;
	private final String p4AVersionAndDate;

	public ClinicalStudyTestInformationDto(Date date, String userId, ClinicalStudyTestContext context, String contextOtherDescription,
			String lcVersionAndDate, String p4aPluginVersion, String p4aVersionAndDate) {
		super();
		this.date = date;
		this.userId = userId;
		this.context = context;
		this.contextOtherDescription = contextOtherDescription;
		this.lcVersionAndDate = lcVersionAndDate;
		this.p4APluginVersion = p4aPluginVersion;
		this.p4AVersionAndDate = p4aVersionAndDate;
	}

	public Date getDate() {
		return date;
	}

	public String getUserId() {
		return userId;
	}

	public ClinicalStudyTestContext getContext() {
		return context;
	}

	public String getContextOtherDescription() {
		return contextOtherDescription;
	}

	public String getLcVersionAndDate() {
		return lcVersionAndDate;
	}

	public String getP4APluginVersion() {
		return p4APluginVersion;
	}

	public String getP4AVersionAndDate() {
		return p4AVersionAndDate;
	}

	@Override
	public String toString() {
		return "ClinicalStudyTestInformationDto [date=" + date + ", userId=" + userId + ", context=" + context + ", contextOtherDescription="
				+ contextOtherDescription + ", lcVersionAndDate=" + lcVersionAndDate + ", p4APluginVersion=" + p4APluginVersion
				+ ", p4AVersionAndDate=" + p4AVersionAndDate + "]";
	}

}
