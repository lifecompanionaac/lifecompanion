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

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.util.RobotProvider;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RobotVirtualKeyboard implements VirtualKeyboardI {
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotVirtualKeyboard.class);


    /**
     * Delay between any key event typed before the original system clipboard is restored.<br>
     * (this is done because the virtual keyboard use the copy/paste shortcut to write content)
     */
    private static final int DELAY_BEFORE_RESTORING_CLIPBOARD = 1000;


    /**
     * AWT robot to simulate key events
     */
    private Robot robot;

    /**
     * The current restoring system clipboard Thread.<br>
     * The thread might be finished (if the restoring is true)
     */
    private RestoringClipboardThread currentRestoringClipboardThread;

    // PUBLIC API
    //========================================================================
    @Override
    public void sendText(String rawText) throws Exception {
        if (rawText != null && !rawText.isEmpty()) {
            if (StringUtils.safeLength(rawText) > 1) {
                sendTextUsingClipboard(rawText);
            } else {
                sendTextUsingKeyPresses(rawText);
            }
        }
    }

    @Override
    public void keyTyped(KeyCode keyCode) throws Exception {
        this.awtKeyPressThenRelease(this.convertJavaFXToAwt(keyCode));
    }

    @Override
    public void multiKeyPressThenRelease(KeyCode... keyCodes) throws Exception {
        this.awtKeyPressThenRelease(Arrays.stream(keyCodes).mapToInt(this::convertJavaFXToAwt).filter(i -> i >= 0).toArray());
    }
    //========================================================================

    // INIT/END
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.robot = RobotProvider.getInstance();
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        this.robot = null;
    }
    //========================================================================


    // SEND WITH CLIPBOARD
    //========================================================================
    private void sendTextUsingClipboard(String rawText) {
        String textWithoutSpaceAtTheEnd = rawText;
        // Trailing spaces are removed and sent as key stroke, because a lot of software trim pasted texts.
        int spaceCount = 0;
        while (textWithoutSpaceAtTheEnd.endsWith(" ") && textWithoutSpaceAtTheEnd.length() > 1) {
            textWithoutSpaceAtTheEnd = textWithoutSpaceAtTheEnd.substring(0, textWithoutSpaceAtTheEnd.length() - 1);
            spaceCount++;
        }
        final String textToCopyPaste = textWithoutSpaceAtTheEnd;
        final int spaceToAdd = spaceCount;
        LCUtils.runOnFXThread(() -> {
            //Set content in the clip board
            final Clipboard clipboard = Clipboard.getSystemClipboard();

            //Issue #100 : previous clipboard should be saved
            Map<DataFormat, Object> savedClipboard = saveClipboardContent(clipboard);

            //Put string to write
            final ClipboardContent content = new ClipboardContent();
            content.putString(textToCopyPaste);
            clipboard.setContent(content);

            //Simulate the Paste event with Ctrl+V
            this.robot.waitForIdle();
            this.awtKeyPressThenRelease(KeyEvent.VK_CONTROL, KeyEvent.VK_V);

            // Add the spaces (after previous events sent)
            if (spaceToAdd > 0) {
                this.robot.waitForIdle();
                for (int i = 0; i < spaceToAdd; i++) {
                    this.awtKeyPressThenRelease(KeyEvent.VK_SPACE);
                }
            }
            //Existing restoring Thread not finished : cancel it and get its clipboard to restore
            if (this.currentRestoringClipboardThread != null && !currentRestoringClipboardThread.restored) {
                this.currentRestoringClipboardThread.cancelled = true;
                this.currentRestoringClipboardThread = new RestoringClipboardThread(this.currentRestoringClipboardThread.originalContent);
                this.currentRestoringClipboardThread.start();
            }
            //No existing Thread, or already restored : create a new restoring Thread
            else if (!savedClipboard.isEmpty()) {
                this.currentRestoringClipboardThread = new RestoringClipboardThread(savedClipboard);
                this.currentRestoringClipboardThread.start();
            }
        });
    }


    /**
     * Thread to restore the original system clipboard after virtual keyboard use.
     * Related to issue #100
     */
    private static class RestoringClipboardThread extends Thread {
        private final Map<DataFormat, Object> originalContent;
        private volatile boolean cancelled;
        private volatile boolean restored;

        public RestoringClipboardThread(Map<DataFormat, Object> originalContent) {
            super("LCRestoringSystemClipboardThread");
            this.setDaemon(true);
            this.originalContent = originalContent;
        }

        @Override
        public void run() {
            LCUtils.safeSleep(DELAY_BEFORE_RESTORING_CLIPBOARD);
            if (!cancelled) {
                LCUtils.runOnFXThread(() -> {
                    if (!cancelled) {
                        final Clipboard clipboard = Clipboard.getSystemClipboard();
                        clipboard.setContent(originalContent);
                        restored = true;
                    }
                });
            }
        }
    }

    private Map<DataFormat, Object> saveClipboardContent(Clipboard clipboard) {
        Map<DataFormat, Object> savedContent = new HashMap<>();
        Set<DataFormat> previousContentTypes = clipboard.getContentTypes();
        for (DataFormat dataFormat : previousContentTypes) {
            Object content = clipboard.getContent(dataFormat);
            if (content != null) {
                savedContent.put(dataFormat, content);
            }
        }
        return savedContent;
    }
    //========================================================================

    // SEND WITH KEYS
    //========================================================================
    private static final Set<Character> VALID_CHAR_BASE_ALPHABET = IntStream.range(97, 123).mapToObj(c -> (char) c).collect(Collectors.toSet()); // a to z
    private static final Set<Character> VALID_CHAR_BASE_DIGIT = IntStream.range(48, 58).mapToObj(c -> (char) c).collect(Collectors.toSet()); // 0 to 9
    private static final Map<Character, Integer> DIGIT_MAP = FluentHashMap
            .map('0', KeyEvent.VK_NUMPAD0)
            .with('1', KeyEvent.VK_NUMPAD1)
            .with('2', KeyEvent.VK_NUMPAD2)
            .with('3', KeyEvent.VK_NUMPAD3)
            .with('4', KeyEvent.VK_NUMPAD4)
            .with('5', KeyEvent.VK_NUMPAD5)
            .with('6', KeyEvent.VK_NUMPAD6)
            .with('7', KeyEvent.VK_NUMPAD7)
            .with('8', KeyEvent.VK_NUMPAD8)
            .with('9', KeyEvent.VK_NUMPAD9);

    private void sendTextUsingKeyPresses(String text) {
        try {
            final char charAt = text.charAt(0);
            final boolean isUpper = Character.isUpperCase(charAt);
            // Lower to have a valid char to send
            final char chartAtLowered = Character.toLowerCase(charAt);
            final boolean isDigit = VALID_CHAR_BASE_DIGIT.contains(chartAtLowered);
            final boolean isSpace = charAt == ' ';
            if (VALID_CHAR_BASE_ALPHABET.contains(chartAtLowered) || isDigit || isSpace) {
                if (isSpace) {
                    awtKeyPressThenRelease(KeyEvent.VK_SPACE);
                } else if (isDigit) {
                    awtKeyPressThenRelease(DIGIT_MAP.get(chartAtLowered));
                } else {
                    if (isUpper && !Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
                        awtKeyPressThenRelease(KeyEvent.VK_SHIFT, KeyEvent.getExtendedKeyCodeForChar(chartAtLowered));
                    } else {
                        awtKeyPressThenRelease(KeyEvent.getExtendedKeyCodeForChar(chartAtLowered));
                    }
                }
            } else {
                sendTextUsingClipboard(text);
            }
        } catch (Throwable t) {
            LOGGER.error("Couldn't send direct keys event for virtual keyboard, will fallback to copy/paste method", t);
            sendTextUsingClipboard(text);
        }
    }
    //========================================================================

    // AWT ROBOT
    //========================================================================
    private void awtKeyPressThenRelease(final int... keyCodes) {
        this.robot.waitForIdle();
        for (int keyCode : keyCodes) {
            this.awtKeyPress(keyCode);
        }
        this.robot.delay(VirtualKeyboardController.KEY_PRESS_DELAY);
        this.robot.waitForIdle();
        for (int i = keyCodes.length - 1; i >= 0; i--) {
            this.awtKeyRelease(keyCodes[i]);
        }
        this.robot.delay(VirtualKeyboardController.AFTER_TYPE_DELAY);
    }


    private void awtKeyRelease(final int keyCode) {
        if (keyCode >= 0)
            this.robot.keyRelease(keyCode);
    }

    private void awtKeyPress(final int keyCode) {
        if (keyCode >= 0)
            this.robot.keyPress(keyCode);
    }

    private int convertJavaFXToAwt(final KeyCode keyCode) {
        Integer awtCode = KeyCodeConverter.KEY_CODES.get(keyCode);
        if (awtCode == null)
            LOGGER.warn("Didn't find the corresponding AWT Key code for JavaFX key code {}", keyCode);
        return awtCode != null ? awtCode : -1;
    }
    //========================================================================
}
