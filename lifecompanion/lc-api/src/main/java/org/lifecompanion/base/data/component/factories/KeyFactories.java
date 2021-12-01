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

package org.lifecompanion.base.data.component.factories;


import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.api.component.factories.KeyFactory;
import org.lifecompanion.base.data.component.simple.GridPartKeyComponent;

/**
 * Create the keys for component.<br>
 * This provide base implementation to create component.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum KeyFactories implements KeyFactory {
	DEFAULT;

	@Override
	public GridPartComponentI createKey(final int rowP, final int columnP, final int spanRowP, final int spanColumnP) {
		GridPartKeyComponent key = new GridPartKeyComponent();
		key.rowProperty().set(rowP);
		key.columnProperty().set(columnP);
		key.rowSpanProperty().set(spanRowP);
		key.columnSpanProperty().set(spanColumnP);
		return key;
	}

}
