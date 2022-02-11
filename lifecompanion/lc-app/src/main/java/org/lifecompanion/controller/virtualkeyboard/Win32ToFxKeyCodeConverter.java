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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Generated with https://docs.microsoft.com/en-us/windows/win32/inputdev/virtual-key-codes.<br>
 * Help special key mappings for {@link WinAutoHotKeyKeyboardReceiverController}
 */
public class Win32ToFxKeyCodeConverter {
    public static final Map<Integer, KeyCode> WIN32_TO_JAVAFX = new HashMap<>(200);
    public static final Map<KeyCode, Integer> JAVAFX_TO_WIN32 = new HashMap<>(200);

    private static final Logger LOGGER = LoggerFactory.getLogger(Win32ToFxKeyCodeConverter.class);


    static {
        WIN32_TO_JAVAFX.put(0x03, KeyCode.CANCEL); // 0x03 - VK_CANCEL - Control-break processing
        WIN32_TO_JAVAFX.put(0x08, KeyCode.BACK_SPACE); // 0x08 - VK_BACK - BACKSPACE key
        WIN32_TO_JAVAFX.put(0x09, KeyCode.TAB); // 0x09 - VK_TAB - TAB key
        WIN32_TO_JAVAFX.put(0x0C, KeyCode.CLEAR); // 0x0C - VK_CLEAR - CLEAR key
        WIN32_TO_JAVAFX.put(0x0D, KeyCode.ENTER); // 0x0D - VK_RETURN - ENTER key
        WIN32_TO_JAVAFX.put(0x10, KeyCode.SHIFT); // 0x10 - VK_SHIFT - SHIFT key
        WIN32_TO_JAVAFX.put(0x11, KeyCode.CONTROL); // 0x11 - VK_CONTROL - CTRL key
        WIN32_TO_JAVAFX.put(0x12, KeyCode.ALT); // 0x12 - VK_MENU - ALT key
        WIN32_TO_JAVAFX.put(0x13, KeyCode.PAUSE); // 0x13 - VK_PAUSE - PAUSE key
        WIN32_TO_JAVAFX.put(0x14, KeyCode.CAPS); // 0x14 - VK_CAPITAL - CAPS LOCK key
        WIN32_TO_JAVAFX.put(0x15, KeyCode.KANA); // 0x15 - VK_KANA - IME Kana mode
        WIN32_TO_JAVAFX.put(0x18, KeyCode.FINAL); // 0x18 - VK_FINAL - IME final mode
        WIN32_TO_JAVAFX.put(0x19, KeyCode.KANJI); // 0x19 - VK_KANJI - IME Kanji mode
        WIN32_TO_JAVAFX.put(0x1B, KeyCode.ESCAPE); // 0x1B - VK_ESCAPE - ESC key
        WIN32_TO_JAVAFX.put(0x1C, KeyCode.CONVERT); // 0x1C - VK_CONVERT - IME convert
        WIN32_TO_JAVAFX.put(0x1D, KeyCode.NONCONVERT); // 0x1D - VK_NONCONVERT - IME nonconvert
        WIN32_TO_JAVAFX.put(0x1E, KeyCode.ACCEPT); // 0x1E - VK_ACCEPT - IME accept
        WIN32_TO_JAVAFX.put(0x1F, KeyCode.MODECHANGE); // 0x1F - VK_MODECHANGE - IME mode change request
        WIN32_TO_JAVAFX.put(0x20, KeyCode.SPACE); // 0x20 - VK_SPACE - SPACEBAR
        WIN32_TO_JAVAFX.put(0x21, KeyCode.PAGE_UP); // 0x21 - VK_PRIOR - PAGE UP key
        WIN32_TO_JAVAFX.put(0x22, KeyCode.PAGE_DOWN); // 0x22 - VK_NEXT - PAGE DOWN key
        WIN32_TO_JAVAFX.put(0x23, KeyCode.END); // 0x23 - VK_END - END key
        WIN32_TO_JAVAFX.put(0x24, KeyCode.HOME); // 0x24 - VK_HOME - HOME key
        WIN32_TO_JAVAFX.put(0x25, KeyCode.LEFT); // 0x25 - VK_LEFT - LEFT ARROW key
        WIN32_TO_JAVAFX.put(0x26, KeyCode.UP); // 0x26 - VK_UP - UP ARROW key
        WIN32_TO_JAVAFX.put(0x27, KeyCode.RIGHT); // 0x27 - VK_RIGHT - RIGHT ARROW key
        WIN32_TO_JAVAFX.put(0x28, KeyCode.DOWN); // 0x28 - VK_DOWN - DOWN ARROW key
        WIN32_TO_JAVAFX.put(0x2C, KeyCode.PRINTSCREEN); // 0x2C - VK_SNAPSHOT - PRINT SCREEN key
        WIN32_TO_JAVAFX.put(0x2D, KeyCode.INSERT); // 0x2D - VK_INSERT - INS key
        WIN32_TO_JAVAFX.put(0x2E, KeyCode.DELETE); // 0x2E - VK_DELETE - DEL key
        WIN32_TO_JAVAFX.put(0x2F, KeyCode.HELP); // 0x2F - VK_HELP - HELP key
        WIN32_TO_JAVAFX.put(0x5B, KeyCode.WINDOWS); // 0x5B - VK_LWIN - Left Windows key (Natural keyboard)
        WIN32_TO_JAVAFX.put(0x5C, KeyCode.WINDOWS); // 0x5C - VK_RWIN - Right Windows key (Natural keyboard)
        WIN32_TO_JAVAFX.put(0x60, KeyCode.NUMPAD0); // 0x60 - VK_NUMPAD0 - Numeric keypad 0 key
        WIN32_TO_JAVAFX.put(0x61, KeyCode.NUMPAD1); // 0x61 - VK_NUMPAD1 - Numeric keypad 1 key
        WIN32_TO_JAVAFX.put(0x62, KeyCode.NUMPAD2); // 0x62 - VK_NUMPAD2 - Numeric keypad 2 key
        WIN32_TO_JAVAFX.put(0x63, KeyCode.NUMPAD3); // 0x63 - VK_NUMPAD3 - Numeric keypad 3 key
        WIN32_TO_JAVAFX.put(0x64, KeyCode.NUMPAD4); // 0x64 - VK_NUMPAD4 - Numeric keypad 4 key
        WIN32_TO_JAVAFX.put(0x65, KeyCode.NUMPAD5); // 0x65 - VK_NUMPAD5 - Numeric keypad 5 key
        WIN32_TO_JAVAFX.put(0x66, KeyCode.NUMPAD6); // 0x66 - VK_NUMPAD6 - Numeric keypad 6 key
        WIN32_TO_JAVAFX.put(0x67, KeyCode.NUMPAD7); // 0x67 - VK_NUMPAD7 - Numeric keypad 7 key
        WIN32_TO_JAVAFX.put(0x68, KeyCode.NUMPAD8); // 0x68 - VK_NUMPAD8 - Numeric keypad 8 key
        WIN32_TO_JAVAFX.put(0x69, KeyCode.NUMPAD9); // 0x69 - VK_NUMPAD9 - Numeric keypad 9 key
        WIN32_TO_JAVAFX.put(0x6A, KeyCode.MULTIPLY); // 0x6A - VK_MULTIPLY - Multiply key
        WIN32_TO_JAVAFX.put(0x6B, KeyCode.ADD); // 0x6B - VK_ADD - Add key
        WIN32_TO_JAVAFX.put(0x6C, KeyCode.SEPARATOR); // 0x6C - VK_SEPARATOR - Separator key
        WIN32_TO_JAVAFX.put(0x6D, KeyCode.SUBTRACT); // 0x6D - VK_SUBTRACT - Subtract key
        WIN32_TO_JAVAFX.put(0x6E, KeyCode.DECIMAL); // 0x6E - VK_DECIMAL - Decimal key
        WIN32_TO_JAVAFX.put(0x6F, KeyCode.DIVIDE); // 0x6F - VK_DIVIDE - Divide key
        WIN32_TO_JAVAFX.put(0x70, KeyCode.F1); // 0x70 - VK_F1 - F1 key
        WIN32_TO_JAVAFX.put(0x71, KeyCode.F2); // 0x71 - VK_F2 - F2 key
        WIN32_TO_JAVAFX.put(0x72, KeyCode.F3); // 0x72 - VK_F3 - F3 key
        WIN32_TO_JAVAFX.put(0x73, KeyCode.F4); // 0x73 - VK_F4 - F4 key
        WIN32_TO_JAVAFX.put(0x74, KeyCode.F5); // 0x74 - VK_F5 - F5 key
        WIN32_TO_JAVAFX.put(0x75, KeyCode.F6); // 0x75 - VK_F6 - F6 key
        WIN32_TO_JAVAFX.put(0x76, KeyCode.F7); // 0x76 - VK_F7 - F7 key
        WIN32_TO_JAVAFX.put(0x77, KeyCode.F8); // 0x77 - VK_F8 - F8 key
        WIN32_TO_JAVAFX.put(0x78, KeyCode.F9); // 0x78 - VK_F9 - F9 key
        WIN32_TO_JAVAFX.put(0x79, KeyCode.F10); // 0x79 - VK_F10 - F10 key
        WIN32_TO_JAVAFX.put(0x7A, KeyCode.F11); // 0x7A - VK_F11 - F11 key
        WIN32_TO_JAVAFX.put(0x7B, KeyCode.F12); // 0x7B - VK_F12 - F12 key
        WIN32_TO_JAVAFX.put(0x7C, KeyCode.F13); // 0x7C - VK_F13 - F13 key
        WIN32_TO_JAVAFX.put(0x7D, KeyCode.F14); // 0x7D - VK_F14 - F14 key
        WIN32_TO_JAVAFX.put(0x7E, KeyCode.F15); // 0x7E - VK_F15 - F15 key
        WIN32_TO_JAVAFX.put(0x7F, KeyCode.F16); // 0x7F - VK_F16 - F16 key
        WIN32_TO_JAVAFX.put(0x80, KeyCode.F17); // 0x80 - VK_F17 - F17 key
        WIN32_TO_JAVAFX.put(0x81, KeyCode.F18); // 0x81 - VK_F18 - F18 key
        WIN32_TO_JAVAFX.put(0x82, KeyCode.F19); // 0x82 - VK_F19 - F19 key
        WIN32_TO_JAVAFX.put(0x83, KeyCode.F20); // 0x83 - VK_F20 - F20 key
        WIN32_TO_JAVAFX.put(0x84, KeyCode.F21); // 0x84 - VK_F21 - F21 key
        WIN32_TO_JAVAFX.put(0x85, KeyCode.F22); // 0x85 - VK_F22 - F22 key
        WIN32_TO_JAVAFX.put(0x86, KeyCode.F23); // 0x86 - VK_F23 - F23 key
        WIN32_TO_JAVAFX.put(0x87, KeyCode.F24); // 0x87 - VK_F24 - F24 key
        WIN32_TO_JAVAFX.put(0x90, KeyCode.NUM_LOCK); // 0x90 - VK_NUMLOCK - NUM LOCK key
        WIN32_TO_JAVAFX.put(0x91, KeyCode.SCROLL_LOCK); // 0x91 - VK_SCROLL - SCROLL LOCK key
        WIN32_TO_JAVAFX.put(0xA0, KeyCode.SHIFT); // 0xA0 - VK_LSHIFT - Left SHIFT key
        WIN32_TO_JAVAFX.put(0xA1, KeyCode.SHIFT); // 0xA1 - VK_RSHIFT - Right SHIFT key
        WIN32_TO_JAVAFX.put(0xA2, KeyCode.CONTROL); // 0xA2 - VK_LCONTROL - Left CONTROL key
        WIN32_TO_JAVAFX.put(0xA3, KeyCode.CONTROL); // 0xA3 - VK_RCONTROL - Right CONTROL key
        WIN32_TO_JAVAFX.put(0xA4, KeyCode.CONTEXT_MENU); // 0xA4 - VK_LMENU - Left MENU key
        WIN32_TO_JAVAFX.put(0xA5, KeyCode.CONTEXT_MENU); // 0xA5 - VK_RMENU - Right MENU key
        WIN32_TO_JAVAFX.put(0xAD, KeyCode.MUTE); // 0xAD - VK_VOLUME_MUTE - Volume Mute key
        WIN32_TO_JAVAFX.put(0xAE, KeyCode.VOLUME_DOWN); // 0xAE - VK_VOLUME_DOWN - Volume Down key
        WIN32_TO_JAVAFX.put(0xAF, KeyCode.VOLUME_UP); // 0xAF - VK_VOLUME_UP - Volume Up key
        WIN32_TO_JAVAFX.put(0xB0, KeyCode.TRACK_NEXT); // 0xB0 - VK_MEDIA_NEXT_TRACK - Next Track key
        WIN32_TO_JAVAFX.put(0xB1, KeyCode.TRACK_PREV); // 0xB1 - VK_MEDIA_PREV_TRACK - Previous Track key
        WIN32_TO_JAVAFX.put(0xB2, KeyCode.STOP); // 0xB2 - VK_MEDIA_STOP - Stop Media key
        WIN32_TO_JAVAFX.put(0xFA, KeyCode.PLAY); // 0xFA - VK_PLAY - Play key

        WIN32_TO_JAVAFX.forEach((win32Code, keyCode) -> {
            if (JAVAFX_TO_WIN32.containsKey(keyCode))
                LOGGER.error("JAVAFX TO WIN32 MAPPING ISSUE, duplicated JavaFX key code {} : previous {}, new {}", keyCode, JAVAFX_TO_WIN32.get(keyCode), win32Code);
            JAVAFX_TO_WIN32.put(keyCode, win32Code);
        });
    }

    public static String javaFXKeyCodeToAutoHotKey(KeyCode keyCode, String modifier) {
        Integer valueInWin32 = Win32ToFxKeyCodeConverter.JAVAFX_TO_WIN32.get(keyCode);
        if (valueInWin32 == null) {
            LOGGER.warn("Didn't find a Win32 equivalent for key {} will use raw JavaFX code", keyCode);
            valueInWin32 = keyCode.getCode();
        }
        return "{vk" + Integer.toString(valueInWin32, 16) + (modifier != null ? " " + modifier : "") + "}";
    }
}