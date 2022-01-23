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

package org.lifecompanion.base.data.voice;

import org.lifecompanion.api.voice.VoiceInfoI;

import java.util.Locale;

/**
 * Represent the voice informations in the synthesizer.<br>
 * Should never be used out of the synthesizer.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VoiceInfo implements VoiceInfoI {
	private String id;
	private String name;
	private Locale locale;

	/**
	 * Custom user data, can be use to select the correct voice
	 */
	private Object userData;

	public VoiceInfo(final String id, final String name, final Locale locale, final Object userData) {
		super();
		this.id = id;
		this.name = name;
		this.locale = locale;
		this.userData = userData;
	}

	/**
	 * @return id of the selected voice
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * @return name of the selected voice
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * @return locale of the selected voice
	 */
	@Override
	public Locale getLocale() {
		return this.locale;
	}

	/**
	 * @return the user data about this voice (custom data)
	 */
	@Override
	public Object getUserData() {
		return this.userData;
	}

	@Override
	public String toString() {
		return "VoiceInfo{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", locale=" + locale +
				", userData=" + userData +
				'}';
	}
}
