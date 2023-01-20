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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.DuplicableComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.CopyUtils;

import java.util.Map;
import java.util.stream.Collectors;

public class LCCalendar implements XMLSerializable<IOContextI>, DuplicableComponentI {
    private final ObservableList<CalendarDay> days;
    private final ObservableList<CalendarLeisure> availableLeisure;
    private final IntegerProperty maxAlarmRepeatTimeMs, repeatAlarmIntervalTimeMs;

    public LCCalendar() {
        days = FXCollections.observableArrayList();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            final CalendarDay calendarDay = new CalendarDay();
            calendarDay.dayOfWeekProperty().set(dayOfWeek);
            days.add(calendarDay);
        }
        availableLeisure = FXCollections.observableArrayList();
        maxAlarmRepeatTimeMs = new SimpleIntegerProperty(1000 * 60 * 5); // 5 minute
        repeatAlarmIntervalTimeMs = new SimpleIntegerProperty(1000 * 20); // 20 second
    }

    public ObservableList<CalendarDay> getDays() {
        return days;
    }

    public ObservableList<CalendarLeisure> getAvailableLeisure() {
        return availableLeisure;
    }

    public IntegerProperty maxAlarmRepeatTimeMsProperty() {
        return maxAlarmRepeatTimeMs;
    }

    public IntegerProperty repeatAlarmIntervalTimeMsProperty() {
        return repeatAlarmIntervalTimeMs;
    }

    @Override
    public DuplicableComponentI duplicate(boolean changeID) {
        return CopyUtils.createDeepCopyViaXMLSerialization(this, false);
    }

    @Override
    public void idsChanged(Map<String, String> changes) {
    }

    private static final String NODE_CALENDAR = "LCCalendar", NODE_DAYS = "CalendarDays", NODE_LEISURE = "CalendarLeisure";

    @Override
    public Element serialize(IOContextI context) {
        Element calendarElement = XMLObjectSerializer.serializeInto(LCCalendar.class, this, new Element(NODE_CALENDAR));
        ConfigurationComponentIOHelper.addTypeAlias(this, calendarElement, context);
        Element calendarDays = new Element(NODE_DAYS);
        for (CalendarDay day : days) {
            calendarDays.addContent(day.serialize(context));
        }
        Element calendarLeisure = new Element(NODE_LEISURE);
        for (CalendarLeisure leisure : availableLeisure) {
            calendarLeisure.addContent(leisure.serialize(context));
        }
        calendarElement.addContent(calendarDays);
        calendarElement.addContent(calendarLeisure);
        return calendarElement;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(LCCalendar.class, this, node);
        final Element daysElement = node.getChild(NODE_DAYS);
        if (daysElement != null && !daysElement.getChildren().isEmpty()) {
            days.clear();
            for (Element child : daysElement.getChildren()) {
                CalendarDay day = new CalendarDay();
                day.deserialize(child, context);
                days.add(day);
            }
        }
        // FIXME : delete this dev
//        CalendarDay calendarDay = days.stream().filter(d -> d.dayOfWeekProperty().get() == DayOfWeek.WEDNESDAY).findAny().orElse(null);
//        if (calendarDay != null) {
//            days.forEach(day -> {
//                if (day.dayOfWeekProperty().get() != DayOfWeek.WEDNESDAY) {
//                    day.getEvents().clear();
//                    day.getEvents().addAll(calendarDay.getEvents().stream().map(event -> event.duplicate(true)).collect(Collectors.toList()));
//                }
//            });
//        }
        final Element leisureElement = node.getChild(NODE_LEISURE);
        if (leisureElement != null) {
            for (Element child : leisureElement.getChildren()) {
                CalendarLeisure leisure = new CalendarLeisure();
                leisure.deserialize(child, context);
                this.availableLeisure.add(leisure);
            }
        }
    }
}
