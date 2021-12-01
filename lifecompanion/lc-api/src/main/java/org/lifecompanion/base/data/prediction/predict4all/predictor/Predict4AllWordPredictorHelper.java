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

package org.lifecompanion.base.data.prediction.predict4all.predictor;

import org.lifecompanion.api.prediction.WordPredictionI;
import org.lifecompanion.api.prediction.WordPredictionResultI;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.data.io.IOManager;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.predict4all.nlp.language.LanguageModel;
import org.predict4all.nlp.language.french.FrenchDefaultCorrectionRuleGenerator;
import org.predict4all.nlp.language.french.FrenchLanguageModel;
import org.predict4all.nlp.ngram.dictionary.DynamicNGramDictionary;
import org.predict4all.nlp.ngram.dictionary.StaticNGramTrieDictionary;
import org.predict4all.nlp.prediction.PredictionParameter;
import org.predict4all.nlp.prediction.WordPredictionResult;
import org.predict4all.nlp.prediction.WordPredictor;
import org.predict4all.nlp.utils.Pair;
import org.predict4all.nlp.words.WordDictionary;
import org.predict4all.nlp.words.correction.CorrectionRuleNode;
import org.predict4all.nlp.words.correction.CorrectionRuleNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Helper to share load behavior between predictor and configuration views.
 */
public class Predict4AllWordPredictorHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(Predict4AllWordPredictorHelper.class);

    // CONSTANT
    //========================================================================
    public final static String EXT_PATH_P4A_WORD_PREDICTOR = LCConstant.EXT_PATH_DATA + "p4a-word-predictor" + File.separator;
    public static final String P4A_STATIC_PREDICTION = EXT_PATH_P4A_WORD_PREDICTOR + "ngrams.bin";
    public static final String P4A_STATIC_DICTIONARY = EXT_PATH_P4A_WORD_PREDICTOR + "words.bin";
    public static final String P4A_PROFILE_DIR_NAME = "predict4all";
    public static final String P4A_PROFILE_CONFIG_NAME = "configuration.json";
    public static final String P4A_USER_TEXTS_NAME = "user-texts";
    public static final String P4A_USER_PREDICTION_NAME = "user-predictions.bin";
    public static final String P4A_USER_DICTIONARY_NAME = "user-dictionary.bin";
    public static final File PREDICTION_FILE = new File(P4A_STATIC_PREDICTION), DICTIONARY_FILE = new File(P4A_STATIC_DICTIONARY);
    private final static LanguageModel languageModel = new FrenchLanguageModel();
    //========================================================================

    private Predict4AllWordPredictorHelper() {
    }

    static {
        FrenchDefaultCorrectionRuleGenerator.setTranslationProvider(Translation::getText);
    }

    // PREDICTION
    //========================================================================
    public static WordPredictionResultI predictorOn(final WordPredictor wordPredictor, final String textBeforeCaret, final String textAfterCaret, final int count) {
        try {
            long start = System.currentTimeMillis();
            WordPredictionResult pr = wordPredictor.predict(textBeforeCaret, textAfterCaret, count);
            List<WordPredictionI> predictions = pr.getPredictions()
                    .stream()
                    .map(p -> new org.lifecompanion.base.data.prediction2.WordPrediction(
                            p.getPredictionToDisplay(),
                            p.getPredictionToInsert(),
                            pr.getNextCharCountToRemove(),
                            p.getPreviousCharCountToRemove(),
                            p.isInsertSpacePossible(),
                            p.getScore(),
                            p.getDebugInformation(),
                            p)
                    )
                    .collect(Collectors.toList());
            return new org.lifecompanion.base.data.prediction2.WordPredictionResult(textBeforeCaret, System.currentTimeMillis() - start, predictions);
        } catch (Exception e) {
            LOGGER.error("Couldn't predict next words", e);
        }
        return null;
    }

    //========================================================================

    // LOADING
    //========================================================================
    public static PredictorModelDto loadData(String configurationId) throws IOException {
        PredictionParameter predictionParameter = loadOrGetCurrentPredictionParameter(configurationId);
        Pair<Boolean, WordDictionary> wordDictionaryAndFailed = loadDictionaryAndUserDictionary(configurationId);
        WordDictionary wordDictionary = wordDictionaryAndFailed.getRight();
        DynamicNGramDictionary dynamicNGramDictionary = null;
        if (predictionParameter.isDynamicModelEnabled()) {
            if (wordDictionaryAndFailed.getLeft()) {
                LOGGER.warn("Ignore dynamic ngram dictionary loading because the user dictionary loading failed (create a new model)");
                dynamicNGramDictionary = new DynamicNGramDictionary(4);
            } else {
                dynamicNGramDictionary = loadOrCreateDynamicNGramDictionary(configurationId);
            }
        }
        return new PredictorModelDto(wordDictionary, dynamicNGramDictionary, predictionParameter);
    }

    private static PredictionParameter loadOrGetCurrentPredictionParameter(String configurationId) {
        File predictionParamPath = getCurrentConfigurationPath(configurationId);
        if (predictionParamPath.exists()) {
            try {
                return PredictionParameter.loadFrom(languageModel, predictionParamPath);
            } catch (Exception e) {
                LOGGER.info("Couldn't get prediction parameter from configuration file, will return default parameter", e);
            }
        }
        // Configuration default prediction parameter
        PredictionParameter predictionParameter = new PredictionParameter(languageModel);
        predictionParameter.setEnableWordCorrection(true);
        CorrectionRuleNode node = new CorrectionRuleNode(CorrectionRuleNodeType.NODE);
        node.addChild(FrenchDefaultCorrectionRuleGenerator.CorrectionRuleType.WORD_SPACE_APOSTROPHE.generateNodeFor(predictionParameter));
        node.addChild(FrenchDefaultCorrectionRuleGenerator.CorrectionRuleType.ACCENTS.generateNodeFor(predictionParameter));
        node.setName(Translation.getText("predict4all.rule.default.root.name"));
        predictionParameter.setCorrectionRulesRoot(node);
        return predictionParameter;
    }

    private static DynamicNGramDictionary loadOrCreateDynamicNGramDictionary(String configurationId) {
        File dynamicNGramFile = new File(getAndInitializeCurrentProfileRoot(configurationId) + File.separator + P4A_USER_PREDICTION_NAME);
        if (dynamicNGramFile.exists()) {
            try {
                return DynamicNGramDictionary.load(dynamicNGramFile);
            } catch (Exception e) {
                LOGGER.error("Couldn't load custom ngram dictionary from {}, will initialize a new one", dynamicNGramFile, e);
                return new DynamicNGramDictionary(4);
            }
        } else {
            return new DynamicNGramDictionary(4);
        }
    }

    public static StaticNGramTrieDictionary loadStaticNGramDictionary() throws IOException {
        return StaticNGramTrieDictionary.open(PREDICTION_FILE);
    }

    private static Pair<Boolean, WordDictionary> loadDictionaryAndUserDictionary(String configurationId) throws IOException {
        boolean userDictionaryFailed = false;
        WordDictionary wordDictionary = WordDictionary.loadDictionary(languageModel, DICTIONARY_FILE);
        File dynamicDictionaryFile = new File(getAndInitializeCurrentProfileRoot(configurationId) + File.separator + P4A_USER_DICTIONARY_NAME);
        if (dynamicDictionaryFile.exists()) {
            try {
                wordDictionary.loadUserDictionary(dynamicDictionaryFile);
            } catch (Exception e) {
                userDictionaryFailed = true;
                LOGGER.error("Loading user dictionary failed, will try to load last temp file", e);
                File[] tempFiles = new File(getAndInitializeCurrentProfileRoot(configurationId) + File.separator).listFiles();
                Optional<File> lastModifiedUserDic = Arrays.stream(tempFiles)//
                        .filter(f -> StringUtils.startWithIgnoreCase(f.getName(), P4A_USER_DICTIONARY_NAME))//
                        .filter(f -> StringUtils.endsWithIgnoreCase(f.getName(), ".tmp"))//
                        .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))//
                        .findFirst();
                // Use temp file
                if (lastModifiedUserDic.isPresent()) {
                    LOGGER.info("Will try to use temp file {} to load dynamic user dictionary", lastModifiedUserDic.get());
                    IOUtils.copyFiles(lastModifiedUserDic.get(), dynamicDictionaryFile);
                    lastModifiedUserDic.get().delete();
                    try {
                        wordDictionary.loadUserDictionary(dynamicDictionaryFile);
                        userDictionaryFailed = false;
                    } catch (Exception e1) {
                        LOGGER.error("Loading temp file user dictionary failed", e1);
                    }
                } else {
                    LOGGER.warn("Didn't find any temp user model, will ignore user model loading...");
                }
            }
        }
        return Pair.of(userDictionaryFailed, wordDictionary);
    }
    //========================================================================

    // SAVING
    //========================================================================
    public static void saveDynamicNGramDictionary(String configurationId, final DynamicNGramDictionary ngramDictionary) {
        if (ngramDictionary != null) {
            File dynamicNGramFile = new File(getAndInitializeCurrentProfileRoot(configurationId) + File.separator + P4A_USER_PREDICTION_NAME);
            File dynamicNGramFileTemp = new File(
                    getAndInitializeCurrentProfileRoot(configurationId) + File.separator + P4A_USER_PREDICTION_NAME + "_" + UUID.randomUUID().toString() + ".tmp");
            try {
                ngramDictionary.saveDictionary(dynamicNGramFileTemp);
                IOUtils.copyFiles(dynamicNGramFileTemp, dynamicNGramFile);
                dynamicNGramFileTemp.delete();
            } catch (IOException e) {
                LOGGER.error("Couldn't save dynamic ngram dictionary", e);
            }
        }
    }

    public static void saveUserDictionary(String configurationId, final WordDictionary wordDictionary) {
        if (wordDictionary != null) {
            File dynamicDictionaryFileTemp = new File(
                    getAndInitializeCurrentProfileRoot(configurationId) + File.separator + P4A_USER_DICTIONARY_NAME + "_" + UUID.randomUUID().toString() + ".tmp");
            File dynamicDictionaryFile = new File(getAndInitializeCurrentProfileRoot(configurationId) + File.separator + P4A_USER_DICTIONARY_NAME);
            try {
                wordDictionary.saveUserDictionary(dynamicDictionaryFileTemp);
                IOUtils.copyFiles(dynamicDictionaryFileTemp, dynamicDictionaryFile);
                dynamicDictionaryFileTemp.delete();
            } catch (IOException e) {
                LOGGER.error("Couldn't save user dictionary", e);
            }
        }
    }
    //========================================================================


    // PATH
    //========================================================================
    public static File getAndInitializeCurrentProfileRoot(String configurationId) {
        File newP4ADir = new File(IOManager.INSTANCE.getConfigurationDirectoryPath(AppController.INSTANCE.currentProfileProperty().get().getID(), configurationId) + File.separator + P4A_PROFILE_DIR_NAME + File.separator);
        // Backward compatibility : copy old profile configuration when needed
        File oldP4ADir = new File(IOManager.INSTANCE.getProfileDirectoryPath(AppController.INSTANCE.currentProfileProperty().get().getID()) + File.separator + P4A_PROFILE_DIR_NAME + File.separator);
        if (!newP4ADir.exists() && oldP4ADir.exists()) {
            try {
                IOUtils.copyDirectory(oldP4ADir, newP4ADir);
            } catch (Exception e) {
                LOGGER.error("Couldn't copy old P4A configuration from {} to {}", oldP4ADir, newP4ADir, e);
            }
        } else {
            newP4ADir.mkdirs();
        }
        return newP4ADir;
    }

    public static File getCurrentConfigurationPath(String configurationId) {
        return new File(getAndInitializeCurrentProfileRoot(configurationId) + File.separator + P4A_PROFILE_CONFIG_NAME);
    }

    //========================================================================
}
