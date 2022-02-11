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

package org.lifecompanion.model.impl.textprediction;

import org.lifecompanion.model.api.textprediction.WordPredictionI;
import org.lifecompanion.model.api.textprediction.WordPredictionResultI;

import java.util.List;
import java.util.stream.Collectors;


public class WordPredictionResult implements WordPredictionResultI {
    private final String inputText;
    private final long time;
    private final List<WordPredictionI> predictions;

    public WordPredictionResult(String inputText, long time, List<WordPredictionI> predictions) {
        super();
        this.inputText = inputText;
        this.time = time;
        this.predictions = predictions;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public String getInputText() {
        return inputText;
    }

    @Override
    public List<WordPredictionI> getPredictions() {
        return predictions;
    }

    @Override
    public String toString() {
        return "WordPredictionResult{" +
                "inputText='" + inputText + '\'' +
                ", time=" + time +
                ", predictions=" + (predictions != null ? predictions.stream().map(WordPredictionI::getPredictionToDisplay).collect(Collectors.joining(", ")) : "X") +
                '}';
    }
}
