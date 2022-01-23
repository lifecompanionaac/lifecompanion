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

package org.lifecompanion.base.data.control.virtual.keyboard.mapping;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import javafx.scene.input.KeyCode;

/**
 * Utils class to convert JavaFX key code to AWT {@link KeyEvent} codes.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class KeyCodeConverter {
	public final static HashMap<KeyCode, Integer> KEY_CODES = new HashMap<>(KeyCode.values().length);

	static {
		KeyCodeConverter.KEY_CODES.put(KeyCode.ENTER, KeyEvent.VK_ENTER);
		KeyCodeConverter.KEY_CODES.put(KeyCode.BACK_SPACE, KeyEvent.VK_BACK_SPACE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.TAB, KeyEvent.VK_TAB);
		KeyCodeConverter.KEY_CODES.put(KeyCode.CANCEL, KeyEvent.VK_CANCEL);
		KeyCodeConverter.KEY_CODES.put(KeyCode.CLEAR, KeyEvent.VK_CLEAR);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SHIFT, KeyEvent.VK_SHIFT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.CONTROL, KeyEvent.VK_CONTROL);
		KeyCodeConverter.KEY_CODES.put(KeyCode.ALT, KeyEvent.VK_ALT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.PAUSE, KeyEvent.VK_PAUSE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.CAPS, KeyEvent.VK_CAPS_LOCK);
		KeyCodeConverter.KEY_CODES.put(KeyCode.ESCAPE, KeyEvent.VK_ESCAPE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SPACE, KeyEvent.VK_SPACE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.PAGE_UP, KeyEvent.VK_PAGE_UP);
		KeyCodeConverter.KEY_CODES.put(KeyCode.PAGE_DOWN, KeyEvent.VK_PAGE_DOWN);
		KeyCodeConverter.KEY_CODES.put(KeyCode.END, KeyEvent.VK_END);
		KeyCodeConverter.KEY_CODES.put(KeyCode.HOME, KeyEvent.VK_HOME);
		KeyCodeConverter.KEY_CODES.put(KeyCode.LEFT, KeyEvent.VK_LEFT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.UP, KeyEvent.VK_UP);
		KeyCodeConverter.KEY_CODES.put(KeyCode.RIGHT, KeyEvent.VK_RIGHT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DOWN, KeyEvent.VK_DOWN);
		KeyCodeConverter.KEY_CODES.put(KeyCode.COMMA, KeyEvent.VK_COMMA);
		KeyCodeConverter.KEY_CODES.put(KeyCode.MINUS, KeyEvent.VK_MINUS);
		KeyCodeConverter.KEY_CODES.put(KeyCode.PERIOD, KeyEvent.VK_PERIOD);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SLASH, KeyEvent.VK_SLASH);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DIGIT0, KeyEvent.VK_0);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DIGIT1, KeyEvent.VK_1);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DIGIT2, KeyEvent.VK_2);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DIGIT3, KeyEvent.VK_3);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DIGIT4, KeyEvent.VK_4);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DIGIT5, KeyEvent.VK_5);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DIGIT6, KeyEvent.VK_6);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DIGIT7, KeyEvent.VK_7);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DIGIT8, KeyEvent.VK_8);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DIGIT9, KeyEvent.VK_9);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SEMICOLON, KeyEvent.VK_SEMICOLON);
		KeyCodeConverter.KEY_CODES.put(KeyCode.EQUALS, KeyEvent.VK_EQUALS);
		KeyCodeConverter.KEY_CODES.put(KeyCode.A, KeyEvent.VK_A);
		KeyCodeConverter.KEY_CODES.put(KeyCode.B, KeyEvent.VK_B);
		KeyCodeConverter.KEY_CODES.put(KeyCode.C, KeyEvent.VK_C);
		KeyCodeConverter.KEY_CODES.put(KeyCode.D, KeyEvent.VK_D);
		KeyCodeConverter.KEY_CODES.put(KeyCode.E, KeyEvent.VK_E);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F, KeyEvent.VK_F);
		KeyCodeConverter.KEY_CODES.put(KeyCode.G, KeyEvent.VK_G);
		KeyCodeConverter.KEY_CODES.put(KeyCode.H, KeyEvent.VK_H);
		KeyCodeConverter.KEY_CODES.put(KeyCode.I, KeyEvent.VK_I);
		KeyCodeConverter.KEY_CODES.put(KeyCode.J, KeyEvent.VK_J);
		KeyCodeConverter.KEY_CODES.put(KeyCode.K, KeyEvent.VK_K);
		KeyCodeConverter.KEY_CODES.put(KeyCode.L, KeyEvent.VK_L);
		KeyCodeConverter.KEY_CODES.put(KeyCode.M, KeyEvent.VK_M);
		KeyCodeConverter.KEY_CODES.put(KeyCode.N, KeyEvent.VK_N);
		KeyCodeConverter.KEY_CODES.put(KeyCode.O, KeyEvent.VK_O);
		KeyCodeConverter.KEY_CODES.put(KeyCode.P, KeyEvent.VK_P);
		KeyCodeConverter.KEY_CODES.put(KeyCode.Q, KeyEvent.VK_Q);
		KeyCodeConverter.KEY_CODES.put(KeyCode.R, KeyEvent.VK_R);
		KeyCodeConverter.KEY_CODES.put(KeyCode.S, KeyEvent.VK_S);
		KeyCodeConverter.KEY_CODES.put(KeyCode.T, KeyEvent.VK_T);
		KeyCodeConverter.KEY_CODES.put(KeyCode.U, KeyEvent.VK_U);
		KeyCodeConverter.KEY_CODES.put(KeyCode.V, KeyEvent.VK_V);
		KeyCodeConverter.KEY_CODES.put(KeyCode.W, KeyEvent.VK_W);
		KeyCodeConverter.KEY_CODES.put(KeyCode.X, KeyEvent.VK_X);
		KeyCodeConverter.KEY_CODES.put(KeyCode.Y, KeyEvent.VK_Y);
		KeyCodeConverter.KEY_CODES.put(KeyCode.Z, KeyEvent.VK_Z);
		KeyCodeConverter.KEY_CODES.put(KeyCode.OPEN_BRACKET, KeyEvent.VK_OPEN_BRACKET);
		KeyCodeConverter.KEY_CODES.put(KeyCode.BACK_SLASH, KeyEvent.VK_BACK_SLASH);
		KeyCodeConverter.KEY_CODES.put(KeyCode.CLOSE_BRACKET, KeyEvent.VK_CLOSE_BRACKET);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NUMPAD0, KeyEvent.VK_NUMPAD0);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NUMPAD1, KeyEvent.VK_NUMPAD1);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NUMPAD2, KeyEvent.VK_NUMPAD2);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NUMPAD3, KeyEvent.VK_NUMPAD3);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NUMPAD4, KeyEvent.VK_NUMPAD4);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NUMPAD5, KeyEvent.VK_NUMPAD5);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NUMPAD6, KeyEvent.VK_NUMPAD6);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NUMPAD7, KeyEvent.VK_NUMPAD7);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NUMPAD8, KeyEvent.VK_NUMPAD8);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NUMPAD9, KeyEvent.VK_NUMPAD9);
		KeyCodeConverter.KEY_CODES.put(KeyCode.MULTIPLY, KeyEvent.VK_MULTIPLY);
		KeyCodeConverter.KEY_CODES.put(KeyCode.ADD, KeyEvent.VK_ADD);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SEPARATOR, KeyEvent.VK_SEPARATOR);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SUBTRACT, KeyEvent.VK_SUBTRACT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DECIMAL, KeyEvent.VK_DECIMAL);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DIVIDE, KeyEvent.VK_DIVIDE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DELETE, KeyEvent.VK_DELETE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NUM_LOCK, KeyEvent.VK_NUM_LOCK);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SCROLL_LOCK, KeyEvent.VK_SCROLL_LOCK);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F1, KeyEvent.VK_F1);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F2, KeyEvent.VK_F2);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F3, KeyEvent.VK_F3);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F4, KeyEvent.VK_F4);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F5, KeyEvent.VK_F5);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F6, KeyEvent.VK_F6);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F7, KeyEvent.VK_F7);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F8, KeyEvent.VK_F8);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F9, KeyEvent.VK_F9);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F10, KeyEvent.VK_F10);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F11, KeyEvent.VK_F11);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F12, KeyEvent.VK_F12);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F13, KeyEvent.VK_F13);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F14, KeyEvent.VK_F14);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F15, KeyEvent.VK_F15);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F16, KeyEvent.VK_F16);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F17, KeyEvent.VK_F17);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F18, KeyEvent.VK_F18);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F19, KeyEvent.VK_F19);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F20, KeyEvent.VK_F20);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F21, KeyEvent.VK_F21);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F22, KeyEvent.VK_F22);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F23, KeyEvent.VK_F23);
		KeyCodeConverter.KEY_CODES.put(KeyCode.F24, KeyEvent.VK_F24);
		KeyCodeConverter.KEY_CODES.put(KeyCode.PRINTSCREEN, KeyEvent.VK_PRINTSCREEN);
		KeyCodeConverter.KEY_CODES.put(KeyCode.INSERT, KeyEvent.VK_INSERT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.HELP, KeyEvent.VK_HELP);
		KeyCodeConverter.KEY_CODES.put(KeyCode.META, KeyEvent.VK_META);
		KeyCodeConverter.KEY_CODES.put(KeyCode.BACK_QUOTE, KeyEvent.VK_BACK_QUOTE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.QUOTE, KeyEvent.VK_QUOTE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.KP_UP, KeyEvent.VK_KP_UP);
		KeyCodeConverter.KEY_CODES.put(KeyCode.KP_DOWN, KeyEvent.VK_KP_DOWN);
		KeyCodeConverter.KEY_CODES.put(KeyCode.KP_LEFT, KeyEvent.VK_KP_LEFT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.KP_RIGHT, KeyEvent.VK_KP_RIGHT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_GRAVE, KeyEvent.VK_DEAD_GRAVE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_ACUTE, KeyEvent.VK_DEAD_ACUTE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_CIRCUMFLEX, KeyEvent.VK_DEAD_CIRCUMFLEX);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_TILDE, KeyEvent.VK_DEAD_TILDE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_MACRON, KeyEvent.VK_DEAD_MACRON);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_BREVE, KeyEvent.VK_DEAD_BREVE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_ABOVEDOT, KeyEvent.VK_DEAD_ABOVEDOT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_DIAERESIS, KeyEvent.VK_DEAD_DIAERESIS);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_ABOVERING, KeyEvent.VK_DEAD_ABOVERING);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_DOUBLEACUTE, KeyEvent.VK_DEAD_DOUBLEACUTE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_CARON, KeyEvent.VK_DEAD_CARON);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_CEDILLA, KeyEvent.VK_DEAD_CEDILLA);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_OGONEK, KeyEvent.VK_DEAD_OGONEK);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_IOTA, KeyEvent.VK_DEAD_IOTA);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_VOICED_SOUND, KeyEvent.VK_DEAD_VOICED_SOUND);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DEAD_SEMIVOICED_SOUND, KeyEvent.VK_DEAD_SEMIVOICED_SOUND);
		KeyCodeConverter.KEY_CODES.put(KeyCode.AMPERSAND, KeyEvent.VK_AMPERSAND);
		KeyCodeConverter.KEY_CODES.put(KeyCode.ASTERISK, KeyEvent.VK_ASTERISK);
		KeyCodeConverter.KEY_CODES.put(KeyCode.QUOTEDBL, KeyEvent.VK_QUOTEDBL);
		KeyCodeConverter.KEY_CODES.put(KeyCode.LESS, KeyEvent.VK_LESS);
		KeyCodeConverter.KEY_CODES.put(KeyCode.GREATER, KeyEvent.VK_GREATER);
		KeyCodeConverter.KEY_CODES.put(KeyCode.BRACELEFT, KeyEvent.VK_BRACELEFT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.BRACERIGHT, KeyEvent.VK_BRACERIGHT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.AT, KeyEvent.VK_AT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.COLON, KeyEvent.VK_COLON);
		KeyCodeConverter.KEY_CODES.put(KeyCode.CIRCUMFLEX, KeyEvent.VK_CIRCUMFLEX);
		KeyCodeConverter.KEY_CODES.put(KeyCode.DOLLAR, KeyEvent.VK_DOLLAR);
		KeyCodeConverter.KEY_CODES.put(KeyCode.EURO_SIGN, KeyEvent.VK_EURO_SIGN);
		KeyCodeConverter.KEY_CODES.put(KeyCode.EXCLAMATION_MARK, KeyEvent.VK_EXCLAMATION_MARK);
		KeyCodeConverter.KEY_CODES.put(KeyCode.INVERTED_EXCLAMATION_MARK, KeyEvent.VK_INVERTED_EXCLAMATION_MARK);
		KeyCodeConverter.KEY_CODES.put(KeyCode.LEFT_PARENTHESIS, KeyEvent.VK_LEFT_PARENTHESIS);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NUMBER_SIGN, KeyEvent.VK_NUMBER_SIGN);
		KeyCodeConverter.KEY_CODES.put(KeyCode.PLUS, KeyEvent.VK_PLUS);
		KeyCodeConverter.KEY_CODES.put(KeyCode.RIGHT_PARENTHESIS, KeyEvent.VK_RIGHT_PARENTHESIS);
		KeyCodeConverter.KEY_CODES.put(KeyCode.UNDERSCORE, KeyEvent.VK_UNDERSCORE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.WINDOWS, KeyEvent.VK_WINDOWS);
		KeyCodeConverter.KEY_CODES.put(KeyCode.CONTEXT_MENU, KeyEvent.VK_CONTEXT_MENU);
		KeyCodeConverter.KEY_CODES.put(KeyCode.FINAL, KeyEvent.VK_FINAL);
		KeyCodeConverter.KEY_CODES.put(KeyCode.CONVERT, KeyEvent.VK_CONVERT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.NONCONVERT, KeyEvent.VK_NONCONVERT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.ACCEPT, KeyEvent.VK_ACCEPT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.MODECHANGE, KeyEvent.VK_MODECHANGE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.KANA, KeyEvent.VK_KANA);
		KeyCodeConverter.KEY_CODES.put(KeyCode.KANJI, KeyEvent.VK_KANJI);
		KeyCodeConverter.KEY_CODES.put(KeyCode.ALPHANUMERIC, KeyEvent.VK_ALPHANUMERIC);
		KeyCodeConverter.KEY_CODES.put(KeyCode.KATAKANA, KeyEvent.VK_KATAKANA);
		KeyCodeConverter.KEY_CODES.put(KeyCode.HIRAGANA, KeyEvent.VK_HIRAGANA);
		KeyCodeConverter.KEY_CODES.put(KeyCode.FULL_WIDTH, KeyEvent.VK_FULL_WIDTH);
		KeyCodeConverter.KEY_CODES.put(KeyCode.HALF_WIDTH, KeyEvent.VK_HALF_WIDTH);
		KeyCodeConverter.KEY_CODES.put(KeyCode.ROMAN_CHARACTERS, KeyEvent.VK_ROMAN_CHARACTERS);
		KeyCodeConverter.KEY_CODES.put(KeyCode.ALL_CANDIDATES, KeyEvent.VK_ALL_CANDIDATES);
		KeyCodeConverter.KEY_CODES.put(KeyCode.PREVIOUS_CANDIDATE, KeyEvent.VK_PREVIOUS_CANDIDATE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.CODE_INPUT, KeyEvent.VK_CODE_INPUT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.JAPANESE_KATAKANA, KeyEvent.VK_JAPANESE_KATAKANA);
		KeyCodeConverter.KEY_CODES.put(KeyCode.JAPANESE_HIRAGANA, KeyEvent.VK_JAPANESE_HIRAGANA);
		KeyCodeConverter.KEY_CODES.put(KeyCode.JAPANESE_ROMAN, KeyEvent.VK_JAPANESE_ROMAN);
		KeyCodeConverter.KEY_CODES.put(KeyCode.KANA_LOCK, KeyEvent.VK_KANA_LOCK);
		KeyCodeConverter.KEY_CODES.put(KeyCode.INPUT_METHOD_ON_OFF, KeyEvent.VK_INPUT_METHOD_ON_OFF);
		KeyCodeConverter.KEY_CODES.put(KeyCode.CUT, KeyEvent.VK_CUT);
		KeyCodeConverter.KEY_CODES.put(KeyCode.COPY, KeyEvent.VK_COPY);
		KeyCodeConverter.KEY_CODES.put(KeyCode.PASTE, KeyEvent.VK_PASTE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.UNDO, KeyEvent.VK_UNDO);
		KeyCodeConverter.KEY_CODES.put(KeyCode.AGAIN, KeyEvent.VK_AGAIN);
		KeyCodeConverter.KEY_CODES.put(KeyCode.FIND, KeyEvent.VK_FIND);
		KeyCodeConverter.KEY_CODES.put(KeyCode.PROPS, KeyEvent.VK_PROPS);
		KeyCodeConverter.KEY_CODES.put(KeyCode.STOP, KeyEvent.VK_STOP);
		KeyCodeConverter.KEY_CODES.put(KeyCode.COMPOSE, KeyEvent.VK_COMPOSE);
		KeyCodeConverter.KEY_CODES.put(KeyCode.ALT_GRAPH, KeyEvent.VK_ALT_GRAPH);
		KeyCodeConverter.KEY_CODES.put(KeyCode.BEGIN, KeyEvent.VK_BEGIN);
		KeyCodeConverter.KEY_CODES.put(KeyCode.UNDEFINED, KeyEvent.VK_UNDEFINED);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SOFTKEY_0, KeyEvent.VK_0);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SOFTKEY_1, KeyEvent.VK_1);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SOFTKEY_2, KeyEvent.VK_2);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SOFTKEY_3, KeyEvent.VK_3);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SOFTKEY_4, KeyEvent.VK_4);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SOFTKEY_5, KeyEvent.VK_5);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SOFTKEY_6, KeyEvent.VK_6);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SOFTKEY_7, KeyEvent.VK_7);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SOFTKEY_8, KeyEvent.VK_8);
		KeyCodeConverter.KEY_CODES.put(KeyCode.SOFTKEY_9, KeyEvent.VK_9);
	}

	/**
	 * Method used to generate AWT key constant from JavaFX key codes
	 */
	static void generateKeys() {
		KeyCode[] values = KeyCode.values();
		for (KeyCode keyCode : values) {
			System.out.println("KEY_CODES.put(KeyCode." + keyCode.name() + ",KeyEvent.VK_" + keyCode.name() + ");");
		}
	}
}
