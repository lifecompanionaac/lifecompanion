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

package org.lifecompanion.base.data.useevent.impl.configuration.status;

import org.jdom2.Element;

import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.useevent.category.DefaultUseEventSubCategories;
import org.lifecompanion.base.data.useevent.baseimpl.BaseUseEventGeneratorImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ConfigurationStartedEventGenerator extends BaseUseEventGeneratorImpl {

	private IntegerProperty delay;

	public ConfigurationStartedEventGenerator() {
		super();
		this.delay = new SimpleIntegerProperty(1000);
		this.parameterizableAction = true;
		this.order = -1;
		this.category = DefaultUseEventSubCategories.STATUS;
		this.nameID = "use.event.configuration.started.name";
		this.staticDescriptionID = "use.event.configuration.started.description";
		this.configIconPath = "configuration/icon_configuration_started.png";
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	public IntegerProperty delayProperty() {
		return this.delay;
	}

	// Class part : "Mode start/stop"
	//========================================================================
	/**
	 * This boolean is useful if the configuration is stopped before the event is fired (because delay is greater than the configuration up time)
	 */
	private boolean modeStarted = false;

	@Override
	public void modeStart(final LCConfigurationI configuration) {
		this.modeStarted = true;
		Thread generateStartEventThread = new Thread(() -> {
			LCUtils.safeSleep(this.delay.get());
			if (this.modeStarted) {
				this.useEventListener.fireEvent(this, null, null);
			}
		});
		generateStartEventThread.setDaemon(true);
		generateStartEventThread.start();
	}

	@Override
	public void modeStop(final LCConfigurationI configuration) {
		this.modeStarted = false;
	}
	//========================================================================

	// Class part : "IO"
	//========================================================================
	@Override
	public Element serialize(final IOContextI context) {
		final Element element = super.serialize(context);
		XMLObjectSerializer.serializeInto(ConfigurationStartedEventGenerator.class, this, element);
		return element;
	}

	@Override
	public void deserialize(final Element node, final IOContextI context) throws LCException {
		super.deserialize(node, context);
		XMLObjectSerializer.deserializeInto(ConfigurationStartedEventGenerator.class, this, node);
	}
	//========================================================================

}
