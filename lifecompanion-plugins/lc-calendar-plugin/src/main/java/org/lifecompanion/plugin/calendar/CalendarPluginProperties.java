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
package org.lifecompanion.plugin.calendar;

import javafx.beans.property.ObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.plugin.AbstractPluginConfigProperties;
import org.lifecompanion.plugin.calendar.model.LCCalendar;

import java.util.Map;

public class CalendarPluginProperties extends AbstractPluginConfigProperties {

    private static final String NODE_NAME = "CalendarPluginProperties";

    private LCCalendar calendar;

    protected CalendarPluginProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        super(parentConfiguration);
        calendar = new LCCalendar();
    }

    public LCCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(LCCalendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element element = new Element(NODE_NAME);
        element.addContent(calendar.serialize(context));
        return element;
    }


    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        calendar.deserialize(node.getChildren().get(0), context);
    }

    @Override
    public void serializeUseInformation(Map<String, Element> elements) {
    }

    @Override
    public void deserializeUseInformation(Map<String, Element> elements) throws LCException {
    }
}
