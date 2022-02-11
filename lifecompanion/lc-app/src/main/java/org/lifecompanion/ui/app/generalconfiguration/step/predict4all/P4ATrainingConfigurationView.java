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
package org.lifecompanion.ui.app.generalconfiguration.step.predict4all;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.textprediction.predict4all.P4AConfigurationSteps;
import org.lifecompanion.model.impl.textprediction.predict4all.PredictorModelDto;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.predict4all.nlp.ngram.dictionary.StaticNGramTrieDictionary;
import org.predict4all.nlp.prediction.WordPredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class P4ATrainingConfigurationView extends VBox implements GeneralConfigurationStepViewI {
    private static final Logger LOGGER = LoggerFactory.getLogger(P4ATrainingConfigurationView.class);

    private static final DecimalFormat DECIMAL_FORMAT_WEIGHT = new DecimalFormat("##.####");

    private Button buttonTrainModel;
    private TextArea textAreaTrainingText;
    private PredictorModelDto predictorModelDto;

    public P4ATrainingConfigurationView() {
        this.initAll();
    }

    // UI
    //========================================================================
    @Override
    public boolean shouldBeAddedToMainMenu() {
        return false;
    }

    @Override
    public String getTitleId() {
        return "predict4all.config.view.training.title";
    }

    @Override
    public String getStep() {
        return P4AConfigurationSteps.TRAINING.name();
    }

    @Override
    public String getPreviousStep() {
        return P4AConfigurationSteps.CONFIG_ROOT_ENTRY_POINT.name();
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public String getMenuStepToSelect() {
        return GeneralConfigurationStep.PREDICTIONS_CONFIGURATION.name();
    }

    @Override
    public void initUI() {
        this.textAreaTrainingText = new TextArea();
        this.textAreaTrainingText.setPrefRowCount(10);
        this.textAreaTrainingText.setWrapText(true);
        this.buttonTrainModel = new Button(Translation.getText("predict4all.button.train.dynamic.model"));
        this.buttonTrainModel.setPrefWidth(250.0);

        Label labelExplain = new Label(Translation.getText("predict4all.training.explain.text"));
        labelExplain.setWrapText(true);

        this.setSpacing(8.0);
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        this.getChildren().addAll(labelExplain, this.textAreaTrainingText, this.buttonTrainModel);
    }

    @Override
    public void initListener() {
        this.buttonTrainModel.setOnAction(a -> {
            String txt = this.textAreaTrainingText.getText();
            if (!StringUtils.isBlank(txt)) {
                try {
                    WordPredictor wp = new WordPredictor(this.predictorModelDto.getPredictionParameter(), this.predictorModelDto.getWordDictionary(), new StaticNGramTrieDictionary(), this.predictorModelDto.getDynamicNGramDictionary());
                    wp.trainDynamicModel(txt, false);
                    wp.dispose();
                    this.textAreaTrainingText.clear();
                } catch (Exception e) {
                    P4ATrainingConfigurationView.LOGGER.error("Couldn't train user model", e);
                }
            }
        });
    }
    //========================================================================

    // MODEL
    //========================================================================
    @Override
    public void saveChanges() {
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
        this.predictorModelDto = (PredictorModelDto) stepArgs[0];
    }

    @Override
    public void bind(LCConfigurationI model) {
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.predictorModelDto = null;
    }
    //========================================================================

}
