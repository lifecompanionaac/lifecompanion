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

package org.lifecompanion.util;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class LangUtils {
    public static int nullToZero(final Integer i) {
        return i != null ? i : 0;
    }

    public static double nullToZero(final Double d) {
        return d != null ? d : 0;
    }

    public static int nullToZeroInt(final Number n) {
        return n != null ? n.intValue() : 0;
    }

    public static String nullToEmpty(final String str) {
        return str != null ? str : "";
    }

    public static double toBoundDouble(final double n, double min, double max) {
        return Math.max(min, Math.min(max, n));
    }

    public static int toBoundInt(final int n, int min, int max) {
        return Math.max(min, Math.min(max, n));
    }

    public static double nullToZeroDouble(final Number n) {
        return n != null ? n.doubleValue() : 0;
    }

    public static boolean isTrue(final Boolean b) {
        return b != null && b;
    }

    public static boolean safeEquals(final Object o1, final Object o2) {
        return Objects.equals(o1, o2);
    }

    public static boolean nullToFalse(Boolean value) {
        return value != null ? value : false;
    }

    public static String safeTrimToEmpty(final String str) {
        return str != null ? str.trim() : "";
    }

    public static boolean safeParseBoolean(String str) {
        return Boolean.parseBoolean(StringUtils.stripToEmpty(str));
    }

    public static Integer safeParseInt(String str) {
        try {
            return Integer.parseInt(StringUtils.stripToEmpty(str));
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static Double safeParseDouble(String str) {
        try {
            return Double.parseDouble(StringUtils.stripToEmpty(str));
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static boolean isEgalsTo(final int toTest, final int value, final int threshold) {
        return toTest >= value - threshold && toTest <= value + threshold;
    }

    /**
     * Round a double with 3 decimal
     *
     * @param value the value to round
     * @return rounded value
     */
    public static double tolerantRound(final double value) {
        return tolerantRound(value, 3);
    }

    public static double tolerantRound(final double value, final int scale) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static <T> void consumeEachIn(List<? extends T> list, Consumer<T> consumer) {
        if (consumer != null && list != null) {
            for (T item : list) {
                consumer.accept(item);
            }
        }
    }
}
