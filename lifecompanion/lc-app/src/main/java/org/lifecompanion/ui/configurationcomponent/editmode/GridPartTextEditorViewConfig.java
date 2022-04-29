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

import org.lifecompanion.model.impl.configurationcomponent.GridPartTextEditorComponent;
import org.lifecompanion.ui.configurationcomponent.base.GridPartTextEditorComponentViewBase;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.ui.configurationcomponent.editmode.componentoption.SelectableOption;

public class GridPartTextEditorViewConfig extends GridPartTextEditorComponentViewBase {

    @Override
    public void initUI() {
        super.initUI();
        //Select option
        SelectableOption<GridPartTextEditorComponent> selectableOption = new SelectableOption<>(this.model);
        this.getChildren().add(selectableOption);
        selectableOption.bindSize(this);
    }

    @Override
    public void initListener() {
        super.initListener();
        //Selection
        this.setOnMouseClicked((ea) -> {
            SelectionController.INSTANCE.selectDisplayableComponent(this.model, false);
            this.toFront();
        });
        this.setOnMouseEntered((ea) -> this.model.showPossibleSelectedProperty().set(true));
        this.setOnMouseExited((ea) -> this.model.showPossibleSelectedProperty().set(false));
    }
}
