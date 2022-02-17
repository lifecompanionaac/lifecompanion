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

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.lifecompanion.controller.editmode.DisplayableComponentSnapshotController;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.util.javafx.FXUtils;

public class DisplayableComponentListCell<T extends DisplayableComponentI> extends ListCell<T> {
    private static final double CELL_SIZE = 150.0;

    /**
     * Image view to see configuration preview
     */
    private final ImageView componentSnapshot;

    public DisplayableComponentListCell() {
        this.componentSnapshot = new ImageView();
        this.componentSnapshot.fitWidthProperty().bind(this.widthProperty().subtract(20));
        this.componentSnapshot.fitHeightProperty().bind(this.heightProperty().subtract(40));
        this.componentSnapshot.setPreserveRatio(true);
        this.componentSnapshot.setSmooth(true);

        FXUtils.setFixedSize(this, CELL_SIZE, CELL_SIZE);

        this.setContentDisplay(ContentDisplay.TOP);
        StackPane.setAlignment(this.componentSnapshot, Pos.CENTER);
        this.setAlignment(Pos.CENTER);
        this.setGraphic(this.componentSnapshot);
        this.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);

        this.itemProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                DisplayableComponentSnapshotController.INSTANCE.cancelRequestSnapshot(ov);
            }
        });
    }

    @Override
    protected void updateItem(final T item, final boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            this.componentSnapshot.setImage(null);
            BindingUtils.unbindAndSetNull(textProperty());
        } else {
            this.textProperty().bind(item.nameProperty());
            componentSnapshot.setImage(null);
            DisplayableComponentSnapshotController.INSTANCE.requestSnapshotAsync(item, true, -1, CELL_SIZE, (comp, image) -> {
                if (this.getItem() == comp) {
                    componentSnapshot.setImage(image);
                }
            });
        }
    }
}
