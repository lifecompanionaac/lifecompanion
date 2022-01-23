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

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import okhttp3.MediaType;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.prediction.WordPredictionI;
import org.lifecompanion.api.prediction.WordPredictionResultI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.prediction.predict4all.P4AConfigurationSteps;
import org.lifecompanion.base.data.prediction.predict4all.predictor.Predict4AllWordPredictorHelper;
import org.lifecompanion.base.data.prediction.predict4all.predictor.PredictorModelDto;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStep;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStepViewI;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.predict4all.nlp.prediction.WordPredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class P4ATestingConfigurationView extends VBox implements GeneralConfigurationStepViewI {
    private static final Logger LOGGER = LoggerFactory.getLogger(P4ATestingConfigurationView.class);
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public final static Gson GSON = new Gson();

    private static final DecimalFormat SCORE_FORMAT = new DecimalFormat("##0.00000000000");

    private String savedInput;
    private TextArea textAreaInput;
    private TableView<WordPredictionI> tablePredictions;
    private ObservableList<WordPredictionI> currentPredictions;
    private ExecutorService threadPool;
    private Spinner<Integer> spinnerPredictionCountToDisplay;

    private WordPredictor wordPredictor;

    public P4ATestingConfigurationView() {
        this.initAll();
    }

    @Override
    public String getMenuStepToSelect() {
        return GeneralConfigurationStep.PREDICTIONS_CONFIGURATION.name();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        this.textAreaInput = new TextArea();
        this.textAreaInput.setPrefRowCount(1);
        this.textAreaInput.setWrapText(true);

        this.spinnerPredictionCountToDisplay = UIUtils.createIntSpinner(1, 100, 5, 1, 150.0);
        Label labelMinCount = new Label(Translation.getText("predict4all.config.view.label.prediction.count"));
        HBox boxSpinner = new HBox(5.0, labelMinCount, this.spinnerPredictionCountToDisplay);
        labelMinCount.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelMinCount, Priority.ALWAYS);


        this.currentPredictions = FXCollections.observableArrayList();
        this.tablePredictions = new TableView<>(this.currentPredictions);
        this.tablePredictions.setSortPolicy(tv -> false);

        TableColumn<WordPredictionI, String> colWord = new TableColumn<>(Translation.getText("predict4all.config.testing.col.prediction"));
        colWord.getStyleClass().add("col-predicted-word");
        colWord.setCellValueFactory((f) -> new SimpleStringProperty(f.getValue().getPredictionToDisplay()));
        colWord.prefWidthProperty().bind(this.tablePredictions.widthProperty().multiply(0.8));
        this.tablePredictions.getColumns().add(colWord);

        TableColumn<WordPredictionI, String> colScore = new TableColumn<>(Translation.getText("predict4all.config.testing.col.score"));
        colScore.setCellValueFactory((f) -> new SimpleStringProperty(P4ATestingConfigurationView.SCORE_FORMAT.format(100.0 * f.getValue().getScore())));
        colScore.prefWidthProperty().bind(this.tablePredictions.widthProperty().multiply(0.2));
        this.tablePredictions.getColumns().add(colScore);
        this.tablePredictions.getStyleClass().add("table-view-p4a");

        this.setSpacing(5.0);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(this.textAreaInput, boxSpinner, this.tablePredictions);
        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
    }

    @Override
    public void initListener() {
        this.textAreaInput.textProperty().addListener(inv -> this.launchPrediction());
        this.textAreaInput.caretPositionProperty().addListener(inv -> this.launchPrediction());
        this.spinnerPredictionCountToDisplay.valueProperty().addListener(inv -> this.launchPrediction());
    }
    //========================================================================

    private int xIndex;

    private void launchPrediction() {
        int caretPos = this.textAreaInput.getCaretPosition();
        String textFromInput = this.textAreaInput.getText();
        final String text;
        if (textFromInput != null && caretPos >= 0 && caretPos <= textFromInput.length()) {
            text = textFromInput.substring(0, caretPos);
        } else {
            text = StringUtils.trimToEmpty(textFromInput);
        }
        if (this.wordPredictor != null) {
            this.currentPredictions.clear();
            this.threadPool.submit(() -> {
                WordPredictionResultI prediction = Predict4AllWordPredictorHelper.predictorOn(this.wordPredictor, text, "", this.spinnerPredictionCountToDisplay.getValue());
                Platform.runLater(() -> this.currentPredictions.setAll(prediction.getPredictions()));
                return null;
            });
        }
    }


    @Override
    public boolean shouldBeAddedToMainMenu() {
        return false;
    }

    @Override
    public String getTitleId() {
        return "predict4all.config.view.testing.title";
    }

    @Override
    public String getStep() {
        return P4AConfigurationSteps.TESTING.name();
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
    public void saveChanges() {
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.threadPool = Executors.newFixedThreadPool(1, r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
        try {
            PredictorModelDto predictorModelDto = (PredictorModelDto) stepArgs[0];
            this.wordPredictor = new WordPredictor(predictorModelDto.getPredictionParameter(), predictorModelDto.getWordDictionary(), Predict4AllWordPredictorHelper.loadStaticNGramDictionary(), predictorModelDto.getDynamicNGramDictionary());
            this.textAreaInput.setText(this.savedInput);
            this.textAreaInput.end();
        } catch (Exception e) {
            P4ATestingConfigurationView.LOGGER.error("Can't initialize predictor");
        }
    }

    @Override
    public void afterHide() {
        try {
            if (this.wordPredictor != null) {
                this.wordPredictor.dispose();
                this.wordPredictor = null;
            }
            this.savedInput = this.textAreaInput.getText();
            this.textAreaInput.clear();
            this.currentPredictions.clear();
        } catch (Exception e) {
            P4ATestingConfigurationView.LOGGER.error("Can't dispose predictor");
        }
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.threadPool.shutdown();
        this.threadPool = null;
    }
    //========================================================================

}
