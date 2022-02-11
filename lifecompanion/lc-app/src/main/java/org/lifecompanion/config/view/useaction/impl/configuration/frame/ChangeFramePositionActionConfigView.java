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

package org.lifecompanion.config.view.useaction.impl.configuration.frame;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.FramePosition;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.ChangeFramePositionAction;
import org.lifecompanion.config.view.pane.tabs.style.cell.FramePositionDetailledCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ChangeFramePositionActionConfigView extends VBox implements UseActionConfigurationViewI<ChangeFramePositionAction> {

	private ComboBox<FramePosition> comboboxFramePosition;

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public Class<ChangeFramePositionAction> getConfiguredActionType() {
		return ChangeFramePositionAction.class;
	}

	@Override
	public void initUI() {
		this.comboboxFramePosition = new ComboBox<>(FXCollections.observableArrayList(FramePosition.values()));
		this.comboboxFramePosition.setCellFactory((lv) -> new FramePositionDetailledCell());
		this.comboboxFramePosition.setButtonCell(new FramePositionDetailledCell());
		this.comboboxFramePosition.setMaxWidth(Double.MAX_VALUE);
		Label labelFramePosition = new Label(Translation.getText("frame.position.for.action"));
		this.getChildren().addAll(labelFramePosition, this.comboboxFramePosition);
	}

	@Override
	public void editStarts(final ChangeFramePositionAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		this.comboboxFramePosition.getSelectionModel().select(element.framePositionProperty().get());
	}

	@Override
	public void editEnds(final ChangeFramePositionAction element) {
		element.framePositionProperty().set(this.comboboxFramePosition.getSelectionModel().getSelectedItem());
	}
}
