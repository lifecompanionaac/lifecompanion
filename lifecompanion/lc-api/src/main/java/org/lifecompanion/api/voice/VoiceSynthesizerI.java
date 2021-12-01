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
package org.lifecompanion.api.voice;

import org.lifecompanion.framework.commons.SystemType;

import java.util.List;
import java.util.Locale;

/**
 * Represent a voice synthesizer.<br>
 * This is the responsibility of the synthesizer to be as optimized as possible.<br>
 * For example, the {@link #setVoice(VoiceInfoI)} can be called many times with the same voice, and the synthesizer should change the voice only if needed.<br>
 * It is also very important that the {@link #speak(String)} works <strong>synchronously</strong>.<br>
 * Synthesizer can cache the parameters set if they need to be used only when {@link #speak(String)} is called.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface VoiceSynthesizerI {

    /**
     * @return a unique ID for this synthesizer, this id shouldn't change in the time
     */
    String getId();

    /**
     * @return a name for this synthesizer, the name should be understandable for a user
     */
    String getName();

    /**
     * @return a full description of this synthesizer
     */
    String getDescription();

    /**
     * @return the list of all compatible systems with synthesizer.<br>
     * If the current system is not compatible, the synthesizer will not be added to available synthesizers
     */
    List<SystemType> getCompatibleSystems();

    /**
     * Must initialize the synthesizer.<br>
     * The synthesizer should be able to work after this call.
     */
    void initialize() throws Exception;

    /**
     * @return true if and if only the synthesizer is initialized.<br>
     * On certain synthesizer, this can return true even if the {@link #initialize()} was not called.
     */
    boolean isInitialized();

    /**
     * Should dispose all resources that this synthesizer use.
     *
     * @throws Exception if dispose can't be done
     */
    void dispose() throws Exception;

    /**
     * Should speak the given text in a synchronized manner.<br>
     * <strong>This method should return only when the speech ended.</strong>
     *
     * @param text the text to speak, can contains special char.<br>
     *             The synthesizer should clean it if needed.
     */
    void speak(String text);

    /**
     * Should try to current call to {@link #speak(String)}<br>
     * This will be called from a different Thread than the one that called {@link #speak(String)}, it can be used to cancel the current speech.<br>
     * Note that after this call, the synthesizer should be able to get subsequent calls.<br>
     * Also note that this can be called even the last {@link #speak(String)} has ended, the synthesizer is responsible to ignore the call.
     */
    void stopCurrentSpeak();

    /**
     * @return the list of all voice available for this synthesizer.<br>
     * This can return a empty list if {@link #initialize()} was not called.
     */
    List<VoiceInfoI> getVoices();

    /**
     * Should return the best default for the given locale
     *
     * @param locale the user locale
     * @return the best default default voice found
     */
    VoiceInfoI getDefaultVoice(Locale locale);

    /**
     * Should change the current used voice.
     *
     * @param voiceInfo the voice to set
     */
    void setVoice(VoiceInfoI voiceInfo);

    /**
     * Should change the volume for synthesizer
     *
     * @param volume the volume, between 0 to 100
     */
    void setVolume(int volume);

    /**
     * Should change the speech rate
     *
     * @param rate the rate, between -10 to 10 (0 : normal)
     */
    void setRate(int rate);

    /**
     * Should change the voice pitch
     *
     * @param pitch the pitch, between -10 to 10 (0 : normal)
     */
    void setPitch(int pitch);

    /**
     * Should return the closest voice for the given voice info.<br>
     * This is typically done to change voice when one is not available.
     *
     * @param voiceInfo the original voice info
     * @return the closest voice info, should return null only if the the {@link #getVoices()} method return a empty list
     */
    VoiceInfoI getClosestVoice(VoiceParameterI voiceInfo);

}
