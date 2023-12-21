package org.lifecompanion.util.javafx;

import javafx.scene.input.KeyCode;
import org.lifecompanion.framework.commons.translation.Translation;

import java.security.Key;
import java.util.List;
import java.util.Set;

public class KeyCodeUtils {

    /**
     * Gets the translated name of the KeyCode. The key for the translation is the
     * String "keyboard.key." to which the default english name with spaces replaced
     * by dots and put to lowercase is added. For example the keyCode with the name
     * "Scroll Lock" has a translation searched for with the key
     * "keyboard.key.scroll.lock".
     *
     * @param keyCode                   the keyCode whose name should be translated
     * @param emptyKeyCodeTranslationId translation to return if given keyCode is null, if value is null, will return null
     * @return the String of the translated name or the default name if the key doesn't exist in the translation
     */
    public static String getTranslatedKeyCodeName(final KeyCode keyCode, String emptyKeyCodeTranslationId) {
        if (keyCode != null) {
            String keyCodeKeyString = "keyboard.key." + keyCode.name().toLowerCase().replace('_', '.');
            if (Translation.isTranslationExit(keyCodeKeyString)) {
                return Translation.getText(keyCodeKeyString);
            } else {
                return Translation.getText(keyCode.getName());
            }
        } else {
            return emptyKeyCodeTranslationId != null ? Translation.getText(emptyKeyCodeTranslationId) : null;
        }
    }

    public static boolean isTextGeneratingKeyCode(final KeyCode keyCode) {
        Set<KeyCode> producingText = Set.of(
                KeyCode.COMMA,
                KeyCode.MINUS,
                KeyCode.PERIOD,
                KeyCode.SLASH,
                KeyCode.SEMICOLON,
                KeyCode.EQUALS,
                KeyCode.OPEN_BRACKET,
                KeyCode.CLOSE_BRACKET,
                KeyCode.MULTIPLY,
                KeyCode.ADD,
                KeyCode.SEPARATOR,
                KeyCode.SUBTRACT,
                KeyCode.DECIMAL,
                KeyCode.DIVIDE,
                KeyCode.BACK_QUOTE,
                KeyCode.QUOTE,
                KeyCode.DEAD_GRAVE,
                KeyCode.DEAD_ACUTE,
                KeyCode.DEAD_CIRCUMFLEX,
                KeyCode.DEAD_TILDE,
                KeyCode.DEAD_MACRON,
                KeyCode.DEAD_BREVE,
                KeyCode.DEAD_ABOVEDOT,
                KeyCode.DEAD_DIAERESIS,
                KeyCode.DEAD_ABOVERING,
                KeyCode.DEAD_DOUBLEACUTE,
                KeyCode.DEAD_CARON,
                KeyCode.DEAD_CEDILLA,
                KeyCode.DEAD_OGONEK,
                KeyCode.DEAD_IOTA,
                KeyCode.DEAD_VOICED_SOUND,
                KeyCode.AMPERSAND,
                KeyCode.ASTERISK,
                KeyCode.QUOTEDBL,
                KeyCode.LESS,
                KeyCode.GREATER,
                KeyCode.BRACELEFT,
                KeyCode.BRACERIGHT,
                KeyCode.AT,
                KeyCode.COLON,
                KeyCode.CIRCUMFLEX,
                KeyCode.DOLLAR,
                KeyCode.EURO_SIGN,
                KeyCode.EXCLAMATION_MARK,
                KeyCode.INVERTED_EXCLAMATION_MARK,
                KeyCode.LEFT_PARENTHESIS,
                KeyCode.NUMBER_SIGN,
                KeyCode.PLUS,
                KeyCode.RIGHT_PARENTHESIS,
                KeyCode.UNDERSCORE
        );
        if (keyCode != null) {
            return keyCode.isKeypadKey() || keyCode.isLetterKey() || keyCode.isDigitKey() || keyCode.isWhitespaceKey() || producingText.contains(keyCode);
        }
        return false;
    }
}
