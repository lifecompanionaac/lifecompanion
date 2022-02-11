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

import javafx.application.Platform;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.PredictionParameterI;
import org.lifecompanion.model.api.textprediction.CharPredictorI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AutoCharKeyOption;
import org.lifecompanion.controller.plugin.PluginManager;
import org.lifecompanion.model.impl.textprediction.charprediction.LCCharPredictor;

import java.util.List;
import java.util.Set;

/**
 * Controller for auto char prediction.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class AutoCharPredictionController extends AbstractPredictionController<CharPredictorI, Character, AutoCharKeyOption> {
    //Enum can't extends abstract class
    public static final AutoCharPredictionController INSTANCE = new AutoCharPredictionController();

    protected AutoCharPredictionController() {
        super(AutoCharKeyOption.class);
        this.initializePredictorListener(PluginManager.INSTANCE.getCharPredictors());
    }

    @Override
    public void lcStart() {
        super.lcStart();
        getAvailablePredictor().add(LCCharPredictor.INSTANCE);
    }

    // Class part : "Abstract implementations"
    //========================================================================
    @Override
    protected List<Character> predict(String textBeforeCaret, String textAfterCaret, int count) {
        return this.currentPredictor.predict(textBeforeCaret, count);
    }

    @Override
    protected Character getWaitingElement() {
        return Character.MIN_VALUE;
    }

    @Override
    public CharPredictorI getDefaultPredictor() {
        return LCCharPredictor.INSTANCE;
    }

    @Override
    protected CharPredictorI getPredictorFor(final PredictionParameterI parameter) {
        return this.getPredictorForId(parameter.selectedCharPredictorIdProperty().get());
    }

    @Override
    protected void dispatchPredictionResult(final List<Character> result, final boolean waitingResult) {
        Set<GridComponentI> grids = this.predictionOptions.keySet();
        for (GridComponentI grid : grids) {
            List<AutoCharKeyOption> options = this.predictionOptions.get(grid);
            for (int i = 0; i < options.size(); i++) {
                final int index = i;
                Platform.runLater(() -> {
                    if (index < result.size()) {
                        Character character = result.get(index);
                        if (character != null && Character.isWhitespace(character)) {
                            options.get(index).predictionProperty().set("" + this.parameter.charPredictionSpaceCharProperty().get());
                        } else {
                            options.get(index).predictionProperty().set("" + character);
                        }
                    } else {
                        options.get(index).predictionProperty().set("");
                    }
                });
            }
        }
    }
    //========================================================================

}
