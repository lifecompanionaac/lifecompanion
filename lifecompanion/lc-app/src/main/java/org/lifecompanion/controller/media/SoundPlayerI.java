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

import javafx.beans.property.ReadOnlyBooleanProperty;

import java.io.File;

/**
 * Represent a class that will be able to play sound from media file.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface SoundPlayerI {

    /**
     * Will play a sound in a synchronized manner, this mean that the method shouldn't return before the sound is played
     *
     * @param filePath the sound file to play
     */
	void playSoundSync(File filePath, boolean maxGain);

    /**
     * Will play a sound in a async manner, this method will return immediatly once the sound is loaded.
     *
     * @param filePath the sound file to play
     */
	void playSoundAsync(File filePath, boolean maxGain);

    /**
     * @return a property that indicate if the sound player is disabled
     */
	ReadOnlyBooleanProperty disableSoundPlayerProperty();

    /**
     * To switch the disabled state of the player.<br>
     * If the player becomes disabled, the current playing sounds are stopped.
     */
	void switchDisableSoundPlayer();

    /**
     * Stop all currently playing sounds.
     */
	void stopEveryPlayer();
}
