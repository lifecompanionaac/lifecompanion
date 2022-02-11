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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;

public class PressTimeReachedEventGenerator extends BaseUseEventGeneratorImpl {

	private IntegerProperty timeToReach;

	public PressTimeReachedEventGenerator() {
		super();
		this.parameterizableAction = true;
		this.order = 0;
		this.timeToReach = new SimpleIntegerProperty(5000);
		this.category = DefaultUseEventSubCategories.CLIC;
		this.nameID = "use.event.press.time.reached.name";
		this.staticDescriptionID = "use.event.press.time.reached.static.description";
		this.configIconPath = "control/icon_press_reach_time.png";
		this.variableDescriptionProperty()
				.bind(TranslationFX.getTextBinding("use.event.press.time.reached.variable.description", LCUtils.createDivide1000Binding(this.timeToReach)));
	}

	public IntegerProperty timeToReachProperty() {
		return this.timeToReach;
	}

	// Class part : "Mode start/stop"
	// ========================================================================

	@Override
	public void modeStart(final LCConfigurationI configuration) {
		SelectionModeController.INSTANCE.addAfterPressTimeListener(this.timeToReach.get(), () -> {
			this.useEventListener.fireEvent(this, null, null);
		});
	}

	@Override
	public void modeStop(final LCConfigurationI configuration) {

	}
	// ========================================================================

	// Class part : "IO"
	// ========================================================================
	@Override
	public Element serialize(final IOContextI context) {
		final Element element = super.serialize(context);
		XMLObjectSerializer.serializeInto(PressTimeReachedEventGenerator.class, this, element);
		return element;
	}

	@Override
	public void deserialize(final Element node, final IOContextI context) throws LCException {
		super.deserialize(node, context);
		XMLObjectSerializer.deserializeInto(PressTimeReachedEventGenerator.class, this, node);
	}
	// ========================================================================

}
