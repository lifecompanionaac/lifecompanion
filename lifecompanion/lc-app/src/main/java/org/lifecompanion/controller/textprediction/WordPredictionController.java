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
package org.lifecompanion.controller.textprediction;

import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.PredictionParameterI;
import org.lifecompanion.model.api.textprediction.WordPredictionI;
import org.lifecompanion.model.api.textprediction.WordPredictionResultI;
import org.lifecompanion.model.api.textprediction.WordPredictorI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.WordPredictionKeyOption;
import org.lifecompanion.model.impl.textprediction.WordPrediction;
import org.lifecompanion.model.impl.textprediction.predict4all.Predict4AllWordPredictor;
import org.lifecompanion.util.javafx.FXThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for word prediction.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WordPredictionController extends AbstractPredictionController<WordPredictorI, WordPredictionI, WordPredictionKeyOption> {
    //Enum can't extends abstract class
    public static final WordPredictionController INSTANCE = new WordPredictionController();

    private static final WordPrediction EMPTY_PREDICTION = new WordPrediction("", "", 0, 0, false, 1, null);

    private WordPredictionResultI lastPredictionResult;

    private final Predict4AllWordPredictor predict4AllWordPredictor = new Predict4AllWordPredictor();

    protected WordPredictionController() {
        super(WordPredictionKeyOption.class);
        this.initializePredictorListener(PluginController.INSTANCE.getWordPredictors());
    }

    @Override
    public void lcStart() {
        super.lcStart();
        WordPredictionController.INSTANCE.getAvailablePredictor().add(predict4AllWordPredictor);
    }

    // Class part : "Public API"
    //========================================================================

    /**
     * @return true if a new sentence is started in the current editor
     */
    public boolean isSentenceStarted() {
        String text = WritingStateController.INSTANCE.textBeforeCaretProperty().get();
        return StringUtils.isBlank(text) || this.currentPredictor.isNewSentenceStarted(text);
    }
    //========================================================================

    // Class part : "Abstract implementations"
    //========================================================================
    @Override
    protected WordPredictionI getWaitingElement() {
        return EMPTY_PREDICTION;
    }

    @Override
    protected WordPredictorI getPredictorFor(final PredictionParameterI parameter) {
        return this.getPredictorForId(parameter.selectedWordPredictorIdProperty().get());
    }

    @Override
    public WordPredictorI getDefaultPredictor() {
        return predict4AllWordPredictor;
    }

    public WordPredictionResultI getLastPredictionResult() {
        return this.lastPredictionResult;
    }
    //========================================================================

    @Override
    protected List<WordPredictionI> predict(String textBeforeCaret, String textAfterCaret, int count) {// ajouter une condition d'activation du seuil ou non en fonction du toggle.
        synchronized (this.currentPredictor) {
            this.lastPredictionResult = this.currentPredictor.predict(textBeforeCaret, textAfterCaret, count);

            Double score = 0.0;//pour recupérer le max
            for (WordPredictionI w : lastPredictionResult.getPredictions()) {
                Double score_= w.getScore();
                if (score_>score){
                    score=score_;}
            }
            Double finalScore = score*10/100; // permet de définir de seuil de proba mini pour que la prédiction soit affichée.

            List<WordPredictionI> predictions = lastPredictionResult.getPredictions();
            ArrayList<WordPredictionI> final_predictions = new ArrayList<>();
            for(WordPredictionI pred : predictions){
               if(pred.getScore()>= finalScore){
                   final_predictions.add(pred); // ajoute que les prédictions dont le score est suffisant (10% du plus élevé) pour être dans la liste finale
               }
            }
            return final_predictions;//lastPredictionResult.getPredictions();// (List<WordPredictionI>) lastPredictionResult.getPredictions().stream().filter(wordPredictionI ->wordPredictionI.getScore()>= 0.10);
        }
    }





    @Override
    protected void dispatchPredictionResult(List<WordPredictionI> result, boolean waitingDispatch) {
        Set<GridComponentI> grids = this.predictionOptions.keySet();
        for (GridComponentI grid : grids) {
            List<WordPredictionKeyOption> options = this.predictionOptions.get(grid);
            for (int i = 0; i < options.size(); i++) {
                final int index = i;
                FXThreadUtils.runOnFXThread(() -> {
                    if (index < result.size()) {
                        options.get(index).predictionProperty().set(result.get(index));
                    } else {
                        options.get(index).predictionProperty().set(EMPTY_PREDICTION);
                    }
                });
            }
        }
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        super.modeStop(configuration);
        lastPredictionResult = null;
    }

}
