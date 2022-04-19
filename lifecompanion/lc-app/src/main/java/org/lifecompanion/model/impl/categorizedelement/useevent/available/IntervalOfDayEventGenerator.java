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

import javafx.beans.binding.Bindings;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.configurationcomponent.TimeOfDay;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Generate a event when a hour in the day is reached.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class IntervalOfDayEventGenerator extends BaseUseEventGeneratorImpl {

    private final TimeOfDay startTimeOfDay;
    private final TimeOfDay endTimeOfDay;
    private final UseVariableDefinitionI useVariableWantedStartHour;
    private final UseVariableDefinitionI useVariableWantedEndHour;

    public IntervalOfDayEventGenerator() {
        super();
        this.parameterizableAction = true;
        this.order = 0;
        this.category = DefaultUseEventSubCategories.CYCLIC;
        this.nameID = "use.event.interval.of.day.generator.name";
        this.staticDescriptionID = "use.event.interval.of.day.generator.static.description";
        this.configIconPath = "time/icon_time_of_day_generator.png";
        this.startTimeOfDay = new TimeOfDay();
        this.endTimeOfDay = new TimeOfDay();
        this.endTimeOfDay.hoursProperty().set(13);
        this.variableDescriptionProperty()
                .bind(
                        TranslationFX.getTextBinding("use.event.interval.of.day.generator.variable.description",
                                Bindings.createStringBinding(this.startTimeOfDay::getHumanReadableString,
                                        this.startTimeOfDay.hoursProperty(), this.startTimeOfDay.minutesProperty()),
                                Bindings.createStringBinding(this.endTimeOfDay::getHumanReadableString,
                                        this.endTimeOfDay.hoursProperty(), this.endTimeOfDay.minutesProperty())));
        this.useVariableWantedStartHour = new UseVariableDefinition("WantedStartHourDisplayableFormat",
                "use.variable.interval.of.day.displayable.start.hour.name",
                "use.variable.interval.of.day.displayable.start.hour.description",
                "use.variable.interval.of.day.displayable.start.hour.example");
        this.generatedVariables.add(this.useVariableWantedStartHour);
        this.useVariableWantedEndHour = new UseVariableDefinition("WantedEndHourDisplayableFormat",
                "use.variable.interval.of.day.displayable.end.hour.name",
                "use.variable.interval.of.day.displayable.end.hour.description",
                "use.variable.interval.of.day.displayable.end.hour.example");
        this.generatedVariables.add(this.useVariableWantedEndHour);
    }

    public TimeOfDay getStartTimeOfDay() {
        return startTimeOfDay;
    }

    public TimeOfDay getEndTimeOfDay() {
        return endTimeOfDay;
    }

    // Class part : "Mode start/stop"
    // ========================================================================
    private Timer timer;

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        // Change implementation to only instantiate one timer
        this.timer = new Timer(true);
        this.timer.scheduleAtFixedRate(new TimerTask() {
            private boolean generated = false;

            @Override
            public void run() {
                TimeOfDay nowTimeOfDay = TimeOfDay.now();
                if (!this.generated
                        && IntervalOfDayEventGenerator.this.startTimeOfDay.compareTo(nowTimeOfDay) <= 0
                        && IntervalOfDayEventGenerator.this.endTimeOfDay.compareTo(nowTimeOfDay) >= 0
                ) {
                    this.generated = true;
                    IntervalOfDayEventGenerator.this.useEventListener
                            .fireEvent(IntervalOfDayEventGenerator.this,
                                    List.of(
                                            new StringUseVariable(IntervalOfDayEventGenerator.this.useVariableWantedStartHour,
                                                    Translation.getText("use.event.interval.of.day.generator.hour.format",
                                                            IntervalOfDayEventGenerator.this.startTimeOfDay.getHumanReadableString())),
                                            new StringUseVariable(IntervalOfDayEventGenerator.this.useVariableWantedEndHour,
                                                    Translation.getText("use.event.interval.of.day.generator.hour.format",
                                                            IntervalOfDayEventGenerator.this.endTimeOfDay.getHumanReadableString()))),
                                    null);
                } else if (
                        IntervalOfDayEventGenerator.this.startTimeOfDay.compareTo(nowTimeOfDay) > 0
                                || IntervalOfDayEventGenerator.this.endTimeOfDay.compareTo(nowTimeOfDay) < 0
                ) {
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
    // ========================================================================

    // Class part : "IO"
    // ========================================================================
    public static final String START_TIME_OF_DAY_NODE_NAME = "StartTimeOfDay";
    public static final String END_TIME_OF_DAY_NODE_NAME = "EndTimeOfDay";

    @Override
    public Element serialize(final IOContextI context) {
        final Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(IntervalOfDayEventGenerator.class, this, element);
        element.addContent(XMLObjectSerializer.serializeInto(TimeOfDay.class, startTimeOfDay, new Element(IntervalOfDayEventGenerator.START_TIME_OF_DAY_NODE_NAME)));
        element.addContent(XMLObjectSerializer.serializeInto(TimeOfDay.class, endTimeOfDay, new Element(IntervalOfDayEventGenerator.END_TIME_OF_DAY_NODE_NAME)));
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(IntervalOfDayEventGenerator.class, this, node);
        Element startTimeOfDayNode = node.getChild(IntervalOfDayEventGenerator.START_TIME_OF_DAY_NODE_NAME);
        if (startTimeOfDayNode != null) {
            this.startTimeOfDay.deserialize(startTimeOfDayNode, context);
        }
        Element endTimeOfDayNode = node.getChild(IntervalOfDayEventGenerator.END_TIME_OF_DAY_NODE_NAME);
        if (endTimeOfDayNode != null) {
            this.endTimeOfDay.deserialize(endTimeOfDayNode, context);
        }

    }
    // ========================================================================

}
