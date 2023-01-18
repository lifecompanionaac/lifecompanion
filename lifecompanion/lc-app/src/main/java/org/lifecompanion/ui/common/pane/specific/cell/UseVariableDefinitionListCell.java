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

package org.lifecompanion.ui.common.pane.specific.cell;

import org.lifecompanion.ui.common.control.specific.usevariable.UseVariableTextArea;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
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
            this.getStyleClass().add("soft-selection-cell");
            //Labels
            this.labelVariableName = new Label();
            this.labelVariableName.getStyleClass().addAll("text-fill-primary-dark");
            this.labelVariableDescription = new Label();
            this.labelVariableDescription.getStyleClass().addAll("text-wrap-enabled", "text-font-size-90", "text-fill-dimgrey");
            this.labelVariableDescription.setWrapText(true);
            this.labelVariableDescription.setMaxWidth(UseVariableTextArea.POP_WIDTH - 35.0);
            this.labelExample = new Label();
            this.labelExample.getStyleClass().addAll("text-wrap-enabled", "text-font-size-80", "text-fill-dimgrey", "text-font-italic");
            this.labelExample.setMaxWidth(UseVariableTextArea.POP_WIDTH - 35.0);
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
