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

package org.lifecompanion.config.view.useaction.impl.show.moveto;

import org.lifecompanion.base.data.useaction.impl.show.moveto.MoveToKeyAction;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.useaction.UseActionConfigurationViewI;
import org.lifecompanion.api.component.definition.useevent.UseVariableDefinitionI;
import org.lifecompanion.base.data.useaction.impl.show.moveto.MoveToGridAction;
import org.lifecompanion.config.view.pane.compselector.ComponentSelectorControl;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MoveToGridConfigView extends VBox implements UseActionConfigurationViewI<MoveToGridAction> {
	private ComponentSelectorControl<GridComponentI> componentSelector;

	public MoveToGridConfigView() {}

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public void editStarts(final MoveToGridAction actionP, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		this.componentSelector.selectedComponentProperty().set(actionP.targetGridProperty().get());
	}

	@Override
	public void editEnds(final MoveToGridAction actionP) {
		actionP.targetGridProperty().set(this.componentSelector.selectedComponentProperty().get());
		this.componentSelector.clearSelection();
	}

	@Override
	public void editCancelled(final MoveToGridAction element) {
		this.componentSelector.clearSelection();
	}

	@Override
	public Class<MoveToGridAction> getConfiguredActionType() {
		return MoveToGridAction.class;
	}

	@Override
	public void initUI() {
		this.setSpacing(4.0);
		this.componentSelector = new ComponentSelectorControl<>(GridComponentI.class, Translation.getText("use.action.go.to.grid.grid.to.display"));
		this.getChildren().add(this.componentSelector);
	}
}
