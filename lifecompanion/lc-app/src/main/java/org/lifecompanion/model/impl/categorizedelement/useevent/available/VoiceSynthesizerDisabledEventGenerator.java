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
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import javafx.beans.value.ChangeListener;

public class VoiceSynthesizerDisabledEventGenerator extends BaseUseEventGeneratorImpl {

	public VoiceSynthesizerDisabledEventGenerator() {
		super();
		this.parameterizableAction = false;
		this.order = 3;
		this.category = DefaultUseEventSubCategories.STATUS;
		this.nameID = "use.event.configuration.voice.synthesizer.disable.name";
		this.staticDescriptionID = "use.event.configuration.voice.synthesizer.disable.description";
		this.configIconPath = "configuration/icon_voice_synthesizer_disabled.png";
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	// Class part : "Mode start/stop"
	//========================================================================
	private ChangeListener<? super Boolean> changeListener;

	@Override
	public void modeStart(final LCConfigurationI configuration) {
		this.changeListener = (obs, ov, nv) -> {
			if (nv) {
				this.useEventListener.fireEvent(this, null, null);
			}
		};
		VoiceSynthesizerController.INSTANCE.disableVoiceSynthesizerProperty().addListener(this.changeListener);
	}

	@Override
	public void modeStop(final LCConfigurationI configuration) {
		VoiceSynthesizerController.INSTANCE.disableVoiceSynthesizerProperty().removeListener(this.changeListener);
	}
	//========================================================================
}
