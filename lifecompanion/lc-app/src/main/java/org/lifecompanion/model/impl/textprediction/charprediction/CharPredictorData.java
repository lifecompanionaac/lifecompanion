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

import gnu.trove.map.hash.TCharObjectHashMap;
import org.predict4all.nlp.Separator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Represents the data for char predictor.
 */
public class CharPredictorData {
    private final static Logger LOGGER = LoggerFactory.getLogger(CharPredictorData.class);

    /**
     * Data that are currently in this prediction
     */
    private TCharObjectHashMap<List<CharPrediction>> data;

    /**
     * Comparator to compare char prediction on their count
     */
    private static final Comparator<CharPrediction> CHAR_PREDICTION_COMPARATOR = (p1, p2) -> {
        return Integer.compare(p2.getCount(), p1.getCount());
    };

    public CharPredictorData() {
        data = new TCharObjectHashMap<>(120);//About 120 different char
    }

    // Class part : "Public API"
    //========================================================================

    /**
     * Train the current char predictor data with a given text.
     *
     * @param text the text to train data
     */
    public void executeTraining(String text) {
        //Clean text
        text = cleanText(text);

        //Create the char, and the ngram for all the text
        char[] charArray = text.toCharArray();
        Map<String, Integer> ngramMap = new HashMap<>();
        String currentGram = "";
        for (int index = 0; index < charArray.length; index++) {
            //Convert the char to a valid one
            char currentChar = charArray[index];
            Separator separator = Separator.getSeparatorFor(currentChar);
            if (separator != null) {
                currentChar = separator.getOfficialChar();
            }
            //Add to NGram (increase count, or create the one)
            if (currentGram.length() == CharPredictor.CHAR_NGRAM_SIZE) {
                currentGram = currentGram.substring(1, currentGram.length());
            }
            currentGram += currentChar;
            addToNGramCountMap(currentGram, ngramMap);
        }

        //Transform to prediction data
        ngramMap.forEach((key, value) -> {
            addPrediction(key, value);
        });
        //Sort predictions
        char[] keys = data.keys();
        for (char c : keys) {
            Collections.sort(data.get(c), CHAR_PREDICTION_COMPARATOR);
        }
        LOGGER.info("Found {} char ngram with training ({} different char)", ngramMap.size(), data.size());
    }

    public void printAll() {
        char[] keys = data.keys();
        for (char c : keys) {
            System.out.println("========== \"" + c + "\"");
            List<CharPrediction> pred = data.get(c);
            pred.forEach(System.out::println);
            System.out.println("\n\n");
        }
    }

    public List<CharPrediction> getPredictionFor(char c) {
        return this.data.get(c);
    }

    public void dispose() {
    }

    /**
     * To save all the char predictor data to a prediction file
     *
     * @param file the file that is use to save the data
     */
    public void saveTo(File file) throws Exception {
        try (DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
            dos.writeInt(this.data.size());
            char[] keys = data.keys();
            for (char startChar : keys) {
                List<CharPrediction> charPredictions = this.data.get(startChar);
                //Write start char and prediction size
                dos.writeChar(startChar);
                dos.writeInt(charPredictions.size());
                //Write each prediction
                for (CharPrediction prediction : charPredictions) {
                    dos.writeInt(prediction.getCount());
                    char[] predChars = prediction.getNextChars();
                    for (int i = 0; i < predChars.length; i++) {
                        dos.writeChar(predChars[i]);
                    }
                }
            }
        }
    }

    public void loadFrom(File file) throws Exception {
        long start = System.currentTimeMillis();
        try (DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(file)))) {
            int totalLoad = 0;
            //Read char count
            int totalCharCount = dis.readInt();
            for (int i = 0; i < totalCharCount; i++) {
                //Read starting char and prediction count
                char startChar = dis.readChar();
                int predictionCount = dis.readInt();
                List<CharPrediction> predictionList = new ArrayList<>(predictionCount);
                this.data.put(startChar, predictionList);
                //Read all prediction
                for (int j = 0; j < predictionCount; j++) {
                    int predCount = dis.readInt();
                    char[] predNextChars = new char[CharPredictor.CHAR_NGRAM_SIZE - 1];
                    for (int c = 0; c < CharPredictor.CHAR_NGRAM_SIZE - 1; c++) {
                        predNextChars[c] = dis.readChar();
                    }
                    predictionList.add(new CharPrediction(predNextChars, predCount));
                    totalLoad++;
                }
                //Sort char prediction
                //TODO : use another sort implementation : this one increase memory usage on loading
                Collections.sort(predictionList, CHAR_PREDICTION_COMPARATOR);
            }
            LOGGER.info("Loaded {} char ngram from file {} in {} ms ({} different char)", totalLoad, file, System.currentTimeMillis() - start,
                    data.size());
        }
    }

    //========================================================================

    // Class part : "Internal API"
    //========================================================================
    public String cleanText(String text) {
        return text.toLowerCase();
    }

    private void addPrediction(String ngram, int count) {
        char firstChar = ngram.charAt(0);
        CharPrediction pred = new CharPrediction(ngram.substring(1, ngram.length()).toCharArray(), count);
        if (!data.containsKey(firstChar)) {
            data.put(firstChar, new ArrayList<>());
        }
        data.get(firstChar).add(pred);
    }

    private void addToNGramCountMap(String charNGram, Map<String, Integer> ngramMap) {
        if (charNGram.length() == CharPredictor.CHAR_NGRAM_SIZE) {
            if (ngramMap.containsKey(charNGram)) {
                ngramMap.put(charNGram, ngramMap.get(charNGram) + 1);
            } else {
                ngramMap.put(charNGram, 1);
            }
        }
    }
    //========================================================================

}
