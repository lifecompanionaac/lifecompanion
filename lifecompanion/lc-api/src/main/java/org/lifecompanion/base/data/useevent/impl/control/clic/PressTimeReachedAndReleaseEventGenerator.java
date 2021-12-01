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
package org.lifecompanion.base.data.useevent.impl.control.clic;

import org.jdom2.Element;

import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.base.data.control.SelectionModeController;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.useevent.baseimpl.BaseUseEventGeneratorImpl;
import org.lifecompanion.api.useevent.category.DefaultUseEventSubCategories;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PressTimeReachedAndReleaseEventGenerator extends BaseUseEventGeneratorImpl {

	private IntegerProperty timeToReach;

	public PressTimeReachedAndReleaseEventGenerator() {
		super();
		this.parameterizableAction = true;
		this.order = 0;
		this.category = DefaultUseEventSubCategories.CLIC;
		this.timeToReach = new SimpleIntegerProperty(5000);
		this.nameID = "use.event.press.time.reached.and.released.name";
		this.staticDescriptionID = "use.event.press.time.reached.and.released.static.description";
		this.configIconPath = "control/icon_press_release_reach_time.png";
		this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("use.event.press.time.reached.and.released.variable.description",
				LCUtils.createDivide1000Binding(this.timeToReach)));
	}

	public IntegerProperty timeToReachProperty() {
		return this.timeToReach;
	}

	// Class part : "Mode start/stop"
	// ========================================================================

	@Override
	public void modeStart(final LCConfigurationI configuration) {
		SelectionModeController.INSTANCE.addOnReleaseAfterTimeListener(this.timeToReach.get(), () -> {
			//Issue #192 : control event should be considered as actions to avoid repeating activation
			this.useEventListener.fireEvent(this, null, (ar) -> SelectionModeController.INSTANCE.activationDone());
		});
	}

	@Override
	public void modeStop(final LCConfigurationI configuration) {}
	// ========================================================================

	// Class part : "IO"
	// ========================================================================
	@Override
	public Element serialize(final IOContextI context) {
		final Element element = super.serialize(context);
		XMLObjectSerializer.serializeInto(PressTimeReachedAndReleaseEventGenerator.class, this, element);
		return element;
	}

	@Override
	public void deserialize(final Element node, final IOContextI context) throws LCException {
		super.deserialize(node, context);
		XMLObjectSerializer.deserializeInto(PressTimeReachedAndReleaseEventGenerator.class, this, node);
	}
	// ========================================================================

}
