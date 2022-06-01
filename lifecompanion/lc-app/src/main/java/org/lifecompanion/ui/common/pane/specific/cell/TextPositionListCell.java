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

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.model.api.style.TextPosition;

public class TextPositionListCell extends ListCell<TextPosition> {
    private final ImageView imageView;

    public TextPositionListCell(boolean enableImage) {
        if (enableImage) {
            imageView = new ImageView();
            this.setContentDisplay(ContentDisplay.LEFT);
            this.setGraphicTextGap(10.0);
            this.setPrefHeight(40.0);
            this.setMaxHeight(40.0);
            this.setMinHeight(40.0);
        } else {
            imageView = null;
        }
    }

    @Override
    protected void updateItem(TextPosition item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            if (imageView != null) {
                imageView.setImage(null);
            }
            this.setGraphic(null);
            this.setText(null);
        } else {
            if (imageView != null) {
                imageView.setImage(IconHelper.get(item.getIconUrl()));
                this.setGraphic(imageView);
            }
            this.setText(item.getName());
        }
    }
}
