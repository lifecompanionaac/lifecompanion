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
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import org.lifecompanion.controller.configurationcomponent.GlobalKeyEventController;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.configurationcomponent.TimeOfDay;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PeriodicEventGenerator extends BaseUseEventGeneratorImpl {

    private final UseVariableDefinitionI periodDefinition;
    private final IntegerProperty hourPeriod, minutePeriod;
    private final TimeOfDay timeOfDay;

    public PeriodicEventGenerator() {
        super();
        this.parameterizableAction = false;
        this.order = 0;
        this.category = DefaultUseEventSubCategories.PERIODIC;
        this.timeOfDay = new TimeOfDay();
        this.hourPeriod = timeOfDay.hourProperty();
		this.minutePeriod = timeOfDay.minuteProperty();
        this.hourPeriod.set(1);
        this.minutePeriod.set(30);
        this.nameID = "use.event.periodic.time.name";
        this.staticDescriptionID = "use.event.periodic.time.static.description";
        this.configIconPath = "time/icon_time_of_day_generator.png";
        this.periodDefinition = new UseVariableDefinition("PeriodicEventPeriod", "use.variable.periodic.time.period.name",
                "use.variable.periodic.time.period.description", "use.variable.periodic.time.period.example");
        this.generatedVariables.add(this.periodDefinition);
        this.variableDescriptionProperty()
				.bind(
						TranslationFX.getTextBinding("use.event.periodic.time.variable.description", Bindings.createStringBinding(() -> {
								return this.timeOfDay.getHumanReadableString();
						}, this.hourPeriod, this.minutePeriod)));
    }

    public IntegerProperty hourPeriodProperty() {
		return this.hourPeriod;
	}

	public IntegerProperty minutePeriodProperty() {
		return this.minutePeriod;
	}

    // Class part : "Mode start/stop"
    //========================================================================
	private Timer timer;

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.timer = new Timer(true);
        long periodInMS = ((long) this.hourPeriod.getValue())*3600000 + ((long) this.minutePeriod.getValue())*60000;
		this.timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				PeriodicEventGenerator.this.useEventListener
							.fireEvent(PeriodicEventGenerator.this,
									Arrays.asList(
											new StringUseVariable(PeriodicEventGenerator.this.periodDefinition,
													Translation.getText("use.variable.periodic.time.period.generator.hour.format",
                                                    PeriodicEventGenerator.this.timeOfDay.getHumanReadableString()))),
									null);
			}
		}, 100, periodInMS);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
		this.timer.cancel();
    }
    //========================================================================

    // Class part : "IO"
    //========================================================================
    @Override
    public Element serialize(final IOContextI context) {
        final Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(PeriodicEventGenerator.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(PeriodicEventGenerator.class, this, node);
    }
    //========================================================================

}
