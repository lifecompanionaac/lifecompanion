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

package org.lifecompanion.api.voice;

import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.io.XMLSerializable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

/**
 * Represent a voice selected in the parameters.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface VoiceParameterI extends XMLSerializable<IOContextI> {

	/**
	 * @return voice id that identify this voice
	 */
	public StringProperty voiceIdProperty();

	/**
	 * @return a name for this voice
	 */
	public StringProperty voiceNameProperty();

	/**
	 * @return voice language code for this voice
	 */
	public StringProperty voiceLanguageProperty();

	/**
	 * @return the selected voice (directly selected from the synthesizer list).<br>
	 * This selected voice is selected with this parameters.
	 */
	public ObjectProperty<VoiceInfoI> selectedVoiceInfoProperty();
}
