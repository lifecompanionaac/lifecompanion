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
package org.lifecompanion.plugin.predict4allevaluation.action.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum Predict4AllActionSubCategories implements UseActionSubCategoryI {
	CLINICAL_STUDY("predict4all.action.category.sub.clinical.study", Color.web("#8BC34A"));//

	private String nameId;
	private String id;
	private Color color;
	private ObservableList<BaseUseActionI<?>> actions = FXCollections.observableArrayList();

	private Predict4AllActionSubCategories(final String nameId, final Color color) {
		this.nameId = nameId;
		this.id = this.name();
		this.color = color;
		Predict4AllMainCategory.INSTANCE.getSubCategories().add(this);
	}

	@Override
	public String getName() {
		return Translation.getText(this.nameId);
	}

	@Override
	public UseActionMainCategoryI getMainCategory() {
		return Predict4AllMainCategory.INSTANCE;
	}

	@Override
	public ObservableList<BaseUseActionI<?>> getContent() {
		return this.actions;
	}

	@Override
	public Color getColor() {
		return this.color;
	}

	@Override
	public int order() {
		return this.ordinal();
	}

	@Override
	public String getID() {
		return this.id;
	}

	@Override
	public String generateID() {
		return this.id;
	}
}
