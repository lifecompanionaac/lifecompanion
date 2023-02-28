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
import org.lifecompanion.model.api.configurationcomponent.dynamickey.LinkType;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LinkTypeDetailListCell extends ListCell<LinkType> {

    private VBox boxGraphics;
    private Label labelName, labelDescription;

    public LinkTypeDetailListCell() {
        super();
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.getStyleClass().add("soft-selection-cell");
        this.labelName = new Label();
        this.labelName.getStyleClass().add("text-fill-primary-dark");
        this.labelDescription = new Label();
        this.labelDescription.getStyleClass().addAll("text-wrap-enabled", "text-font-size-90", "text-fill-dimgrey");
        this.labelDescription.setMaxWidth(300.0);
        this.boxGraphics = new VBox();
        VBox.setMargin(this.labelDescription, new Insets(3, 2, 3, 2));
        this.boxGraphics.getChildren().addAll(this.labelName, this.labelDescription);
    }

    @Override
    protected void updateItem(final LinkType item, final boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            this.setGraphic(null);
        } else {
            this.setGraphic(this.boxGraphics);
            this.labelName.setText(item.getName());
            this.labelDescription.setText(item.getDescription());
        }
    }
}
