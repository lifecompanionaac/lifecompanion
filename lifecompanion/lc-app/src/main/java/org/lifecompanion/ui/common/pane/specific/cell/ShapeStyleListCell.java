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
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeType;
import org.lifecompanion.model.api.style.ShapeStyle;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;

public class ShapeStyleListCell extends ListCell<ShapeStyle> {
    private final Region svgContent;
    private SVGPath svgPath;

    public ShapeStyleListCell() {
        svgContent = new Region();
        svgContent.setStyle("-fx-background-color: #03bdf4;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-color:#0277BD;" +
                "-fx-border-width:1px;");
        svgPath = new SVGPath();
        svgContent.setShape(svgPath);
        svgPath.setFill(LCGraphicStyle.MAIN_LIGHT);
        svgPath.setStroke(LCGraphicStyle.MAIN_DARK);
        svgPath.setStrokeType(StrokeType.INSIDE);
        this.setContentDisplay(ContentDisplay.LEFT);
        svgContent.setPrefSize(35, 25.0);
        this.setGraphicTextGap(10.0);
        this.setPrefHeight(40.0);
        this.setMaxHeight(40.0);
        this.setMinHeight(40.0);
    }

    @Override
    protected void updateItem(ShapeStyle item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            svgPath.setContent(null);
            this.setGraphic(null);
            this.setText(null);
        } else {
            svgPath.setContent(item.getCellSvg());
            this.setGraphic(svgContent);
            this.setText(item.getName());
        }
    }
}
