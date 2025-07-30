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

package org.lifecompanion.framework.commons;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Enum that define the current system type.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum SystemType {
	WINDOWS("system.type.windows"), MAC("system.type.mac"), UNIX("system.type.unix"), ANDROID("system.type.android"), IOS("system.type.ios");
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemType.class);

	private String labelID;

	private SystemType(final String labelIDP) {
		this.labelID = labelIDP;
	}

	public String getLabelID() {
		return this.labelID;
	}

	public String getPathCode() {
		return this.name().toLowerCase();
	}

	public String getCode() {
		return this.name();
	}

	private static SystemType current;

	static {
		String osName = System.getProperty("os.name");
		String vendorUrl = System.getProperty("java.vendor.url");
		SystemType.LOGGER.info("Will try to detect system with following properties : OS name : {} | Vendor URL : {}", osName, vendorUrl);
		if (osName != null) {
			if (StringUtils.containsIgnoreCase(osName, "win")) {
				SystemType.current = SystemType.WINDOWS;
			} else if (StringUtils.containsIgnoreCase(vendorUrl, "android")) {
				SystemType.current = SystemType.ANDROID;
			} else if (StringUtils.containsIgnoreCase(osName, "mac")) {
				SystemType.current = SystemType.MAC;
			} else if (StringUtils.containsIgnoreCase(osName, "nux")) {
				SystemType.current = SystemType.UNIX;
			} else {
				SystemType.LOGGER.warn("Couldn't find the system with os name and vendor url, os will be set to IOS");
				SystemType.current = SystemType.IOS;
			}
			SystemType.LOGGER.info("Current detected system is {} ", SystemType.current);
		}
	}

	public static SystemType current() {
		return SystemType.current;
	}

	public static SystemType[] allExpectMobile() {
		return new SystemType[] { WINDOWS, UNIX, MAC };
	}

	public static SystemType[] allExpectComputer() {
		return new SystemType[] { IOS, ANDROID };
	}

	public static boolean isForMobileOnly(SystemType[] systemTypes){
		return systemTypes!=null && Arrays.equals(systemTypes, SystemType.allExpectComputer());
	}

	public static boolean isForComputerOnly(SystemType[] systemTypes){
		return systemTypes!=null && Arrays.equals(systemTypes, SystemType.allExpectMobile());
	}

	public static void setCurrentSystem(final SystemType currentP) {
		SystemType.current = currentP;
	}
}
