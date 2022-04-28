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

import javafx.scene.control.TreeItem;
import org.lifecompanion.model.api.configurationcomponent.TreeDisplayableComponentI;
import javafx.scene.control.TreeCell;
import org.lifecompanion.ui.app.displayablecomponent.CommonComponentStage;
import org.lifecompanion.ui.common.control.specific.componenttree.ComponentSelectionTree;

import java.util.Map;

/**
 * Tree cell to display all elements of the configuration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SelectionItemTreeCell extends TreeCell<TreeDisplayableComponentI> {
    @Override
    protected void updateItem(final TreeDisplayableComponentI item, final boolean empty) {
        super.updateItem(item, empty);
        TreeDisplayableComponentCell.updateItem(this, item, empty);
        this.setOnMouseClicked(me -> {
            if (me.getClickCount() >= 2) {
                TreeItem<TreeDisplayableComponentI> treeItem = this.getTreeItem();
                if (!treeItem.isExpanded()) {
                    CommonComponentStage.getInstance().show();
                }
            }
        });
    }
}
