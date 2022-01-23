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
package org.lifecompanion.base.data.control.prediction;

import javafx.application.Platform;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.PredictionParameterI;
import org.lifecompanion.api.prediction.WordPredictionI;
import org.lifecompanion.api.prediction.WordPredictionResultI;
import org.lifecompanion.api.prediction.WordPredictorI;
import org.lifecompanion.base.data.component.keyoption.WordPredictionKeyOption;
import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.base.data.plugins.PluginManager;
import org.lifecompanion.base.data.prediction.predict4all.predictor.Predict4AllWordPredictor;
import org.lifecompanion.base.data.prediction2.WordPrediction;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.List;
import java.util.Set;

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

    protected WordPredictionController() {
        super(WordPredictionKeyOption.class);
        this.initializePredictorListener(PluginManager.INSTANCE.getWordPredictors());
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
        return getPredictorForId(Predict4AllWordPredictor.ID);
    }

    public WordPredictionResultI getLastPredictionResult() {
        return this.lastPredictionResult;
    }
    //========================================================================

    @Override
    protected List<WordPredictionI> predict(String textBeforeCaret, String textAfterCaret, int count) {
        synchronized (this.currentPredictor) {
            this.lastPredictionResult = this.currentPredictor.predict(textBeforeCaret, textAfterCaret, count);
            return lastPredictionResult.getPredictions();
        }
    }

    @Override
    protected void dispatchPredictionResult(List<WordPredictionI> result, boolean waitingDispatch) {
        Set<GridComponentI> grids = this.predictionOptions.keySet();
        for (GridComponentI grid : grids) {
            List<WordPredictionKeyOption> options = this.predictionOptions.get(grid);
            for (int i = 0; i < options.size(); i++) {
                final int index = i;
                Platform.runLater(() -> {
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
