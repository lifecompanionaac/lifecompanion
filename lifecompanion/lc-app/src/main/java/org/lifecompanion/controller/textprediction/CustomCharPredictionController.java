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

import javafx.util.Pair;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.PredictionParameterI;
import org.lifecompanion.model.api.configurationcomponent.ComponentGridI;
import org.lifecompanion.model.api.textprediction.CharPredictorI;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.CustomCharKeyOption;
import org.lifecompanion.model.impl.textprediction.charprediction.LCCharPredictor;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.util.*;

/**
 * Controller for custom char prediction.<br>
 * For this predictor, the available predictor are based on {@link AutoCharPredictionController}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CustomCharPredictionController extends AbstractPredictionController<CharPredictorI, Character, CustomCharKeyOption> {
    //Enum can't extends abstract class
    public static final CustomCharPredictionController INSTANCE = new CustomCharPredictionController();
    private HashSet<Character> allAcceptedCharacters;
    private Map<GridComponentI, HashSet<Character>> acceptedCharacetersByGrid;
    private Map<GridComponentI, List<Pair<ComponentGridI, Pair<Integer, Integer>>>> positionByGrid;
    private Map<GridComponentI, Map<String, GridPartKeyComponentI>> predictionKeyByGrid;

    //private Map<CustomCharKeyOption,> gridOriginalPosition;

    protected CustomCharPredictionController() {
        super(CustomCharKeyOption.class);
        this.allAcceptedCharacters = new HashSet<>();
        this.acceptedCharacetersByGrid = new HashMap<>();
        this.positionByGrid = new HashMap<>();
        this.predictionKeyByGrid = new HashMap<>();
    }

    // Class part : "Abstract implementations"
    //========================================================================
    @Override
    protected List<Character> predict(String textBeforeCaret, String textAfterCaret, int count) {
        return this.currentPredictor.predict(textBeforeCaret, count, this.allAcceptedCharacters);
    }

    @Override
    protected Character getWaitingElement() {
        return Character.MIN_VALUE;
    }

    @Override
    protected void dispatchPredictionResult(final List<Character> result, final boolean waitingDispatch) {
        Set<GridComponentI> grids = this.predictionOptions.keySet();
        //For each stack : a different prediction
        if (!waitingDispatch) {
            for (GridComponentI grid : grids) {
                HashSet<Character> accepted = this.acceptedCharacetersByGrid.get(grid);
                //List all the position in stack
                List<Pair<ComponentGridI, Pair<Integer, Integer>>> positionInStack = this.positionByGrid.get(grid);
                Map<String, GridPartKeyComponentI> keysInStack = this.predictionKeyByGrid.get(grid);
                if (!keysInStack.isEmpty()) {
                    FXThreadUtils.runOnFXThread(() -> {
                        for (int i = 0; i < positionInStack.size(); i++) {
                            if (i < result.size() && accepted.contains(result.get(i))) {
                                //Prediction should be accepted
                                Character cr = result.get(i);
                                //Get the prediction result
                                String character = "" + cr;
                                if (cr != null && Character.isWhitespace(cr)) {
                                    character = this.parameter.charPredictionSpaceCharProperty().get();
                                }
                                //Get the key in that stack that contains this character
                                GridPartKeyComponentI keyForPrediction = keysInStack.get(character);
                                //Get the key at the wanted position in stack
                                Pair<ComponentGridI, Pair<Integer, Integer>> gridAndPosition = positionInStack.get(i);
                                Pair<Integer, Integer> position = gridAndPosition.getValue();
                                GridPartKeyComponentI originalKey = (GridPartKeyComponentI) gridAndPosition.getKey().getComponent(position.getKey(),
                                        position.getValue());
                                if (originalKey != keyForPrediction && keyForPrediction != null) {
                                    //Switch the two
                                    ConfigurationComponentUtils.invertKeys(originalKey, keyForPrediction);
                                }
                            }
                            //Happen if the prediction is not in accepted char
                            else {
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void keyPrepared() {
        this.allAcceptedCharacters.clear();
        this.positionByGrid.clear();
        this.predictionKeyByGrid.clear();
        Set<GridComponentI> grids = this.predictionOptions.keySet();
        for (GridComponentI grid : grids) {
            //Initialize grid by grid
            HashSet<Character> acceptedChars = new HashSet<>();
            HashMap<String, GridPartKeyComponentI> keysInStack = new HashMap<>();
            List<Pair<ComponentGridI, Pair<Integer, Integer>>> positionInGrids = new ArrayList<>();
            List<CustomCharKeyOption> option = this.predictionOptions.get(grid);
            //Add to grid maps
            this.predictionKeyByGrid.put(grid, keysInStack);
            this.acceptedCharacetersByGrid.put(grid, acceptedChars);
            this.positionByGrid.put(grid, positionInGrids);
            //For each option in grid
            for (CustomCharKeyOption customCharKeyOption : option) {
                GridPartKeyComponentI key = customCharKeyOption.attachedKeyProperty().get();
                this.addKeyChar(keysInStack, acceptedChars, key);
                positionInGrids
                        .add(new Pair<>(key.gridParentProperty().get().getGrid(), new Pair<>(key.rowProperty().get(), key.columnProperty().get())));
            }
            this.allAcceptedCharacters.addAll(acceptedChars);
        }
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        super.modeStop(configuration);
        this.allAcceptedCharacters.clear();
        this.acceptedCharacetersByGrid.clear();
        this.positionByGrid.clear();
        this.predictionKeyByGrid.clear();
    }

    private void addKeyChar(final HashMap<String, GridPartKeyComponentI> keysInStack, final HashSet<Character> acceptedChars,
                            final GridPartKeyComponentI key) {
        String keyText = key.textContentProperty().get();
        if (keyText != null && !keyText.isEmpty()) {
            char charAccepted = keyText.charAt(0);
            keysInStack.put(keyText, key);
            if (StringUtils.isEquals(keyText, this.parameter.charPredictionSpaceCharProperty().get())) {
                charAccepted = ' ';
            }
            if (charAccepted == ' ' || keyText.length() == 1) {
                acceptedChars.add(charAccepted);
            }
        }
    }

    @Override
    protected CharPredictorI getPredictorFor(final PredictionParameterI parameter) {
        return AutoCharPredictionController.INSTANCE.getPredictorForId(parameter.selectedCharPredictorIdProperty().get());
    }

    @Override
    public CharPredictorI getDefaultPredictor() {
        return LCCharPredictor.INSTANCE;
    }

    //========================================================================
}
