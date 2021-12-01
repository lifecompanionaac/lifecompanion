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

package org.lifecompanion.config.view.keyoption.impl;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.config.data.action.impl.KeyOptionActions.ChangeInformationToDisplayAction;
import org.lifecompanion.base.data.component.keyoption.VariableInformationKeyOption;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.view.keyoption.BaseKeyOptionConfigView;
import org.lifecompanion.base.view.reusable.UndoRedoTextInputWrapper;
import org.lifecompanion.config.view.reusable.UseVariableTextArea;
import javafx.scene.control.Label;

public class VariableInformationOptionConfigView extends BaseKeyOptionConfigView<VariableInformationKeyOption> {

	private UseVariableTextArea fieldKeyInformationText;
	private UndoRedoTextInputWrapper fieldKeyInformationWrapper;

	@Override
	public Class<VariableInformationKeyOption> getConfiguredKeyOptionType() {
		return VariableInformationKeyOption.class;
	}

	@Override
	public void initUI() {
		super.initUI();
		Label labelInfo = new Label(Translation.getText("key.option.information.info.text"));
		this.fieldKeyInformationText = new UseVariableTextArea();
		this.fieldKeyInformationText.getTextArea().setPrefColumnCount(20);
		this.fieldKeyInformationWrapper = new UndoRedoTextInputWrapper(this.fieldKeyInformationText.getTextArea(),
				ConfigActionController.INSTANCE.undoRedoEnabled());
		this.getChildren().addAll(labelInfo, this.fieldKeyInformationText);

	}

	@Override
	public void initListener() {
		super.initListener();
		this.fieldKeyInformationWrapper.setListener((oldV, newV) -> {
			if (this.model.get() != null) {
				ConfigActionController.INSTANCE.addAction(new ChangeInformationToDisplayAction(this.model.get(), oldV, newV));
			}
		});

	}

	@Override
	public void initBinding() {
		super.initBinding();

	}

	@Override
	public void bind(final VariableInformationKeyOption model) {
		this.fieldKeyInformationText.textProperty().bindBidirectional(model.wantedDisplayedInformationProperty());
		this.fieldKeyInformationWrapper.clearPreviousValue();
	}

	@Override
	public void unbind(final VariableInformationKeyOption model) {
		this.fieldKeyInformationText.textProperty().unbindBidirectional(model.wantedDisplayedInformationProperty());
		this.fieldKeyInformationText.clear();
	}

}
