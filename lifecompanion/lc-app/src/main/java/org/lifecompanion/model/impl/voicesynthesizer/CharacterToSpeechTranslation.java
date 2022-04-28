package org.lifecompanion.model.impl.voicesynthesizer;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.utils.FluentHashMap;

public class CharacterToSpeechTranslation {
    private static final FluentHashMap<Character, String> TRANSLATIONS = FluentHashMap
            .map(' ', "space")
            .with('ê', "e.circumflex")
            .with('ë', "e.umlaut")
            .with('\t', "tab")
            .with('\n', "linebreak")
            .with('\r', "linebreak");

    public static boolean isManuallyTranslated(char c) {
        return TRANSLATIONS.containsKey(Character.toLowerCase(c));
    }

    public static String getTranslatedName(char c) {
        return Translation.getText("char.to.speech.special." + TRANSLATIONS.get(Character.toLowerCase(c)));
    }
}
