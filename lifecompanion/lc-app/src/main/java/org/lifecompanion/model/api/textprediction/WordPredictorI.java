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

package org.lifecompanion.model.api.textprediction;

/**
 * Represent a word prediction to predict the next words for a typed text.<br>
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface WordPredictorI extends BasePredictorI {

    /**
     * Should predict (async) the possible next words for given text.
     *
     * @param textBeforeCaret the text before the caret (eg for "ho|me" will be "ho"). This is basically the used text to predict the next words
     * @param textAfterCaret  the text after the caret (eg for "ho|me" will be "me"). This is mostly used to compute {@link WordPredictionI#getNextCharCountToRemove()}
     * @param count           the wanted prediction count. The returned result list size can be different than this count (smaller or bigger). Mostly useful for optimization.
     * @return
     */
    WordPredictionResultI predict(String textBeforeCaret, String textAfterCaret, int count);

    /**
     * To detect if a new sentence is started.
     *
     * @param text the raw text
     * @return true if a new sentence is started
     */
    boolean isNewSentenceStarted(String text);

}
