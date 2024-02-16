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

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.VirtualMouseType;
import org.lifecompanion.controller.resource.IconHelper;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

public class VirtualMouseDrawingListCell extends ListCell<VirtualMouseType> {
	private ImageView imageView;

	public VirtualMouseDrawingListCell() {
		this.imageView = new ImageView();
		this.setGraphicTextGap(10.0);
	}

	@Override
	protected void updateItem(final VirtualMouseType item, final boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			this.setText(null);
			this.setGraphic(null);
		} else {
			this.setText(StringUtils.capitalize(item.getText()));
			this.imageView.setImage(IconHelper.get(item.getImagePath()));
			this.setGraphic(this.imageView);
		}
	}
}
