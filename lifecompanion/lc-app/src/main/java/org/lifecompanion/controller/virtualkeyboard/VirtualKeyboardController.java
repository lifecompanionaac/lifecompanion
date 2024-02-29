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
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.textprediction.WordPredictionI;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Virtual keyboard manager.<br>
 * This is to simulate keyboard typing and key press.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum VirtualKeyboardController implements WritingDeviceI, ModeListenerI {
    INSTANCE;
    private final static Logger LOGGER = LoggerFactory.getLogger(VirtualKeyboardController.class);

    /**
     * When a key is held, the time between each press repeat
     */
    public static final int DELAY_REPEAT_KEY_HOLD = 35;

    /**
     * When a key is held, the action is first executed once, and then repeated. This is the time between the first execution, and the repeat start.
     */
    public static final int DELAY_BEFORE_REPEAT_KEY_START = 750;

    /**
     * Delay the key stays pressed before release
     */
    public static final int KEY_PRESS_DELAY = 20;

    /**
     * Delay once every key event is processed before releasing the thread
     */
    public static final int AFTER_TYPE_DELAY = 30;

    private VirtualKeyboardI virtualKeyboardImplementation;

    private final Set<KeyCode> currentlyPressedKeys;

    VirtualKeyboardController() {
        currentlyPressedKeys = Collections.synchronizedSet(new HashSet<>());
    }

    // LINK WITH VKB
    //========================================================================
    @Override
    public void insert(WritingEventSource src, WriterEntryI entry, WriteSpecialChar specialChar) {
        if (specialChar == null && src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.sendText(entry.entryTextProperty().get());
        }
    }

    @Override
    public void insertText(WritingEventSource src, String text) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.sendText(text);
        }
    }

    @Override
    public void insertWordPrediction(WritingEventSource src, String toInsert, WordPredictionI originalPrediction) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.insertText(src, toInsert);
        }
    }

    @Override
    public void insertCharPrediction(WritingEventSource src, String toInsert) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.insertText(src, toInsert);
        }
    }


    @Override
    public void removeLastChar(WritingEventSource src) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.keyPressThenRelease(KeyCode.BACK_SPACE);
        }
    }

    @Override
    public void removeLastChars(WritingEventSource src, int n) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            for (int i = 0; i < n; i++) {
                this.removeLastChar(src);
            }
        }
    }

    @Override
    public void removeNextChar(WritingEventSource src) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.keyPressThenRelease(KeyCode.DELETE);
        }
    }

    @Override
    public void removeNextChars(WritingEventSource src, int n) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            for (int i = 0; i < n; i++) {
                this.removeNextChar(src);
            }
        }
    }

    @Override
    public void moveCaretForward(WritingEventSource src) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.keyPressThenRelease(KeyCode.RIGHT);
        }
    }

    @Override
    public void moveCaretBackward(WritingEventSource src) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.keyPressThenRelease(KeyCode.LEFT);
        }
    }

    @Override
    public void moveCaretToStart(WritingEventSource src) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.keyPressThenRelease(KeyCode.HOME);
        }
    }

    @Override
    public void moveCaretToEnd(WritingEventSource src) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.keyPressThenRelease(KeyCode.END);
        }
    }

    @Override
    public void moveCaretUp(WritingEventSource src) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.keyPressThenRelease(KeyCode.UP);
        }
    }

    @Override
    public void moveCaretDown(WritingEventSource src) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.keyPressThenRelease(KeyCode.DOWN);
        }
    }


    @Override
    public void newLine(WritingEventSource src) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.keyPressThenRelease(KeyCode.ENTER);
        }
    }

    @Override
    public void space(WritingEventSource src) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.keyPressThenRelease(KeyCode.SPACE);
        }
    }

    @Override
    public void tab(WritingEventSource src) {
        if (src != WritingEventSource.EXTERNAL_USER_INPUT) {
            this.keyPressThenRelease(KeyCode.TAB);
        }
    }

    @Override
    public void restoreState() {

    }

    @Override
    public boolean isExternalWritingDevice() {
        return true;
    }

    private final Object vkLock = new Object();
    //========================================================================

    // NOOP
    //========================================================================
    @Override
    public void moveCaretToPosition(WritingEventSource src, WriterDisplayerI displayer, double xInEditor, double yInEditor) {
        // NO-OP in VKB
    }

    @Override
    public void removeLastEntry(WritingEventSource src) {

    }

    @Override
    public void removeLastWord(WritingEventSource src) {

    }

    @Override
    public void removeAll(WritingEventSource src) {

    }
    //========================================================================

    // IMPLEMENTATIONS
    //========================================================================
    private void sendText(String text) {
        if (WritingStateController.INSTANCE.capitalizeNextProperty().get()) {
            text = StringUtils.capitalize(text);
            WritingStateController.INSTANCE.switchCapitalizeNext(WritingEventSource.SYSTEM);
        }
        if (WritingStateController.INSTANCE.upperCaseProperty().get()) {
            text = StringUtils.toUpperCase(text);
        }
        String textF = text;
        synchronized (vkLock) {
            try {
                virtualKeyboardImplementation.sendText(textF);
            } catch (Exception e) {
                LOGGER.error("sendText({}) failed on {}", textF, virtualKeyboardImplementation.getClass().getSimpleName(), e);
            }
        }
    }

    public void keyPressThenRelease(final KeyCode... keyCodes) {
        synchronized (vkLock) {
            if (keyCodes != null) {
                if (keyCodes.length == 1) {
                    try {
                        virtualKeyboardImplementation.keyTyped(keyCodes[0]);
                    } catch (Exception e) {
                        LOGGER.error("keyTyped({}) failed on {}", keyCodes[0], virtualKeyboardImplementation.getClass().getSimpleName(), e);
                    }
                } else {
                    try {
                        virtualKeyboardImplementation.multiKeyPressThenRelease(keyCodes);
                    } catch (Exception e) {
                        LOGGER.error("multiKeyPressThenRelease({}) failed on {}", keyCodes, virtualKeyboardImplementation.getClass().getSimpleName(), e);
                    }
                }
            }
        }
    }

    public boolean toggleKeyPressRelease(final KeyCode keyCode) {
        if (keyCode != null) {
            if (this.currentlyPressedKeys.contains(keyCode)) {
                keyRelease(keyCode);
                return true;
            } else {
                keyPress(keyCode);
                return false;
            }
        }
        return false;
    }

    public void keyPress(final KeyCode keyCode) {
        try {
            LOGGER.info("keyPress({})",keyCode);
            currentlyPressedKeys.add(keyCode);
            virtualKeyboardImplementation.keyDown(keyCode);
        } catch (Exception e) {
            LOGGER.error("keyPress({}) failed on {}", keyCode, virtualKeyboardImplementation.getClass().getSimpleName(), e);
        }
    }

    public void keyRelease(final KeyCode keyCode) {
        try {
            LOGGER.info("keyRelease({})",keyCode);
            currentlyPressedKeys.remove(keyCode);
            virtualKeyboardImplementation.keyUp(keyCode);
        } catch (Exception e) {
            LOGGER.error("keyRelease({}) failed on {}", keyCode, virtualKeyboardImplementation.getClass().getSimpleName(), e);
        }
    }
    //========================================================================

    // MODE
    //========================================================================

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        virtualKeyboardImplementation = SystemType.current() == SystemType.WINDOWS ? new WinAutoHotKeyVirtualKeyboard() : new RobotVirtualKeyboard();
        virtualKeyboardImplementation.modeStart(configuration);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        if (virtualKeyboardImplementation != null) {
            LOGGER.info("Virtual keyboard will release keys on stop : {}", currentlyPressedKeys);
            currentlyPressedKeys.forEach(this::keyRelease);
            currentlyPressedKeys.clear();

            virtualKeyboardImplementation.modeStop(configuration);
            virtualKeyboardImplementation = null;
        }
    }
    //========================================================================
}
