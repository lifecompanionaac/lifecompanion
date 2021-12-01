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

package scripts; import org.lifecompanion.base.data.prediction.charpredictor.CharPredictorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Class to generate the char predictor data from a text.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CharPredictorDataGenerator {
    private final static Logger LOGGER = LoggerFactory.getLogger(CharPredictorDataGenerator.class);

    public void generateData(final String text, File charPredictionFile) {
        try {
            LOGGER.info("Will train char predictor with a text of {} characters", text.length());
            long start = System.currentTimeMillis();
            CharPredictorData charPredictorData = new CharPredictorData();
            charPredictorData.executeTraining(text);
            LOGGER.info("Char predictor trained in {} ms", (System.currentTimeMillis() - start));
            long startS = System.currentTimeMillis();
            charPredictorData.saveTo(charPredictionFile);
            LOGGER.info("Char predictor data saved to {} in {} ms", charPredictionFile, (System.currentTimeMillis() - startS));
        } catch (Exception e) {
            LOGGER.error("Couldn't generate static char predictor data", e);
        }
    }
}
