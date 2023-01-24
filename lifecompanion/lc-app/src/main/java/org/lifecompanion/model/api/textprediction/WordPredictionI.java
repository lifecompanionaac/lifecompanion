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
public interface WordPredictionI {

    /**
     * @return the total prediction that should be used to display the word in a result list.<br>
     * This is can be the same as {@link #getTextToWrite()} but can also be the same.
     */
    String getPredictionToDisplay();

    /**
     * @return the prediction to write once char before the caret {@link #getPreviousCharCountToRemove()} and char after the caret {@link #getNextCharCountToRemove()} are removed.
     */
    String getTextToWrite();

    /**
     * @return the char count that should be removed before inserting the {@link #getTextToWrite()}.<br>
     * This can be useful if we detected an error in the original input text
     */
    int getNextCharCountToRemove();

    /**
     * @return the char count that should be removed before inserting the {@link #getTextToWrite()}.<br>
     * This can be useful if we detected an error in the original input text
     */
    int getPreviousCharCountToRemove();

    /**
     * @return true if a space is possible after this prediction.<br>
     * For example, for a prediction result of "j'" in French, the space is not possible after insertion
     */
    boolean isSpacePossible();

    /**
     * @return the prediction score (free for implementer)
     */
    double getScore();

    /**
     * @return free form information for debug purpose
     */
    String getDebugInformations();

    /**
     * @return free custom object value for debug purpose
     */
    Object getCustomValue();
}
