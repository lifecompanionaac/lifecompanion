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

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

import org.jdom2.Element;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VariableValueChangedEventGenerator extends BaseUseEventGeneratorImpl {
	private static final Logger LOGGER = LoggerFactory.getLogger(VariableValueChangedEventGenerator.class);

	private StringProperty variableName;
	private IntegerProperty delayBetweenFire;
	private UseVariableDefinitionI useVariableNewVariableValue;

	public VariableValueChangedEventGenerator() {
		super();
		this.variableName = new SimpleStringProperty();
		this.delayBetweenFire = new SimpleIntegerProperty(1000);
		this.parameterizableAction = true;
		this.order = 1;
		this.category = DefaultUseEventSubCategories.VARIABLE;
		this.nameID = "use.event.configuration.variable.value.changed.name";
		this.staticDescriptionID = "use.event.configuration.variable.value.changed.description";
		this.configIconPath = "configuration/icon_variable_changed.png";
		//TODO : old variable value ?
		this.useVariableNewVariableValue = new UseVariableDefinition("NewVariableValue", "use.variable.new.variable.value.name",
				"use.variable.new.variable.value.description", "use.variable.new.variable.value.example");
		this.generatedVariables.add(useVariableNewVariableValue);
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	public StringProperty variableNameProperty() {
		return this.variableName;
	}

	public IntegerProperty delayBetweenFireProperty() {
		return this.delayBetweenFire;
	}

	// Class part : "Mode start/stop"
	//========================================================================
	private Consumer<Map<String, UseVariableI<?>>> variableUpdateListener;
	private Object previousValue;
	private long previousChangeTime;

	@Override
	public void modeStart(final LCConfigurationI configuration) {
		if (variableName.get() != null && !variableName.get().trim().isEmpty()) {
			final String variableId = variableName.get();
			this.variableUpdateListener = variables -> {
				UseVariableI<?> useVariableI = variables.get(variableId);
				if (useVariableI != null) {
					Object value = useVariableI.getValue();
					if (value != null && !value.equals(previousValue)) {
						if (System.currentTimeMillis() - previousChangeTime > this.delayBetweenFire.get()) {
							this.previousChangeTime = System.currentTimeMillis();
							this.previousValue = value;
							this.useEventListener.fireEvent(this,
									Arrays.asList(new StringUseVariable(this.useVariableNewVariableValue, value.toString())), null);
						}
					}
				} else {
					LOGGER.warn("Didn't find any use variable for id {}", variableId);
				}
			};
			UseVariableController.INSTANCE.addVariableUpdateListener(variableUpdateListener);
		}
	}

	@Override
	public void modeStop(final LCConfigurationI configuration) {
		this.previousChangeTime = -1;
		this.previousValue = null;
		if (this.variableUpdateListener != null) {
			UseVariableController.INSTANCE.removeVariableUpdateListener(variableUpdateListener);
			this.variableUpdateListener = null;
		}
	}
	//========================================================================

	// Class part : "IO"
	//========================================================================
	@Override
	public Element serialize(final IOContextI context) {
		final Element element = super.serialize(context);
		XMLObjectSerializer.serializeInto(VariableValueChangedEventGenerator.class, this, element);
		return element;
	}

	@Override
	public void deserialize(final Element node, final IOContextI context) throws LCException {
		super.deserialize(node, context);
		XMLObjectSerializer.deserializeInto(VariableValueChangedEventGenerator.class, this, node);
	}
	//========================================================================

}
