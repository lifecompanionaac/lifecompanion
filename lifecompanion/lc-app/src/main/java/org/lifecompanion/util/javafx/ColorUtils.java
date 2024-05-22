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

package org.lifecompanion.util.javafx;

import javafx.scene.paint.Color;

import java.util.Locale;

public class ColorUtils {
    private static final int COLOR_MAX_VAL = 255;
    private static final String DEFAULT_CSS_COLOR = "white";
    public static final double WHITE_COLOR_THRESHOLD = 1.0;
    public static final Color COLOR_DARK = Color.web("#181818");

    public static String toCssColor(final Color color) {
        if (color != null) {
            return new StringBuilder(30).append("rgba(").append((int) (color.getRed() * COLOR_MAX_VAL)).append(",")
                    .append((int) (color.getGreen() * COLOR_MAX_VAL)).append(",").append((int) (color.getBlue() * COLOR_MAX_VAL))
                    .append(",").append(color.getOpacity()).append(")").toString();
        } else {
            return DEFAULT_CSS_COLOR;
        }
    }

    public static String toWebColorWithoutAlpha(final Color c) {
        if (c != null) {
            return String.format((Locale) null, "#%02x%02x%02x",
                    Math.round(c.getRed() * 255),
                    Math.round(c.getGreen() * 255),
                    Math.round(c.getBlue() * 255));
        } else {
            return null;
        }
    }

    public static String toWebColorWithAlpha(final Color c) {
        if (c != null) {
            return String.format((Locale) null, "#%02x%02x%02x%02x",
                    Math.round(c.getRed() * 255),
                    Math.round(c.getGreen() * 255),
                    Math.round(c.getBlue() * 255),
                    Math.round(c.getOpacity() * 255)
            );
        } else {
            return null;
        }
    }

    public static Color fromWebColor(String webColor) {
        try {
            if (webColor.length() >= 8) {
                return Color.rgb(
                        Integer.valueOf(webColor.substring(1, 3), 16),
                        Integer.valueOf(webColor.substring(3, 5), 16),
                        Integer.valueOf(webColor.substring(5, 7), 16),
                        Integer.valueOf(webColor.substring(7), 16) / 255.0);
            } else {
                return Color.rgb(
                        Integer.valueOf(webColor.substring(1, 3), 16),
                        Integer.valueOf(webColor.substring(3, 5), 16),
                        Integer.valueOf(webColor.substring(5, 7), 16));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect color format, expecting color with #RRGGBB or #RRGGBBAA format");
        }
    }

    /**
     * Compare using web string as double are "to precise" for 0-255 values of colors.
     * Note that it doesn't compare opacity
     */
    public static boolean colorEquals(Color c1, Color c2) {
        if (c1 == c2) return true;
        else if (c1 == null || c2 == null) return false;
        else return toWebColorWithoutAlpha(c1).equals(toWebColorWithoutAlpha(c2));
    }

    public static Color getConstratColor(final Color color) {
        if (color != null && color.getOpacity() >= 0.4 && color.getBlue() + color.getRed() + color.getGreen() < WHITE_COLOR_THRESHOLD) {
            return Color.LIGHTGRAY;
        } else {
            return COLOR_DARK;
        }
    }
}
