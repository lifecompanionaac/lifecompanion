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

package org.lifecompanion.config.view.useaction.impl.speak.parameters;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.ChangeVoiceParameterAction;
import org.lifecompanion.model.api.voicesynthesizer.VoiceAndSynthesizerInfoI;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Base action configuration view for {@link ChangeVoiceParameterAction}
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class BaseChangeVoiceParameterConfigView<T extends ChangeVoiceParameterAction> extends VBox
		implements UseActionConfigurationViewI<T> {
	private ComboBox<VoiceAndSynthesizerInfoI> selectedVoice;

	public BaseChangeVoiceParameterConfigView() {}

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public void editStarts(final T action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		//Select by voice and synthesizer ID (because it's not the same instance after deserialization)
		VoiceAndSynthesizerInfoI voiceInfo = action.selectedVoiceProperty().get();
		if (voiceInfo != null) {
			this.selectedVoice.getItems().stream().filter((info) -> {
				return StringUtils.isEquals(info.getVoiceId(), voiceInfo.getVoiceId())
						&& StringUtils.isEquals(info.getSynthesizerId(), voiceInfo.getSynthesizerId());
			}).findFirst().ifPresent((toSelect) -> this.selectedVoice.getSelectionModel().select(toSelect));
		} else {
			this.selectedVoice.getSelectionModel().clearSelection();
		}
	}

	@Override
	public void editEnds(final ChangeVoiceParameterAction action) {
		action.selectedVoiceProperty().set(this.selectedVoice.getSelectionModel().getSelectedItem());
	}

	@Override
	public void initUI() {
		this.selectedVoice = new ComboBox<>(VoiceSynthesizerController.INSTANCE.getAllVoice());
		Label labelVoice = new Label(Translation.getText("change.voice.selected.voice"));
		this.getChildren().addAll(labelVoice, this.selectedVoice);
	}

}
