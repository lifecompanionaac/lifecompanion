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
import org.lifecompanion.model.api.voicesynthesizer.VoiceSynthesizerI;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.stream.Collectors;

/**
 * List to display the voice synthesizer name, and also its informations.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VoiceSynthesizerDetailListCell extends ListCell<VoiceSynthesizerI> {

    private VBox boxGraphics;
    private Label labelSynthesizerName, labelSynthesizerDescription, labelSystems;

    public VoiceSynthesizerDetailListCell() {
        super();
        //Global
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.getStyleClass().add("soft-selection-cell");
        //Labels
        this.labelSynthesizerName = new Label();
        this.labelSynthesizerName.getStyleClass().add("text-fill-primary-dark");
        this.labelSynthesizerDescription = new Label();
        this.labelSynthesizerDescription.getStyleClass().addAll("text-wrap-enabled", "text-font-size-90", "text-fill-dimgrey");
        this.labelSystems = new Label();
        this.labelSystems.getStyleClass().addAll("text-wrap-enabled", "text-font-size-80", "text-fill-dimgrey", "text-font-italic");
        this.boxGraphics = new VBox();
        VBox.setMargin(this.labelSynthesizerDescription, new Insets(3, 2, 3, 2));
        this.boxGraphics.getChildren().addAll(this.labelSynthesizerName, this.labelSynthesizerDescription, this.labelSystems);
    }

    @Override
    protected void updateItem(final VoiceSynthesizerI item, final boolean empty) {
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
            //this.labelActionDescription.textProperty().bind(item.variableDescriptionProperty());
        }
    }
}
