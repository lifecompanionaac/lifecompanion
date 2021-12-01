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
package org.lifecompanion.config.view.pane.compselector;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.lifecompanion.api.component.definition.DisplayableComponentI;
import org.lifecompanion.api.ui.ComponentViewI;
import org.lifecompanion.base.data.common.UIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplayableComponentListCell<T extends DisplayableComponentI> extends ListCell<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisplayableComponentListCell.class);

    /**
     * Image view to see configuration preview
     */
    private ImageView componentSnapshot;

    public DisplayableComponentListCell() {
        this.componentSnapshot = new ImageView();
        this.componentSnapshot.fitWidthProperty().bind(this.widthProperty().subtract(20));
        this.componentSnapshot.fitHeightProperty().bind(this.heightProperty().subtract(40));

        this.setMaxWidth(150.0);
        this.setPrefWidth(150.0);
        this.setPrefHeight(150.0);
        this.setMaxHeight(150.0);

        this.componentSnapshot.setPreserveRatio(true);
        this.componentSnapshot.setSmooth(true);
        this.setContentDisplay(ContentDisplay.TOP);
        StackPane.setAlignment(this.componentSnapshot, Pos.CENTER);
        this.setAlignment(Pos.CENTER);
        this.setGraphic(this.componentSnapshot);
        this.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
    }

    @Override
    protected void updateItem(final T item, final boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            this.componentSnapshot.imageProperty().set(null);
            this.textProperty().unbind();
            this.textProperty().set(null);
        } else {
            this.textProperty().bind(item.nameProperty());
            ComponentViewI<?> display = item.getDisplay();
            if (display != null) {
                Region itemView = display.getView();
                try {
                    // StackChildComponentI displayedProperty > image are not loaded if the child is not visible
                    this.componentSnapshot.setImage(UIUtils.takeNodeSnapshot(itemView, -1, 150));
                } catch (Throwable t) {
                    DisplayableComponentListCell.LOGGER.warn("Impossible to take a component snapshot for component {}", item.nameProperty().get(), t);
                }
            }
        }
    }
}
