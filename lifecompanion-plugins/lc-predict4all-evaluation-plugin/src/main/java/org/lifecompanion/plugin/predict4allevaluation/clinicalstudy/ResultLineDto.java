/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.predict4allevaluation.clinicalstudy;

import java.math.BigDecimal;
import java.util.Date;

public class ResultLineDto {
	private final Date timestamp;
	private final String input;
	private final String predictionDisplayed;
	private final String selectedPrediction;
	private final BigDecimal ksr;
	private final String error = null;
	private final String errorCorrected = null;
	private final String comment;
	private final int insertedLength;
	private final int wordLength;
	private final double instantSpeedInCarPerMin;

	public ResultLineDto(Date timestamp, String input, String predictionDisplayed, String selectedPrediction, BigDecimal ksr, int insertedLength,
			int wordLength, double instantSpeedInCarPerMin, String comment) {
		super();
		this.timestamp = timestamp;
		this.input = input;
		this.predictionDisplayed = predictionDisplayed;
		this.selectedPrediction = selectedPrediction;
		this.ksr = ksr;
		this.insertedLength = insertedLength;
		this.wordLength = wordLength;
		this.instantSpeedInCarPerMin = instantSpeedInCarPerMin;
		this.comment = comment;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getInput() {
		return input;
	}

	public String getPredictionDisplayed() {
		return predictionDisplayed;
	}

	public String getSelectedPrediction() {
		return selectedPrediction;
	}

	public BigDecimal getKsr() {
		return ksr;
	}

	public String getError() {
		return error;
	}

	public String getErrorCorrected() {
		return errorCorrected;
	}

	public int getInsertedLength() {
		return insertedLength;
	}

	public int getWordLength() {
		return wordLength;
	}

	public double getInstantSpeedInCarPerMin() {
		return instantSpeedInCarPerMin;
	}

	public String getComment() {
		return comment;
	}

}
