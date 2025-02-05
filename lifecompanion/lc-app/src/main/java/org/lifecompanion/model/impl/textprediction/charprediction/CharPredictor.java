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
package org.lifecompanion.model.impl.textprediction.charprediction;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


/**
 * OLD CHAR PREDICTOR IMPLEMENTATION EXTRACT FROM OLD PREDICTOR.<br>
 * This implementation is surely not optimized nor efficient, but it works :-)<br>
 * Might be reimplemented later if needed.
 */
public class CharPredictor {
    private final static Logger LOGGER = LoggerFactory.getLogger(CharPredictor.class);

    public static int CHAR_NGRAM_SIZE = 5;
    public static int FREQUENT_CHAR_SIZE = 50;

    /**
     * Predictor data
     */
    private final CharPredictorData predictorData;

    /**
     * First characters, returned if string is blank
     */
    private final List<Character> firstCharacter;

    public CharPredictor(CharPredictorData predictorData) {
        this.predictorData = predictorData;
        this.firstCharacter = predictorData.getPredictionFor(' ').stream().map(cp -> cp.getNextChars()[0]).distinct()
                .limit(FREQUENT_CHAR_SIZE).collect(Collectors.toList());
    }

    // Class part : "Public API"
    //========================================================================
    public List<Character> predict(String text, final int wantedPrediction) {
        return predict(text, wantedPrediction, null);
    }

    public List<Character> predict(String text, final int wantedPrediction, HashSet<Character> acceptedCharacters) {
        return predictImpl(text, wantedPrediction, acceptedCharacters);
    }

    private List<Character> predictImpl(String text, final int wantedPrediction, HashSet<Character> acceptedCharacters) {
        if (StringUtils.isBlank(text)) {
            return this.firstCharacter.stream()//
                    .filter(c -> acceptedCharacters == null || acceptedCharacters.contains(c))//
                    .limit(wantedPrediction)//
                    .collect(Collectors.toList());
        }
        //While the wanted count is not produced
        String lastTextPart = getLastTextPart(this.predictorData.cleanText(text));
        HashSet<Character> alreadyPredicted = new HashSet<>();
        List<Character> predictions = new ArrayList<>(wantedPrediction);
        while (lastTextPart.length() >= 1 && predictions.size() < wantedPrediction) {
            List<Character> currentPrediction = addPredictionFor(lastTextPart, alreadyPredicted, wantedPrediction - predictions.size(),
                    acceptedCharacters);
            predictions.addAll(currentPrediction);
            alreadyPredicted.addAll(currentPrediction);
            lastTextPart = lastTextPart.substring(1);
        }
        //If ended without matching the wanted prediction count : add default chars
        if (predictions.size() < wantedPrediction) {
            this.firstCharacter.stream()//
                    .filter(w -> !alreadyPredicted.contains(w))//
                    .filter(c -> acceptedCharacters == null || acceptedCharacters.contains(c))//
                    .limit(wantedPrediction - predictions.size())//
                    .forEachOrdered(p ->{
                        alreadyPredicted.add(p);
                        predictions.add(p);
                    });
        }
        //If ended without matching the wanted prediction count : add missing accepted chars
        if (predictions.size() < wantedPrediction && acceptedCharacters != null) {
            acceptedCharacters.stream()
                    .filter(w -> !alreadyPredicted.contains(w))//
                    .limit(wantedPrediction - predictions.size())//
                    .forEach(p ->{
                        alreadyPredicted.add(p);
                        predictions.add(p);
                    });
        }
        return predictions;
    }

    public void dispose() {
        this.predictorData.dispose();
    }
    //========================================================================

    // Class part : "Internal API"
    //========================================================================

    /**
     * @param text the text we want the last part
     * @return the last char in the text, to predict the next chars
     */
    private static String getLastTextPart(String text) {
        if (text.length() >= CHAR_NGRAM_SIZE - 1) {
            return text.substring(text.length() - (CHAR_NGRAM_SIZE - 1), text.length());
        } else {
            return text;
        }
    }

    private char[] getCharAfterFirst(String lastTextPart) {
        return lastTextPart.substring(1).toCharArray();
    }

    private boolean areFirstEquals(char[] c1, char[] c2) {
        int min = Math.min(c1.length, c2.length);
        for (int i = 0; i < min; i++) {
            if (c1[i] != c2[i]) {
                return false;
            }
        }
        return true;
    }

    private List<Character> addPredictionFor(String lastTextPart, HashSet<Character> alreadyPredicted, int wantedCount,
                                             HashSet<Character> acceptedCharacters) {
        int charIndex = lastTextPart.length() - 1;
        char startingChar = lastTextPart.charAt(0);
        char[] charAfter = getCharAfterFirst(lastTextPart);
        //Predict
        List<CharPrediction> predictionFor = this.predictorData.getPredictionFor(startingChar);
        LOGGER.debug("Prediction for \"{}\", wanted char index {}", lastTextPart, charIndex);
        //Compute the score for each char (only char that are not already predicted)
        Map<Character, Integer> scores = new HashMap<>();
        if (predictionFor != null) {
            predictionFor.stream().filter(p -> areFirstEquals(charAfter, p.getNextChars())).forEach(p -> {
                char charAt = p.getNextChars()[charIndex];
                if (!alreadyPredicted.contains(charAt) && (acceptedCharacters == null || acceptedCharacters.contains(charAt))) {
                    //LOGGER.debug("Char {} :  {} (count {})", charAt, Arrays.toString(p.getNextChars()), p.getCount());
                    if (scores.containsKey(charAt)) {
                        scores.put(charAt, scores.get(charAt) + p.getCount());
                    } else {
                        scores.put(charAt, p.getCount());
                    }
                }
            });
        }
        //Sort on score, and return characters
        List<Character> collect = scores.entrySet().stream().sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(wantedCount).map(e -> e.getKey()).collect(Collectors.toList());
        LOGGER.debug("Result are {} from a {} total map", collect, scores.size());
        return collect;
    }

    //========================================================================

}
