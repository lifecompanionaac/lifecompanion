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

package org.lifecompanion.ui.app.categorizedelement.useaction;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.controller.categorizedelement.useaction.AvailableUseActionManager;
import org.lifecompanion.ui.app.categorizedelement.AbstractCategorizedSearchView;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.List;
import java.util.function.Consumer;

/**
 * View to display search result for use action.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseActionSearchView extends AbstractCategorizedSearchView<BaseUseActionI<?>> {

	/**
	 * Create a view that display every actions of a sub category
	 */
	public UseActionSearchView(final Consumer<BaseUseActionI<?>> selectionCallbackP) {
		super(selectionCallbackP);
	}

	@Override
	protected Node createItemsView(final ObservableList<BaseUseActionI<?>> resultList, final Consumer<BaseUseActionI<?>> selectionCallback) {
		return new UseActionItemsView(resultList, selectionCallback);
	}

	@Override
	protected List<BaseUseActionI<?>> searchForElement(final String terms) {
		return AvailableUseActionManager.INSTANCE.searchAction(terms);
	}

	@Override
	protected String getSearchPromptText() {
		return Translation.getText("use.action.search.field.prompt");
	}

}
