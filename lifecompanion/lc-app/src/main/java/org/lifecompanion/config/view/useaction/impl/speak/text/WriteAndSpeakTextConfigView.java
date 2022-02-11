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

package org.lifecompanion.config.view.useaction.impl.speak.text;

import org.controlsfx.control.ToggleSwitch;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.WriteAndSpeakTextAction;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.reusable.UseVariableTextArea;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

/**
 * Action configuration view for {@link WriteAndSpeakTextAction}
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WriteAndSpeakTextConfigView extends GridPane implements UseActionConfigurationViewI<WriteAndSpeakTextAction> {

	private ToggleSwitch toggleEnableSpaceAfter, toggleEnableSpeak;
	private UseVariableTextArea fieldTextToWrite, fieldTextToSpeak;

	@Override
	public void initUI() {
		this.toggleEnableSpaceAfter = ConfigUIUtils.createToggleSwitch("quick.communication.enable.space", null);
		this.toggleEnableSpeak = ConfigUIUtils.createToggleSwitch("quick.communication.enable.speak", null);
		Label labelWrite = new Label(Translation.getText("quick.communication.text.to.write"));
		Label labelSpeak = new Label(Translation.getText("quick.communication.text.to.speak"));
		this.fieldTextToWrite = new UseVariableTextArea();
		this.fieldTextToSpeak = new UseVariableTextArea();

		//Add
		this.setVgap(4);
		this.setHgap(120.0);
		this.add(labelWrite, 0, 0);
		this.add(this.fieldTextToWrite, 1, 0);
		this.add(labelSpeak, 0, 1);
		this.add(this.fieldTextToSpeak, 1, 1);
		this.add(this.toggleEnableSpeak, 0, 2, 2, 1);
		this.add(this.toggleEnableSpaceAfter, 0, 3, 2, 1);
		this.setAlignment(Pos.TOP_CENTER);
		this.setMaxWidth(Double.MAX_VALUE);

		//Binding
		this.fieldTextToWrite.textProperty().addListener((obs, ov, nv) -> {
			if (StringUtils.isEquals(ov, this.fieldTextToSpeak.getText())) {
				this.fieldTextToSpeak.setText(nv);
			}
		});
		this.fieldTextToSpeak.disableProperty().bind(this.toggleEnableSpeak.selectedProperty().not());
	}

	@Override
	public void editStarts(final WriteAndSpeakTextAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		this.fieldTextToWrite.clear();
		this.fieldTextToSpeak.clear();
		this.fieldTextToWrite.setAvailableUseVariable(possibleVariables);
		this.fieldTextToSpeak.setAvailableUseVariable(possibleVariables);
		this.fieldTextToWrite.setText(action.textToWriteProperty().get());
		this.fieldTextToSpeak.setText(action.textToSpeakProperty().get());
		this.toggleEnableSpaceAfter.setSelected(action.addSpaceProperty().get());
		this.toggleEnableSpeak.setSelected(action.enableSpeakProperty().get());
	}

	@Override
	public void editEnds(final WriteAndSpeakTextAction action) {
		action.textToWriteProperty().set(this.fieldTextToWrite.getText());
		action.textToSpeakProperty().set(this.fieldTextToSpeak.getText());
		action.addSpaceProperty().set(this.toggleEnableSpaceAfter.isSelected());
		action.enableSpeakProperty().set(this.toggleEnableSpeak.isSelected());
	}

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public Class<WriteAndSpeakTextAction> getConfiguredActionType() {
		return WriteAndSpeakTextAction.class;
	}

}
