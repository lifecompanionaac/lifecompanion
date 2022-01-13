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

package org.lifecompanion.config.view.component.simple;

import org.lifecompanion.api.ui.ViewProviderI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.base.data.component.simple.GridPartTextEditorComponent;
import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.base.view.component.simple.GridPartTextEditorComponentViewBase;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.component.option.SelectableOption;

public class GridPartTextEditorViewConfig extends GridPartTextEditorComponentViewBase {
    private SelectableOption<GridPartTextEditorComponent> selectableOption;

    @Override
    public void initUI() {
        super.initUI();
        //Select option
        this.selectableOption = new SelectableOption<>(this.model, false);
        this.getChildren().add(this.selectableOption);
        this.selectableOption.bindSize(this);
    }

    @Override
    public void initListener() {
        super.initListener();
        //Selection
        this.setOnMouseClicked((ea) -> {
            SelectionController.INSTANCE.selected(this.model, ea.isShortcutDown(), ea.isShiftDown());
            this.toFront();
        });
        this.setOnMouseEntered((ea) -> {
            GridPartComponentI possiblySelected = SelectionController.INSTANCE.getFirstUnselectedParent(this.model);
            possiblySelected.showPossibleSelectedProperty().set(true);
        });
        this.setOnMouseExited((ea) -> {
            GridPartComponentI possiblySelected = SelectionController.INSTANCE.getFirstUnselectedParent(this.model);
            possiblySelected.showPossibleSelectedProperty().set(false);
        });
    }

    @Override
    public void initialize(ViewProviderI viewProvider, boolean useCache, final GridPartTextEditorComponent componentP) {
        super.initialize(viewProvider, useCache, componentP);
        WritingStateController.INSTANCE.initExampleEntriesIfNeeded();
    }
}
