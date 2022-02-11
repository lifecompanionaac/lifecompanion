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

package org.lifecompanion.model.impl.textprediction.predict4all;

import org.predict4all.nlp.ngram.dictionary.DynamicNGramDictionary;
import org.predict4all.nlp.prediction.PredictionParameter;
import org.predict4all.nlp.words.WordDictionary;

public class PredictorModelDto {
    private final WordDictionary wordDictionary;
    private final DynamicNGramDictionary dynamicNGramDictionary;
    private final PredictionParameter predictionParameter;

    public PredictorModelDto(WordDictionary wordDictionary, DynamicNGramDictionary dynamicNGramDictionary, PredictionParameter predictionParameter) {
        super();
        this.wordDictionary = wordDictionary;
        this.dynamicNGramDictionary = dynamicNGramDictionary;
        this.predictionParameter = predictionParameter;
    }

    public WordDictionary getWordDictionary() {
        return wordDictionary;
    }

    public DynamicNGramDictionary getDynamicNGramDictionary() {
        return dynamicNGramDictionary;
    }

    public PredictionParameter getPredictionParameter() {
        return predictionParameter;
    }
}
