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
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.SyncMediaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


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
    private final SyncMediaPlayer syncMediaPlayer;

    SoundPlayerController() {
        this.disableSoundPlayer = new SimpleBooleanProperty(false);
        this.syncMediaPlayer = new SyncMediaPlayer();

        // this.currentMediaPlayer = new CopyOnWriteArrayList<>();
    }

    // Class part : "Public API"
    //========================================================================
    public void playSoundSync(final File filePath, boolean maxGain) {
        if (!this.disableSoundPlayer.get()) {
            this.playSoundImpl(filePath, true, maxGain);
        }
    }

    public void playSoundAsync(final File filePath, boolean maxGain) {
        if (!this.disableSoundPlayer.get()) {
            this.playSoundImpl(filePath, false, maxGain);
        }
    }

    public BooleanProperty disableSoundPlayerProperty() {
        return this.disableSoundPlayer;
    }

    public void switchDisableSoundPlayer() {
        FXThreadUtils.runOnFXThread(() -> {
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
        try {
            syncMediaPlayer.play(filePath, mediaPlayer -> {
                if (maxGain) {
                    final AudioEqualizer audioEqualizer = mediaPlayer.getAudioEqualizer();
                    audioEqualizer.getBands().forEach(band -> band.setGain(EqualizerBand.MAX_GAIN));
                    audioEqualizer.setEnabled(true);
                }
            }, sync);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        //When use mode stop, enable back the voice
        if (this.disableSoundPlayer.get()) {
            this.disableSoundPlayer.set(false);
        }
        this.stopEveryPlayer();
    }

    public void stopEveryPlayer() {
        syncMediaPlayer.stopAllPlaying();
    }
    //========================================================================

}
