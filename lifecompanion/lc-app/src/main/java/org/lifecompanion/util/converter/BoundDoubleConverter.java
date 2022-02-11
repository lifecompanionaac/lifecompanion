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

package org.lifecompanion.util.converter;

import java.text.DecimalFormat;
import java.text.ParseException;

import javafx.util.StringConverter;
import org.lifecompanion.util.LCUtils;

/**
 * Converter to convert a input double between two given bounds.</br>
 * If input is not correct, set to min value.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class BoundDoubleConverter extends StringConverter<Double> {
	// This will use the default Locale for decimal separator (e.g. ',' for French)
	private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("#.##");
	private final double min, max;

	public BoundDoubleConverter(double min, double max) {
		super();
		this.min = min;
		this.max = max;
	}

	@Override
	public Double fromString(String text) {
		double val;
		try {
			val = LCUtils.toBoundDouble(DOUBLE_DECIMAL_FORMAT.parse(text).doubleValue(), min, max);
		} catch (ParseException e) {
			val = min;
		}
		return val;
	}

	@Override
	public String toString(Double n) {
		if (n != null)
			return DOUBLE_DECIMAL_FORMAT.format(n);
		return "";
	}
}
