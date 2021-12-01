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

package org.lifecompanion.config.view.reusable;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.utils.FluentHashMap;

import java.util.Map;

public class ContentDisplayListCell extends ListCell<ContentDisplay> {
    private final ImageView imageView;

    public ContentDisplayListCell(boolean enableImage) {
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
    protected void updateItem(ContentDisplay item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            if (imageView != null) {
                imageView.setImage(null);
            }
            this.setGraphic(null);
            this.setText(null);
        } else {
            ContentDisplayInfo info = CONTENT_DISPLAY_INFO_MAP.get(item);
            if (imageView != null) {
                imageView.setImage(IconManager.get(info.imageUrl));
                this.setGraphic(imageView);
            }
            this.setText(Translation.getText(info.textId));
        }
    }

    private static final Map<ContentDisplay, ContentDisplayInfo> CONTENT_DISPLAY_INFO_MAP = FluentHashMap
            .map(ContentDisplay.BOTTOM, new ContentDisplayInfo("text.location.bottom", "text-position/icon_text_position_bottom.png", "tooltip.text.position.bottom"))
            .with(ContentDisplay.TOP, new ContentDisplayInfo("text.location.top", "text-position/icon_text_position_top.png", "tooltip.text.position.top"))
            .with(ContentDisplay.RIGHT, new ContentDisplayInfo("text.location.right", "text-position/icon_text_position_right.png", "tooltip.text.position.right"))
            .with(ContentDisplay.LEFT, new ContentDisplayInfo("text.location.left", "text-position/icon_text_position_left.png", "tooltip.text.position.left"))
            .with(ContentDisplay.CENTER, new ContentDisplayInfo("text.location.center", "text-position/icon_text_position_center.png", "tooltip.text.position.center"));

    private static class ContentDisplayInfo {
        private final String textId;
        private final String imageUrl;
        private final String tooltipId;

        public ContentDisplayInfo(String textId, String imageUrl, String tooltipId) {
            this.textId = textId;
            this.imageUrl = imageUrl;
            this.tooltipId = tooltipId;
        }
    }
}
