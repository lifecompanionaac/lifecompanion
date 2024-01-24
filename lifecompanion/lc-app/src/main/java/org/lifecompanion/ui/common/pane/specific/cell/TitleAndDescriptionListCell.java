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

import java.util.function.Function;

public class TitleAndDescriptionListCell<T> extends ListCell<T> {
    private VBox boxGraphics;
    private Label labelTitle, labelDescription;
    private final Function<T, String> getTitle, getDescription;

    public TitleAndDescriptionListCell(Function<T, String> getTitle, Function<T, String> getDescription) {
        super();
        this.getTitle = getTitle;
        this.getDescription = getDescription;
        //Global
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.getStyleClass().add("soft-selection-cell");
        //Labels
        this.labelTitle = new Label();
        this.labelTitle.getStyleClass().addAll("text-fill-primary-dark");
        this.labelDescription = new Label();
        this.labelDescription.getStyleClass().addAll("text-wrap-enabled", "text-font-size-90", "text-fill-dimgrey");
        this.labelDescription.setMaxWidth(400.0);
        this.boxGraphics = new VBox();
        VBox.setMargin(this.labelDescription, new Insets(2, 1, 1, 1));
        this.boxGraphics.getChildren().addAll(this.labelTitle, this.labelDescription);
    }

    @Override
    protected void updateItem(final T item, final boolean emptyP) {
        super.updateItem(item, emptyP);
        if (item == null || emptyP) {
            this.setGraphic(null);
        } else {
            this.labelTitle.setText(getTitle.apply(item));
            this.labelDescription.setText(getDescription.apply(item));
            this.setGraphic(boxGraphics);
        }
    }
}
