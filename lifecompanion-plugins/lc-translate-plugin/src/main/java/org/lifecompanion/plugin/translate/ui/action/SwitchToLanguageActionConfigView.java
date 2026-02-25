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

package org.lifecompanion.plugin.translate.ui.action;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.configurationcomponent.FramePosition;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.ChangeFramePositionAction;
import org.lifecompanion.plugin.translate.model.useaction.AvailableTranslation;
import org.lifecompanion.plugin.translate.model.useaction.SwitchToLanguageAction;
import org.lifecompanion.ui.common.pane.specific.cell.FramePositionDetailledCell;
import org.lifecompanion.ui.common.pane.specific.cell.SimpleTextListCell;

public class SwitchToLanguageActionConfigView extends VBox implements UseActionConfigurationViewI<SwitchToLanguageAction> {

	private ComboBox<AvailableTranslation> comboboxTargetLanguage;

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public Class<SwitchToLanguageAction> getConfiguredActionType() {
		return SwitchToLanguageAction.class;
	}

	@Override
	public void initUI() {
		this.comboboxTargetLanguage = new ComboBox<>(FXCollections.observableArrayList(AvailableTranslation.values()));
		this.comboboxTargetLanguage.setCellFactory((lv) -> new SimpleTextListCell<>(AvailableTranslation::getTranslation));
		this.comboboxTargetLanguage.setButtonCell(new SimpleTextListCell<>(AvailableTranslation::getTranslation));
		this.comboboxTargetLanguage.setMaxWidth(Double.MAX_VALUE);
		Label labelFramePosition = new Label(Translation.getText("lc.translate.plugin.field.target.language"));
		this.getChildren().addAll(labelFramePosition, this.comboboxTargetLanguage);
	}

	@Override
	public void editStarts(final SwitchToLanguageAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		this.comboboxTargetLanguage.getSelectionModel().select(element.targetLanguageProperty().get());
	}

	@Override
	public void editEnds(final SwitchToLanguageAction element) {
		element.targetLanguageProperty().set(this.comboboxTargetLanguage.getSelectionModel().getSelectedItem());
	}
}
