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
package org.lifecompanion.model.api.voicesynthesizer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;

/**
 * Parameter to control the voice synthesizer.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface VoiceSynthesizerParameterI extends XMLSerializable<IOContextI> {

    /**
     * @return the property that contains the currently selected voice synthesizer.<br>
     * On loading, this voice synthesizer can change if it is not available.
     */
    ObjectProperty<VoiceSynthesizerI> selectedVoiceSynthesizerProperty();

    /**
     * @return the selected voice in the voice synthesizer.<br>
     * On loading, this can change too if the voice is not installed.
     */
    VoiceParameterI getVoiceParameter();

    /**
     * @return the volume of speech, between 0 to 100 (inclusive)
     */
    IntegerProperty volumeProperty();

    /**
     * @return the rate of the speech, between -10 to 10 (0 = normal rate)
     */
    IntegerProperty rateProperty();

    /**
     * @return the pitch of the speech, between -10 to 10 (0= normal pitch)
     */
    IntegerProperty pitchProperty();

    /**
     * @return the list of all pronunciation exceptions
     */
    ObservableList<PronunciationExceptionI> getPronunciationExceptions();
}
