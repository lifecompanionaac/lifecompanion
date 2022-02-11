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
package org.lifecompanion.controller.media;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.util.LCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;


/**
 * Represent a class that will be able to play sound from media file.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum SoundPlayerController implements ModeListenerI {
    INSTANCE;

    private final Logger LOGGER = LoggerFactory.getLogger(SoundPlayerController.class);

    /**
     * Specific sound player that can be use on embedded devices
     */
    private SoundPlayerI specificSoundPlayer;

    /**
     * To disable sound player
     */
    private final BooleanProperty disableSoundPlayer;

    /**
     * All current media player
     */
    private final CopyOnWriteArrayList<MediaPlayer> currentMediaPlayer;

    SoundPlayerController() {
        this.disableSoundPlayer = new SimpleBooleanProperty(false);
        this.currentMediaPlayer = new CopyOnWriteArrayList<>();
    }

    // Class part : "Public API"
    //========================================================================
    public void playSoundSync(final File filePath, boolean maxGain) {
        if (!this.disableSoundPlayer.get()) {
            if (this.specificSoundPlayer != null) {
                this.specificSoundPlayer.playSoundSync(filePath, maxGain);
            } else {
                this.playSoundImpl(filePath, true, maxGain);
            }
        }
    }

    public void playSoundAsync(final File filePath, boolean maxGain) {
        if (!this.disableSoundPlayer.get()) {
            if (this.specificSoundPlayer != null) {
                this.specificSoundPlayer.playSoundSync(filePath, maxGain);
            } else {
                this.playSoundImpl(filePath, false, maxGain);
            }
        }
    }

    public void setSpecificSoundPlayer(final SoundPlayerI specificSoundPlayer) {
        this.specificSoundPlayer = specificSoundPlayer;
    }

    public BooleanProperty disableSoundPlayerProperty() {
        return this.disableSoundPlayer;
    }

    public void switchDisableSoundPlayer() {
        LCUtils.runOnFXThread(() -> {
            this.disableSoundPlayer.set(!this.disableSoundPlayer.get());
            if (this.disableSoundPlayer.get()) {
                this.stopEveryPlayer();
            }
        });
    }
    //========================================================================

    // Class part : "Private"
    //========================================================================
    private void playSoundImpl(final File filePath, boolean sync, boolean maxGain) {
        final CountDownLatch countDownLatch = sync ? new CountDownLatch(1) : null;
        Media media = new Media(filePath.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        this.currentMediaPlayer.add(player);

        if (maxGain) {
            final AudioEqualizer audioEqualizer = player.getAudioEqualizer();
            audioEqualizer.getBands().forEach(band -> band.setGain(EqualizerBand.MAX_GAIN));
            audioEqualizer.setEnabled(true);
        }

        //Unlock on end/error
        final Runnable removeAndReleaseCDL = () -> removeAndReleaseCountDownLatch(countDownLatch, player);
        player.setOnEndOfMedia(removeAndReleaseCDL);
        player.setOnStopped(removeAndReleaseCDL);
        player.setOnHalted(removeAndReleaseCDL);
        player.setOnError(() -> {
            this.LOGGER.warn("Error for sound {}", filePath, player.getError());
            removeAndReleaseCountDownLatch(countDownLatch, player);
        });

        //Start and lock
        player.play();
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Exception e) {
                this.LOGGER.warn("Can't wait for player to finish", e);
            }
        }
    }

    private void removeAndReleaseCountDownLatch(CountDownLatch countDownLatch, MediaPlayer player) {
        this.currentMediaPlayer.remove(player);
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }
    //========================================================================

    // Class part : "Mode"
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        this.stopEveryPlayer();
    }

    public void stopEveryPlayer() {
        //Stop every players
        for (MediaPlayer mediaPlayer : this.currentMediaPlayer) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        this.currentMediaPlayer.clear();
    }
    //========================================================================

}
