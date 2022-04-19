package org.lifecompanion.util.javafx;

import javafx.scene.input.KeyCode;
import org.lifecompanion.framework.commons.translation.Translation;

public class FXKeyCodeTranslatorUtils {

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
}
