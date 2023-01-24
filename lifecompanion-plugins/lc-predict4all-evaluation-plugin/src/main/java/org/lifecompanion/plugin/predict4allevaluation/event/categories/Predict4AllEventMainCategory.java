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
package org.lifecompanion.plugin.predict4allevaluation.event.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum Predict4AllEventMainCategory implements UseEventMainCategoryI {
	INSTANCE;
	private static final String ID = "P4A_EVENT_MAIN_CATEGORY";

	private ObservableList<UseEventSubCategoryI> subCategories = FXCollections.observableArrayList();

	@Override
	public String getStaticDescription() {
		return Translation.getText("predict4all.action.category.main.name");
	}

	@Override
	public String getName() {
		return Translation.getText("predict4all.action.category.main.name");
	}

	@Override
	public String getConfigIconPath() {
		return "use-actions/icon_p4a.png";
	}

	@Override
	public Color getColor() {
		return Color.FORESTGREEN;
	}

	@Override
	public ObservableList<UseEventSubCategoryI> getSubCategories() {
		return this.subCategories;
	}

	@Override
	public int order() {
		return 1000;//at the end
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String generateID() {
		return ID;
	}

}
