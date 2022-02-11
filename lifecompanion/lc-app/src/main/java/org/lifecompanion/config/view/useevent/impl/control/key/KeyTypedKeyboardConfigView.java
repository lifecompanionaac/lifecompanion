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

package org.lifecompanion.config.view.useevent.impl.control.key;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.model.impl.categorizedelement.useevent.available.KeyTypedKeyboardEventGenerator;
import org.lifecompanion.config.view.reusable.KeySelectorControl;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class KeyTypedKeyboardConfigView extends VBox implements UseEventGeneratorConfigurationViewI<KeyTypedKeyboardEventGenerator> {

	private KeySelectorControl keySelectorControl;

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public Class<KeyTypedKeyboardEventGenerator> getConfiguredActionType() {
		return KeyTypedKeyboardEventGenerator.class;
	}

	@Override
	public void initUI() {
		this.keySelectorControl = new KeySelectorControl(Translation.getText("use.event.key.pressed.select.key.label"));
		Label labelExplain = new Label(Translation.getText("use.event.key.pressed.select.no.key.explain"));
		labelExplain.setWrapText(true);
		this.getChildren().addAll(this.keySelectorControl, labelExplain);
	}

	@Override
	public void editEnds(final KeyTypedKeyboardEventGenerator element) {
		element.keyPressedProperty().set(this.keySelectorControl.valueProperty().get());
	}

	@Override
	public void editStarts(final KeyTypedKeyboardEventGenerator element) {
		this.keySelectorControl.valueProperty().set(element.keyPressedProperty().get());
	}

}
