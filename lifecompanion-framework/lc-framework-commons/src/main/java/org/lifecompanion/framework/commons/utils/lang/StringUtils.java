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

package org.lifecompanion.framework.commons.utils.lang;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class StringUtils {

    //Can't create
    private StringUtils() {
    }

    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final SimpleDateFormat DATE_WITH_HOUR = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
    private static final SimpleDateFormat DATE_WITHOUT_HOUR = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATE_ONLY_HOURS_MIN_SECOND = new SimpleDateFormat("HH:mm:ss");

    /**
     * @param email a email to check
     * @return true if email is valid, else return false
     */
    public static boolean isEmailValid(final String email) {
        return email.matches(EMAIL_REGEX);
    }

    /**
     * @param date the date to convert to string
     * @return the string that represent the date without hour displayed (dd/MM/yyyy)
     */
    public static String dateToStringWithoutHour(final Date date) {
        return DATE_WITHOUT_HOUR.format(date);
    }

    /**
     * Check if a string is blank or null
     *
     * @param str string to test
     * @return true if str == null or blank
     */
    public static boolean isBlank(final String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String reverse(final String str) {
        return str == null ? null : new StringBuilder(str).reverse().toString();
    }

    /**
     * @param date the date to convert to string
     * @return the string that represent the date with hour displayed (dd/MM/yyyy - HH:mm)
     */
    public static String dateToStringDateWithHour(final Date date) {
        return DATE_WITH_HOUR.format(date);
    }

    /**
     * @param date the date to convert to string
     * @return the string that represent the date with just time displayed (HH:mm:ss)
     */
    public static String dateToStringDateWithOnlyHoursMinuteSecond(final Date date) {
        return DATE_ONLY_HOURS_MIN_SECOND.format(date);
    }

    /**
     * @return a new unique String ID
     */
    public static String getNewID() {
        return UUID.randomUUID().toString();
    }

    /**
     * @param str string to check
     * @return true if str is null, or empty with a trim
     */
    public static boolean isEmpty(final String str) {
        return str == null || str.trim().isEmpty();
    }

    public static int safeLength(final String str) {
        return str != null ? str.length() : 0;
    }

    /**
     * @param str1 first string
     * @param str2 second string
     * @return true if string are different (null,null) will return false
     */
    public static boolean isDifferent(final String str1, final String str2) {
        return !isEquals(str1, str2);
    }

    /**
     * Return true if the given value contains given contains without case sensitive
     *
     * @param value    the value that could contains contains
     * @param contains the string that could be inside value
     * @return true if value contains contains without case
     */
    public static boolean containsIgnoreCase(final String value, final String contains) {
        if (value != null && contains != null) {
            return value.toUpperCase().contains(contains.toUpperCase());
        } else {
            return false;
        }
    }

    /**
     * Check if a string starts with another (ignoring case)
     *
     * @param value the string to test
     * @param start begining of the string
     * @return true if starts ignore case
     */
    public static boolean startWithIgnoreCase(final String value, final String start) {
        if (value != null && start != null) {
            return value.toUpperCase().startsWith(start.toUpperCase());
        } else {
            return false;
        }
    }

    /**
     * Check if a string ends with another (ignoring case)
     *
     * @param value the string to test
     * @return true if ends ignore case
     */
    public static boolean endsWithIgnoreCase(final String value, final String end) {
        if (value != null && end != null) {
            return value.toUpperCase().endsWith(end.toUpperCase());
        } else {
            return false;
        }
    }

    /**
     * Count the number of times that one of the contains element is contained ignore case into value
     *
     * @param value    the value to check
     * @param contains all the element to test
     * @return number of times that value is true with {@link #containsIgnoreCase(String, String)} with contains element
     */
    public static int countContainsIgnoreCase(final String value, final List<String> contains) {
        int t = 0;
        for (String c : contains) {
            t += containsIgnoreCase(value, c) ? 1 : 0;
        }
        return t;
    }

    /**
     * @param str1 first string
     * @param str2 second string
     * @return true if string are equals (null,null) will return true
     */
    public static boolean isEquals(final String str1, final String str2) {
        return Objects.equals(str1, str2);
    }

    public static boolean isEqualsIgnoreCase(final String str1, final String str2) {
        return Objects.equals(toLowerCase(str1), toLowerCase(str2));
    }

    /**
     * Capitalize the string (null tolerant)
     *
     * @param str the string to capitalize
     * @return capitalized string (or null if given string is null)
     */
    public static String capitalize(final String str) {
        int length;
        if (str == null || (length = str.length()) == 0) {
            return str;
        }
        final char firstC = str.charAt(0);
        if (Character.isTitleCase(firstC)) {
            return str;
        }
        return new StringBuilder(length).append(Character.toTitleCase(firstC)).append(str.substring(1)).toString();
    }

    /**
     * Set a string to upper case (null tolerant)
     *
     * @param str the string to upper case
     * @return upper case string (or null if given string is null)
     */
    public static String toUpperCase(final String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.toUpperCase();
    }

    public static String toLowerCase(final String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.toLowerCase();
    }

    /**
     * Execute a safe string but with a index checking
     *
     * @param text                the text to substring
     * @param beginIndexInclusive start index inclusive
     * @param endIndexExclusive   end index inclusive
     * @return the substring (fixed if needed, when index are incorrect)
     */
    public static String safeSubstring(final String text, int beginIndexInclusive, int endIndexExclusive) {
        if (text == null) {
            return text;
        } else {
            if (beginIndexInclusive < 0) {
                beginIndexInclusive = 0;
            }
            if (beginIndexInclusive > text.length() && text.length() != 0) {
                beginIndexInclusive = text.length() - 1;
            }
            if (endIndexExclusive > text.length()) {
                endIndexExclusive = text.length();
            }
            if (endIndexExclusive < beginIndexInclusive) {
                endIndexExclusive = beginIndexInclusive;
            }
            return text.substring(beginIndexInclusive, endIndexExclusive);
        }
    }

    public static String trimToEmpty(final String str) {
        return str == null ? "" : str.trim();
    }

    public static String stripToEmpty(final String str) {
        return str == null ? "" : str.strip();
    }

    /**
     * Strips accents from a string
     *
     * @param str the string to clean
     * @return the string with accents replaced by their non accented equivalent
     */
    public static String stripAccents(final String str) {
        if (str == null) {
            return null;
        }
        final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        final StringBuilder decomposed = new StringBuilder(Normalizer.normalize(str, Normalizer.Form.NFD));
        return pattern.matcher(decomposed).replaceAll("");
    }
}
