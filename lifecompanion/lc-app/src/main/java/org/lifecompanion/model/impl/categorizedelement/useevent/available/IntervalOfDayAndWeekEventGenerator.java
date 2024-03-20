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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.configurationcomponent.TimeOfDay;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.util.ThreadUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Generate a event when a hour in the day is reached.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class IntervalOfDayAndWeekEventGenerator extends BaseUseEventGeneratorImpl {
    private static final long DELAY_ON_START = 4000;
    private static final long DELAY_ON_NEW_DAY = 4000;

    private final TimeOfDay startTimeOfDay;
    private final TimeOfDay endTimeOfDay;
    private final IntegerProperty wantedDay;

    private final UseVariableDefinitionI useVariableWantedStartHour;
    private final UseVariableDefinitionI useVariableWantedEndHour;
    private final UseVariableDefinitionI useVariableWantedDayLabel;


    public IntervalOfDayAndWeekEventGenerator() {
        super();
        this.parameterizableAction = true;
        this.order = 0;
        this.category = DefaultUseEventSubCategories.CYCLIC;
        this.nameID = "use.event.interval.of.day.and.week.generator.name";
        this.staticDescriptionID = "use.event.interval.of.day.and.week.generator.static.description";
        this.configIconPath = "time/icon_time_of_day_and_week_generator.png";
        this.startTimeOfDay = new TimeOfDay();
        this.endTimeOfDay = new TimeOfDay();
        this.endTimeOfDay.hoursProperty().set(13);
        this.wantedDay = new SimpleIntegerProperty(1);
        this.variableDescriptionProperty()
                .bind(
                        TranslationFX.getTextBinding("use.event.interval.of.day.and.week.generator.variable.description",
                                Bindings.createStringBinding(() -> {
                                if (this.wantedDay.get() >= Calendar.SUNDAY && this.wantedDay.get() <= Calendar.SATURDAY) {
                                    return DayOfWeek.of(this.wantedDay.get()).getDisplayName(TextStyle.FULL, Locale.getDefault());
                                } else {
                                    return "";
                                }
                            }, this.wantedDay),
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
        this.useVariableWantedDayLabel = new UseVariableDefinition("WantedDayDisplayableFormat", "use.variable.day.of.week.displayable.day.name",
                "use.variable.day.of.week.displayable.day.description", "use.variable.day.of.week.displayable.day.example");
        this.generatedVariables.add(this.useVariableWantedDayLabel);

    }

    public TimeOfDay getStartTimeOfDay() {
        return startTimeOfDay;
    }

    public TimeOfDay getEndTimeOfDay() {
        return endTimeOfDay;
    }

    public IntegerProperty wantedDayProperty() {
        return this.wantedDay;
    }

    private Thread checkCurrentDayThread;

    // Class part : "Mode start/stop"
    // ========================================================================
    private Timer timer;

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        checkCurrentDayThread = LCNamedThreadFactory.daemonThreadFactory("DayOfWeekEventGenerator").newThread(() -> {
            ThreadUtils.safeSleep(IntervalOfDayAndWeekEventGenerator.DELAY_ON_START);//Delay on start
            while (checkCurrentDayThread != null) {
                // Check if current day
                if (LocalDate.now().getDayOfWeek().getValue() == this.wantedDay.get()) {
                    // Change implementation to only instantiate one timer
                    this.timer = new Timer(true);
                    this.timer.scheduleAtFixedRate(new TimerTask() {
                        private boolean generated = false;

                        @Override
                        public void run() {
                            TimeOfDay nowTimeOfDay = TimeOfDay.now();
                            if ( IntervalOfDayAndWeekEventGenerator.this.useEventListener != null
                                    && !this.generated
                                    && IntervalOfDayAndWeekEventGenerator.this.startTimeOfDay.compareTo(nowTimeOfDay) <= 0
                                    && IntervalOfDayAndWeekEventGenerator.this.endTimeOfDay.compareTo(nowTimeOfDay) >= 0
                            ) {
                                this.generated = true;
                                IntervalOfDayAndWeekEventGenerator.this.useEventListener
                                        .fireEvent(IntervalOfDayAndWeekEventGenerator.this,
                                                List.of(
                                                        new StringUseVariable(IntervalOfDayAndWeekEventGenerator.this.useVariableWantedStartHour,
                                                                Translation.getText("use.event.interval.of.day.generator.hour.format",
                                                                        IntervalOfDayAndWeekEventGenerator.this.startTimeOfDay.getHumanReadableString())),
                                                        new StringUseVariable(IntervalOfDayAndWeekEventGenerator.this.useVariableWantedEndHour,
                                                                Translation.getText("use.event.interval.of.day.generator.hour.format",
                                                                        IntervalOfDayAndWeekEventGenerator.this.endTimeOfDay.getHumanReadableString()))),
                                                null);
                            } else if (
                                    IntervalOfDayAndWeekEventGenerator.this.startTimeOfDay.compareTo(nowTimeOfDay) > 0
                            ) {
                                this.generated = false;
                            } else if (
                                    IntervalOfDayAndWeekEventGenerator.this.endTimeOfDay.compareTo(nowTimeOfDay) < 0
                            ) {
                                this.generated = false;
                                nextDay();
                            }
                        }
                    }, DELAY_BEFORE_GENERATE_MS, 1000);
               } else {
                    nextDay();
                }
            }
        });
        checkCurrentDayThread.start();
    }

    private void nextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        ThreadUtils.safeSleep(calendar.getTimeInMillis() - System.currentTimeMillis() + IntervalOfDayAndWeekEventGenerator.DELAY_ON_NEW_DAY);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        if ( this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        if (this.checkCurrentDayThread != null) {
            this.checkCurrentDayThread.interrupt();
            this.checkCurrentDayThread = null;
        }
    }
    // ========================================================================

    // Class part : "IO"
    // ========================================================================
    public static final String START_TIME_OF_DAY_NODE_NAME = "StartTimeOfDay";
    public static final String END_TIME_OF_DAY_NODE_NAME = "EndTimeOfDay";

    @Override
    public Element serialize(final IOContextI context) {
        final Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(IntervalOfDayAndWeekEventGenerator.class, this, element);
        element.addContent(XMLObjectSerializer.serializeInto(TimeOfDay.class, startTimeOfDay, new Element(IntervalOfDayAndWeekEventGenerator.START_TIME_OF_DAY_NODE_NAME)));
        element.addContent(XMLObjectSerializer.serializeInto(TimeOfDay.class, endTimeOfDay, new Element(IntervalOfDayAndWeekEventGenerator.END_TIME_OF_DAY_NODE_NAME)));
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(IntervalOfDayAndWeekEventGenerator.class, this, node);
        Element startTimeOfDayNode = node.getChild(IntervalOfDayAndWeekEventGenerator.START_TIME_OF_DAY_NODE_NAME);
        if (startTimeOfDayNode != null) {
            this.startTimeOfDay.deserialize(startTimeOfDayNode, context);
        }
        Element endTimeOfDayNode = node.getChild(IntervalOfDayAndWeekEventGenerator.END_TIME_OF_DAY_NODE_NAME);
        if (endTimeOfDayNode != null) {
            this.endTimeOfDay.deserialize(endTimeOfDayNode, context);
        }

    }
    // ========================================================================

}
