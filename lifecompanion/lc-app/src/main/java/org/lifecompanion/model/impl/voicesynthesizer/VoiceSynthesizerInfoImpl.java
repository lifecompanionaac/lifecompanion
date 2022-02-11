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
package org.lifecompanion.model.impl.voicesynthesizer;

import org.jdom2.Element;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceAndSynthesizerInfoI;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

/**
 * Voice information
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VoiceSynthesizerInfoImpl implements VoiceAndSynthesizerInfoI {
    private String voiceId, synthesizerId;
    private String voiceLabel, synthesizerLabel;

    public VoiceSynthesizerInfoImpl() {
    }

    public VoiceSynthesizerInfoImpl(final String voiceId, final String synthesizerId, final String voiceLabel, final String synthesizerLabel) {
        this();
        this.voiceId = voiceId;
        this.synthesizerId = synthesizerId;
        this.voiceLabel = voiceLabel;
        this.synthesizerLabel = synthesizerLabel;
    }

    @Override
    public String getVoiceId() {
        return this.voiceId;
    }

    public void setVoiceId(final String voiceId) {
        this.voiceId = voiceId;
    }

    @Override
    public String getSynthesizerId() {
        return this.synthesizerId;
    }

    public void setSynthesizerId(final String synthesizerId) {
        this.synthesizerId = synthesizerId;
    }

    @Override
    public String getVoiceLabel() {
        return this.voiceLabel;
    }

    public void setVoiceLabel(final String voiceLabel) {
        this.voiceLabel = voiceLabel;
    }

    @Override
    public String getSynthesizerLabel() {
        return this.synthesizerLabel;
    }

    public void setSynthesizerLabel(final String synthesizerLabel) {
        this.synthesizerLabel = synthesizerLabel;
    }

    @Override
    public String getDisplayableLabel() {
        return this.voiceLabel + " ( " + this.synthesizerLabel + " )";
    }

    @Override
    public String toString() {
        return this.getDisplayableLabel();
    }

    public static final String NODE_SELECTED_VOICE = "SelectedVoice";

    @Override
    public Element serialize(final IOContextI contextP) {
        Element xmlElement = new Element(VoiceSynthesizerInfoImpl.NODE_SELECTED_VOICE);
        XMLObjectSerializer.serializeInto(VoiceSynthesizerInfoImpl.class, this, xmlElement);
        return xmlElement;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        XMLObjectSerializer.deserializeInto(VoiceSynthesizerInfoImpl.class, this, nodeP);
    }
}
