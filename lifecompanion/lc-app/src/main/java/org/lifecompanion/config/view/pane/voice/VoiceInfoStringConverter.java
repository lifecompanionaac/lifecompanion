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

package org.lifecompanion.config.view.pane.voice;

import org.lifecompanion.model.api.voicesynthesizer.VoiceInfoI;
import javafx.util.StringConverter;

/**
 * Convert to convert a voice into string
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VoiceInfoStringConverter extends StringConverter<VoiceInfoI> {

	@Override
	public String toString(final VoiceInfoI info) {
		if (info == null) {
			return null;
		} else {
			return info.getName() + (info.getLocale() != null ? "  ( " + info.getLocale().getLanguage() + " )" : "");
		}
	}

	@Override
	public VoiceInfoI fromString(final String stringP) {
		//Never used now
		return null;
	}

}
