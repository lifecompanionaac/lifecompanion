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

import org.lifecompanion.model.api.selectionmode.ProgressDrawMode;
import javafx.scene.control.ListCell;

/**
 * Cell to display the progress draw mode.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProgressDrawModeSimpleListCell extends ListCell<ProgressDrawMode> {

	@Override
	protected void updateItem(final ProgressDrawMode item, final boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			this.setText(null);
		} else {
			this.setText(item.getLabel());
		}
	}

}
