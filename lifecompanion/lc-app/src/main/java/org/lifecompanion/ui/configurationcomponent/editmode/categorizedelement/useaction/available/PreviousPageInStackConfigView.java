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

package org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useaction.available;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.PreviousPageInStackAction;
import org.lifecompanion.ui.common.control.specific.selector.ComponentSelectorControl;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PreviousPageInStackConfigView extends VBox implements UseActionConfigurationViewI<PreviousPageInStackAction> {
	private ComponentSelectorControl<StackComponentI> componentSelector;

	public PreviousPageInStackConfigView() {}

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public void editStarts(final PreviousPageInStackAction actionP, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		this.componentSelector.selectedComponentProperty().set(actionP.changedPageParentStackProperty().get());
	}

	@Override
	public void editEnds(final PreviousPageInStackAction actionP) {
		actionP.changedPageParentStackIdProperty().set(this.componentSelector.getSelectedComponentID());
		this.componentSelector.clearSelection();
	}

	@Override
	public void editCancelled(final PreviousPageInStackAction element) {
		this.componentSelector.clearSelection();
	}

	@Override
	public Class<PreviousPageInStackAction> getConfiguredActionType() {
		return PreviousPageInStackAction.class;
	}

	@Override
	public void initUI() {
		this.setSpacing(4.0);
		this.componentSelector = new ComponentSelectorControl<>(StackComponentI.class, Translation.getText("use.action.previous.page.stack.to.change"));
		this.getChildren().add(this.componentSelector);
	}
}
