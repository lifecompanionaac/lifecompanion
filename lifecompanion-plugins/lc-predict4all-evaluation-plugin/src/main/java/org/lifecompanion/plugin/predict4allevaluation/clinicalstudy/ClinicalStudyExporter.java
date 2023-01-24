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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Manual convert from raw json file to Excel
public class ClinicalStudyExporter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClinicalStudyExporter.class);

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File outputDirectory = new File("./analyzed");
		File sourceDirectory = new File("./input");

		File infoFile = new File(sourceDirectory.getPath() + File.separator + "informations.json");
		File logFile = new File(sourceDirectory.getPath() + File.separator + "raw-log.json");

		LOGGER.info("Will read information file from {}", logFile);
		ClinicalStudyTestInformationDto informations = null;
		try (FileReader fr = new FileReader(infoFile)) {
			informations = Predict4AllClinicalStudyManager.GSON_OUTPUT.fromJson(fr, ClinicalStudyTestInformationDto.class);
		}
		LOGGER.info("Information = {}", informations);

		File outputResultDirectory = new File(outputDirectory.getPath() + File.separator + sourceDirectory.getName());
		outputResultDirectory.mkdirs();
		File outputExcelFile = new File(outputResultDirectory.getPath() + File.separator + sourceDirectory.getName() + "_result.xls");
		LOGGER.info("Will output result analyzed file to {}\n\t{}", outputResultDirectory.getPath(), outputExcelFile.getPath());

		Predict4AllClinicalStudyManager.handleResultFile(logFile, outputExcelFile, informations, true);
		LOGGER.info("Analyze finished");
	}
}
