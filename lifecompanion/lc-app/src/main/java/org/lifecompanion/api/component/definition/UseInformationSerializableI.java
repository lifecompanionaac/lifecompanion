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

package org.lifecompanion.api.component.definition;

import java.util.Map;

import org.jdom2.Element;

import org.lifecompanion.api.exception.LCException;

/**
 * Represent a component that can saves information relative to the use mode.<br>
 * The serialize method will be called each time use mode ends, and deserialize on each use mode starts.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface UseInformationSerializableI {

	/**
	 * Serialize all the use information about this component and about the sub components.
	 * @param elements the map containing the use information ( ID <=> XML Node)
	 */
	void serializeUseInformation(Map<String, Element> elements);

	/**
	 * Should read all the use information of this component and these sub components.
	 * @param elements the map containing all the use information read from file ( ID <=> XML Node)
	 * @throws LCException if the node inside can't be parsed
	 */
	void deserializeUseInformation(Map<String, Element> elements) throws LCException;
}
