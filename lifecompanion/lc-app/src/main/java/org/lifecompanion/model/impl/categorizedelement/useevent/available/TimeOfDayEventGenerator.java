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

package org.lifecompanion.model.impl.categorizedelement.useevent.available;

import java.util.*;

import org.jdom2.Element;

import org.lifecompanion.controller.io.XMLHelper;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.configurationcomponent.TimeOfDay;
import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.binding.Bindings;

/**
 * Generate a event when a hour in the day is reached.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class TimeOfDayEventGenerator extends BaseUseEventGeneratorImpl {

    private final TimeOfDay timeOfDay;
    private final UseVariableDefinitionI useVariableWantedHour;

    public TimeOfDayEventGenerator() {
        super();
        this.parameterizableAction = true;
        this.order = 0;
        this.category = DefaultUseEventSubCategories.CYCLIC;
        this.nameID = "use.event.time.of.day.generator.name";
        this.staticDescriptionID = "use.event.time.of.day.generator.static.description";
        this.configIconPath = "time/icon_time_of_day_generator.png";
        this.timeOfDay = new TimeOfDay();
        this.variableDescriptionProperty()
                .bind(
                        TranslationFX.getTextBinding("use.event.time.of.day.generator.variable.description", Bindings.createStringBinding(this.timeOfDay::getHumanReadableString, this.timeOfDay.hoursProperty(), this.timeOfDay.minutesProperty()))
                );
        this.useVariableWantedHour = new UseVariableDefinition("WantedHourDisplayableFormat", "use.variable.time.of.day.displayable.hour.name",
                "use.variable.time.of.day.displayable.hour.description", "use.variable.time.of.day.displayable.hour.example");
        this.generatedVariables.add(this.useVariableWantedHour);
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    // Class part : "Mode start/stop"
    //========================================================================
    private Timer timer;

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        //TODO : use Thread sleep implementation (see IntervalOfDayAndWeekEventGenerator)
        //Change implementation to only instantiate one timer
        this.timer = new Timer(true);
        this.timer.scheduleAtFixedRate(new TimerTask() {
            private boolean generated = false;

            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int wantedHour = TimeOfDayEventGenerator.this.timeOfDay.hoursProperty().get();
                int wantedMinute = TimeOfDayEventGenerator.this.timeOfDay.minutesProperty().get();
                if (!this.generated && wantedHour == calendar.get(Calendar.HOUR_OF_DAY)
                        && wantedMinute == calendar.get(Calendar.MINUTE)) {
                    this.generated = true;
                    TimeOfDayEventGenerator.this.useEventListener
                            .fireEvent(TimeOfDayEventGenerator.this,
                                    List.of(
                                            new StringUseVariable(TimeOfDayEventGenerator.this.useVariableWantedHour,
                                                    Translation.getText("use.event.time.of.day.generator.hour.format",
                                                            TimeOfDayEventGenerator.this.timeOfDay.getHumanReadableString()))),
                                    null);
                } else if (wantedHour != calendar.get(Calendar.HOUR_OF_DAY)
                        || wantedMinute != calendar.get(Calendar.MINUTE)) {
                    this.generated = false;
                }
            }
        }, DELAY_BEFORE_GENERATE_MS, 1000);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        this.timer.cancel();
        this.timer = null;
    }
    //========================================================================

    // Class part : "IO"
    //========================================================================
    @Override
    public Element serialize(final IOContextI context) {
        final Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(TimeOfDayEventGenerator.class, this, element);
        element.addContent(timeOfDay.serialize(context));
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(TimeOfDayEventGenerator.class, this, node);
        Element timeOfDayNode = node.getChild(TimeOfDay.NODE_NAME);
        if (timeOfDayNode != null) {
            this.timeOfDay.deserialize(timeOfDayNode, context);
        }
        // Backward compatibility
        else if (node.getAttribute("wantedHour") != null && node.getAttribute("wantedMinute") != null) {
            XMLUtils.read(timeOfDay.hoursProperty(), "wantedHour", node);
            XMLUtils.read(timeOfDay.minutesProperty(), "wantedMinute", node);
        }
    }
    //========================================================================

}
