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

import javafx.util.StringConverter;

import java.text.DecimalFormat;

/**
 * Converter to convert a input int between two given bounds.</br>
 * If input is not correct, set to min value.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class BoundIntConverter extends StringConverter<Integer> {
    private final int min, max;
    private final DecimalFormat format;

    public BoundIntConverter(int min, int max) {
        this(min, max, null);
    }

    public BoundIntConverter(int min, int max, DecimalFormat format) {
        super();
        this.min = min;
        this.max = max;
        this.format = format;
    }

    @Override
    public Integer fromString(String text) {
        int val;
        try {
            val = LCUtils.toBoundInt(Integer.parseInt(text), min, max);
        } catch (NumberFormatException nfe) {
            val = min;
        }
        return val;
    }

    @Override
    public String toString(Integer n) {
        if (n != null)
            return format != null ? format.format(n) : String.valueOf(n);
        return "";
    }
}
