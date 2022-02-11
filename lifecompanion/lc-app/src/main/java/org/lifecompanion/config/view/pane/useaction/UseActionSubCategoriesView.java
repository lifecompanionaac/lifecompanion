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

package org.lifecompanion.config.view.pane.useaction;

import java.util.function.Consumer;

import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;
import org.lifecompanion.config.view.pane.categorized.AbstractSubCategoriesView;
import javafx.scene.Node;

/**
 * View to display every use action of a main category.<br>
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseActionSubCategoriesView extends AbstractSubCategoriesView<BaseUseActionI<?>, UseActionSubCategoryI, UseActionMainCategoryI> {

	public UseActionSubCategoriesView(final UseActionMainCategoryI mainCategoryP, final Consumer<BaseUseActionI<?>> selectionCallbackP) {
		super(mainCategoryP, selectionCallbackP);
	}

	@Override
	protected Node createContentView(final UseActionSubCategoryI subCategory, final Consumer<BaseUseActionI<?>> selectionCallback) {
		return new UseActionSubCategoryContentView(subCategory, selectionCallback);
	}

}
