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

package org.lifecompanion.base.data.ui;


import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.ui.AddTypeEnum;
import org.lifecompanion.api.ui.PossibleAddCategoryEnum;
import org.lifecompanion.api.ui.PossibleAddComponentCategoryI;
import org.lifecompanion.api.ui.PossibleAddComponentI;
import org.lifecompanion.base.data.component.simple.GridPartKeyComponent;

/**
 * Default action to add a key : use the target to create the key to add.</br>
 * This allows the added key to keep the style and content of original key.</br>
 * Only actions are removed.
 */
public abstract class AbstractAddKey implements PossibleAddComponentI<GridPartKeyComponentI> {

    @Override
    public GridPartKeyComponentI getNewComponent(AddTypeEnum addType, Object... optionalParams) {
        GridPartKeyComponentI gridPartKey = new GridPartKeyComponent();
        GridComponentI parentGrid = null;
        GridPartKeyComponentI targetKey = null;
        if (optionalParams != null) {
            if (optionalParams.length > 0 && optionalParams[0] instanceof GridComponentI)
                parentGrid = (GridComponentI) optionalParams[0];
            if (optionalParams.length > 1 && optionalParams[1] instanceof GridPartKeyComponentI) {
                targetKey = (GridPartKeyComponentI) optionalParams[1];
                gridPartKey = (GridPartKeyComponentI) targetKey.duplicate(true);
                // Delete actions (most of the time, changing key option is used to change key actions)
                gridPartKey.userNameProperty().set(null);
                gridPartKey.getActionManager().clear();
            }
        }
        configureKey(gridPartKey, parentGrid, targetKey);
        return gridPartKey;
    }

    protected abstract void configureKey(GridPartKeyComponentI key, GridComponentI parentGrid, GridPartKeyComponentI targetKey);

    @Override
    public PossibleAddComponentCategoryI getCategory() {
        return PossibleAddCategoryEnum.KEYS;
    }

}