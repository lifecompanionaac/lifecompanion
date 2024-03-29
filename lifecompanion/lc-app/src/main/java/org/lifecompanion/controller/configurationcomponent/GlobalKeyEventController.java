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

package org.lifecompanion.controller.configurationcomponent;

import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.virtualkeyboard.WinAutoHotKeyKeyboardReceiverController;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * To manage abstraction on key event in LifeCompanion.<br>
 * This can represent events related to JavaFX input stack (directly fired on stage) but also on external device (on Windows only, see {@link WinAutoHotKeyKeyboardReceiverController}).<br>
 * This class was created to keep listeners on a same semantic level no matter they input source was.
 */
public enum GlobalKeyEventController implements ModeListenerI {
    INSTANCE;

    /**
     * All the listener for key events
     */
    private final Set<Consumer<LCKeyEvent>> keyEventListener;

    private final Set<KeyCode> blockedKeyCodes;

    private boolean listenToAllKeysActivated;

    GlobalKeyEventController() {
        this.keyEventListener = new HashSet<>();
        this.blockedKeyCodes = new HashSet<>();
    }

    // CALLED BY EVENT GENERATOR
    //========================================================================
    public boolean javaFxEventFired(final KeyEvent keyEvent) {
        return dispatchEvent(LCKeyEvent.from(keyEvent));
    }

    public boolean genericLcEventFired(final LCKeyEvent keyEvent) {
        return dispatchEvent(keyEvent);
    }

    private boolean dispatchEvent(LCKeyEvent keyEvent) {
        if (AppModeController.INSTANCE.isUseMode()) {
            for (Consumer<LCKeyEvent> keyListener : this.keyEventListener) {
                keyListener.accept(keyEvent);
            }
        }
        return blockedKeyCodes.contains(keyEvent.getKeyCode());
    }
    //========================================================================


    /**
     * To add a listener on every key event generated (for current mode only, listener is removed on mode stop)<br>
     * Note that listener are call once per instance (stored in a set)<br>
     * Also note that sometimes, listener could not be fired on key event producing texts.
     *
     * @param listener the listener to add
     */
    public void addKeyEventListenerForCurrentUseMode(final Consumer<LCKeyEvent> listener) {
        this.keyEventListener.add(listener);
    }

    public void removeKeyEventListenerForCurrentUseMode(final Consumer<LCKeyEvent> listener) {
        this.keyEventListener.remove(listener);
    }

    /**
     * Add a keycode to block for the current use mode.<br>
     * If this keycode is detected as a global event (no matter the associated event : released, pressed, etc.),
     * LifeCompanion will try to block it from being dispatched to sub items (e.g. LifeCompanion itself or sub nodes/components).
     * The blocked keycode will call listener added to {@link #addKeyEventListenerForCurrentUseMode(Consumer)} before being blocked
     *
     * @param keyCode the key code to block
     */
    public void addKeyCodeToBlockForCurrentUseMode(KeyCode keyCode) {
        this.blockedKeyCodes.add(keyCode);
    }

    public void removeKeyCodeToBlockForCurrentUseMode(KeyCode keyCode) {
        this.blockedKeyCodes.remove(keyCode);
    }

    public Set<KeyCode> getBlockedKeyCodes() {
        return blockedKeyCodes;
    }

    public void activateListenToAllKeys() {
        this.listenToAllKeysActivated = true;
    }

    public boolean isListenToAllKeysActivated() {
        return listenToAllKeysActivated;
    }

    // START/STOP
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.keyEventListener.clear();
        this.blockedKeyCodes.clear();
        this.listenToAllKeysActivated = false;
    }
    //========================================================================

    // MODEL
    //========================================================================
    public static class LCKeyEvent {
        private final KeyCode keyCode;
        private final LCKeyEventType eventType;

        public LCKeyEvent(KeyCode keyCode, LCKeyEventType eventType) {
            this.keyCode = keyCode;
            this.eventType = eventType;
        }

        public KeyCode getKeyCode() {
            return keyCode;
        }

        public LCKeyEventType getEventType() {
            return eventType;
        }

        public static LCKeyEvent from(final KeyEvent keyEvent) {
            return new LCKeyEvent(keyEvent.getCode(), LCKeyEventType.convert(keyEvent.getEventType()));
        }

        @Override
        public String toString() {
            return "LCKeyEvent{" +
                    "keyCode=" + keyCode +
                    ", eventType=" + eventType +
                    '}';
        }
    }

    public enum LCKeyEventType {
        PRESSED, RELEASED;

        static LCKeyEventType convert(EventType<KeyEvent> fxEventType) {
            if (fxEventType == KeyEvent.KEY_PRESSED) return PRESSED;
            else return RELEASED;
        }
    }
    //========================================================================
}
