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
package org.lifecompanion.model.impl.plugin;

import java.util.HashSet;

public class PluginLoadingResult {
	private String pluginFile;
	private HashSet<String> errorTextIds;
	private boolean found;

	public PluginLoadingResult(final String pluginFile) {
		super();
		this.pluginFile = pluginFile;
		this.errorTextIds = new HashSet<>();
	}

	public String getPluginFile() {
		return this.pluginFile;
	}

	public boolean isFound() {
		return found;
	}

	public void setPluginFile(final String pluginFile) {
		this.pluginFile = pluginFile;
	}

	public HashSet<String> getErrorTextIds() {
		return this.errorTextIds;
	}

	public void validPluginFound() {
		this.found = true;
	}

}
