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

package org.lifecompanion.api.mode;

import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.ui.ViewProviderI;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;

/**
 * Represent the application mode.<br>
 * For now can just be use, or configuration.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface LCModeI extends LCStateListener {

	// Class part : "View"
	//========================================================================
	/**
	 * The view provider for this application mode.<br>
	 * The view provider can be shared between modes, but can be unique to provide view related to the current mode.
	 * @return the view provider for this mode
	 */
	ViewProviderI getViewProvider();

	/**
	 * The scene that should be displayed for this mode
	 * @return the scene for this mode
	 */
	Scene initializeAndGetScene();
	//========================================================================

	// Class part : "Mode info"
	//========================================================================
	/**
	 * @return the app mode that represent this mode in the mode enum
	 */
	AppMode getMode();

	/**
	 * @return the current configuration for this mode (use by this mode)
	 */
	ObjectProperty<LCConfigurationI> currentConfigurationProperty();

	/**
	 * @return the previous configuration before stopping this mode.<br>
	 * Can be used to restore the configuration.
	 */
	ObjectProperty<LCConfigurationI> configurationBeforeChangeProperty();
	//========================================================================

	// Class part : "Mode start/stop"
	//========================================================================
	/**
	 * Called when LifeCompanion switch current mode to use this mode
	 * @param configuration the configuration that will be in use in this new selected mode
	 */
	void modeStart(LCConfigurationI configuration);

	/**
	 * Called when LifeCompanion switch current mode to use another mode than this mode
	 * @param configuration the configuration that was in use in this mode
	 */
	void modeStop(LCConfigurationI configuration);

	boolean isSkipNextModeStartAndReset();
	//========================================================================

}
