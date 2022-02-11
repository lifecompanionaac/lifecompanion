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

package org.lifecompanion.ui.app.generalconfiguration.step.predict4all.correction;

public enum CorrectionCategory {
	INSERT("predict4all.config.rule.type.insert.name", "predict4all.config.rule.type.insert.description"), //
	DELETE("predict4all.config.rule.type.delete.name", "predict4all.config.rule.type.delete.description"), //
	CONFUSION("predict4all.config.rule.type.confusion.name", "predict4all.config.rule.type.confusion.description"), //
	CLASSIC("predict4all.config.rule.type.classic.name", "predict4all.config.rule.type.classic.description");

	private final String nameId, descriptionId;

	private CorrectionCategory(final String nameId, final String descriptionId) {
		this.nameId = nameId;
		this.descriptionId = descriptionId;
	}

	public String getNameId() {
		return this.nameId;
	}

	public String getDescriptionId() {
		return this.descriptionId;
	}
}
