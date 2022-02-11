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
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SimulateKeyboardUntilReleaseAction;
import org.lifecompanion.ui.common.control.specific.KeyCodeSelectorControl;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SimulateKeyboardUntilReleaseConfigView extends VBox implements UseActionConfigurationViewI<SimulateKeyboardUntilReleaseAction> {
	private KeyCodeSelectorControl keyCodeSelectorControl1, keyCodeSelectorControl2, keyCodeSelectorControl3;

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public Class<SimulateKeyboardUntilReleaseAction> getConfiguredActionType() {
		return SimulateKeyboardUntilReleaseAction.class;
	}

	@Override
	public void initUI() {
		this.setSpacing(10.0);
		this.setPadding(new Insets(10.0));
		this.keyCodeSelectorControl1 = new KeyCodeSelectorControl(Translation.getText("use.action.simulate.key.press.label"));
		this.keyCodeSelectorControl2 = new KeyCodeSelectorControl(Translation.getText("use.action.simulate.key.press.second.label"));
		this.keyCodeSelectorControl3 = new KeyCodeSelectorControl(Translation.getText("use.action.simulate.key.press.third.label"));
		this.getChildren().addAll(this.keyCodeSelectorControl1, this.keyCodeSelectorControl2, this.keyCodeSelectorControl3);

	}

	@Override
	public void editStarts(final SimulateKeyboardUntilReleaseAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		this.keyCodeSelectorControl1.valueProperty().set(element.keyPressed1Property().get());
		this.keyCodeSelectorControl2.valueProperty().set(element.keyPressed2Property().get());
		this.keyCodeSelectorControl3.valueProperty().set(element.keyPressed3Property().get());
	}

	@Override
	public void editEnds(final SimulateKeyboardUntilReleaseAction element) {
		element.keyPressed1Property().set(this.keyCodeSelectorControl1.valueProperty().get());
		element.keyPressed2Property().set(this.keyCodeSelectorControl2.valueProperty().get());
		element.keyPressed3Property().set(this.keyCodeSelectorControl3.valueProperty().get());
	}

}
