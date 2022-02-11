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

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;

public class UpperCaseActivationEventGenerator extends BaseUseEventGeneratorImpl {

	public UpperCaseActivationEventGenerator() {
		super();
		this.parameterizableAction = false;
		this.order = 0;
		this.category = DefaultUseEventSubCategories.STATUS;
		this.nameID = "use.event.configuration.upper.case.activation.name";
		this.staticDescriptionID = "use.event.configuration.upper.case.activation.description";
		this.configIconPath = "configuration/icon_uppercase_enabled.png";
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	// Class part : "Mode start/stop"
	//========================================================================
	private BooleanBinding upperCaseProperty;
	private ChangeListener<? super Boolean> changeListener;

	@Override
	public void modeStart(final LCConfigurationI configuration) {
		this.upperCaseProperty = WritingStateController.INSTANCE.capitalizeNextProperty().or(WritingStateController.INSTANCE.upperCaseProperty());
		this.changeListener = (obs, ov, nv) -> {
			if (nv) {
				this.useEventListener.fireEvent(this, null, null);
			}
		};
		this.upperCaseProperty.addListener(this.changeListener);
	}

	@Override
	public void modeStop(final LCConfigurationI configuration) {
		this.upperCaseProperty.removeListener(this.changeListener);
	}
	//========================================================================
}
