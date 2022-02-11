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

package org.lifecompanion.ui.common.pane.specific.cell;

import org.lifecompanion.model.api.textprediction.BasePredictorI;
import javafx.scene.control.ListCell;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class BasePredictorSimpleListCell<T extends BasePredictorI> extends ListCell<T> {

	@Override
	protected void updateItem(final T itemP, final boolean emptyP) {
		super.updateItem(itemP, emptyP);
		if (itemP == null || emptyP) {
			this.setText(null);
		} else {
			this.setText(itemP.getName());
		}
	}

}
