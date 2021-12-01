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
package org.lifecompanion.base.data.voice;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.voice.PronunciationExceptionI;
import org.lifecompanion.api.voice.VoiceParameterI;
import org.lifecompanion.api.voice.VoiceSynthesizerI;
import org.lifecompanion.api.voice.VoiceSynthesizerParameterI;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;

import java.util.List;

/**
 * Parameters for voice synthesizers.<br>
 * Voice controller should take this parameters to account.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VoiceSynthesizerParameter implements VoiceSynthesizerParameterI {
    private IntegerProperty volume;
    private IntegerProperty rate;
    private IntegerProperty pitch;
    private ObjectProperty<VoiceSynthesizerI> selectedVoiceSynthesizer;
    private ObservableList<PronunciationExceptionI> pronunciationExceptions;
    private VoiceParameterI voiceParameter;

    public VoiceSynthesizerParameter() {
        this.volume = new SimpleIntegerProperty(this, "volume", 100);
        this.rate = new SimpleIntegerProperty(this, "rate", 0);
        this.pitch = new SimpleIntegerProperty(this, "pitch", 0);
        this.selectedVoiceSynthesizer = new SimpleObjectProperty<>(this, "selectedVoiceSynthesizer", null);
        this.pronunciationExceptions = FXCollections.observableArrayList();
        this.voiceParameter = new VoiceParameter();
        //When change the synthesizer, try to find the closest voice
        this.selectedVoiceSynthesizer.addListener((obs, ov, nv) -> {
            if (nv != null) {
                this.selectVoiceClosestVoice(nv);
            }
        });
        //Set the default synthesizer
        this.selectedVoiceSynthesizer.set(VoiceSynthesizerController.INSTANCE.getSystemDefault());
    }

    private void selectVoiceClosestVoice(final VoiceSynthesizerI nv) {
        VoiceSynthesizerController.INSTANCE.selectCorrectVoice(this.voiceParameter, nv);
    }

    @Override
    public IntegerProperty volumeProperty() {
        return this.volume;
    }

    @Override
    public IntegerProperty rateProperty() {
        return this.rate;
    }

    @Override
    public IntegerProperty pitchProperty() {
        return this.pitch;
    }

    @Override
    public ObjectProperty<VoiceSynthesizerI> selectedVoiceSynthesizerProperty() {
        return this.selectedVoiceSynthesizer;
    }

    @Override
    public VoiceParameterI getVoiceParameter() {
        return this.voiceParameter;
    }

    @Override
    public ObservableList<PronunciationExceptionI> getPronunciationExceptions() {
        return this.pronunciationExceptions;
    }

    // Class part : "XML"
    //========================================================================
    public static final String NODE_VOICE_PARAMETERS = "VoiceSynthesizerParameters";
    private static final String NODE_PRONUNCIATION_EXCEPTIONS = "PronunciationExceptions";

    private static final String ATB_SYNTHESIZER_ID = "synthesizerId";

    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = new Element(VoiceSynthesizerParameter.NODE_VOICE_PARAMETERS);
        XMLObjectSerializer.serializeInto(VoiceSynthesizerParameter.class, this, node);
        String synthesizerId = null;
        if (this.selectedVoiceSynthesizer.get() != null) {
            synthesizerId = this.selectedVoiceSynthesizer.get().getId();
            String pluginIdForSynthesizer = VoiceSynthesizerController.INSTANCE.getPluginIdForSynthesizer(synthesizerId);
            if (pluginIdForSynthesizer != null) {
                contextP.getAutomaticPluginDependencyIds().add(pluginIdForSynthesizer);
            }
        }
        XMLUtils.write(synthesizerId, VoiceSynthesizerParameter.ATB_SYNTHESIZER_ID, node);
        node.addContent(this.voiceParameter.serialize(contextP));
        //Exceptions
        Element elementExceptions = new Element(VoiceSynthesizerParameter.NODE_PRONUNCIATION_EXCEPTIONS);
        node.addContent(elementExceptions);
        for (PronunciationExceptionI exc : this.pronunciationExceptions) {
            elementExceptions.addContent(exc.serialize(contextP));
        }
        return node;
    }

    @Override
    public void deserialize(final Element node, final IOContextI contextP) throws LCException {
        XMLObjectSerializer.deserializeInto(VoiceSynthesizerParameter.class, this, node);

        //Read voice (before synthesizer loaded, because the synthesizer change will fire voice set)
        Element voiceNode = node.getChild(VoiceParameter.NODE_VOICE);
        this.voiceParameter.deserialize(voiceNode, contextP);

        //Get synthesizer first
        String synthesizerId = XMLUtils.readString(VoiceSynthesizerParameter.ATB_SYNTHESIZER_ID, node);
        if (synthesizerId != null) {
            this.selectedVoiceSynthesizer.set(VoiceSynthesizerController.INSTANCE.getVoiceSynthesizerOrDefault(synthesizerId));
        }
        //Select the closest voice (because synthesizer may not change if it's the same than default)
        if (this.selectedVoiceSynthesizer.get() != null) {
            this.selectVoiceClosestVoice(this.selectedVoiceSynthesizer.get());
        }

        //Exceptions
        Element exceptionsNode = node.getChild(VoiceSynthesizerParameter.NODE_PRONUNCIATION_EXCEPTIONS);
        if (exceptionsNode != null) {
            List<Element> children = exceptionsNode.getChildren();
            for (Element excChild : children) {
                PronunciationException exc = new PronunciationException();
                exc.deserialize(excChild, contextP);
                this.pronunciationExceptions.add(exc);
            }
        }
    }
    //========================================================================

    @Override
    public String toString() {
        return "VoiceSynthesizerParameter{" +
                "volume=" + volume.get() +
                ", rate=" + rate.get() +
                ", pitch=" + pitch.get() +
                ", voiceParameter=" + voiceParameter.toString() +
                '}';
    }
}
