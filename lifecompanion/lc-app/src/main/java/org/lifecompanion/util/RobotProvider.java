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

package org.lifecompanion.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * Just a simple class to provide the AWT {@link Robot} instance.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RobotProvider {
	private final static Logger LOGGER = LoggerFactory.getLogger(RobotProvider.class);
	private static Robot instance;

	public static Robot getInstance() {
		if (RobotProvider.instance == null) {
			try {
				RobotProvider.instance = new Robot();
			} catch (Throwable t) {
				RobotProvider.LOGGER.warn("Couldn't create the AWT robot instance", t);
			}
		}
		return RobotProvider.instance;
	}
}
