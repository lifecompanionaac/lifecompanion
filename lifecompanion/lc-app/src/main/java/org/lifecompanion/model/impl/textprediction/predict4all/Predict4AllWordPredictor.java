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

import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.textprediction.WordPredictionResultI;
import org.lifecompanion.model.api.textprediction.WordPredictorI;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.predict4all.nlp.Separator;
import org.predict4all.nlp.ngram.dictionary.DynamicNGramDictionary;
import org.predict4all.nlp.prediction.WordPredictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Predict4All integration
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class Predict4AllWordPredictor implements WordPredictorI {
    public static final String ID = "p4a-word-predictor";

    private static final boolean USER_TEXTS_ENCRYPTED = false;
    private final static Logger LOGGER = LoggerFactory.getLogger(Predict4AllWordPredictor.class);

    private WordPredictor wordPredictor;

    private final Set<String> usedConfigurationIds;

    public Predict4AllWordPredictor() {
        usedConfigurationIds = new HashSet<>();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getConfigStepId() {
        return P4AConfigurationSteps.CONFIG_ROOT_ENTRY_POINT.name();
    }

    @Override
    public String getName() {
        return Translation.getText("predict4all.predictor.name");
    }

    @Override
    public String getDescription() {
        return Translation.getText("predict4all.predictor.description");
    }

    @Override
    public List<SystemType> getCompatibleSystems() {
        return Arrays.asList(SystemType.values());
    }

    @Override
    public boolean isNewSentenceStarted(final String text) {
        return isNewSentenceStartedIn(text);
    }

    @Override
    public WordPredictionResultI predict(final String textBeforeCaret, final String textAfterCaret, final int count) {
        this.wordPredictor.getPredictionParameter().setEnableDebugInformation(false);
        return Predict4AllWordPredictorHelper.predictorOn(this.wordPredictor, textBeforeCaret, textAfterCaret, count);
    }

    public WordPredictor getWordPredictor() {
        return this.wordPredictor;
    }


    @Override
    public void trainDynamicModel(final String text) {
        // Train dynamic model is quite particular here : because predictor is disposed on modeStop(), training should initialize the predictor again and then dispose it
        if (!StringUtils.isBlank(text)) {
            for (String usedConfigurationId : usedConfigurationIds) {
                Predict4AllWordPredictor.LOGGER.info("Will train P4A data for configuration {} with a text of length {}", usedConfigurationId, text.length());
                try {
                    this.loadWordPredictor(usedConfigurationId);
                    this.wordPredictor.trainDynamicModel(text, Predict4AllWordPredictor.USER_TEXTS_ENCRYPTED);
                    this.dispose();
                } catch (Exception e) {
                    Predict4AllWordPredictor.LOGGER.error("Couldn't train Predict4All", e);
                }
            }
            //P4AUserModelUtils.saveUserModel(new File(Predict4AllWordPredictorHelper.getAndInitializeCurrentProfileRoot() + File.separator + Predict4AllWordPredictorHelper.P4A_USER_TEXTS_NAME), false, text);
        } else {
            Predict4AllWordPredictor.LOGGER.info("Model training is ignored for this session because clinical study was enabled");
        }
    }


    @Override
    public void dispose() {
        if (wordPredictor != null) {
            try {
                this.wordPredictor.dispose();
                this.wordPredictor = null;
            } catch (Exception e) {
                LOGGER.error("Wasn't able to dispose word predictor on mode stop", e);
            }
        }
    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    @Override
    public void initialize() throws Exception {
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        try {
            loadWordPredictor(configuration.getID());
        } catch (Exception e) {
            LOGGER.error("Problem on P4A word predictor initialization", e);
        }
    }

    private void loadWordPredictor(String configurationId) throws IOException {
        PredictorModelDto predictorModelDto = Predict4AllWordPredictorHelper.loadData(configurationId);
        this.wordPredictor = new WordPredictor(predictorModelDto.getPredictionParameter(),
                predictorModelDto.getWordDictionary(),
                Predict4AllWordPredictorHelper.loadStaticNGramDictionary(),
                predictorModelDto.getDynamicNGramDictionary());
    }


    @Override
    public void modeStop(final LCConfigurationI configuration) {
        if (this.wordPredictor != null) {
            usedConfigurationIds.add(configuration.getID());
            Predict4AllWordPredictorHelper.saveUserDictionary(configuration.getID(), this.wordPredictor.getWordDictionary());
            Predict4AllWordPredictorHelper.saveDynamicNGramDictionary(configuration.getID(), (DynamicNGramDictionary) this.wordPredictor.getDynamicNGramDictionary());
        }
    }


    // P4A CONFIGURATION IN CONFIGs
    //========================================================================
    private static final String NODE_P4A_WORD_PREDICTOR = "Predict4AllPredictor";

    @Override
    public Element serialize(final IOContextI context) {
        return new Element(Predict4AllWordPredictor.NODE_P4A_WORD_PREDICTOR);
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        //Element element = node.getChild(Predict4AllWordPredictor.NODE_P4A_WORD_PREDICTOR);
    }
    //========================================================================

    // UTILS
    //========================================================================
    public static boolean isNewSentenceStartedIn(final String text) {
        if (text == null || text.length() == 0) {
            return true;
        }
        char last = text.charAt(text.length() - 1);
        Separator separatorFor = Separator.getSeparatorFor(last);
        //If there is a space, go back, because a lot of sentence ends are with a space then the word begin
        if ((separatorFor == Separator.NEWLINE || separatorFor == Separator.SPACE || separatorFor == Separator.TAB)) {
            return isNewSentenceStartedIn(text.substring(0, text.length() - 1));
        } else {
            return separatorFor != null && separatorFor.isSentenceSeparator();
        }
    }
    //========================================================================
}
