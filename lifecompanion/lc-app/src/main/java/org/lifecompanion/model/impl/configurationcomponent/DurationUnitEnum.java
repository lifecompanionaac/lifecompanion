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

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.lifecompanion.framework.commons.translation.Translation;

/**
 * A simple class to store the different units for a duration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum DurationUnitEnum {
    MILLISECOND("duration.unit.millisecond", "ms", 1.0, "000"),
    SECOND("duration.unit.second", "s", 1000.0, "00"),
    MINUTE("duration.unit.minute", "mn", 60_000.0, "00"),
    HOUR("duration.unit.hour", "h", 3600_000.0, "");

    private final String translationId;
    private final String symbol;
    private final double toMsRatio;
    private final DecimalFormat format;

    DurationUnitEnum(String translationId, String symbol, double toMsRatio, String formatString) {
        this.translationId = translationId;
        this.toMsRatio = toMsRatio;
        this.symbol = symbol;
        this.format = new DecimalFormat(formatString);
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

    public DecimalFormat getFormat() {
        return this.format;
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