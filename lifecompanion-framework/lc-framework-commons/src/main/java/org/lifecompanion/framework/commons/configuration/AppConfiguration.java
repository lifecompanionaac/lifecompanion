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

package org.lifecompanion.framework.commons.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;

public class AppConfiguration {
	private final Properties properties;

	private AppConfiguration() {
		this.properties = new Properties();
	}

	// Class part : "GET"
	//========================================================================
	public String getString(String key, String defaultValue) {
		return this.properties.getProperty(key, defaultValue);
	}

	public String getStringOrFail(String key) throws AppConfigurationException {
		String val = this.properties.getProperty(key);
		if (StringUtils.isBlank(val))
			throw new AppConfigurationException("Property \"" + key + "\" in AppConfiguration shouldn't be blank (value = \"" + val + "\")");
		else return val;
	}
	//========================================================================

	// Class part : "SET"
	//========================================================================
	//========================================================================

	// Class part : "IO"
	//========================================================================
	public void write(File file) throws IOException {
		try (final FileOutputStream fos = new FileOutputStream(file)) {
			this.properties.store(fos, null);
		}
	}
	//========================================================================

	// Class part : "INIT"
	//========================================================================
	public static AppConfiguration read(File file) throws IOException {
		return read(file, false);
	}

	public static AppConfiguration read(File file, boolean failOnNotFound) throws IOException {
		AppConfiguration config = new AppConfiguration();
		if (file.exists() || failOnNotFound) {
			try (final FileInputStream fis = new FileInputStream(file)) {
				config.properties.load(fis);
			}
		}
		return config;
	}

	public static AppConfiguration read(String ressourcePath) throws IOException {
		AppConfiguration config = new AppConfiguration();
		try (InputStream is = AppConfiguration.class.getResourceAsStream(ressourcePath)) {
			config.properties.load(is);
		}
		return config;
	}
	//========================================================================

}
