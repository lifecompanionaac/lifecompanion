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

import org.lifecompanion.controller.resource.IconHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.impl.selectionmode.SelectionModeEnum;

/**
 * List cell to display a style mode with its detail.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SelectionModeDetailListCell extends ListCell<SelectionModeEnum> {
    private static final double ROW_WIDTH = 500.0, ROW_HEIGHT = 95.0;

    private VBox boxLabels;
    private BorderPane paneGraphics;
    private ImageView imageView;
    private Label labelModeName, labelModeDescription;

    public SelectionModeDetailListCell() {
        super();

        this.paneGraphics = new BorderPane();
        // Global
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.getStyleClass().add("soft-selection-cell");
        // Labels
        this.labelModeName = new Label();
        this.labelModeName.getStyleClass().addAll("text-fill-primary-dark");
        this.labelModeDescription = new Label();
        this.labelModeDescription.getStyleClass().addAll("text-wrap-enabled", "text-font-size-90", "text-fill-dimgrey");
        this.labelModeDescription.setWrapText(true);
        // Image
        this.imageView = new ImageView();
        this.imageView.setFitWidth(64);
        this.imageView.setPreserveRatio(true);
        BorderPane.setMargin(this.imageView, new Insets(5));

        // Add all
        this.boxLabels = new VBox();
        VBox.setMargin(this.labelModeDescription, new Insets(1.0, 0.0, 0.0, 0.0));
        this.paneGraphics = new BorderPane();
        BorderPane.setAlignment(this.imageView, Pos.CENTER);
        BorderPane.setMargin(this.imageView, new Insets(0, 10.0, 0, 0));
        this.boxLabels.getChildren().addAll(this.labelModeName, this.labelModeDescription);
        this.paneGraphics.setCenter(this.boxLabels);
        this.paneGraphics.setLeft(this.imageView);

        // Configuration
        this.setPrefWidth(ROW_WIDTH);
        this.setPrefHeight(ROW_HEIGHT);
        this.setGraphic(this.paneGraphics);
    }

    @Override
    protected void updateItem(final SelectionModeEnum item, final boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            this.labelModeDescription.setText(item.getDescription());
            this.labelModeName.setText(item.getName());
            this.imageView.setImage(IconHelper.get(SelectionModeEnum.ICON_URL_SELECTION_MODE + item.getLogoUrl()));
        }
    }

}
