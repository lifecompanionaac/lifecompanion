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
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.util.ThreadUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Generate a event after mode start on a specific day
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class DayOfWeekEventGenerator extends BaseUseEventGeneratorImpl {
    private static final long DELAY_ON_START = 4000;

    private final IntegerProperty wantedDay;
    private final UseVariableDefinitionI useVariableWantedDayLabel;

    public DayOfWeekEventGenerator() {
        super();
        this.parameterizableAction = true;
        this.order = 1;
        this.category = DefaultUseEventSubCategories.CYCLIC;
        this.nameID = "use.event.day.of.week.generator.name";
        this.staticDescriptionID = "use.event.day.of.week.generator.static.description";
        this.configIconPath = "time/icon_event_day_of_week.png";
        this.wantedDay = new SimpleIntegerProperty(1);
        this.variableDescriptionProperty()
                .bind(TranslationFX.getTextBinding("use.event.day.of.week.generator.variable.description", Bindings.createStringBinding(() -> {
                    if (this.wantedDay.get() >= Calendar.SUNDAY && this.wantedDay.get() <= Calendar.SATURDAY) {
                        return DayOfWeek.of(this.wantedDay.get()).getDisplayName(TextStyle.FULL, Locale.getDefault());
                    } else {
                        return "";
                    }
                }, this.wantedDay)));
        this.useVariableWantedDayLabel = new UseVariableDefinition("WantedDayDisplayableFormat", "use.variable.day.of.week.displayable.day.name",
                "use.variable.day.of.week.displayable.day.description", "use.variable.day.of.week.displayable.day.example");
        this.generatedVariables.add(this.useVariableWantedDayLabel);
    }

    public IntegerProperty wantedDayProperty() {
        return this.wantedDay;
    }

    private Thread checkCurrentDayThread;

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        checkCurrentDayThread = LCNamedThreadFactory.daemonThreadFactory("DayOfWeekEventGenerator").newThread(() -> {
            ThreadUtils.safeSleep(DayOfWeekEventGenerator.DELAY_ON_START);//Delay on start
            while (checkCurrentDayThread != null) {
                // Check if current day
                if (LocalDate.now().getDayOfWeek().getValue() == this.wantedDay.get()) {
                    this.useEventListener.fireEvent(this, List.of(new StringUseVariable(this.useVariableWantedDayLabel, DayOfWeek.of(this.wantedDay.get()).getDisplayName(TextStyle.FULL, Locale.getDefault()))), null);
                }
                // Wait till next day start
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                ThreadUtils.safeSleep(calendar.getTimeInMillis() - System.currentTimeMillis());
            }
        });
        checkCurrentDayThread.start();
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        if (this.checkCurrentDayThread != null) {
            this.checkCurrentDayThread.interrupt();
            this.checkCurrentDayThread = null;
        }
    }

    @Override
    public Element serialize(final IOContextI context) {
        final Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(DayOfWeekEventGenerator.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(DayOfWeekEventGenerator.class, this, node);
    }
}
