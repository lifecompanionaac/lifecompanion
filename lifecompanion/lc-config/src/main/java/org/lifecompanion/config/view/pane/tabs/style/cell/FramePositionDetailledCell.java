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

package org.lifecompanion.config.view.pane.tabs.style.cell;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.api.component.definition.FramePosition;
import org.lifecompanion.base.data.config.IconManager;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

public class FramePositionDetailledCell extends ListCell<FramePosition> {
	private ImageView imageView;

	public FramePositionDetailledCell() {
		this.imageView = new ImageView();
	}

	@Override
	protected void updateItem(final FramePosition item, final boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			this.setText(null);
			this.setGraphic(null);
		} else {
			this.setText(StringUtils.capitalize(item.getText()));
			this.imageView.setImage(IconManager.get(item.getImagePath()));
			this.setGraphic(this.imageView);
		}
	}

}
