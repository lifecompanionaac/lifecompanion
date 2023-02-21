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

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.voicesynthesizer.VoiceInfoI;
import org.lifecompanion.model.impl.exception.UnavailableFeatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class use to create text to speech with MacOS "say" command.
 *
 * @author Paul THEBAUD
 */
public class SayCommandVoiceSynthesizer extends AbstractVoiceSynthesizer {
    private final Logger LOGGER = LoggerFactory.getLogger(SayCommandVoiceSynthesizer.class);

    /**
     * Selected voice id.
     */
    private String voice;

    /**
     * The current say process.
     */
    private Process sayRunningProcess;

    /**
     * Create a process builder for the "say" command with the given arguments.
     *
     * @param voice   The voice to choose.
     * @param message The optional message to say.
     * @return The created process builder.
     */
    private ProcessBuilder prepareSayCommand(final String voice, final String message) {
        final List<String> commandAndArgs = new ArrayList<>();

        commandAndArgs.add("say");
        commandAndArgs.add("-v");
        commandAndArgs.add(voice);

        if (message != null) {
            commandAndArgs.add(message);
        }

        return new ProcessBuilder(commandAndArgs);
    }

    // IMPLEMENTATION
    //========================================================================
    @Override
    public String getName() {
        return Translation.getText("macos.say.voice.name");
    }

    @Override
    public String getDescription() {
        return Translation.getText("macos.say.voice.description");
    }

    @Override
    public void initialize() throws Exception {
        this.LOGGER.info("initializing voices");

        final ProcessBuilder processBuilder = this.prepareSayCommand("?", null);
        final Process process = processBuilder.start();
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            final Pattern nameRegex = Pattern.compile("^[a-z-]+", Pattern.CASE_INSENSITIVE);
            final Pattern languageRegex = Pattern.compile("[a-z]{2}_[A-Z]{2}");
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Matcher nameMatcher, languageMatcher;
                if ((nameMatcher = nameRegex.matcher(line)).find() && (languageMatcher = languageRegex.matcher(line)).find()) {
                    final String name = nameMatcher.group();
                    this.voices.add(new VoiceInfo(name, name, Locale.forLanguageTag(languageMatcher.group().replace('_', '-')), null));
                }
            }
        }
        if (process.waitFor() == 0) {
            this.LOGGER.info("successfully initialized voices");
        }
    }

    @Override
    public void dispose() {
        this.stopCurrentSpeak();
    }

    @Override
    public void speak(final String text, boolean trimSilences) {
        final ProcessBuilder processBuilder = this.prepareSayCommand(this.voice, text);

        try {
            this.sayRunningProcess = processBuilder.start();
            this.sayRunningProcess.waitFor();
        } catch (IOException e) {
            LOGGER.error("Couldn't speak with say command voice", e);
        } catch (InterruptedException e) {
            LOGGER.debug("Interrupted say command voice", e);
        }

        this.sayRunningProcess = null;
    }

    @Override
    public void speakSsml(String ssml, boolean trimSilences) throws UnavailableFeatureException {
        throw new UnavailableFeatureException();
    }

    @Override
    public void stopCurrentSpeak() {
        if (this.sayRunningProcess != null) {
            this.sayRunningProcess.destroy();
            this.sayRunningProcess = null;
        }
    }

    @Override
    public void setVoice(final VoiceInfoI voiceInfo) {
        this.voice = voiceInfo.getId();
    }

    @Override
    public void setVolume(final int volumeP) {
        // Can't set the volume on "say" command.
        this.LOGGER.info("Volume value ignored for {}", this.getId());
    }

    @Override
    public void setRate(final int rateP) {
        // Can't set the rate on "say" command.
        this.LOGGER.info("Rate value ignored for {}", this.getId());
    }

    @Override
    public void setPitch(final int pitchP) {
        // Can't set the pitch on "say" command.
        this.LOGGER.info("Pitch value ignored for {}", this.getId());
    }

    @Override
    public String getId() {
        return "say-command-macos-synthesizer";
    }

    @Override
    public List<SystemType> getCompatibleSystems() {
        return Collections.singletonList(SystemType.MAC);
    }

    @Override
    public boolean isInitialized() {
        return this.voices.size() > 0;
    }

    @Override
    public List<VoiceInfoI> getVoices() {
        return this.voices;
    }
    //========================================================================
}
