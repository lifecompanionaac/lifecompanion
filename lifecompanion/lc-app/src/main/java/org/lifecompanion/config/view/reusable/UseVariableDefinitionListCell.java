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

package org.lifecompanion.config.view.reusable;

import org.lifecompanion.api.component.definition.useevent.UseVariableDefinitionI;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class UseVariableDefinitionListCell extends ListCell<UseVariableDefinitionI> {
	private final boolean titleOnly;
	private VBox boxGraphics;
	private Label labelVariableName, labelVariableDescription, labelExample;

	public UseVariableDefinitionListCell(boolean titleOnly) {
		super();
		this.titleOnly = titleOnly;
		if (!this.titleOnly) {
			//Global
			this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			this.getStyleClass().add("use-variable-list-cell");
			//Labels
			this.labelVariableName = new Label();
			this.labelVariableName.getStyleClass().add("use-variable-cell-name");
			this.labelVariableDescription = new Label();
			this.labelVariableDescription.getStyleClass().add("use-variable-cell-description");
			this.labelVariableDescription.setWrapText(true);
			this.labelVariableDescription.setMaxWidth(UseVariableTextArea.POP_WIDTH - 35.0);
			this.labelExample = new Label();
			this.labelExample.getStyleClass().add("use-variable-cell-example");
			this.boxGraphics = new VBox();
			this.boxGraphics.getChildren().addAll(this.labelVariableName, this.labelExample, this.labelVariableDescription);
		}
	}

	public UseVariableDefinitionListCell() {
		this(false);
	}

	@Override
	protected void updateItem(final UseVariableDefinitionI item, final boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			this.setGraphic(null);
			this.setText(null);
		} else {
			if (this.titleOnly) {
				this.setText(item.getName() + " (" + item.getId() + ")");
			} else {
				this.setGraphic(this.boxGraphics);
				this.labelVariableName.setText(item.getName());
				this.labelVariableDescription.setText(item.getDescription());
				this.labelExample.setText(item.getId() + " - " + item.getExampleValueToString());
			}
		}
	}

}
