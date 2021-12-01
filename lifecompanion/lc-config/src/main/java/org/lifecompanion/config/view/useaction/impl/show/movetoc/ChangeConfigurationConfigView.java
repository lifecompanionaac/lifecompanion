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

package org.lifecompanion.config.view.useaction.impl.show.movetoc;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.api.component.definition.useaction.UseActionConfigurationViewI;
import org.lifecompanion.api.component.definition.useevent.UseVariableDefinitionI;
import org.lifecompanion.base.data.useaction.impl.show.movetoc.ChangeConfigurationAction;
import org.lifecompanion.config.view.pane.compselector.ConfigurationSelectorControl;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ChangeConfigurationConfigView extends VBox implements UseActionConfigurationViewI<ChangeConfigurationAction> {

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public Class<ChangeConfigurationAction> getConfiguredActionType() {
		return ChangeConfigurationAction.class;
	}

	private ConfigurationSelectorControl configurationSelectorControl;

	@Override
	public void initUI() {
		this.configurationSelectorControl = new ConfigurationSelectorControl(Translation.getText("change.configuration.action.configuration.to.open"));
		this.getChildren().add(this.configurationSelectorControl);
	}

	@Override
	public void editStarts(final ChangeConfigurationAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		this.configurationSelectorControl.valueProperty().set(element.getConfigurationDescription());
	}

	@Override
	public void editEnds(final ChangeConfigurationAction element) {
		element.updateConfigurationDescription(this.configurationSelectorControl.valueProperty().get());
	}
}
