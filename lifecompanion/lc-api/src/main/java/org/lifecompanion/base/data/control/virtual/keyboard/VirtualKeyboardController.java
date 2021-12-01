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

package org.lifecompanion.base.data.control.virtual.keyboard;

import javafx.scene.input.KeyCode;
import org.lifecompanion.api.component.definition.*;
import org.lifecompanion.api.control.events.WritingEventSource;
import org.lifecompanion.api.mode.ModeListenerI;
import org.lifecompanion.api.prediction.WordPredictionI;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.base.data.control.virtual.keyboard.impl.RobotVirtualKeyboard;
import org.lifecompanion.base.data.control.virtual.keyboard.impl.WinAutoHotKeyVirtualKeyboard;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    //========================================================================

    // MODE
    //========================================================================

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        if (!AppController.INSTANCE.isOnEmbeddedDevice()) {
            virtualKeyboardImplementation = SystemType.current() == SystemType.WINDOWS ? new WinAutoHotKeyVirtualKeyboard() : new RobotVirtualKeyboard();
            virtualKeyboardImplementation.modeStart(configuration);
        }
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        if (virtualKeyboardImplementation != null) {
            virtualKeyboardImplementation.modeStop(configuration);
            virtualKeyboardImplementation = null;
        }
    }
    //========================================================================


    //========================================================================


    // BACKWARD COMP
    //========================================================================

    /**
     * @deprecated Used in calendar plugin, will be removed later
     */
    @Deprecated
    public void keyPressAndReleaseJavaFxKeyEvent(final KeyCode... keyCodes) {
        keyPressThenRelease(keyCodes);
    }
    //========================================================================


}
