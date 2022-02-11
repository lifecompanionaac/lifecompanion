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

package org.lifecompanion.config.view.useaction.impl.computera.keyboard;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SimulateKeyboardUntilReleaseAction;
import org.lifecompanion.config.view.reusable.KeySelectorControl;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SimulateKeyboardUntilReleaseConfigView extends VBox implements UseActionConfigurationViewI<SimulateKeyboardUntilReleaseAction> {
	private KeySelectorControl keySelectorControl1, keySelectorControl2, keySelectorControl3;

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
		this.keySelectorControl1 = new KeySelectorControl(Translation.getText("use.action.simulate.key.press.label"));
		this.keySelectorControl2 = new KeySelectorControl(Translation.getText("use.action.simulate.key.press.second.label"));
		this.keySelectorControl3 = new KeySelectorControl(Translation.getText("use.action.simulate.key.press.third.label"));
		this.getChildren().addAll(this.keySelectorControl1, this.keySelectorControl2, this.keySelectorControl3);

	}

	@Override
	public void editStarts(final SimulateKeyboardUntilReleaseAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		this.keySelectorControl1.valueProperty().set(element.keyPressed1Property().get());
		this.keySelectorControl2.valueProperty().set(element.keyPressed2Property().get());
		this.keySelectorControl3.valueProperty().set(element.keyPressed3Property().get());
	}

	@Override
	public void editEnds(final SimulateKeyboardUntilReleaseAction element) {
		element.keyPressed1Property().set(this.keySelectorControl1.valueProperty().get());
		element.keyPressed2Property().set(this.keySelectorControl2.valueProperty().get());
		element.keyPressed3Property().set(this.keySelectorControl3.valueProperty().get());
	}

}
