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

package org.lifecompanion.config.view.pane.general.view.predict4all.correction;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;

public class CorrectionCategoryDetailledListCell extends ListCell<CorrectionCategory> {

    private static final double ROW_WIDTH = 400.0, ROW_HEIGHT = 80.0;

    private VBox boxLabels;
    private Label labelModeName, labelModeDescription;

    public CorrectionCategoryDetailledListCell() {
        super();
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.getStyleClass().add("rule-category-list-cell");
        this.labelModeName = new Label();
        this.labelModeName.getStyleClass().add("rule-category-cell-name");
        this.labelModeDescription = new Label();
        this.labelModeDescription.getStyleClass().add("rule-category-cell-description");
        this.labelModeDescription.setWrapText(true);
        this.boxLabels = new VBox(this.labelModeName, this.labelModeDescription);
        this.setPrefWidth(CorrectionCategoryDetailledListCell.ROW_WIDTH);
        this.setMaxHeight(CorrectionCategoryDetailledListCell.ROW_HEIGHT);
    }

    @Override
    protected void updateItem(final CorrectionCategory item, final boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            this.labelModeDescription.setText(Translation.getText(item.getDescriptionId()));
            this.labelModeName.setText(Translation.getText(item.getNameId()));
            this.setGraphic(this.boxLabels);
        } else {
            this.setGraphic(null);
        }
    }

}
