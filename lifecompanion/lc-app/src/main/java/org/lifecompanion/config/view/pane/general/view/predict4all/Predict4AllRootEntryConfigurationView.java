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

package org.lifecompanion.config.view.pane.general.view.predict4all;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.prediction.predict4all.P4AConfigurationSteps;
import org.lifecompanion.base.data.prediction.predict4all.predictor.Predict4AllWordPredictorHelper;
import org.lifecompanion.base.data.prediction.predict4all.predictor.PredictorModelDto;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStep;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStepViewI;
import org.lifecompanion.config.data.component.general.GeneralConfigurationController;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.view.pane.general.view.predict4all.correction.MinCountToFireListCell;
import org.lifecompanion.framework.commons.translation.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Main configuration view for predict4all word predictor.
 */
public class Predict4AllRootEntryConfigurationView extends VBox implements GeneralConfigurationStepViewI {
    private static final Logger LOGGER = LoggerFactory.getLogger(Predict4AllRootEntryConfigurationView.class);

    private static final ObservableList<Integer> COUNT_FIRE_PRED_CORR = FXCollections.observableArrayList(0, 1, 2, 3, 4);

    private ToggleSwitch toggleDynamicModelEnabled, toggleAddNewWord;
    private Spinner<Integer> spinnerMinUseCountToValidateNewWord;
    private ToggleSwitch toggleEnableCorrection;
    private Label labelMinCountToAddNewWords;
    private ComboBox<Integer> comboboxMinCountPrediction, comboboxMinCountCorrection;
    private Button buttonDictionaryConfig, buttonCorrectionConfig, buttonTrainingConfig, buttonTestingConfig;

    // Synchronized runtime modified configuration between tabs (injected to sub steps)
    private PredictorModelDto predictorModelDto;
    private LCConfigurationI currentConfiguration;

    public Predict4AllRootEntryConfigurationView() {
        initAll();
    }

    @Override
    public String getMenuStepToSelect() {
        return GeneralConfigurationStep.PREDICTIONS_CONFIGURATION.name();
    }

    @Override
    public void initUI() {
        // General configuration
        this.toggleDynamicModelEnabled = new ToggleSwitch(Translation.getText("predict4all.config.enable.dynamic.model"));
        this.toggleAddNewWord = new ToggleSwitch(Translation.getText("predict4all.config.learn.new.words"));
        this.spinnerMinUseCountToValidateNewWord = UIUtils.createIntSpinner(1, 100, 4, 1, 150.0);
        this.labelMinCountToAddNewWords = new Label(Translation.getText("predict4all.spinner.min.count.validate"));
        HBox boxSpinnerCountValidateWords = new HBox(5.0, this.labelMinCountToAddNewWords, this.spinnerMinUseCountToValidateNewWord);
        this.labelMinCountToAddNewWords.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.labelMinCountToAddNewWords, Priority.ALWAYS);
        boxSpinnerCountValidateWords.setAlignment(Pos.CENTER);

        Label labelMinCountPrediction = new Label(Translation.getText("predict4all.spinner.min.count.display.prediction"));
        HBox boxSpinnerMinCountPred = new HBox(5.0, labelMinCountPrediction, this.comboboxMinCountPrediction = this.createComboboxMinCount());
        labelMinCountPrediction.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelMinCountPrediction, Priority.ALWAYS);

        buttonDictionaryConfig = UIUtils.createRightTextButton(Translation.getText("predict4all.action.button.dictionary.configuration"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).size(20).color(LCGraphicStyle.MAIN_DARK),
                "predict4all.action.button.dictionary.configuration.explain");
        buttonDictionaryConfig.setAlignment(Pos.CENTER);
        buttonDictionaryConfig.setMaxWidth(Double.MAX_VALUE);

        buttonTrainingConfig = UIUtils.createRightTextButton(Translation.getText("predict4all.action.button.training.configuration"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).size(20).color(LCGraphicStyle.MAIN_DARK),
                "predict4all.action.button.training.configuration.explain");
        buttonTrainingConfig.setAlignment(Pos.CENTER);
        buttonTrainingConfig.setMaxWidth(Double.MAX_VALUE);

        this.toggleDynamicModelEnabled.setMaxWidth(Double.MAX_VALUE);
        this.toggleAddNewWord.setMaxWidth(Double.MAX_VALUE);

        this.toggleEnableCorrection = new ToggleSwitch(Translation.getText("predict4all.config.enable.correction"));
        this.toggleEnableCorrection.setMaxWidth(Double.MAX_VALUE);

        buttonCorrectionConfig = UIUtils.createRightTextButton(Translation.getText("predict4all.action.button.correction.configuration"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).size(20).color(LCGraphicStyle.MAIN_DARK),
                "predict4all.action.button.correction.configuration.explain");

        buttonCorrectionConfig.setAlignment(Pos.CENTER);
        buttonCorrectionConfig.setMaxWidth(Double.MAX_VALUE);

        Label labelMinCountCorrection = new Label(Translation.getText("predict4all.spinner.min.count.provide.correction"));
        HBox boxSpinnerMinCountCorr = new HBox(5.0, labelMinCountCorrection, this.comboboxMinCountCorrection = this.createComboboxMinCount());
        labelMinCountCorrection.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelMinCountCorrection, Priority.ALWAYS);
        boxSpinnerMinCountCorr.setAlignment(Pos.CENTER);

        buttonTestingConfig = UIUtils.createSimpleTextButton(Translation.getText("predict4all.action.button.testing.configuration"),
                "predict4all.action.button.testing.configuration.explain");
        buttonTestingConfig.setAlignment(Pos.CENTER);
        buttonTestingConfig.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(buttonTestingConfig, new Insets(20.0, 0, 0, 0));

        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        this.setSpacing(8.0);
        this.getChildren().addAll(
                UIUtils.createTitleLabel("predict4all.config.part.title.prediction"), boxSpinnerMinCountPred, buttonDictionaryConfig,
                UIUtils.createTitleLabel("predict4all.config.part.title.dynamic.model"), this.toggleDynamicModelEnabled, this.toggleAddNewWord, boxSpinnerCountValidateWords, buttonTrainingConfig,
                UIUtils.createTitleLabel("predict4all.config.part.title.correction"), this.toggleEnableCorrection, boxSpinnerMinCountCorr, buttonCorrectionConfig,
                new Separator(Orientation.HORIZONTAL), this.buttonTestingConfig
        );
    }

    @Override
    public void initBinding() {
        this.toggleAddNewWord.disableProperty().bind(this.toggleDynamicModelEnabled.selectedProperty().not());
        this.spinnerMinUseCountToValidateNewWord.disableProperty()
                .bind(this.toggleDynamicModelEnabled.selectedProperty().not().or(this.toggleAddNewWord.selectedProperty().not()));
        this.labelMinCountToAddNewWords.disableProperty().bind(this.spinnerMinUseCountToValidateNewWord.disabledProperty());
        this.comboboxMinCountCorrection.disableProperty().bind(this.toggleEnableCorrection.selectedProperty().not());
        buttonCorrectionConfig.disableProperty().bind(toggleEnableCorrection.selectedProperty().not());
    }

    @Override
    public void initListener() {
        buttonDictionaryConfig.setOnAction(e -> GeneralConfigurationController.INSTANCE.showStep(P4AConfigurationSteps.DICTIONARY_CONFIG.name(), predictorModelDto));
        buttonCorrectionConfig.setOnAction(e -> GeneralConfigurationController.INSTANCE.showStep(P4AConfigurationSteps.CORRECTION_CONFIG.name(), predictorModelDto));
        buttonTrainingConfig.setOnAction(e -> GeneralConfigurationController.INSTANCE.showStep(P4AConfigurationSteps.TRAINING.name(), predictorModelDto));
        buttonTestingConfig.setOnAction(e -> GeneralConfigurationController.INSTANCE.showStep(P4AConfigurationSteps.TESTING.name(), predictorModelDto));
    }

    private ComboBox<Integer> createComboboxMinCount() {
        ComboBox<Integer> combo = new ComboBox<>(COUNT_FIRE_PRED_CORR);
        combo.setCellFactory(lv -> new MinCountToFireListCell());
        combo.setButtonCell(new MinCountToFireListCell());
        combo.setPrefWidth(150.0);
        return combo;
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return false;
    }

    @Override
    public String getTitleId() {
        return "predict4all.config.view.general.title";
    }

    @Override
    public String getStep() {
        return P4AConfigurationSteps.CONFIG_ROOT_ENTRY_POINT.name();
    }

    @Override
    public String getPreviousStep() {
        return GeneralConfigurationStep.PREDICTIONS_CONFIGURATION.name();
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public void saveChanges() {
        // Save common models
        try {
            this.predictorModelDto.getPredictionParameter().saveTo(Predict4AllWordPredictorHelper.getCurrentConfigurationPath(currentConfiguration.getID()));
        } catch (IOException e) {
            LOGGER.error("Couldn't save prediction parameters to file", e);
        }
        Predict4AllWordPredictorHelper.saveUserDictionary(currentConfiguration.getID(), this.predictorModelDto.getWordDictionary());
        Predict4AllWordPredictorHelper.saveDynamicNGramDictionary(currentConfiguration.getID(), this.predictorModelDto.getDynamicNGramDictionary());
    }


    @Override
    public void bind(LCConfigurationI model) {
        this.currentConfiguration = model;
        try {
            this.predictorModelDto = Predict4AllWordPredictorHelper.loadData(model.getID());
            // Update configuration
            this.toggleAddNewWord.setSelected(this.predictorModelDto.getPredictionParameter().isAddNewWordsEnabled());
            this.toggleDynamicModelEnabled.setSelected(this.predictorModelDto.getPredictionParameter().isDynamicModelEnabled());
            this.spinnerMinUseCountToValidateNewWord.getValueFactory().setValue(this.predictorModelDto.getPredictionParameter().getMinUseCountToValidateNewWord());
            this.toggleEnableCorrection.setSelected(this.predictorModelDto.getPredictionParameter().isEnableWordCorrection());
            this.comboboxMinCountCorrection.setValue(this.predictorModelDto.getPredictionParameter().getMinCountToProvideCorrection());
            this.comboboxMinCountPrediction.setValue(this.predictorModelDto.getPredictionParameter().getMinCountToProvidePrediction());
        } catch (Exception e) {
            LOGGER.error("Couldn't load dictionary", e);
        }
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.predictorModelDto = null;
        this.currentConfiguration = null;
    }

    @Override
    public void afterHide() {
        if (this.predictorModelDto != null) {
            this.predictorModelDto.getPredictionParameter().setAddNewWordsEnabled(this.toggleAddNewWord.isSelected());
            this.predictorModelDto.getPredictionParameter().setDynamicModelEnabled(this.toggleDynamicModelEnabled.isSelected());
            this.predictorModelDto.getPredictionParameter().setMinUseCountToValidateNewWord(this.spinnerMinUseCountToValidateNewWord.getValue());
            this.predictorModelDto.getPredictionParameter().setEnableWordCorrection(this.toggleEnableCorrection.isSelected());
            this.predictorModelDto.getPredictionParameter().setMinCountToProvidePrediction(this.comboboxMinCountPrediction.getValue());
            this.predictorModelDto.getPredictionParameter().setMinCountToProvideCorrection(this.comboboxMinCountCorrection.getValue());
        }
    }
}
