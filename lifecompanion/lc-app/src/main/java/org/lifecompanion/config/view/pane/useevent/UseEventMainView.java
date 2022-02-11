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

package org.lifecompanion.config.view.pane.useevent;

import java.util.function.Consumer;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedConfigurationViewI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;
import org.lifecompanion.config.view.pane.categorized.AbstractCategorizedMainView;
import org.lifecompanion.config.view.pane.categorized.AbstractCategorizedSearchView;
import org.lifecompanion.config.view.pane.categorized.AbstractMainCategoriesView;
import org.lifecompanion.config.view.pane.categorized.AbstractSubCategoriesView;
import org.lifecompanion.config.view.useevent.UseEventConfigurationViewProvider;

public class UseEventMainView extends AbstractCategorizedMainView<UseEventGeneratorI, UseEventSubCategoryI, UseEventMainCategoryI> {

	@Override
	public AbstractSubCategoriesView<UseEventGeneratorI, UseEventSubCategoryI, UseEventMainCategoryI> createSubCategoriesView(
			final UseEventMainCategoryI mainCategory, final Consumer<UseEventGeneratorI> actionSelectedCallback) {
		return new UseEventSubCategoriesView(mainCategory, actionSelectedCallback);
	}

	@Override
	public CategorizedConfigurationViewI<UseEventGeneratorI> getConfigurationViewFor(final UseEventGeneratorI value) {
		return UseEventConfigurationViewProvider.INSTANCE.getConfigurationViewFor(value);
	}

	@Override
	public AbstractCategorizedSearchView<UseEventGeneratorI> createSearchView(final Consumer<UseEventGeneratorI> actionSelectedCallback) {
		return new UseEventSearchView(actionSelectedCallback);
	}

	@Override
	public AbstractMainCategoriesView<UseEventMainCategoryI> createMainCategoriesView(
			final Consumer<UseEventMainCategoryI> categorySelectionCallback) {
		return new UseEventMainCategoriesView(categorySelectionCallback);
	}

	@Override
	public void callEditStartOn(final CategorizedConfigurationViewI<UseEventGeneratorI> configView, final UseEventGeneratorI value) {
		UseEventGeneratorConfigurationViewI<UseEventGeneratorI> useEventConfigView = (UseEventGeneratorConfigurationViewI<UseEventGeneratorI>) configView;
		useEventConfigView.editStarts(value);
	}

	@Override
	protected String getSearchTitle() {
		return Translation.getText("title.useevent.search");
	}

	@Override
	protected String getSearchButtonTooltipID() {
		return "tooltip.useevent.list.search";
	}

	@Override
	protected String getButtonOkTooltipID() {
		return "tooltip.useevent.ok.button";
	}

	@Override
	protected String getButtonCancelTooltipID() {
		return "tooltip.useevent.cancel.button";
	}

}
