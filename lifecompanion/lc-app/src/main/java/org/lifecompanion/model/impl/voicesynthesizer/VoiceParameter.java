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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceInfoI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceParameterI;
import org.lifecompanion.model.impl.exception.LCException;

/**
 * Implementation for selected voice.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VoiceParameter implements VoiceParameterI {

    private final StringProperty voiceId;
    private final StringProperty voiceName;
    private final StringProperty voiceLanguage;

    private final ObjectProperty<VoiceInfoI> selectedVoiceInfo;

    public VoiceParameter() {
        this.voiceId = new SimpleStringProperty(null);
        this.voiceName = new SimpleStringProperty(null);
        this.voiceLanguage = new SimpleStringProperty(null);
        this.selectedVoiceInfo = new SimpleObjectProperty<>(null);
        this.selectedVoiceInfo.addListener((obs, ov, nv) -> {
            if (ov != null) {
                this.voiceId.set(null);
                this.voiceName.set(null);
                this.voiceLanguage.set(null);
            }
            if (nv != null) {
                this.voiceId.set(nv.getId());
                this.voiceName.set(nv.getName());
                this.voiceLanguage.set(nv.getLocale().getLanguage());
            }
        });
    }

    // Class part : "Properties"
    //========================================================================
    @Override
    public StringProperty voiceIdProperty() {
        return this.voiceId;
    }

    @Override
    public StringProperty voiceNameProperty() {
        return this.voiceName;
    }

    @Override
    public StringProperty voiceLanguageProperty() {
        return this.voiceLanguage;
    }

    @Override
    public ObjectProperty<VoiceInfoI> selectedVoiceInfoProperty() {
        return this.selectedVoiceInfo;
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    public static final String NODE_VOICE = "VoiceParameter";

    @Override
    public Element serialize(final IOContextI contextP) {
        Element voiceElement = new Element(VoiceParameter.NODE_VOICE);
        XMLObjectSerializer.serializeInto(VoiceParameter.class, this, voiceElement);
        return voiceElement;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        XMLObjectSerializer.deserializeInto(VoiceParameter.class, this, nodeP);
        //Correct voice is selected by VoiceSynthesizerParameter, after setting the synthesizer
    }
    //========================================================================


    @Override
    public String toString() {
        return "VoiceParameter{" +
                "voiceId=" + voiceId.get() +
                ", voiceName=" + voiceName.get() +
                ", voiceLanguage=" + voiceLanguage.get() +
                ", selectedVoiceInfo=" + selectedVoiceInfo.get() +
                '}';
    }
}
