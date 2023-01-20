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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.DuplicableComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.CopyUtils;

import java.util.Map;

public class CalendarDay implements XMLSerializable<IOContextI>, DuplicableComponentI {

    @XMLGenericProperty(DayOfWeek.class)
    private final ObjectProperty<DayOfWeek> dayOfWeek;

    private final ObservableList<CalendarEvent> events;

    public CalendarDay() {
        this.dayOfWeek = new SimpleObjectProperty<>();
        this.events = FXCollections.observableArrayList();
    }

    public ObjectProperty<DayOfWeek> dayOfWeekProperty() {
        return dayOfWeek;
    }

    public ObservableList<CalendarEvent> getEvents() {
        return events;
    }

    @Override
    public DuplicableComponentI duplicate(boolean changeID) {
        return CopyUtils.createDeepCopyViaXMLSerialization(this, false);
    }

    @Override
    public void idsChanged(Map<String, String> changes) {
    }

    @Override
    public Element serialize(IOContextI context) {
        Element dayElement = new Element("CalendarDay");
        XMLObjectSerializer.serializeInto(CalendarDay.class, this, dayElement);
        for (CalendarEvent event : events) {
            dayElement.getChildren().add(event.serialize(context));
        }
        return dayElement;
    }


    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(CalendarDay.class, this, node);
        for (Element child : node.getChildren()) {
            CalendarEvent event = new CalendarEvent();
            event.deserialize(child, context);
            events.add(event);
        }
    }

    @Override
    public String toString() {
        return "CalendarDay{" +
                "dayOfWeek=" + dayOfWeek.get() +
                ", events=" + events.size() +
                '}';
    }
}
