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

package org.lifecompanion.plugin.calendar.model;

import java.util.Calendar;
import java.util.HashMap;

public enum DayOfWeek {
    MONDAY("calendar.plugin.day.of.week.monday", Calendar.MONDAY),
    TUESDAY("calendar.plugin.day.of.week.tuesday", Calendar.TUESDAY),
    WEDNESDAY("calendar.plugin.day.of.week.wednesday", Calendar.WEDNESDAY),
    THURSDAY("calendar.plugin.day.of.week.thursday", Calendar.THURSDAY),
    FRIDAY("calendar.plugin.day.of.week.friday", Calendar.FRIDAY),
    SATURDAY("calendar.plugin.day.of.week.saturday", Calendar.SATURDAY),
    SUNDAY("calendar.plugin.day.of.week.sunday", Calendar.SUNDAY);

    private final String translationId;
    private final int calendarFieldValue;
    private static HashMap<Integer, DayOfWeek> perCalendarId;

    DayOfWeek(String translationId, int calendarFieldValue) {
        this.translationId = translationId;
        this.calendarFieldValue = calendarFieldValue;
    }

    public String getTranslationId() {
        return translationId;
    }

    public static DayOfWeek current() {
        if (perCalendarId == null) {
            perCalendarId = new HashMap<>();
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                perCalendarId.put(dayOfWeek.calendarFieldValue, dayOfWeek);
            }
        }
        return perCalendarId.get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    }
}
