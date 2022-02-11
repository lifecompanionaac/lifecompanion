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

package org.lifecompanion.model.api.voicesynthesizer;

import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;

/**
 * Represent a information dto that contains both voice and synthesizer id.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface VoiceAndSynthesizerInfoI extends XMLSerializable<IOContextI> {

	/**
	 * @return voice id
	 */
	String getVoiceId();

	/**
	 * @return voice synthesizer id
	 */
	String getSynthesizerId();

	/**
	 * @return voice label (for the voice associated with the id)
	 */
	String getVoiceLabel();

	/**
	 * @return synthesizer label (for the synthesizer associated with the id)
	 */
	String getSynthesizerLabel();

	/**
	 * @return a displayable string for this voice information
	 */
	String getDisplayableLabel();

}
