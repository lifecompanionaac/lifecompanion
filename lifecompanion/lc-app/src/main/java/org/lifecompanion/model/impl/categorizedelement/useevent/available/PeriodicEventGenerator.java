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
import org.lifecompanion.model.impl.usevariable.LongUseVariable;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.impl.configurationcomponent.DurationUnitEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PeriodicEventGenerator extends BaseUseEventGeneratorImpl {

    private final UseVariableDefinitionI periodDefinition;
    private final UseVariableDefinitionI repetitionDefinition;
    private final IntegerProperty periodInMS;
    private long numberOfRepetitions;

    public PeriodicEventGenerator() {
        super();
        this.numberOfRepetitions = 0;
        this.parameterizableAction = true;
        this.order = 0;
        this.category = DefaultUseEventSubCategories.PERIODIC;
		this.periodInMS = new SimpleIntegerProperty(100);
        this.nameID = "use.event.periodic.time.name";
        this.staticDescriptionID = "use.event.periodic.time.static.description";
        this.configIconPath = "time/icon_time_of_day_generator.png";
        this.periodDefinition = new UseVariableDefinition("PeriodicEventPeriod", "use.variable.periodic.time.period.name",
                "use.variable.periodic.time.period.description", "use.variable.periodic.time.period.example");
        this.repetitionDefinition = new UseVariableDefinition("PeriodicEventNumberOfGeneratedEvents", "use.variable.periodic.time.number.of.generated.event.name",
                "use.variable.periodic.time.number.of.generated.event.description", "use.variable.periodic.time.number.of.generated.event.example");
        this.generatedVariables.add(this.periodDefinition);
        this.generatedVariables.add(this.repetitionDefinition);
        this.variableDescriptionProperty()
				.bind(
						TranslationFX.getTextBinding("use.event.periodic.time.variable.description", Bindings.createStringBinding(() -> {
                            return this.getFormattedPeriodString();
                    }, this.periodInMS)));
    }

    public IntegerProperty periodInMSProperty() {
        return this.periodInMS;
    }

    private String getFormattedPeriodString() {
        int breakHandler = 0;
        String formattedString = "";
        ArrayList<DurationUnitEnum> durationUnitsInOrder = DurationUnitEnum.getUnitsInOrder();
        int periodValue = periodInMS.get();
        for (DurationUnitEnum durationUnitEnum : durationUnitsInOrder) {
            int intMsRatio = (int) durationUnitEnum.getToMsRatio();
            int truncatedValueInUnit = periodValue/intMsRatio;
            periodValue -= truncatedValueInUnit*intMsRatio;
            breakHandler += breakHandler*10;
            if (truncatedValueInUnit > 0) {
                formattedString += durationUnitEnum.getFormat().format(truncatedValueInUnit) + durationUnitEnum.getSymbol();
                breakHandler += 1;
            }
            if (breakHandler > 9) {break;}
        }
        return formattedString;
    }

    // Class part : "Mode start/stop"
    //========================================================================
	private Timer timer;

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.timer = new Timer(true);
		this.timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
                PeriodicEventGenerator.this.numberOfRepetitions += 1;
				PeriodicEventGenerator.this.useEventListener
							.fireEvent(PeriodicEventGenerator.this,
									Arrays.asList(
											new StringUseVariable(PeriodicEventGenerator.this.periodDefinition,
													Translation.getText("use.variable.periodic.time.period.generator.hour.format",
                                                    PeriodicEventGenerator.this.getFormattedPeriodString())),
                                            new LongUseVariable(PeriodicEventGenerator.this.repetitionDefinition,
                                                    PeriodicEventGenerator.this.numberOfRepetitions)),
									null);
			}
		}, 100, this.periodInMS.get());
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
