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

package org.lifecompanion.config.view.pane.general.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStep;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStepViewI;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class CharPredictionConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private LCConfigurationI model;
    private TextField fieldCharPrediction;

    public CharPredictionConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return false;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.char.predictor.step.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.CHAR_PREDICTION_CONFIGURATION.name();
    }

    @Override
    public String getMenuStepToSelect() {
        return GeneralConfigurationStep.PREDICTIONS_CONFIGURATION.name();
    }

    @Override
    public String getPreviousStep() {
        return getMenuStepToSelect();
    }

    @Override
    public Node getViewNode() {
        return this;
    }


    // UI
    //========================================================================
    @Override
    public void initUI() {
        //Grid layout
        GridPane gridPaneTotal = new GridPane();
        gridPaneTotal.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneTotal.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        gridPaneTotal.setAlignment(Pos.TOP_CENTER);

        Label labelCharSpace = new Label(Translation.getText("char.prediction.string.for.space"));
        labelCharSpace.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        this.fieldCharPrediction = new TextField();
        fieldCharPrediction.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(fieldCharPrediction, Priority.ALWAYS);
        Label labelGenPartTitle = UIUtils.createTitleLabel(Translation.getText("general.configuration.char.predictor.general.configuration.part"));

        int gridRowIndex = 0;
        gridPaneTotal.add(labelGenPartTitle, 0, gridRowIndex++, 2, 1);
        gridPaneTotal.add(labelCharSpace, 0, gridRowIndex);
        gridPaneTotal.add(fieldCharPrediction, 1, gridRowIndex++);

        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        this.setCenter(gridPaneTotal);
    }
    //========================================================================

    @Override
    public void saveChanges() {
        model.getPredictionParameters().charPredictionSpaceCharProperty().set(fieldCharPrediction.getText());
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        this.fieldCharPrediction.setText(model.getPredictionParameters().charPredictionSpaceCharProperty().get());
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.model = null;
    }


}
