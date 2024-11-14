package org.lifecompanion.plugin.phonecontrol2.utils;

import java.util.Set;

public class PhoneControlUtils {
    public static String withOnly(String word, Set<Character> chars) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (chars.contains(word.charAt(i))) {
                r.append(word.charAt(i));
            }
        }
        return r.toString();
    }

    public static String oneOnTwoChar(int startIndex, String word) {
        StringBuilder r = new StringBuilder();
        for (int i = startIndex; i < word.length(); i += 2) {
            r.append(word.charAt(i));
        }
        return r.toString();
    }
}
