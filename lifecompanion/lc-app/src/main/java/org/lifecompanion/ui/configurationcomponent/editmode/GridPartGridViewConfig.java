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

package org.lifecompanion.ui.configurationcomponent.editmode;

import org.lifecompanion.model.impl.configurationcomponent.GridPartGridComponent;
import org.lifecompanion.ui.configurationcomponent.base.GridPartGridViewBase;
import org.lifecompanion.ui.configurationcomponent.editmode.componentoption.SelectableOption;

/**
 * Node that display a {@link GridPartGridComponent}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridPartGridViewConfig extends GridPartGridViewBase {

    @Override
    public void initUI() {
        super.initUI();
        //Selectable component option
        SelectableOption<GridPartGridComponent> option = new SelectableOption<>(this.model);
        option.bindSize(this);
        this.getChildren().add(option);
    }

    @Override
    public String toString() {
        return "" + this.model;
    }
}
