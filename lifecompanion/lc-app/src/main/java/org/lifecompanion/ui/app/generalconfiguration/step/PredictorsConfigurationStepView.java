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

package org.lifecompanion.ui.app.generalconfiguration.step;

import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.textprediction.CharPredictorI;
import org.lifecompanion.model.api.textprediction.WordPredictorI;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.textprediction.AutoCharPredictionController;
import org.lifecompanion.controller.textprediction.WordPredictionController;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.controller.resource.LCGlyphFont;
import org.lifecompanion.ui.common.pane.specific.cell.BasePredictorDetailListCell;
import org.lifecompanion.ui.common.pane.specific.cell.BasePredictorSimpleListCell;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class PredictorsConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private LCConfigurationI model;

    private ComboBox<WordPredictorI> comboboxWordPredictor;
    private ComboBox<CharPredictorI> comboboxCharPredictor;

    private Button buttonWordPredictionConfiguration, buttonCharPredictionConfiguration;


    public PredictorsConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.predictors.step.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.PREDICTIONS_CONFIGURATION.name();
    }

    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }


    // UI
    //========================================================================
    @Override
    public void initUI() {
        //Word prediction
        Label labelPartWP = UIUtils.createTitleLabel(Translation.getText("general.configuration.predictors.part.word.predictor"));
        Label labelWordPredictor = new Label(Translation.getText("word.prediction.engine"));
        labelWordPredictor.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        this.comboboxWordPredictor = new ComboBox<>(WordPredictionController.INSTANCE.getAvailablePredictor());
        this.comboboxWordPredictor.setCellFactory((lv) -> new BasePredictorDetailListCell<>());
        this.comboboxWordPredictor.setButtonCell(new BasePredictorSimpleListCell<>());
        this.comboboxWordPredictor.setMaxWidth(Double.MAX_VALUE);
        this.buttonWordPredictionConfiguration = UIUtils.createRightTextButton(Translation.getText("general.configuration.predictors.button.configure.word.prediction"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).size(20).color(LCGraphicStyle.MAIN_DARK),
                null);
        GridPane.setHalignment(buttonWordPredictionConfiguration, HPos.CENTER);

        //Letter prediction
        Label labelPartCP = UIUtils.createTitleLabel(Translation.getText("general.configuration.predictors.part.char.predictor"));
        Label labelCharPredictor = new Label(Translation.getText("char.prediction.engine"));
        this.comboboxCharPredictor = new ComboBox<>(AutoCharPredictionController.INSTANCE.getAvailablePredictor());
        this.comboboxCharPredictor.setCellFactory((lv) -> new BasePredictorDetailListCell<>());
        this.comboboxCharPredictor.setButtonCell(new BasePredictorSimpleListCell<>());
        this.comboboxCharPredictor.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(comboboxCharPredictor, Priority.ALWAYS);
        this.buttonCharPredictionConfiguration = UIUtils.createRightTextButton(Translation.getText("general.configuration.predictors.button.configure.char.prediction"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).size(20).color(LCGraphicStyle.MAIN_DARK),
                null);
        GridPane.setHalignment(buttonCharPredictionConfiguration, HPos.CENTER);

        //Grid layout
        GridPane grid = new GridPane();
        grid.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        grid.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        grid.setAlignment(Pos.TOP_CENTER);

        int gridRowIndex = 0;
        grid.add(labelPartWP, 0, gridRowIndex++, 2, 1);
        grid.add(labelWordPredictor, 0, gridRowIndex);
        grid.add(comboboxWordPredictor, 1, gridRowIndex++);
        grid.add(buttonWordPredictionConfiguration, 0, gridRowIndex++, 2, 1);
        grid.add(labelPartCP, 0, gridRowIndex++, 2, 1);
        grid.add(labelCharPredictor, 0, gridRowIndex);
        grid.add(comboboxCharPredictor, 1, gridRowIndex++);
        grid.add(buttonCharPredictionConfiguration, 0, gridRowIndex++, 2, 1);

        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        this.setCenter(grid);
    }

    @Override
    public void initListener() {
        buttonWordPredictionConfiguration.setOnAction(e -> {
            String stepId = comboboxWordPredictor.getValue() != null ? comboboxWordPredictor.getValue().getConfigStepId() : null;
            if (stepId != null) {
                GeneralConfigurationController.INSTANCE.showStep(stepId);
            }
        });
        buttonCharPredictionConfiguration.setOnAction(e -> GeneralConfigurationController.INSTANCE.showStep(GeneralConfigurationStep.CHAR_PREDICTION_CONFIGURATION));
    }

    @Override
    public void initBinding() {
        buttonWordPredictionConfiguration.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            return comboboxWordPredictor.getValue() == null || comboboxWordPredictor.getValue().getConfigStepId() == null;
        }, comboboxWordPredictor.valueProperty()));
    }
    //========================================================================

    @Override
    public void saveChanges() {
        model.getPredictionParameters().selectedWordPredictorIdProperty().set(comboboxWordPredictor.getValue().getId());
        model.getPredictionParameters().selectedCharPredictorIdProperty().set(comboboxCharPredictor.getValue().getId());
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        this.comboboxWordPredictor.getSelectionModel()
                .select(WordPredictionController.INSTANCE.getPredictorForId(model.getPredictionParameters().selectedWordPredictorIdProperty().get()));
        this.comboboxCharPredictor.getSelectionModel().select(
                AutoCharPredictionController.INSTANCE.getPredictorForId(model.getPredictionParameters().selectedCharPredictorIdProperty().get()));
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.model = null;
    }


}
