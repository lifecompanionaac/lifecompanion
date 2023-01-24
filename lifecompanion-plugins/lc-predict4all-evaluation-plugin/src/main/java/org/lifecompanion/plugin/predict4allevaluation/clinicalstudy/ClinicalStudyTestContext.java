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

public enum ClinicalStudyTestContext {
	WITHOUT_PREDICTION("predict4all.config.clinical.study.test.context.without.prediction"), //
	WITH_PREDICTION("predict4all.config.clinical.study.test.context.with.prediction"), //
	WITH_PREDICTION_CORRECTION("predict4all.config.clinical.study.test.context.with.prediction.correction"), // 
	OTHER("predict4all.config.clinical.study.test.context.with.other")//
	;

	private final String nameId;

	private ClinicalStudyTestContext(String nameId) {
		this.nameId = nameId;
	}

	public String getNameId() {
		return nameId;
	}

}
