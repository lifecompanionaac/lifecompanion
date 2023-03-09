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

package org.lifecompanion.controller.virtualkeyboard;

import javafx.scene.input.KeyCode;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;

import java.security.Key;

public interface VirtualKeyboardI extends ModeListenerI {

    /**
     * Should do its best to send text as key strokes (or any other way)
     *
     * @param rawText the raw text to be sent
     * @throws Exception if can't send the text
     */
    void sendText(String rawText) throws Exception;

    /**
     * Should do its best to send a single key stroke to simulate keyboard key typed
     *
     * @param keyCode key code of simulated key
     * @throws Exception if can't send the text
     */
    void keyTyped(final KeyCode keyCode) throws Exception;

    /**
     * Should do its best to send a key combination (ex CTRL + C)<br>
     * Keys should be all pressed in the order, then released in backward order.<nr></nr>
     * If a single key is sent, implementation can fallback to {@link #keyTyped(KeyCode)}
     *
     * @param keyCodes key combination
     * @throws Exception if can't send the text
     */
    void multiKeyPressThenRelease(final KeyCode... keyCodes) throws Exception;

    /**
     * Should try to set the given key down (ex Shift or Ctrl).<br>
     * The key should not be released by the implementation, it will be the caller responsibility to release keys with {@link #keyUp(KeyCode)} later.
     * @param keyCode the key to set down
     * @throws Exception if can't key down
     */
    void keyDown(final KeyCode keyCode) throws Exception;

    /**
     * Should try to set the given key up (ex Shift or Ctrl).<br>
     * This can be called after a {@link #keyDown(KeyCode)} but also without
     * @param keyCode the key to set up
     * @throws Exception if can't key up
     */
    void keyUp(final KeyCode keyCode) throws Exception;
}
