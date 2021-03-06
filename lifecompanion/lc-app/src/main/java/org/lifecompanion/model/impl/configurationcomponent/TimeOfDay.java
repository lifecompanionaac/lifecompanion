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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.impl.exception.LCException;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeOfDay implements XMLSerializable<IOContextI>, Comparable<TimeOfDay> {
    private static final DecimalFormat HOUR_FORMAT = new DecimalFormat("#0");
    private static final DecimalFormat MINUTE_FORMAT = new DecimalFormat("00");
    private final IntegerProperty hours;
    private final IntegerProperty minutes;

    public TimeOfDay() {
        this.hours = new SimpleIntegerProperty(12);
        this.minutes = new SimpleIntegerProperty(0);
    }

    public IntegerProperty hoursProperty() {
        return hours;
    }

    public IntegerProperty minutesProperty() {
        return minutes;
    }

    public static final String NODE_NAME = "TimeOfDay";

    @Override
    public Element serialize(IOContextI context) {
        return XMLObjectSerializer.serializeInto(TimeOfDay.class, this, new Element(NODE_NAME));
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(TimeOfDay.class, this, node);
    }

    public static TimeOfDay now() {
        final Calendar jCalendar = Calendar.getInstance();
        int hour = jCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = jCalendar.get(Calendar.MINUTE);
        TimeOfDay now = new TimeOfDay();
        now.hours.set(hour);
        now.minutes.set(minute);
        return now;
    }

    public Date getDateForToday() {
        final Calendar jCalendar = Calendar.getInstance();
        jCalendar.set(Calendar.HOUR_OF_DAY, hours.get());
        jCalendar.set(Calendar.MINUTE, minutes.get());
        jCalendar.set(Calendar.SECOND, 0);
        return jCalendar.getTime();
    }

    @Override
    public int compareTo(TimeOfDay o) {
        int hc = Integer.compare(this.hours.get(), o.hours.get());
        return hc != 0 ? hc : Integer.compare(this.minutes.get(), o.minutes.get());
    }

    @Override
    public String toString() {
        return "TimeOfDay{" +
                "hours=" + hours.get() +
                ", minutes=" + minutes.get() +
                '}';
    }

    public String getHumanReadableString() {
        return HOUR_FORMAT.format(hours.get()) + "h" + MINUTE_FORMAT.format(minutes.get());
    }
}
