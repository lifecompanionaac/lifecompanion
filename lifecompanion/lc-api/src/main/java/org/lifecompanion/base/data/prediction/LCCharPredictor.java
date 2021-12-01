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
package org.lifecompanion.base.data.prediction;

import org.jdom2.Element;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.prediction.CharPredictorI;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.prediction.charpredictor.CharPredictor;
import org.lifecompanion.base.data.prediction.charpredictor.CharPredictorData;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum LCCharPredictor implements CharPredictorI {
    INSTANCE;

    private final Logger LOGGER = LoggerFactory.getLogger(LCCharPredictor.class);
    /**
     * Prediction files
     */
    private File predictionFile = new File(LCConstant.FUS_CHAR_PREDICTION_FILE);

    /**
     * FUS predictor
     */
    private CharPredictor charPredictor;

    LCCharPredictor() {
    }

    @Override
    public String getId() {
        return "fus-char-predictor";
    }

    @Override
    public String getConfigStepId() {
        return null;
    }

    @Override
    public String getName() {
        return Translation.getText("fus.char.predictor.name");
    }

    @Override
    public String getDescription() {
        return Translation.getText("fus.char.predictor.description");
    }

    @Override
    public List<SystemType> getCompatibleSystems() {
        return Arrays.asList(SystemType.ANDROID, SystemType.UNIX, SystemType.WINDOWS, SystemType.MAC);
    }

    public void setFilesPath(final File predictionFileP) {
        this.predictionFile = predictionFileP;
    }

    @Override
    public synchronized void initialize() throws Exception {
        CharPredictorData datas = new CharPredictorData();
        datas.loadFrom(this.predictionFile);
        this.charPredictor = new CharPredictor(datas);
    }

    @Override
    public synchronized boolean isInitialized() {
        return this.charPredictor != null;
    }

    @Override
    public void dispose() throws Exception {
        this.charPredictor.dispose();
    }

    @Override
    public List<Character> predict(final String text, final int limit) {
        return this.charPredictor.predict(text, limit);
    }

    @Override
    public List<Character> predict(final String text, final int limit, final HashSet<Character> acceptedCharacters)
            throws UnsupportedOperationException {
        return this.charPredictor.predict(text, limit, acceptedCharacters);
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
    }

    @Override
    public void trainDynamicModel(String text) {
    }

    @Override
    public Element serialize(IOContextI context) {
        return null;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {

    }
}
