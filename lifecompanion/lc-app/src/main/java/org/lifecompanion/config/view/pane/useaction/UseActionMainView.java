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

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedConfigurationViewI;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.base.data.control.UseVariableController;
import org.lifecompanion.config.view.pane.categorized.AbstractCategorizedMainView;
import org.lifecompanion.config.view.pane.categorized.AbstractCategorizedSearchView;
import org.lifecompanion.config.view.pane.categorized.AbstractMainCategoriesView;
import org.lifecompanion.config.view.pane.categorized.AbstractSubCategoriesView;
import org.lifecompanion.config.view.useaction.UseActionConfigurationViewProvider;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Gallery view to display available use action.<br>
 * Also display action configuration when needed.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseActionMainView extends AbstractCategorizedMainView<BaseUseActionI<?>, UseActionSubCategoryI, UseActionMainCategoryI> {

	private ReadOnlyObjectProperty<UseEventGeneratorI> associatedUseEventGenerator;

	public UseActionMainView(final ReadOnlyObjectProperty<UseEventGeneratorI> associatedUseEventGenerator) {
		this.associatedUseEventGenerator = associatedUseEventGenerator;
	}

	@Override
	public AbstractMainCategoriesView<UseActionMainCategoryI> createMainCategoriesView(
			final Consumer<UseActionMainCategoryI> categorySelectionCallback) {
		return new UseActionMainCategoriesView(categorySelectionCallback);
	}

	@Override
	public AbstractSubCategoriesView<BaseUseActionI<?>, UseActionSubCategoryI, UseActionMainCategoryI> createSubCategoriesView(
			final UseActionMainCategoryI mainCategory, final Consumer<BaseUseActionI<?>> actionSelectedCallback) {
		return new UseActionSubCategoriesView(mainCategory, actionSelectedCallback);
	}

	@Override
	public CategorizedConfigurationViewI<BaseUseActionI<?>> getConfigurationViewFor(final BaseUseActionI<?> value) {
		return UseActionConfigurationViewProvider.INSTANCE.getConfigurationViewFor(value);
	}

	@Override
	public AbstractCategorizedSearchView<BaseUseActionI<?>> createSearchView(final Consumer<BaseUseActionI<?>> actionSelectedCallback) {
		return new UseActionSearchView(actionSelectedCallback);
	}

	@Override
	public void callEditStartOn(final CategorizedConfigurationViewI<BaseUseActionI<?>> configView, final BaseUseActionI<?> value) {
		UseActionConfigurationViewI<BaseUseActionI<?>> useActionConfigView = (UseActionConfigurationViewI<BaseUseActionI<?>>) configView;
		useActionConfigView.editStarts(value, UseVariableController.INSTANCE.getPossibleVariableList(this.associatedUseEventGenerator));
	}

	@Override
	protected String getSearchTitle() {
		return Translation.getText("title.useaction.search");
	}

	@Override
	protected String getSearchButtonTooltipID() {
		return "tooltip.useaction.list.search";
	}

	@Override
	protected String getButtonOkTooltipID() {
		return "tooltip.useaction.ok.button";
	}

	@Override
	protected String getButtonCancelTooltipID() {
		return "tooltip.useaction.cancel.button";
	}

}
