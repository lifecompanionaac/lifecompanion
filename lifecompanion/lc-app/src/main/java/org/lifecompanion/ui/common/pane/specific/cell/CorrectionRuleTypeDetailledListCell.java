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

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.predict4all.nlp.language.french.FrenchDefaultCorrectionRuleGenerator.CorrectionRuleType;

public class CorrectionRuleTypeDetailledListCell extends ListCell<CorrectionRuleType> {

    private static final double ROW_WIDTH = 400.0, ROW_HEIGHT = 80.0;

    private VBox boxLabels;
    private Label labelRuleName, labelRuleDescription, labelRuleExample;

    public CorrectionRuleTypeDetailledListCell() {
        super();
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.getStyleClass().add("rule-category-list-cell");
        this.labelRuleName = new Label();
        this.labelRuleName.getStyleClass().add("rule-category-cell-name");
        this.labelRuleDescription = new Label();
        this.labelRuleDescription.getStyleClass().add("rule-category-cell-description");
        this.labelRuleDescription.setWrapText(true);
        this.labelRuleExample = new Label();
        this.labelRuleExample.getStyleClass().add("rule-category-cell-example");
        this.labelRuleExample.setWrapText(true);
        this.boxLabels = new VBox(this.labelRuleName, this.labelRuleDescription, this.labelRuleExample);
        this.setPrefWidth(CorrectionRuleTypeDetailledListCell.ROW_WIDTH);
        this.setMaxHeight(CorrectionRuleTypeDetailledListCell.ROW_HEIGHT);
    }

    @Override
    protected void updateItem(final CorrectionRuleType item, final boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            this.labelRuleDescription.setText(Translation.getText(item.getDescriptionId()));
            this.labelRuleName.setText(Translation.getText(item.getNameId()));
            this.labelRuleExample.setText(Translation.getText(item.getExampleId()).replace("\n", "  |  "));
            this.setGraphic(this.boxLabels);
        } else {
            this.setGraphic(null);
        }
    }

}
