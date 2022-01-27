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

import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.glyphfont.FontAwesome;

import org.lifecompanion.base.data.control.refacto.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.config.data.action.impl.KeyOptionActions.ChangeEnableSpeakAction;
import org.lifecompanion.config.data.action.impl.KeyOptionActions.ChangeQuickComAddSpaceAction;
import org.lifecompanion.config.data.action.impl.KeyOptionActions.ChangeTextToSpeakAction;
import org.lifecompanion.config.data.action.impl.KeyOptionActions.ChangeTextToWriteAction;
import org.lifecompanion.config.data.common.LCConfigBindingUtils;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.component.keyoption.QuickComKeyOption;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.base.data.voice.VoiceSynthesizerController;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.keyoption.BaseKeyOptionConfigView;
import org.lifecompanion.base.view.reusable.UndoRedoTextInputWrapper;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class QuickComOptionConfigView extends BaseKeyOptionConfigView<QuickComKeyOption> {

	private ToggleSwitch toggleEnableSpaceAfter, toggleEnableSpeak;
	private TextField fieldTextToWrite, fieldTextToSpeak;
	private UndoRedoTextInputWrapper fieldTextToWriteWrapper, fieldTextToSpeakWrapper;

	private ChangeListener<Boolean> changeListenerAddSpaceAfter, changeListenerEnableSpeak;
	private ChangeListener<String> changeListenerTextToWrite;

	private Button buttonPlayExample;

	@Override
	public Class<QuickComKeyOption> getConfiguredKeyOptionType() {
		return QuickComKeyOption.class;
	}

	@Override
	public void initUI() {
		super.initUI();
		//Fields
		this.toggleEnableSpaceAfter = ConfigUIUtils.createToggleSwitch("quick.communication.enable.space",
				"tooltip.explain.quick.communication.space");
		this.toggleEnableSpeak = ConfigUIUtils.createToggleSwitch("quick.communication.enable.speak", "tooltip.explain.quick.communication.speak");
		Label labelWrite = new Label(Translation.getText("quick.communication.text.to.write"));
		Label labelSpeak = new Label(Translation.getText("quick.communication.text.to.speak"));
		this.fieldTextToWrite = new TextField();
		UIUtils.createAndAttachTooltip(fieldTextToWrite, "tooltip.explain.quick.communication.text.write");
		this.fieldTextToSpeak = new TextField();
		UIUtils.createAndAttachTooltip(fieldTextToSpeak, "tooltip.explain.quick.communication.text.speak");

		//Play example
		this.buttonPlayExample = UIUtils.createGraphicButton(
				LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_UP).size(12).color(LCGraphicStyle.MAIN_PRIMARY),
				"tooltip.quick.com.play.example");

		//Undo/redo wrapper
		this.fieldTextToWriteWrapper = new UndoRedoTextInputWrapper(this.fieldTextToWrite, ConfigActionController.INSTANCE.undoRedoEnabled());
		this.fieldTextToSpeakWrapper = new UndoRedoTextInputWrapper(this.fieldTextToSpeak, ConfigActionController.INSTANCE.undoRedoEnabled());

		//Layout
		GridPane gridPane = new GridPane();
		gridPane.setVgap(4.0);
		gridPane.setHgap(5.0);
		gridPane.add(labelWrite, 0, 0);
		gridPane.add(this.fieldTextToWrite, 1, 0);
		gridPane.add(labelSpeak, 0, 1);
		gridPane.add(this.fieldTextToSpeak, 1, 1);
		gridPane.add(this.buttonPlayExample, 2, 1);
		gridPane.add(this.toggleEnableSpeak, 0, 2, 2, 1);
		gridPane.add(this.toggleEnableSpaceAfter, 0, 3, 2, 1);
		this.getChildren().addAll(gridPane);
	}

	@Override
	public void initListener() {
		super.initListener();
		this.fieldTextToWriteWrapper.setListener((oldV, newV) -> {
			if (this.model.get() != null) {
				ConfigActionController.INSTANCE.addAction(new ChangeTextToWriteAction(this.model.get(), oldV, newV));
			}
		});
		this.fieldTextToSpeakWrapper.setListener((oldV, newV) -> {
			if (this.model.get() != null) {
				ConfigActionController.INSTANCE.addAction(new ChangeTextToSpeakAction(this.model.get(), oldV, newV));
			}
		});
		this.buttonPlayExample.setOnAction(a -> {
			VoiceSynthesizerController.INSTANCE.speakAsync(this.fieldTextToSpeak.getText(),
					AppModeController.INSTANCE.getEditModeContext().configurationProperty().get().getVoiceSynthesizerParameter(), null);
		});

	}

	@Override
	public void initBinding() {
		super.initBinding();
		this.fieldTextToSpeak.disableProperty().bind(this.toggleEnableSpeak.selectedProperty().not());
		this.buttonPlayExample.disableProperty().bind(this.fieldTextToSpeak.disableProperty());
		this.changeListenerAddSpaceAfter = LCConfigBindingUtils.createSimpleBinding(this.toggleEnableSpaceAfter.selectedProperty(), this.model,
				m -> m.addSpaceProperty().get(), ChangeQuickComAddSpaceAction::new);
		this.changeListenerEnableSpeak = LCConfigBindingUtils.createSimpleBinding(this.toggleEnableSpeak.selectedProperty(), this.model,
				m -> m.enableSpeakProperty().get(), ChangeEnableSpeakAction::new);
		//This listener allows user to see the end of the text while typing text
		this.changeListenerTextToWrite = (obs, ov, nv) -> {
			//When text to write change, move the caret to the end of text to speak caret
			String text = this.fieldTextToSpeak.getText();
			if (text != null && StringUtils.isEquals(text, nv)) {
				this.fieldTextToSpeak.positionCaret(text.length());
			}
			//When key text change (and text to write is not edited)
			if (!this.fieldTextToWrite.isFocused()) {
				String textToWrite = this.fieldTextToWrite.getText();
				if (text != null) {
					this.fieldTextToWrite.positionCaret(textToWrite.length());
				}
			}
		};
	}

	@Override
	public void bind(final QuickComKeyOption model) {
		this.fieldTextToWrite.textProperty().bindBidirectional(model.textToWriteProperty());
		model.textToWriteProperty().addListener(this.changeListenerTextToWrite);
		this.fieldTextToWriteWrapper.clearPreviousValue();
		this.fieldTextToSpeak.textProperty().bindBidirectional(model.textToSpeakProperty());
		this.fieldTextToSpeakWrapper.clearPreviousValue();
		this.toggleEnableSpaceAfter.setSelected(model.addSpaceProperty().get());
		this.toggleEnableSpeak.setSelected(model.enableSpeakProperty().get());
		model.enableSpeakProperty().addListener(this.changeListenerEnableSpeak);
		model.addSpaceProperty().addListener(this.changeListenerAddSpaceAfter);
	}

	@Override
	public void unbind(final QuickComKeyOption model) {
		this.fieldTextToWrite.textProperty().unbindBidirectional(model.textToWriteProperty());
		model.textToWriteProperty().removeListener(this.changeListenerTextToWrite);
		this.fieldTextToWrite.clear();
		this.fieldTextToSpeak.textProperty().unbindBidirectional(model.textToSpeakProperty());
		this.fieldTextToSpeak.clear();
		model.enableSpeakProperty().removeListener(this.changeListenerEnableSpeak);
		model.addSpaceProperty().removeListener(this.changeListenerAddSpaceAfter);
	}

}
