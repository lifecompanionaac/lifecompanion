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

package org.lifecompanion.ui.app.categorizedelement.useevent;

import java.util.List;
import java.util.function.Consumer;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.controller.categorizedelement.useevent.AvailableUseEventController;
import org.lifecompanion.ui.app.categorizedelement.AbstractCategorizedSearchView;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class UseEventSearchView extends AbstractCategorizedSearchView<UseEventGeneratorI> {

	/**
	 * Create a view that display every actions of a sub category
	 * @param subCategoryP the sub category to display
	 */
	public UseEventSearchView(final Consumer<UseEventGeneratorI> selectionCallbackP) {
		super(selectionCallbackP);
	}

	@Override
	protected Node createItemsView(final ObservableList<UseEventGeneratorI> resultList, final Consumer<UseEventGeneratorI> selectionCallback) {
		return new UseEventItemsView(resultList, selectionCallback);
	}

	@Override
	protected List<UseEventGeneratorI> searchForElement(final String terms) {
		return AvailableUseEventController.INSTANCE.searchUseEvent(terms);
	}

	@Override
	protected String getSearchPromptText() {
		return Translation.getText("use.event.search.field.prompt");
	}

}
