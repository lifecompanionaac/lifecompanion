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

import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.base.data.media.SoundPlayer;
import org.lifecompanion.base.data.useevent.baseimpl.BaseUseEventGeneratorImpl;
import org.lifecompanion.api.useevent.category.DefaultUseEventSubCategories;
import javafx.beans.value.ChangeListener;

public class SoundPlayerDisbaledEventGenerator extends BaseUseEventGeneratorImpl {

	public SoundPlayerDisbaledEventGenerator() {
		super();
		this.parameterizableAction = false;
		this.order = 5;
		this.category = DefaultUseEventSubCategories.STATUS;
		this.nameID = "use.event.configuration.sound.player.disabled.name";
		this.staticDescriptionID = "use.event.configuration.sound.player.disabled.name";
		this.configIconPath = "configuration/icon_sound_enabled.png";
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
		SoundPlayer.INSTANCE.disableSoundPlayerProperty().addListener(this.changeListener);
	}

	@Override
	public void modeStop(final LCConfigurationI configuration) {
		SoundPlayer.INSTANCE.disableSoundPlayerProperty().removeListener(this.changeListener);
	}
	//========================================================================
}
