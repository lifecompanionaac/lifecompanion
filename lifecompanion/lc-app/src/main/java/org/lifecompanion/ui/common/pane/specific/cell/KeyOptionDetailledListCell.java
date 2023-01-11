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

import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;

public class KeyOptionDetailledListCell extends ListCell<KeyOptionI> {
    private VBox boxGraphics;
    private Label labelKeyOptionName, labelKeyOptionDescription;

    public KeyOptionDetailledListCell() {
        super();
        //Global
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.getStyleClass().add("soft-selection-cell");
        //Labels
        this.labelKeyOptionName = new Label();
        this.labelKeyOptionName.getStyleClass().addAll("text-fill-primary-dark");
        this.labelKeyOptionDescription = new Label();
        this.labelKeyOptionDescription.getStyleClass().addAll("text-wrap-enabled", "text-font-size-90", "text-fill-dimgrey");
        this.labelKeyOptionDescription.setMaxWidth(400.0);
        this.boxGraphics = new VBox();
        VBox.setMargin(this.labelKeyOptionDescription, new Insets(2, 1, 1, 1));
        this.boxGraphics.getChildren().addAll(this.labelKeyOptionName, this.labelKeyOptionDescription);
    }

    @Override
    protected void updateItem(final KeyOptionI itemP, final boolean emptyP) {
        super.updateItem(itemP, emptyP);
        if (itemP == null || emptyP) {
            this.setGraphic(null);
        } else {
            this.labelKeyOptionName.setText(itemP.getOptionName());
            this.labelKeyOptionDescription.setText(itemP.getOptionDescription());
            this.setGraphic(boxGraphics);
        }
    }
}
