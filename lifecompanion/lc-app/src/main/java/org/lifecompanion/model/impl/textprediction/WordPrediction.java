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

public class WordPrediction implements WordPredictionI {

	private final String predictionToDisplay, textToWrite;
	private final int nextCharCountToRemove, previousCharCountToRemove;
	private final boolean spacePossible;
	private final double score;
	private final String debugInformations;
	private final Object customValue;

	public WordPrediction(String predictionToDisplay, String textToWrite, int nextCharCountToRemove, int previousCharCountToRemove,
			boolean spacePossible, double score, String debugInformations, Object customValue) {
		super();
		this.predictionToDisplay = predictionToDisplay;
		this.textToWrite = textToWrite;
		this.nextCharCountToRemove = nextCharCountToRemove;
		this.previousCharCountToRemove = previousCharCountToRemove;
		this.spacePossible = spacePossible;
		this.score = score;
		this.debugInformations = debugInformations;
		this.customValue = customValue;
	}

	public WordPrediction(String predictionToDisplay, String textToWrite, int nextCharCountToRemove, int previousCharCountToRemove,
			boolean spacePossible, double score, String debugInformations) {
		this(predictionToDisplay, textToWrite, nextCharCountToRemove, previousCharCountToRemove, spacePossible, score, debugInformations, null);
	}

	@Override
	public String getTextToWrite() {
		return textToWrite;
	}

	@Override
	public int getNextCharCountToRemove() {
		return nextCharCountToRemove;
	}

	@Override
	public int getPreviousCharCountToRemove() {
		return previousCharCountToRemove;
	}

	@Override
	public boolean isSpacePossible() {
		return spacePossible;
	}

	@Override
	public String toString() {
		return "WordPrediction [predictionToDisplay=" + predictionToDisplay + ", textToWrite=" + textToWrite + ", nextCharCountToRemove="
				+ nextCharCountToRemove + ", previousCharCountToRemove=" + previousCharCountToRemove + ", spacePossible=" + spacePossible + "]";
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public String getDebugInformations() {
		return debugInformations;
	}

	@Override
	public String getPredictionToDisplay() {
		return predictionToDisplay;
	}

	@Override
	public Object getCustomValue() {
		return customValue;
	}

}
