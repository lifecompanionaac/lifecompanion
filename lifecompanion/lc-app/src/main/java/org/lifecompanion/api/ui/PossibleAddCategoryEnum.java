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

package org.lifecompanion.api.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Represent all the possible add category.<br>
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum PossibleAddCategoryEnum implements PossibleAddComponentCategoryI {
	BASE_COMPONENT("add.category.base.components"), //
	GRIDS("add.category.grids"), //
	KEYS("add.category.keys")//
	;
	private String titleId;
	private ObservableList<PossibleAddComponentI<?>> possibleAddList;

	private PossibleAddCategoryEnum(final String titleId) {
		this.titleId = titleId;
		this.possibleAddList = FXCollections.observableArrayList();
	}

	@Override
	public String getTitle() {
		return Translation.getText(this.titleId);
	}

	@Override
	public ObservableList<PossibleAddComponentI<?>> getPossibleAddList() {
		return this.possibleAddList;
	}
}
