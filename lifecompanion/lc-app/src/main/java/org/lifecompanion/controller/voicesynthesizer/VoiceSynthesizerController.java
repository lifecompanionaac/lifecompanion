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
package org.lifecompanion.controller.voicesynthesizer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.voicesynthesizer.*;
import org.lifecompanion.model.impl.voicesynthesizer.SAPIVoiceSynthesizer;
import org.lifecompanion.model.impl.voicesynthesizer.SayCommandVoiceSynthesizer;
import org.lifecompanion.model.impl.voicesynthesizer.VoiceSynthesizerInfoImpl;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller that allow text to speech.<br>
 * Controller use different synthesizer to create sounds, depending on the {@link VoiceSynthesizerParameterI} given.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum VoiceSynthesizerController implements LCStateListener, ModeListenerI {
    INSTANCE;

    private final Logger LOGGER = LoggerFactory.getLogger(VoiceSynthesizerController.class);

    /**
     * Thread pool to speak text in background
     */
    private final ExecutorService voiceSpeakExecutor;

    /**
     * List of all available voice synthesizer
     */
    private final ObservableList<VoiceSynthesizerI> voiceSynthesizers;

    /**
     * List of all voice default synthesizer by system
     */
    private final Map<SystemType, VoiceSynthesizerI> systemDefaultSynthesizers;

    /**
     * Contains plugin ids for synthesizer ids
     */
    private final Map<String, String> pluginIdsForSynthesizerId;

    /**
     * To disable voice synthesizer sound
     */
    private final BooleanProperty disableVoiceSynthesizer;

    /**
     * Currently "speaking" synthesizer
     */
    private VoiceSynthesizerI currentSynthesizer;

    /**
     * All voices for all synthesizer
     */
    private final ObservableList<VoiceAndSynthesizerInfoI> allVoice;

    /**
     * Contains all the queued speak tasks
     */
    private final CopyOnWriteArrayList<SpeakTask> queuedTasks;


    VoiceSynthesizerController() {
        this.systemDefaultSynthesizers = new HashMap<>();
        this.pluginIdsForSynthesizerId = new HashMap<>();
        this.voiceSynthesizers = FXCollections.observableArrayList();
        this.allVoice = FXCollections.observableArrayList();
        this.voiceSpeakExecutor = Executors.newSingleThreadExecutor(LCNamedThreadFactory.threadFactory("VoiceSynthesizerController"));
        this.disableVoiceSynthesizer = new SimpleBooleanProperty(false);
        this.queuedTasks = new CopyOnWriteArrayList<>();
        this.init();
    }

    private void init() {
        PluginController.INSTANCE.getVoiceSynthesizers().registerListenerAndDrainCache((pluginId, voiceSynthType) -> {
            try {
                VoiceSynthesizerI synthesizer = voiceSynthType.getConstructor().newInstance();
                registrerVoiceSynthesizer(synthesizer);
                pluginIdsForSynthesizerId.put(synthesizer.getId(), pluginId);
            } catch (Exception e) {
                LOGGER.error("Can't create voice synthesizer {} from plugin", voiceSynthType, e);
            }
        });
    }

    public String getPluginIdForSynthesizer(String voiceSynthesizerId) {
        return pluginIdsForSynthesizerId.get(voiceSynthesizerId);
    }

    /**
     * To speak sync : the method will return when speak finished
     *
     * @param text             the text to pronounce
     * @param speakEndCallback callback called on speak end (can be null)
     */
    // Class part : "Public speak API"
    //========================================================================
    public void speakSync(final String text, final Runnable speakEndCallback, final boolean spell) {
        if (!this.disableVoiceSynthesizer.get()) {
            final VoiceSynthesizerParameterI parameters = AppModeController.INSTANCE.getUseModeContext().configurationProperty().get().getVoiceSynthesizerParameter();
            Future<?> futureTask = this.submitExecutorTask(text, parameters, speakEndCallback, spell);
            try {
                futureTask.get();
            } catch (Exception e) {
                this.LOGGER.warn("Couldn't wait for the speak to end", e);
            }
        } else {
            if (speakEndCallback != null) {
                speakEndCallback.run();
            }
        }
    }

    public void speakSync(final String text, final Runnable speakEndCallback) {
        this.speakSync(text, speakEndCallback, false);
    }

    public void speakSync(final String text) {
        this.speakSync(text, null, false);
    }

    public void spellSync(final String text) {
        this.speakSync(text, null, true);
    }

    /**
     * To speak async : this method will immediately return and launch a background thread to execute the text to speech.
     *
     * @param text             the text to pronounce
     * @param parameters       text to speech parameters
     * @param speakEndCallback callback that will be called when speak ended (can be null)
     */
    public void speakAsync(final String text, final VoiceSynthesizerParameterI parameters, final Runnable speakEndCallback) {
        if (!this.disableVoiceSynthesizer.get()) {
            this.submitExecutorTask(text, parameters, speakEndCallback, false);
        } else {
            if (speakEndCallback != null) {
                speakEndCallback.run();
            }
        }
    }

    public void stopCurrentSpeakAndClearQueue() {
        if (this.currentSynthesizer != null) {
            this.queuedTasks.forEach(SpeakTask::disable);
            this.currentSynthesizer.stopCurrentSpeak();
        }
    }

    private String cleanTextBeforeSpeak(String text, final VoiceSynthesizerParameterI parameters) {
        ObservableList<PronunciationExceptionI> exception = parameters.getPronunciationExceptions();
        String original = text;
        for (PronunciationExceptionI exc : exception) {
            if (!StringUtils.isEmpty(exc.originalTextProperty().get()) && !StringUtils.isEmpty(exc.replaceTextProperty().get())) {
                text = text.replaceAll("\\b" + Pattern.quote(exc.originalTextProperty().get()) + "\\b",
                        Matcher.quoteReplacement(exc.replaceTextProperty().get()));
                this.LOGGER.debug("Replace {} with {}", exc.originalTextProperty().get(), exc.replaceTextProperty().get());
            }
        }
        this.LOGGER.debug("Original text : \"{}\"\nNew text \"{}\"", original, text);
        return text;
    }

    /**
     * @return if the voice synthesizer is disabled (all call to speak(...) method will be ignored)
     */
    public ReadOnlyBooleanProperty disableVoiceSynthesizerProperty() {
        return this.disableVoiceSynthesizer;
    }

    /**
     * @return a list of all available voice and synthesizer (list all voices for all synthesizer)
     */
    public ObservableList<VoiceAndSynthesizerInfoI> getAllVoice() {
        return this.allVoice;
    }

    /**
     * Switch the {@link #disableVoiceSynthesizerProperty()} value
     */
    public void switchDisableVoiceSynthesizer() {
        FXThreadUtils.runOnFXThread(() -> this.disableVoiceSynthesizer.set(!this.disableVoiceSynthesizer.get()));
    }


    private Future<?> submitExecutorTask(final String text, final VoiceSynthesizerParameterI parameters, final Runnable speakEndCallback, final boolean spell) {
        //Create task
        SpeakTask speakTask = new SpeakTask(queuedTasks) {
            @Override
            protected void executeSpeakAction() {
                //Get selected voice synthesizer or system default
                currentSynthesizer = parameters.selectedVoiceSynthesizerProperty().get();
                if (currentSynthesizer == null) {
                    currentSynthesizer = checkInitialize(systemDefaultSynthesizers.get(SystemType.current()));
                }
                //Set parameters
                currentSynthesizer.setVolume(parameters.volumeProperty().get());
                currentSynthesizer.setRate(parameters.rateProperty().get());
                currentSynthesizer.setPitch(parameters.pitchProperty().get());
                final VoiceInfoI voiceInfo = checkVoiceInfo(currentSynthesizer, parameters.getVoiceParameter().selectedVoiceInfoProperty().get());
                if (voiceInfo != null) {
                    currentSynthesizer.setVoice(voiceInfo);
                } else {
                    LOGGER.warn("Could not get valid voice information from selected : {}, voices might not be initialized ?", parameters.getVoiceParameter().selectedVoiceInfoProperty().get());
                }
                //Speak with selected synthesizer
                if (StringUtils.isNotBlank(text)) {
                    if (spell) {
                        for (int i = 0; i < text.length(); i++) {
                            char[] charToSpeakArray = {text.charAt(i)};
                            String charToSpeakString = new String(charToSpeakArray);
                            currentSynthesizer.speak(charToSpeakString);
                        }
                    } else {
                        currentSynthesizer.speak(cleanTextBeforeSpeak(text, parameters));
                    }
                }
                //When speak ends, callback if needed
                if (speakEndCallback != null) {
                    speakEndCallback.run();
                }
            }
        };
        return this.voiceSpeakExecutor.submit(speakTask);
    }

    /**
     * Try to find the wanted synthesizer by its ID, if there is not synthesizer with the given id, will return the current default synthesizer for system.
     *
     * @return the voice synthesizer by its id, or the default for the current system
     */
    public VoiceSynthesizerI getVoiceSynthesizerOrDefault(final String id) {
        VoiceSynthesizerI found = this.getVoiceSynthesizer(id);
        return found != null ? found : this.checkInitialize(this.systemDefaultSynthesizers.get(SystemType.current()));
    }

    /**
     * To get a voice synthesizer by its id
     *
     * @param id the synthesizer id
     * @return the voice synthesizer, or null if not found
     */
    public VoiceSynthesizerI getVoiceSynthesizer(final String id) {
        for (VoiceSynthesizerI voiceSynthesizerI : this.voiceSynthesizers) {
            if (voiceSynthesizerI.getId().equals(id)) {
                return this.checkInitialize(voiceSynthesizerI);
            }
        }
        this.LOGGER.info("Didn't find any voice synthesizer for the id {}", id);
        return null;
    }

    /**
     * @return the current system default voice synthesizer.<br>
     * Can return null if there is no voice synthesizer for the current system.
     */
    public VoiceSynthesizerI getSystemDefault() {
        return this.checkInitialize(this.systemDefaultSynthesizers.get(SystemType.current()));
    }

    /**
     * Select a voice into selected voice for the given previous selected voice.<br>
     * If the correct voice (= by id) is not found, will select the closest voice returned by synthesizer.
     *
     * @param selectedVoice the selected voice to change
     * @param synthesizer   the synthesizer that will be use will the given voice.
     */
    public void selectCorrectVoice(final VoiceParameterI selectedVoice, final VoiceSynthesizerI synthesizer) {
        this.checkInitialize(synthesizer);
        //First, try to select by id
        for (VoiceInfoI voiceInfo : synthesizer.getVoices()) {
            if (voiceInfo.getId().equals(selectedVoice.voiceIdProperty().get())) {
                selectedVoice.selectedVoiceInfoProperty().set(voiceInfo);
                return;
            }
        }
        //Take the closest if the correct voice is not found
        selectedVoice.selectedVoiceInfoProperty().set(synthesizer.getClosestVoice(selectedVoice));
    }

    private VoiceInfoI checkVoiceInfo(final VoiceSynthesizerI synthesizer, final VoiceInfoI voiceInfo) {
        this.checkInitialize(synthesizer);
        if (voiceInfo == null || !synthesizer.getVoices().contains(voiceInfo)) {
            this.LOGGER.info("Incorrect voice info, will select the default voice (voice was {})", voiceInfo);
            return synthesizer.getDefaultVoice(Locale.forLanguageTag(UserConfigurationController.INSTANCE.userLanguageProperty().get()));
        }
        return voiceInfo;
    }

    /**
     * Initialize the given synthesizer if it is not initialized
     *
     * @param synthesizer the synthesizer to initialize
     * @return the given synthesizer for better use in code
     */
    private VoiceSynthesizerI checkInitialize(final VoiceSynthesizerI synthesizer) {
        if (synthesizer != null && !synthesizer.isInitialized()) {
            this.LOGGER.info("Voice synthesizer {} is not initialized, will try to initialize it", synthesizer.getId());
            try {
                synthesizer.initialize();
                //Add voices // TODO : if voices are removed ?
                List<VoiceInfoI> voices = synthesizer.getVoices();
                for (VoiceInfoI voiceInfoI : voices) {
                    this.allVoice
                            .add(new VoiceSynthesizerInfoImpl(voiceInfoI.getId(), synthesizer.getId(), voiceInfoI.getName(), synthesizer.getName()));
                }
            } catch (Exception e) {
                this.LOGGER.warn("Couldn't initialize synthesizer {}", synthesizer.getId(), e);
            }
        }
        return synthesizer;
    }
    //========================================================================

    /**
     * @return a list that contains all available voice synthesizer
     */
    // Class part : "Initializing"
    //========================================================================
    public ObservableList<VoiceSynthesizerI> getVoiceSynthesizers() {
        return this.voiceSynthesizers;
    }

    /**
     * To register a voice synthesizer (will add it to {@link #getVoiceSynthesizers()} and initialize it)
     *
     * @param synthesizer the synthesizer to add
     */
    public void registrerVoiceSynthesizer(final VoiceSynthesizerI synthesizer) {
        if (synthesizer.getCompatibleSystems().contains(SystemType.current())) {
            this.voiceSynthesizers.add(synthesizer);
            this.LOGGER.info("Voice synthesizer added {}, will now try to initialize it", synthesizer.getId());
            this.checkInitialize(synthesizer);
        } else {
            this.LOGGER.info("Voice synthesizer {} is not compatible with the current system {}, it will not be added in available synthesizer",
                    synthesizer.getId(), SystemType.current());
        }
    }

    public void removeVoiceSynthesizer(final VoiceSynthesizerI synthesizer) {
        //Dispose
        this.disposeVoiceSynthesizer(synthesizer);
        //Remove from list
        this.voiceSynthesizers.remove(synthesizer);
    }

    /**
     * To define a synthesizer as the default synthesizer for a system
     *
     * @param system      the system type
     * @param synthesizer the default synthesizer for this system
     */
    private void setDefaultVoiceSynthesizer(final SystemType system, final VoiceSynthesizerI synthesizer) {
        if (this.voiceSynthesizers.contains(synthesizer)) {
            this.systemDefaultSynthesizers.put(system, synthesizer);
            this.LOGGER.info("Default voice synthesizer for {} set : {}", system, synthesizer.getId());
        } else {
            this.LOGGER.warn("Didn't set the default system voice synthesizer {} for system {} because it's not in the synthesizer list",
                    synthesizer.getId(), system);
        }
    }

    @Override
    public void lcStart() {
        SAPIVoiceSynthesizer synthesizer = new SAPIVoiceSynthesizer();
        registrerVoiceSynthesizer(synthesizer);
        setDefaultVoiceSynthesizer(SystemType.WINDOWS, synthesizer);
        SayCommandVoiceSynthesizer sayCommandVoiceSynthesizer = new SayCommandVoiceSynthesizer();
        registrerVoiceSynthesizer(sayCommandVoiceSynthesizer);
        setDefaultVoiceSynthesizer(SystemType.MAC, sayCommandVoiceSynthesizer);
    }

    @Override
    public void lcExit() {
        this.voiceSpeakExecutor.shutdownNow();
        this.LOGGER.info("Speak executor disposed");
        //Release every config
        for (VoiceSynthesizerI synthesizer : this.voiceSynthesizers) {
            this.disposeVoiceSynthesizer(synthesizer);
        }

    }

    private void disposeVoiceSynthesizer(final VoiceSynthesizerI synthesizer) {
        if (synthesizer.isInitialized()) {
            try {
                this.LOGGER.info("Will try to dispose {}", synthesizer.getId());
                synthesizer.dispose();
            } catch (Exception e) {
                this.LOGGER.warn("Couldn't dispose {}", synthesizer.getId(), e);
            }
        }
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        //When use mode stop, enable back the voice
        if (this.disableVoiceSynthesizer.get()) {
            this.disableVoiceSynthesizer.set(false);
        }
        this.stopCurrentSpeakAndClearQueue();
        this.currentSynthesizer = null;

    }
    //========================================================================

}
