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

import org.lifecompanion.model.api.voicesynthesizer.VoiceInfoI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceParameterI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceSynthesizerI;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractVoiceSynthesizer implements VoiceSynthesizerI {
    /**
     * Voices
     */
    protected List<VoiceInfoI> voices;

    protected AbstractVoiceSynthesizer() {
        this.voices = new ArrayList<>();
    }

    @Override
    public VoiceInfoI getClosestVoice(final VoiceParameterI voiceInfoP) {
        String language = voiceInfoP.voiceLanguageProperty().get() != null ? voiceInfoP.voiceLanguageProperty().get()
                : Locale.getDefault().getLanguage();
        //Search for voices with the same locale
        for (VoiceInfoI voiceInfo : this.voices) {
            if (voiceInfo.getLocale().getLanguage().equals(Locale.forLanguageTag(language).getLanguage())) {
                return voiceInfo;
            }
        }
        //Not found, return first voice
        return this.voices.isEmpty() ? null : this.voices.get(0);
    }

    @Override
    public List<VoiceInfoI> getVoices() {
        return this.voices;
    }

    @Override
    public VoiceInfoI getDefaultVoice(Locale locale) {
        for (VoiceInfoI voiceInfo : this.voices) {
            if (voiceInfo.getLocale().getLanguage().equals(locale.getLanguage())) {
                return voiceInfo;
            }
        }
        return voices.isEmpty() ? null : voices.get(0);
    }

}
