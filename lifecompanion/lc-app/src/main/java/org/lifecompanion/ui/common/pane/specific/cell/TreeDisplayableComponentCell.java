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

import javafx.scene.control.Cell;
import javafx.scene.image.ImageView;
import org.lifecompanion.model.api.configurationcomponent.TreeDisplayableComponentI;
import org.lifecompanion.controller.resource.IconHelper;

/**
 * Cell to display all elements of the configuration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class TreeDisplayableComponentCell {

    protected static <T extends TreeDisplayableComponentI> void updateItem(final Cell<T> cell, final T item, final boolean empty) {
        if (item == null || empty) {
            cell.textProperty().unbind();
            cell.textProperty().set("");
            cell.graphicProperty().set(null);
        } else {
            //Bind to name
            cell.textProperty().unbind();
            cell.textProperty().bind(item.nameProperty());
            //Create the image view when needed
            ImageView imView = (ImageView) cell.graphicProperty().get();
            if (imView == null) {
                imView = new ImageView();
                cell.graphicProperty().set(imView);
            }
            //Display icon
            imView.setImage(item.getNodeType().isIconValid() ? IconHelper.get(item.getNodeType().getIconPath()) : null);
        }
    }
}
