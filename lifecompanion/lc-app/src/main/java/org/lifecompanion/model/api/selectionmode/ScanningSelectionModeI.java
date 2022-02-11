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

package org.lifecompanion.model.api.selectionmode;

import javafx.beans.property.ReadOnlyBooleanProperty;

import java.util.function.Supplier;

/**
 * Selection mode where mouse position doesn't count, only global mouse event are handled and component of the configuration are "scanned".
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface ScanningSelectionModeI extends SelectionModeI {
    // Class part : "Event handling"
    //========================================================================

    /**
     * When mouse is pressed by user
     *
     * @param skipActionFire if this event should fire any action
     */
    void selectionPress(boolean skipActionFire);

    /**
     * When mouse is released by user
     *
     * @param skipActionFire if this event should fire any action
     */
    void selectionRelease(boolean skipActionFire);

    /**
     * When next scan event is pressed by user
     */
    void nextScanSelectionPress();

    /**
     * When next scan event is released by user
     */
    void nextScanSelectionRelease();
    //========================================================================

    // Class part : "Scanning"
    //========================================================================

    /**
     * Generate the scanning parts for the current scanned grid (if there is one)
     */
    void generateScanningPartForCurrentGrid();

    /**
     * Restart the scanning in the current grid
     */
    void restart();

    /**
     * Play the current scanning (if the scanning is not started, or if it's paused)
     */
    void play();

    /**
     * Pause the scanning
     */
    void pause();

    /**
     * Completely stop the scanning
     */
    void stop();

    /**
     * Just pause scanning until a next selection is done (in {@link #selectionPress(boolean)} or {@link #selectionRelease(boolean)} )
     *
     * @param nextSelectionListener the listener that will wait for the next selection listener, and that will return true if the scanning should be restarted, or false if it should just be played
     */
    void pauseUntilNextSelection(Supplier<Boolean> nextSelectionListener);

    /**
     * @return a property that is true if this scanning mode is playing
     */
    ReadOnlyBooleanProperty playingProperty();
    //========================================================================

}
