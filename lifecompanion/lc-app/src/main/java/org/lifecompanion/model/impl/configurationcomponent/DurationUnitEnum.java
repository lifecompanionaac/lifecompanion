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

package org.lifecompanion.model.impl.configurationcomponent;

import org.lifecompanion.framework.commons.translation.Translation;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple class to store the different units for a duration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum DurationUnitEnum {
    MILLISECOND("duration.unit.millisecond", "ms", 1.0),
    SECOND("duration.unit.second", "s", 1000.0),
    MINUTE("duration.unit.minute", "m", 60_000.0),
    HOUR("duration.unit.hour", "h", 3600_000.0);

    private static final DecimalFormat DECIMAL_FORMAT_TIME = new DecimalFormat("#.##");

    private final String translationId;
    private final String symbol;
    private final double toMsRatio;

    DurationUnitEnum(String translationId, String symbol, double toMsRatio) {
        this.translationId = translationId;
        this.toMsRatio = toMsRatio;
        this.symbol = symbol;
    }

    /**
     * Returns a list of the duration units in descending order.
     */
    public static ArrayList<DurationUnitEnum> getUnitsInOrder() {
        ArrayList<DurationUnitEnum> durationUnitsInOrder = new ArrayList<DurationUnitEnum>();
        durationUnitsInOrder.add(DurationUnitEnum.HOUR);
        durationUnitsInOrder.add(DurationUnitEnum.MINUTE);
        durationUnitsInOrder.add(DurationUnitEnum.SECOND);
        durationUnitsInOrder.add(DurationUnitEnum.MILLISECOND);
        return durationUnitsInOrder;
    }

    public static String getBestFormattedTime(long timeInMs) {
        for (int i = values().length - 1; i >= 0; i--) {
            if (timeInMs / DurationUnitEnum.values()[i].toMsRatio >= 1.0) {
                return DECIMAL_FORMAT_TIME.format(timeInMs / DurationUnitEnum.values()[i].toMsRatio) + " " + DurationUnitEnum.values()[i].getTranslatedName();
            }
        }
        return null;
    }

    public double getToMsRatio() {
        return this.toMsRatio;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getTranslatedName() {
        return Translation.getText(translationId);
    }
}