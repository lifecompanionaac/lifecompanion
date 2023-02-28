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

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.voicesynthesizer.VoiceInfoI;
import org.lifecompanion.model.impl.exception.UnavailableFeatureException;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.SoundUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * Class use to create text to speech with Ubuntu "pico2wave" command.
 *
 * @author Paul THEBAUD
 */
public class PicoTTSVoiceSynthesizer extends AbstractVoiceSynthesizer {
    private final Logger LOGGER = LoggerFactory.getLogger(PicoTTSVoiceSynthesizer.class);
    private String voice;
    private Process picoTTSRunningProcess;
    private MediaPlayer currentMediaPlayer;

    private double volume = 100;

    // IMPLEMENTATION
    //========================================================================
    @Override
    public String getName() {
        return Translation.getText("picotts.voice.synthesizer.name");
    }

    @Override
    public String getDescription() {
        return Translation.getText("picotts.voice.synthesizer.description");
    }

    @Override
    public void initialize() throws Exception {
        this.voices.add(new VoiceInfo("fr-FR", "French (fr-FR)", Locale.FRANCE, null));
        this.voices.add(new VoiceInfo("de-DE", "German (de-DE)", Locale.GERMANY, null));
        this.voices.add(new VoiceInfo("en-US", "English, US (en-US)", Locale.US, null));
        this.voices.add(new VoiceInfo("en-GB", " English, GB (en-GB)", Locale.UK, null));
        this.voices.add(new VoiceInfo("es-ES", "Spanish (es-ES)", Locale.forLanguageTag("es-ES"), null));
        this.voices.add(new VoiceInfo("it-IT", "Italian (it-IT)", Locale.ITALY, null));
    }

    @Override
    public void dispose() {
        this.stopCurrentSpeak();
    }

    @Override
    public void speak(final String text, boolean trimSilences) {
        File tempWavFile = IOUtils.getTempFile("picotts", ".wav");
        try {
            this.picoTTSRunningProcess = new ProcessBuilder()
                    .redirectOutput(IOUtils.getTempFile("picotts-stdout", ".txt"))
                    .redirectError(IOUtils.getTempFile("picotts-stderr", ".txt"))
                    .command("pico2wave", "-l", this.voice, "-w", tempWavFile.getAbsolutePath(), text)
                    .start();
            int i = this.picoTTSRunningProcess.waitFor();
            if (i == 0) {
                playWavFileSync(trimSilences ? SoundUtils.trimSilences(tempWavFile, 0.01) : tempWavFile);
            } else {
                throw new IOException("pico2wave command didn't finish correctly (see picotts-stderr in /tmp/LifeCompanion/logs for details");
            }
        } catch (Exception e) {
            LOGGER.error("Could not run speak command with PicoTTS", e);
        } finally {
            this.picoTTSRunningProcess = null;
        }
    }

    private void playWavFileSync(File wavFile) throws Exception {
        this.currentMediaPlayer = new MediaPlayer(new Media(wavFile.toURI().toURL().toString()));
        this.currentMediaPlayer.setVolume(this.volume / 100.0);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        //Unlock on end/error
        final Runnable releaseCDL = countDownLatch::countDown;
        currentMediaPlayer.setOnEndOfMedia(releaseCDL);
        currentMediaPlayer.setOnStopped(releaseCDL);
        currentMediaPlayer.setOnHalted(releaseCDL);
        currentMediaPlayer.setOnError(() -> {
            this.LOGGER.error("Error for sound", currentMediaPlayer.getError());
            releaseCDL.run();
        });
        //Start and lock
        currentMediaPlayer.play();
        try {
            countDownLatch.await();
        } catch (Exception e) {
            this.LOGGER.error("Can't wait for player to finish", e);
        } finally {
            currentMediaPlayer = null;
        }
    }

    @Override
    public void speakSsml(String ssml, boolean trimSilences) throws UnavailableFeatureException {
        throw new UnavailableFeatureException();
    }

    @Override
    public void stopCurrentSpeak() {
        if (this.picoTTSRunningProcess != null) {
            this.picoTTSRunningProcess.destroy();
            this.picoTTSRunningProcess = null;
        }
        if (this.currentMediaPlayer != null) {
            MediaPlayer oldMediaPlayer = this.currentMediaPlayer;
            oldMediaPlayer.stop();
            oldMediaPlayer.dispose();
            this.currentMediaPlayer = null;
        }
    }

    @Override
    public void setVoice(final VoiceInfoI voiceInfo) {
        this.voice = voiceInfo.getId();
    }

    @Override
    public void setVolume(final int volumeP) {
        this.volume = volumeP;
    }

    @Override
    public void setRate(final int rateP) {
        // Can't set the rate on "pico2wave" command.
        this.LOGGER.info("Rate value ignored for {}", this.getId());
    }

    @Override
    public void setPitch(final int pitchP) {
        // Can't set the pitch on "pico2wave" command.
        this.LOGGER.info("Pitch value ignored for {}", this.getId());
    }

    @Override
    public String getId() {
        return "picotts-unix-synthesizer";
    }

    @Override
    public List<SystemType> getCompatibleSystems() {
        return Collections.singletonList(SystemType.UNIX);
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
