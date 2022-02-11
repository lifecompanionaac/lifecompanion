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
import org.lifecompanion.model.api.textprediction.BasePredictorI;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.stream.Collectors;

/**
 * List cell to display a predictor information
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class BasePredictorDetailListCell<T extends BasePredictorI> extends ListCell<T> {

    private VBox boxGraphics;
    private Label labelSynthesizerName, labelSynthesizerDescription, labelSystems;

    public BasePredictorDetailListCell() {
        super();
        //Global
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.getStyleClass().add("voice-synthesizer-list-cell");
        //Labels
        this.labelSynthesizerName = new Label();
        this.labelSynthesizerName.getStyleClass().add("voice-synthesizer-cell-name");
        this.labelSynthesizerDescription = new Label();
        this.labelSynthesizerDescription.getStyleClass().add("voice-synthesizer-cell-description");
        this.labelSynthesizerDescription.setMaxWidth(300.0);
        this.labelSystems = new Label();
        this.labelSystems.getStyleClass().add("voice-synthesizer-cell-systems");
        this.boxGraphics = new VBox();
        VBox.setMargin(this.labelSynthesizerDescription, new Insets(5, 2, 3, 2));
        this.boxGraphics.getChildren().addAll(this.labelSynthesizerName, this.labelSynthesizerDescription, this.labelSystems);
    }

    @Override
    protected void updateItem(final T item, final boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            this.setGraphic(null);
        } else {
            this.setGraphic(this.boxGraphics);
            //Bind properties
            this.labelSynthesizerName.setText(item.getName());
            this.labelSynthesizerDescription.setText(item.getDescription());
            String systemString = item.getCompatibleSystems().stream().map(s -> Translation.getText(s.getLabelID()))
                    .collect(Collectors.joining(", "));
            this.labelSystems.setText(Translation.getText("voice.synthesizer.system.compatible", systemString));
        }
    }
}
