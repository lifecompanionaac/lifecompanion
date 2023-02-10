package org.lifecompanion.controller.editaction;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;

import java.util.Arrays;
import java.util.concurrent.TransferQueue;

public enum AutoCompleteKeyboardEnum {
    AZERTY("autocomplete.keyboard.azerty.name", false, "a", "z", "e", "r", "t", "y", "u", "i", "o", "p", "q", "s", "d", "f", "g", "h", "j", "k", "l", "m", "w", "x", "c", "v", "b", "n"),
    ABC("autocomplete.keyboard.abc.name", false, "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"),
    NUMBER_0("autocomplete.keyboard.numbers0.name", false, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
    NUMBER_1("autocomplete.keyboard.numbers1.name", false, "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
    ACCENTS0("autocomplete.keyboard.accents0.name", false, "é", "è", "à", "ç", "ù", "ê", "ë", "ô"),
    SPECIAL0("autocomplete.keyboard.special0.name", false, ",", ".", ";", ":", "!", "?", "/"),
    QUICK_WORDS("autocomplete.keyboard.quick.words.name",
            true,
            "keyboard.quick.word.hello",
            "keyboard.quick.word.thanks",
            "keyboard.quick.word.goodbye",
            "keyboard.quick.word.please",
            "keyboard.quick.word.yes",
            "keyboard.quick.word.no",
            "keyboard.quick.word.dontknow"),
    ;

    private final String nameId;
    private final String[] elements;

    AutoCompleteKeyboardEnum(String nameId, boolean translate, String... elements) {
        this.nameId = nameId;
        this.elements = translate ? Arrays.stream(elements).map(Translation::getText).toArray(String[]::new) : elements;
    }

    public static AutoCompleteKeyboardEnum getMatchingPattern(GridPartKeyComponentI key1, GridPartKeyComponentI key2) {
        for (AutoCompleteKeyboardEnum autoCompleteKeyboardEnum : values()) {
            for (int i = 0; i < autoCompleteKeyboardEnum.elements.length - 1; i++) {
                if (autoCompleteKeyboardEnum.getCompletionStartIndex(key1, key2) >= 0) return autoCompleteKeyboardEnum;
            }
        }
        return null;
    }

    public String[] getElements() {
        return elements;
    }

    public int getCompletionStartIndex(GridPartKeyComponentI key1, GridPartKeyComponentI key2) {
        for (int i = 0; i < elements.length - 1; i++) {
            if (StringUtils.isEqualsIgnoreCase(elements[i], StringUtils.trimToEmpty(key1.textContentProperty().get()))
                    && StringUtils.isEqualsIgnoreCase(elements[i + 1], StringUtils.trimToEmpty(key2.textContentProperty().get()))) return i + 2;
        }
        return -1;
    }

    public String getName() {
        return Translation.getText(nameId);
    }
}
